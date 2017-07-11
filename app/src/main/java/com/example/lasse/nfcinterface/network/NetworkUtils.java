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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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
    private static final String DYNAMIC_REST_URL = "179.106.217.220:8080";

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

    public static URL getUrlForGetAll() {
        try {
            URL url = new URL(buildUriForAllUsers().toString());
            Log.v(TAG,"URL : "+url);
            return url;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getUrlForDelete(String uid) {
        try {
            URL url = new URL(buildUriToDelete(uid).toString());
            Log.v(TAG,"URL : "+url);
            return url;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Uri buildUriToDelete(String uid) {
        TYPE_QUERY = "delete";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).encodedAuthority(DYNAMIC_REST_URL)
                .appendPath(PROJECT_NAME)
                .appendPath(REST_PATH)
                .appendPath(REST_ACCESS)
                .appendPath(TYPE_QUERY)
                .appendPath(uid);

        return builder.build();
    }

    private static Uri buildUriForAllUsers() {

        TYPE_QUERY = "get";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).encodedAuthority(DYNAMIC_REST_URL)
                .appendPath(PROJECT_NAME)
                .appendPath(REST_PATH)
                .appendPath(REST_ACCESS)
                .appendPath(TYPE_QUERY)
                .appendPath("all");

        return builder.build();
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
                .appendPath("details")
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

    public static void getResponseFromHttpUrlForUid(Context context,URL url,final NetworkUtils.ResponseUserExistCallback callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"JSON RESPONSE DETAIL USER "+response.toString());
                callback.responseUserInDb(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.responseUserInDb(null);
            }
        });

        Volley.newRequestQueue(context).add(jsonObjectRequest);

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
                if( networkResponse != null && networkResponse.statusCode == HTTP_STATUS_CONFLICT) reponse[0] = -1;
                else reponse[0] = -2;
                callback.reponseHttpPostRequete(reponse[0]);
            }
        }){

        };



        callback.reponseHttpPostRequete(-1);
        Volley.newRequestQueue(context).add(stringRequest);
        Log.d(TAG,String.valueOf(reponse[0]));

    }

    public static void sendGetAllUsersRequest(Context context,URL url,final NetworkUtils.ResponseHttpCallback callback) {
        JSONArray jArray = new JSONArray();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"Reponse : "+response.toString());
                callback.reponseHttpGetAllRequete(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error on retrieving JSON --- "+ error.getMessage());
            }
        });
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public static void sendDeleteUserRequest(Context context,URL url,final NetworkUtils.ReponseDeleteHttpCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"DELETE USER SUCCESS");
                callback.reponseHttpDeleteUser(0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"DELETE USER ERROR");
                callback.reponseHttpDeleteUser(-1);
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public interface ResponseUserExistCallback {
        void responseUserInDb(JSONObject jobj);
    }
    public interface ReponseDeleteHttpCallback {
        void reponseHttpDeleteUser(int reponse);
    }
    public interface ResponseHttpCallback {
        public void reponseHttpPostRequete(int reponse);
        void reponseHttpGetAllRequete(JSONObject jObj);
    }


}
