package com.richpanel.android;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class RichpanelSDK {

    private static final String TAG = "RichpanelSDK";
    private String apiKey;
    private String apiSecret;
    private Context context;
    private String deviceId;
    private RequestQueue queue;
    private String url = "https://e93df050.ngrok.io/";
    private Map userProperties;

    public RichpanelSDK(Context context, String apiKey, String apiSecret) {
        this.context = context;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.queue = Volley.newRequestQueue(context);
        this.userProperties = new HashMap();
        this.deviceId = Secure.getString(context.getContentResolver(), "android_id");
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

            if (userProperties != null && !userProperties.isEmpty() && eventName != "identify") {
                preparedData.put("userProperties", userProperties);
            }

            JSONObject preparedJsonData = new JSONObject(preparedData);

            HashMap requestDataMap = new HashMap();
            requestDataMap.put("h", Base64.encodeToString(preparedJsonData.toString().getBytes(), Base64.DEFAULT));

            JSONObject requestData = new JSONObject(requestDataMap);

            JsonObjectRequest request = new JsonObjectRequest
            (Request.Method.POST, url, requestData, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d( TAG, "Response: " + response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d( TAG, "Response: " + error.getMessage());
                }
            });

            this.queue.add(request);
        }

    }

    public void InitiateMessenger() {
        Intent intent = new Intent(this.context, MessengerActivity.class);
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

}
