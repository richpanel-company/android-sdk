package com.richpanel.android;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RichpanelAPI {

    private static final String TAG = "RichpanelAPI";
    private static RichpanelAPI instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private RichpanelAPI (Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
//        this.queue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static synchronized RichpanelAPI getInstance(Context context) {
        if (instance == null) {
            instance = new RichpanelAPI(context);
        }
        return instance;
    }

    public void callPostAPI(String url, JSONObject data) {
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

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

        this.addToRequestQueue(request);
    }
}
