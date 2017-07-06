package com.example.lasse.nfcinterface.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;

/**
 * Created by lasse on 05/07/17.
 */

public class HandleEmulator {

    public static final String IS_EMULATED = "45452548";
    public static final String UID_EMULATED = "54665255";
    private static SharedPreferences sharedPreferences;
    public static final String getUidFromSharedPrefs(Context context) {
        String uid = "";
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPrefsKey),Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(HandleEmulator.IS_EMULATED)) return "";
        else if(sharedPreferences.getBoolean(HandleEmulator.IS_EMULATED,false))
            uid = sharedPreferences.getString(NetworkUtils.UID_FIELD,"");
        return uid;
    }
}
