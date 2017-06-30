package com.example.lasse.nfcinterface.network;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lasse on 29/06/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String SCHEME = "http";
    private static final String DYNAMIC_REST_URL = "192.168.1.13:8080";
    private static final String PORT = "8080";
    private static final String PROJECT_NAME = "RestTest";
    private static final String REST_PATH = "webapi";
    private static final String REST_ACCESS = "nfcaccess";
    private static final String TYPE_QUERY = "get";

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

    private static Uri buildUrlWithGetQuery(String uid) {
        UID = uid;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).encodedAuthority(DYNAMIC_REST_URL)
                .appendPath(PROJECT_NAME)
                .appendPath(REST_PATH)
                .appendPath(REST_ACCESS)
                .appendPath(TYPE_QUERY)
                .appendPath(UID);

        return builder.build();
    }


}
