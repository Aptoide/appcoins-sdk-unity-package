import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;

import com.appcoins.sdk.billing.listeners.*;
import com.appcoins.sdk.billing.AppcoinsBillingClient;
import com.appcoins.sdk.billing.PurchasesUpdatedListener;
import com.appcoins.sdk.billing.BillingFlowParams;
import com.appcoins.sdk.billing.Purchase;
import com.appcoins.sdk.billing.PurchasesResult;
import com.appcoins.sdk.billing.ResponseCode;
import com.appcoins.sdk.billing.SkuDetails;
import com.appcoins.sdk.billing.SkuDetailsParams;
import com.appcoins.sdk.billing.helpers.CatapultBillingAppCoinsFactory;
import com.appcoins.sdk.billing.types.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;

public class AptoBridge {
    private static String MSG_INITIAL_RESULT = "InitialResult";
    private static String MSG_CONNECTION_LOST = "ConnectionLost";
    private static String MSG_PRODUCTS_GET_RESULT = "ProductsGetResult";
    private static String MSG_LAUNCH_BILLING_RESULT = "LaunchBillingResult";
    private static String MSG_PRODUCTS_PAY_RESULT = "ProductsPayResult";
    private static String MSG_PRODUCTS_CONSUME_RESULT = "ProductsConsumeResult";
    private static String MSG_QUERY_PURCHASES_RESULT = "QueryPurchasesResult";

    private static String LOG_TAG = "[AptoBridge]";

    private static Activity activity;
    private static String unityClassName = "SDKLogic";
    private static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvqN6DDp164Z6T/sc6wfMX+8GSkNr3GJYG+DqgpD07ke/CwaXC+dkuahivhGOUFVDHi6l4iHhcnfy+mv6aZOttgvmbBsqjY5BgTUZV7yYXR0vnElvxXYge9Yor7q8x5elKF3wXHp6EVgyU1zGtVjivaiJRip6E6kpSagkY4DpdBS2SVEZbIhl+5yHW6spnZrE4thgOZCd7rdg5Nn1HlMkajlpnfACRsqWPoBpk8fgfCptDKicO7hY1tRkvSrtCXa7fJC6cwt6j2JdbzpbHUNS6fdMUGOnds9cyGTHtTp+z9R8ffb+y1DXGLVEu/4YFPFiJuWN7esSv/xEIliEIfK1GwIDAQAB";

    private static boolean needLog = true;

    private static AppCoinsBillingStateListener appCoinsBillingStateListener = new AppCoinsBillingStateListener() {
        @Override
        public void onBillingSetupFinished(int responseCode) {
            AptoLog("onBillingSetupFinished responseCode = " + responseCode);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("msg", MSG_INITIAL_RESULT);
                jsonObject.put("succeed", responseCode == ResponseCode.OK.getValue());
                jsonObject.put("responseCode", responseCode);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            SendUnityMessage(jsonObject);
        }

        @Override
        public void onBillingServiceDisconnected() {
            AptoLog("onBillingServiceDisconnected");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("msg", MSG_CONNECTION_LOST);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            SendUnityMessage(jsonObject);
        }
    };

    public static AppcoinsBillingClient cab = null;

    private static PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(int responseCode, List<Purchase> purchases)
        {
            AptoLog("purchasesUpdatedListener " + responseCode);
            JSONObject jsonObject = new JSONObject();
            JSONArray purchasesJson = new JSONArray();
            for(Purchase purchase: purchases)
            {
                JSONObject purchaseJson = GetPurchaseJson(purchase);
                purchasesJson.put(purchaseJson);
            }

            try {
                jsonObject.put("msg", MSG_PRODUCTS_PAY_RESULT);
                jsonObject.put("succeed", responseCode == ResponseCode.OK.getValue());
                jsonObject.put("responseCode", responseCode);
                jsonObject.put("purchases", purchasesJson);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            SendUnityMessage(jsonObject);
        }
    };

    private static ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
        @Override public void onConsumeResponse(int responseCode, String purchaseToken) {
            AptoLog("Consumption finished. Purchase: " + purchaseToken + ", result: " + responseCode);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("msg", MSG_PRODUCTS_CONSUME_RESULT);
                jsonObject.put("succeed", responseCode == ResponseCode.OK.getValue());
                jsonObject.put("purchaseToken", purchaseToken);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            SendUnityMessage(jsonObject);
        }
    };

    private static SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
        @Override
        public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
            AptoLog("Received skus " + responseCode);
            JSONObject jsonObject = new JSONObject();
            if(responseCode == ResponseCode.OK.getValue()) {
                JSONArray jsonSkus = new JSONArray();
                for (SkuDetails skuDetails : skuDetailsList) {
                    JSONObject detailJson = GetSkuDetailsJson(skuDetails);
                    jsonSkus.put(detailJson);
                }
                try {
                    jsonObject.put("msg", MSG_PRODUCTS_GET_RESULT);
                    jsonObject.put("succeed", true);
                    jsonObject.put("responseCode", responseCode);
                    jsonObject.put("products", jsonSkus);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    jsonObject.put("msg", MSG_PRODUCTS_GET_RESULT);
                    jsonObject.put("succeed", false);
                    jsonObject.put("responseCode", responseCode);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            SendUnityMessage(jsonObject);
        }
    };

    public static void Initialize(String _unityClassName, String _publicKey, boolean _needLog)
    {
        AptoLog("Apto Initialize");
        activity = UnityPlayer.currentActivity;
        unityClassName = _unityClassName;
        publicKey = _publicKey;
        needLog = _needLog;

        cab = CatapultBillingAppCoinsFactory.BuildAppcoinsBilling(
                activity,
                publicKey,
                purchasesUpdatedListener
        );
        cab.startConnection(appCoinsBillingStateListener);
    }

    public static void ProductsStartGet(String strSku)
    {
        AptoLog("Products Start Get");
        List<String> skuList = new ArrayList<String>(Arrays.asList(strSku.split(";")));
        AptoLog("skuList = " + skuList);

        SkuDetailsParams skuDetailsParams = new SkuDetailsParams();
        skuDetailsParams.setItemType(SkuType.inapp.toString());
        skuDetailsParams.setMoreItemSkus(skuList);
        cab.querySkuDetailsAsync(skuDetailsParams, skuDetailsResponseListener);
    }

    public static void ProductsStartPay(String sku, String developerPayload)
    {
        AptoLog("Launching purchase flow.");
        // Your sku type, can also be SkuType.subs.toString()
        String skuType = SkuType.inapp.toString();
        BillingFlowParams billingFlowParams =
                new BillingFlowParams(
                        sku,
                        skuType,
                        "orderId=" +System.currentTimeMillis(),
                        developerPayload,
                        "BDS"
                );

        final int responseCode = cab.launchBillingFlow(activity, billingFlowParams);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", MSG_LAUNCH_BILLING_RESULT);
            jsonObject.put("succeed", responseCode == ResponseCode.OK.getValue());
            jsonObject.put("responseCode", responseCode);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        SendUnityMessage(jsonObject);
          
    }

    public static void QueryPurchases()
    {
        PurchasesResult purchasesResult = cab.queryPurchases(SkuType.inapp.toString());
        List<Purchase> purchases = purchasesResult.getPurchases();

        JSONArray purchasesJson = new JSONArray();
        for (Purchase purchase : purchases) {
            JSONObject detailJson = GetPurchaseJson(purchase);
            purchasesJson.put(detailJson);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", MSG_QUERY_PURCHASES_RESULT);
            jsonObject.put("succeed", true);
            jsonObject.put("purchases", purchasesJson);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        SendUnityMessage(jsonObject);
    }

    public static void ProductsStartConsume(String strToken)
    {
        AptoLog("Products Start Consume");
        List<String> tokenList = new ArrayList<String>(Arrays.asList(strToken.split(";")));
        AptoLog("tokenList = " + tokenList);

        for(String token: tokenList)
        {
            cab.consumeAsync(token, consumeResponseListener);
        }
    }

    public static void SendUnityMessage(JSONObject jsonObject)
    {
        UnityPlayer.UnitySendMessage(unityClassName, "OnMsgFromPlugin", jsonObject.toString());
    }

    public static JSONObject GetSkuDetailsJson(SkuDetails skuDetails)
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("appPrice", skuDetails.getAppcPrice());
            jsonObject.put("appcPriceAmountMicros", skuDetails.getAppcPriceAmountMicros());
            jsonObject.put("appcPriceCurrencyCode", skuDetails.getAppcPriceCurrencyCode());
            jsonObject.put("description", skuDetails.getDescription());
            jsonObject.put("fiatPrice", skuDetails.getFiatPrice());
            jsonObject.put("fiatPriceAmountMicros", skuDetails.getFiatPriceAmountMicros());
            jsonObject.put("fiatPriceCurrencyCode", skuDetails.getFiatPriceCurrencyCode());
            jsonObject.put("itemType", skuDetails.getItemType());
            jsonObject.put("price", skuDetails.getPrice());
            jsonObject.put("priceAmountMicros", skuDetails.getPriceAmountMicros());
            jsonObject.put("priceCurrencyCode", skuDetails.getPriceCurrencyCode());
            jsonObject.put("sku", skuDetails.getSku());
            jsonObject.put("title", skuDetails.getTitle());
            jsonObject.put("type", skuDetails.getType());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject GetPurchaseJson(Purchase purchase)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("developerPayload", purchase.getDeveloperPayload());
            jsonObject.put("isAutoRenewing", purchase.isAutoRenewing());
            jsonObject.put("itemType", purchase.getItemType());
            jsonObject.put("orderId", purchase.getOrderId());
            jsonObject.put("originalJson", purchase.getOriginalJson());
            jsonObject.put("packageName", purchase.getPackageName());
            jsonObject.put("purchaseState", purchase.getPurchaseState());
            jsonObject.put("purchaseTime", purchase.getPurchaseTime());
            jsonObject.put("sku", purchase.getSku());
            jsonObject.put("token", purchase.getToken());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public static void ShareActivityResult(int requestCode, int resultCode, String dataPurchase, String dataSignature) {
        AptoLog("Launching Shared Activity Result. reqCode " + requestCode + " resultCode: " + resultCode + " dataPurchase: " + dataPurchase + "dataSignature: " + dataSignature);
        Intent intent = new Intent();
        if(requestCode==51){
            intent.putExtra("INAPP_PURCHASE_DATA", dataPurchase);
            intent.putExtra("INAPP_DATA_SIGNATURE", dataSignature);
            cab.onActivityResult(requestCode, resultCode, intent);
        }else{
            intent.removeExtra("INAPP_PURCHASE_DATA");
            intent.removeExtra("INAPP_DATA_SIGNATURE");
        }
    }


    public static void AptoLog(String msg) {
        if (needLog) {
            Log.d(LOG_TAG, msg);
        }
    }
}
