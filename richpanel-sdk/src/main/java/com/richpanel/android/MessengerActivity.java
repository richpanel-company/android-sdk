package com.richpanel.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.net.URI;
import java.net.URL;

import okhttp3.HttpUrl;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";

    private WebView webview;
    private WebSettings webSettings;

    private String apiKey;
    private String apiSecret;
    private String deviceId;
    private String did;
    private String url;
    private String encryptUserData;

    private final String scheme = "https";
    private final String host = "app-dev.richpanel.com";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.activity_messenger);

        Intent intent = getIntent();
        this.apiKey = intent.getStringExtra("apiKey");
        this.apiSecret = intent.getStringExtra("apiSecret");
        this.deviceId = intent.getStringExtra("deviceId");
        this.encryptUserData = intent.getStringExtra("encryptUserData");
        this.url = this.getURL();

        this.webview = (WebView)this.findViewById(R.id.webview);
        this.webSettings = this.webview.getSettings();

        this.initiateMessenger();

        /** https://developer.android.com/guide/webapps/webview.html#BindingJavaScript **/
        this.webview.addJavascriptInterface(new MessengerInterface(this), "Android");
        this.openMessenger();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        this.webview.saveState(bundle);
//    }

//    https://stackoverflow.com/questions/45403060/how-to-not-reload-webview-when-resuming-app-again
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        this.webview.onResume();
//    }

    @Override
    public void onResume() {
        if (this.webview != null) {
            this.webview.onResume();
        }
        super.onResume();
    }

    private String getURL() {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .addQueryParameter("appClientId", this.apiKey)
                .addQueryParameter("did", this.deviceId)
                .addQueryParameter("encryptUserData", this.encryptUserData)
                .addQueryParameter("helpcenterMode", "fullscreen")
                .build();

        return httpUrl.toString();
    }

    protected void initiateMessenger() {
        CookieManager.getInstance().setAcceptCookie(true);

        this.webSettings.setJavaScriptEnabled(true);
        this.webSettings.setDomStorageEnabled(true);
        this.webSettings.setUseWideViewPort(true);
        this.webSettings.setLoadWithOverviewMode(true);
    }

    protected void openMessenger() {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading..", "Please wait.. ", true);
        progressDialog.setCancelable(false);

        this.webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }
        });


//        "https://ambiguous-cover.surge.sh/"
        this.webview.loadUrl(this.url);
    }

    /**
     * Refernce: https://developer.android.com/guide/webapps/webview#java
     * @param keyCode
     * @param event
     * @return
     */

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (this.webview != null) {
//            if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webview.canGoBack()) {
//                this.webview.goBack();
//                return true;
//            } else if ((keyCode == KeyEvent.KEYCODE_FORWARD) && this.webview.canGoForward()) {
//                this.webview.goForward();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onBackPressed() {
        if(this.webview.canGoBack()) {
            this.webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void getFirebaseToken() {
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

                // Log and toast
                Log.d(TAG, token);
                Toast.makeText(MessengerActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
