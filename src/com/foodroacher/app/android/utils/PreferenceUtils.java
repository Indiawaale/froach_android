package com.foodroacher.app.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferenceUtils {
    private static final String PREFS_APP = "prefs";

    private static final String PREFS_KEY_USER = "userid";
    private static final String PREFS_KEY_IS_REGISTERED = "is_registered";

    public static boolean saveUser(Context context, String userId){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_APP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREFS_KEY_USER, userId);
        editor.putBoolean(PREFS_KEY_IS_REGISTERED,true);
        return editor.commit();
    }
    public static String getUserId(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_APP,Context.MODE_PRIVATE);
        return sharedPref.getString(PREFS_KEY_USER, null);
    }
    public static boolean setRegistered(Context context, boolean isRegistered){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_APP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(PREFS_KEY_IS_REGISTERED,isRegistered);
        return editor.commit();
    }
    
    public static boolean isRegistered(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_APP,Context.MODE_PRIVATE);
        return sharedPref.getBoolean(PREFS_KEY_IS_REGISTERED, false);
    }
}
