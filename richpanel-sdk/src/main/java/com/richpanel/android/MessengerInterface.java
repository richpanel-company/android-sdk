package com.richpanel.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

public class MessengerInterface {
    Context mContext;
    private final String sharedPreferenceFile = "preference_file_key";
    private SharedPreferences sharedPref;
    private final String DID_KEY = "richpanel_did";

    MessengerInterface(Context ctx){
        this.mContext = ctx;
        this.sharedPref = mContext.getSharedPreferences(this.sharedPreferenceFile, Context.MODE_PRIVATE);
    }

    /**
     * Call Android.updateDid(did) from Javascript if changed
     * @param did
     */
    @JavascriptInterface
    public void updateDid(String did) {
        //Get the string value to process
        persistDid(did);
    }

    private void persistDid (String did) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.DID_KEY, did);
        editor.commit();
    }
}
