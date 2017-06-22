package com.kieling.rssreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

public final class Utils {
    public static final String RSS_PARAMETER_KEY = "rss";
    public static final String IMAGE_PARAMETER_KEY = "image";
    public static final String RSS_URL_SET_KEY = "rssList";
    private static final String TAG = "Utils";

    public static void writeStringToSharedPreferences(Context context, String key, String value) {
        Log.d(TAG, String.format("Saving String %s: %s", key, value));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String readStringFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void writeObjectToSharedPreferences(Context context, String key, Object value) {
        Log.d(TAG, String.format("Saving object %s: %s", key, value));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public static Object readObjectFromSharedPreferences(Context context, String key, Class objClass) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString(key, "");
        return new Gson().fromJson(json, objClass);
    }
}
