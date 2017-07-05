package com.example.lasse.nfcinterface.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lasse.nfcinterface.network.NetworkUtils;

/**
 * Created by lasse on 05/07/17.
 */

public class SharedPrefsHelper {

    private static SharedPreferences sharedPreferences;
    public static SharedPreferences getSharedPrefs(Context context,String key, int mode) {
        sharedPreferences = context.getSharedPreferences(key,mode);
        return sharedPreferences;
    }

    public static int getRank() {
        if(sharedPreferences != null && sharedPreferences.contains(NetworkUtils.RANK_FIELD)) {
            return sharedPreferences.getInt(NetworkUtils.RANK_FIELD,-1);
        }
        return -1;
    }
}
