package com.xedrux.cclouds.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.xedrux.cclouds.R;
import java.util.Locale;

/**
 * Created by reinier on 14/08/2015.
 */
public class Preferences extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        /*Preference pref = (Preference) findPreference("languageName");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), 1);
                return true;
            }
        });*/

        Preference serverPref = findPreference("serverAddress");
        serverPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                //Toast.makeText(getApplicationContext(),data.getExtras().getString("languageName"),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("languageName")) {
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            Preference p = findPreference("languageName");
            p.setSummary("(" + sp.getString("languageName", Locale.getDefault().getDisplayLanguage()) + ")");
        }
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        Preference p = findPreference("languageName");
        p.setSummary("(" + sp.getString("languageName", Locale.getDefault().getDisplayLanguage()) + ")");

        p = findPreference("serverAddress");
        p.setSummary(sp.getString("serverAddress","http://www.example.com"));
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
