package com.richpanel.android;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONObject;

import java.util.HashMap;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private String apiUrl = "https://ws.richpanel.com/ucp/updateCookieAndroidDeviceToken";
    private String deviceId;
    private Context context;
    private String appClientId;
    private String did;

    public void initialize(Context context, String deviceId, String appClientId, String did) {
        this.context = context;
        this.deviceId = deviceId;
        this.appClientId = appClientId;
        this.did = did;

        FirebaseInstanceId.getInstance().getInstanceId()
        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                Log.d(TAG, "New token : " + token);

                sendRegistrationToServer(token);
            }
        });
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Todo - sendRegistrationToServer

        if (deviceId != null) {
            HashMap requestDataMap = new HashMap();
            requestDataMap.put("appClientId", this.appClientId);
            requestDataMap.put("androidFirebaseToken", token);
            requestDataMap.put("androidDeviceId", this.deviceId);
            requestDataMap.put("did", this.did);

            JSONObject requestData = new JSONObject(requestDataMap);

            RichpanelAPI.getInstance(context.getApplicationContext()).callPostAPI(apiUrl, requestData);
        }
    }

}
