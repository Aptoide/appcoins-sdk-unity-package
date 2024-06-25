package com.appcoins.osp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.unity3d.player.UnityPlayer

class OspActivity : Activity() {
    companion object {
        private val TAG = OspActivity::class.java.simpleName
        private const val REQUEST_CODE = 1234
        lateinit var url: String
        //var intent: Intent = Intent()
        //var ospUrl: String = ""

        @JvmStatic
        fun start(context: Activity, url: String) {
            Log.d(TAG, "$context && $url")
            OspActivity.url = url
            //OspActivity.intent = intent
            //ospUrl = url
            val starter = Intent(context, OspActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = buildTargetIntent(url)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     * This method generates the intent with the provided One Step URL to target the
     * AppCoins Wallet.
     * @param url The url that generated by following the One Step payment rules
     *
     * @return The intent used to call the wallet
     */
    private fun buildTargetIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        // Check if there is an application that can process the AppCoins Billing
        // flow
        val packageManager = applicationContext.packageManager
        val appsList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (app in appsList) {
            if (app.activityInfo.packageName == "cm.aptoide.pt") {
                // If there's aptoide installed always choose Aptoide as default to open
                // url
                intent.setPackage(app.activityInfo.packageName)
                break
            } else if (app.activityInfo.packageName == "com.appcoins.wallet") {
                // If Aptoide is not installed and wallet is installed then choose Wallet
                // as default to open url
                intent.setPackage(app.activityInfo.packageName)
            }
        }
        return intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (data == null || data.extras == null) {
                finish()
                return
            }
            val transactionHash = data.getStringExtra("transaction_hash")
            val url = URL("https://api.catappult.io/broker/8.20200101/transactions?hash=$transactionHash")
            Thread {
                val ospUri = Uri.parse(OspActivity.url)
                UnityPlayer.UnitySendMessage(
                    "PurchaseManager",
                    "OnPurchaseFinished",
                    ospUri.getQueryParameter("product")
                )
            }.start()
            Log.wtf(TAG, "$transactionHash")
            finish()
        }
    }
}