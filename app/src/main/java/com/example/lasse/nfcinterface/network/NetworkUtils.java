package com.example.lasse.nfcinterface.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Naif on 29/06/17.
 */

public class NetworkUtils {

    public static final String UID_FIELD = "uid";
    public static final String NAME_FIELD = "name";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String RANK_FIELD = "rank";
    public static final String POST_JSON_RESPONSE_FIELD = "reponse";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String SCHEME = "http";
    private static final String DYNAMIC_REST_URL = "192.168.1.13:8080";
    private static final String PORT = "8080";
    private static final String PROJECT_NAME = "RestTest";
    private static final String REST_PATH = "webapi";
    private static final String REST_ACCESS = "nfcaccess";
    private static final int HTTP_STATUS_CONFLICT = 409;
    private static  String TYPE_QUERY = "get";
    private static String UID = "";

    public static URL getUrl(String uid) {
        try {
            URL url = new URL(buildUrlWithGetQuery(uid).toString());
            Log.v(TAG,"URL : "+url);
            return url;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getUrlForPost() {
        try {
            URL url = new URL(buildUriForPostQuery().toString());
            Log.v(TAG,"URL : "+url);
            return url;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Uri buildUrlWithGetQuery(String uid) {
        UID = uid;
        TYPE_QUERY = "get";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).encodedAuthority(DYNAMIC_REST_URL)
                .appendPath(PROJECT_NAME)
                .appendPath(REST_PATH)
                .appendPath(REST_ACCESS)
                .appendPath(TYPE_QUERY)
                .appendPath(UID);

        return builder.build();
    }

    private static Uri buildUriForPostQuery() {
        TYPE_QUERY = "post";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).encodedAuthority(DYNAMIC_REST_URL)
                .appendPath(PROJECT_NAME)
                .appendPath(REST_PATH)
                .appendPath(REST_ACCESS)
                .appendPath(TYPE_QUERY);


        return builder.build();
    }

    private static boolean getResponseFromHttpUrl(Context context,URL url) {
        final boolean[] isInDb = {false};
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HTTP RESPONSE ------"+response);

                if(Integer.parseInt(response.trim()) >= 0) {
                    isInDb[0] = true;
                } else isInDb[0] = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isInDb[0] = false;
            }
        }) {
            protected Map<String, String> getParams() {
                //data to send
                Map<String, String> data = new HashMap<String, String>();
                return data;
            }
        };

        requestQueue.add(stringRequest);
        return isInDb[0];
    }

    public static void sendPostRequest(Context context, URL url, final JSONObject jsonData,final NetworkUtils.ResponseHttpCallback callback) {
        final int[] reponse = {-1};
        Log.d(TAG,"JSON DATA : "+jsonData.toString());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url.toString(), jsonData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG,"REPONSE ----- "+String.valueOf(response.get("reponse")));
                    reponse[0] = Integer.parseInt(String.valueOf(response.get(POST_JSON_RESPONSE_FIELD)));
                    callback.reponseHttpPostRequete(reponse[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;

                Log.d(TAG,"ERROR ----- "+error.getMessage());
                if(networkResponse.statusCode == HTTP_STATUS_CONFLICT) reponse[0] = -1;
                else reponse[0] = -2;
                callback.reponseHttpPostRequete(reponse[0]);
            }
        }){

        };



        callback.reponseHttpPostRequete(-1);
        Volley.newRequestQueue(context).add(stringRequest);
        Log.d(TAG,String.valueOf(reponse[0]));

    }

    public interface ResponseHttpCallback {
        public void reponseHttpPostRequete(int reponse);
    }

}
