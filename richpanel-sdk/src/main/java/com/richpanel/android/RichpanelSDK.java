package com.richpanel.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

public class RichpanelSDK implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "RichpanelSDK";
    private String apiKey;
    private String apiSecret;
    private Context context;
    private String deviceId;
    private String did;
    private String eventUrl = "https://api.richpanel.com/v2/t";
    private Map userProperties;
    private final String DID_KEY = "richpanel_did";
    private final String sharedPreferenceFile = "preference_file_key";
    private SharedPreferences sharedPref;
    private MessagingService messagingService;

    public RichpanelSDK(Context context, String apiKey, String apiSecret) {
        this.context = context;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.userProperties = new HashMap();
        this.deviceId = Secure.getString(context.getContentResolver(), "android_id");

        this.sharedPref = context.getSharedPreferences(this.sharedPreferenceFile, Context.MODE_PRIVATE);

        initializeDid();
        messagingService = new MessagingService(); //deviceId
        messagingService.initialize(context, deviceId, appClientId, did);
    }

    private String encryptUserData() {
        if (userProperties != null) {
            // To do encrypt data
        }
        return null;
    }

    private void logEventWithValidation(String eventName, Map<String, Object> properties) {
        if (TextUtils.isEmpty(eventName)) {
            Log.i("RichpanelSDK", "The event name is null or empty. We can't log an event with this string.");
        } else {

            Log.d(TAG, "Event: " +eventName);

            HashMap preparedData = new HashMap();
            preparedData.put("event", eventName);
            preparedData.put("properties", properties);
            preparedData.put("appClientId", this.apiKey);
            preparedData.put("did", this.deviceId);

            String encryptUserData = encryptUserData();
            preparedData.put("encryptUserData", encryptUserData);

            if (userProperties != null && !userProperties.isEmpty() && eventName != "identify") {
                preparedData.put("userProperties", userProperties);
            }

            JSONObject preparedJsonData = new JSONObject(preparedData);

            HashMap requestDataMap = new HashMap();
            requestDataMap.put("h", Base64.encodeToString(preparedJsonData.toString().getBytes(), Base64.DEFAULT));

            JSONObject requestData = new JSONObject(requestDataMap);

            RichpanelAPI.getInstance(this.context.getApplicationContext()).callPostAPI(eventUrl, requestData);
        }

    }

    public void InitiateMessenger() {
        Intent intent = new Intent(this.context, MessengerActivity.class);
        intent.putExtra("apiKey", this.apiKey);
        intent.putExtra("apiSecret", this.apiSecret);
        intent.putExtra("deviceId", this.deviceId);
        this.context.startActivity(intent);
    }

    public void identify(Map userProperties) {
        if (userProperties == null) {
            Log.i("RichpanelSDK", "The properties provided is null, can't log event");
        } else if (userProperties.isEmpty()) {
            Log.i("RichpanelSDK", "The properties provided is empty, can't log event");
        } else if (userProperties.get("uid") == null) {
            Log.i("RichpanelSDK", "The property uid is null, can't log event");
        } else {
            this.userProperties = userProperties;
            this.logEventWithValidation("identify", userProperties);
        }
    }

    public void track(String eventName) {
        this.logEventWithValidation(eventName, Collections.EMPTY_MAP);
    }

    public void track(String eventName, Map properties) {
        if (properties == null) {
            Log.i("RichpanelSDK", "The properties provided is null, logging event with no properties");
            properties = new HashMap();
        } else if (properties.isEmpty()) {
            Log.i("RichpanelSDK", "The properties provided is empty, logging event with no properties");
        }

        this.logEventWithValidation(eventName, properties);
    }

    private void initializeDid () {
        did = sharedPref.getString(this.DID_KEY, UUID.randomUUID().toString());
        persistDid(did);
    }

    private void updateDid() {
        did = UUID.randomUUID().toString();
        persistDid(did);
    }

    private void persistDid (String did) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.DID_KEY, did);
        editor.commit();
    }

    public void logout() {
        if (did != null) {
            updateDid();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (this.DID_KEY == key) {
            this.did = sharedPreferences.getString(this.DID_KEY, UUID.randomUUID().toString());
        }
    }
}
