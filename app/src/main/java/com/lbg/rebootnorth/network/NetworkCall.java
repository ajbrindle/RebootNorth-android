package com.lbg.rebootnorth.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 29/10/2017
 */

public class NetworkCall {

    //private static ProgressDialog progressDialog;
    private static RequestQueue queue;

    private static final String UPLOAD_URL = "https://westeurope.api.cognitive.microsoft.com/vision/v1.0/describe?maxCandidates=1";
    private static final String USER_URL = "http://www.sk7software.co.uk/mileage/user.php";
    private static final String TAG = NetworkCall.class.getSimpleName();

    public interface NetworkCallback {
        public void onRequestCompleted(Map<String, Integer>callbackData);
        public void onError(Exception e);
    }

    private synchronized static RequestQueue getQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public static void uploadPicture(final Context context, final String url, final NetworkCallback callback) {
        Map<String, String> json = new HashMap<>();
        json.put("url", url);
        Log.d(TAG, "Uploading: " + json);
        try {
            JSONObject urlData = new JSONObject(json);
            Log.d(TAG, "JSON:" + urlData);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, UPLOAD_URL, urlData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, "Response: " + response);
//                                    if (progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
                                    callback.onRequestCompleted(null);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Error => " + error.toString());
//                                    if (progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
                                    NetworkResponse response = error.networkResponse;
                                    if (response != null) {
                                        try {
                                            String res = new String(response.data,
                                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                            // Now you can use any deserializer to make sense of data
                                            Log.d(TAG, res);
                                        } catch (UnsupportedEncodingException e1) {
                                            // Couldn't properly decode data to string
                                            e1.printStackTrace();
                                        }
                                    }
                                    callback.onError(error);
                                }
                            }
                    ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Ocp-Apim-Subscription-Key", "814e4d250ea342b58225a708909db37b");
                    return params;
                }
            };
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 4, 1));
            getQueue(context).add(jsObjRequest);
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Saving Route");
//            progressDialog.show();
        } catch (Exception e) {
            Log.d(TAG, "Error uploading route: " + e.getMessage());
        }
    }
}
