package com.xedrux.cclouds.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.xedrux.cclouds.constants.SHARED_PREF_IDS;

public class HandleSharePref {

    public static final String tag = "HandleSharePref";

    public static void saveLanguageCode(Context ctx, String languageCode) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString("languageCode", languageCode);

        edit.commit();
    }

    public static void saveLanguageName(Context ctx, String languageName) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString("languageName", languageName);
        edit.commit();
    }

    public static String getLanguageCode(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("languageCode", Locale.getDefault().getLanguage());
    }

    public static String getLanguageName(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("languageName", Locale.getDefault().getDisplayLanguage());
    }

    public static void saveAppDescription(Context ctx, String app, String description) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString(app + "_desc", description);
        edit.commit();
    }

    public static void saveAppName(Context ctx, String app, String name) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString(app + "_name", name);
        edit.commit();
    }

    public static String getAppDesription(Context ctx, String app) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(app + "_desc", "");
    }

    public static String getAppName(Context ctx, String app) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(app + "_name", "");
    }

    public static void saveApp(Context ctx, String app) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putBoolean(app + getLanguageCode(ctx), true);
        edit.commit();
    }

    public static boolean getApp(Context ctx, String app) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(app + getLanguageCode(ctx), false);
    }

    public static boolean getFirstTimeLaunched(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(SHARED_PREF_IDS.FIRST_TIME_LAUNCHED, true);
    }

    public static boolean getShareInfo(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("shareInfo", false);
    }

    public static int getCategorySelected(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt("pos", 0);
    }

}
