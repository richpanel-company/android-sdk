package com.richpanel.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Layout;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MessengerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.activity_messenger);
        this.openMessenger();
    }

    protected void openMessenger() {
        CookieManager.getInstance().setAcceptCookie(true);
        WebView webview = (WebView)this.findViewById(R.id.webview);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading..", "Please wait.. ", true);
        progressDialog.setCancelable(false);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webview.setWebViewClient(new WebViewClient(){
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


        webview.loadUrl("https://ambiguous-cover.surge.sh/");
    }
}
