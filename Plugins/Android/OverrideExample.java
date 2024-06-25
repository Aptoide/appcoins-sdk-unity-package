package com.appcoins.diceroll;

import com.unity3d.player.UnityPlayerActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;

import android.content.SharedPreferences;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class OverrideExample extends UnityPlayerActivity {  
  
  public SharedPreferences pref;
  public SharedPreferences.Editor editor;

  protected void onCreate(Bundle savedInstanceState) {
    // Calls UnityPlayerActivity.onCreate()
    super.onCreate(savedInstanceState);

    Log.d("OverrideExample", "onCreate called!");

    pref = getApplicationContext().getSharedPreferences("com.appcoins.diceroll.v2.playerprefs", MODE_PRIVATE);
    //String newquestion=pref.getString("key_question", null);
    editor = pref.edit();
    editor.putString("hasDonePurchase", "--");
    editor.apply();
  }
  
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    String dataPurchaseJson = data.getStringExtra("INAPP_PURCHASE_DATA");
    String dataSignatureJson = data.getStringExtra("INAPP_DATA_SIGNATURE");

    if(requestCode==51){
      if(resultCode==-1){
        sharedPrefHasDonePurchase("1",dataPurchaseJson,dataSignatureJson);

      }else{
        sharedPrefHasDonePurchase("0","","");
      }
    }
  }
    
  public void sharedPrefHasDonePurchase(String value, String dataPurchaseJson, String dataSignatureJson)
  {
    editor.putString("hasDonePurchase", value);
    editor.putString("purchaseData", dataPurchaseJson);
    editor.putString("purchaseSignature", dataSignatureJson);
    editor.apply();  
  }

}