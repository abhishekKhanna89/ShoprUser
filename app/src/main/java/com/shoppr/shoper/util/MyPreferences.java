package com.shoppr.shoper.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    public static SharedPreferences.Editor editor;
    public static SharedPreferences preferences;


    public static void saveBoolean(Context context, String key, boolean value) {
        editor = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit();
        editor.putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void saveString(Context context, String key, String value) {
        editor = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit();
        editor.putString(key, value).commit();
    }

    public static String getSavedString(Context context, String key) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void clerPref(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        preferences.edit().clear().commit();

    }

    public static void saveInt(Context context, String key, Integer data) {
        editor = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit();
        editor.putInt(key, data).apply();
    }

    public static Integer getInt(Context context, String key) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

}
