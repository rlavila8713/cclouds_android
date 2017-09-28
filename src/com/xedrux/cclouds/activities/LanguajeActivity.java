package com.xedrux.cclouds.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.adapters.LanguageAdapter;
import com.xedrux.cclouds.constants.FRAGMENT_ACTIONS;
import com.xedrux.cclouds.interfaces.OnActionPerformed;
import com.xedrux.cclouds.utils.HandleSharePref;

import java.util.Locale;

/**
 * Created by Reinier on 14/11/2015.
 */
public class LanguajeActivity extends Activity implements OnActionPerformed, AdapterView.OnItemClickListener {
    LanguageAdapter lanAdapter;
    String previowsLanguage;
    OnActionPerformed listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        listener = (OnActionPerformed)this;
        previowsLanguage = HandleSharePref.getLanguageCode(this);
        init();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        changeLang(lanAdapter.languages.get(i).code);
        HandleSharePref.saveLanguageName(this, lanAdapter.languages.get(i).name);
        listener.onActionPerformed(FRAGMENT_ACTIONS.GO_BACK, false);
        Intent backIntent = new Intent();
        backIntent.putExtra("languageName", lanAdapter.languages.get(i).name);
        setResult(Activity.RESULT_OK, backIntent);
    }

    public void init() {
        ListView list = (ListView) findViewById(R.id.listView1);
        lanAdapter = new LanguageAdapter(this);
        list.setAdapter(lanAdapter);
        list.setOnItemClickListener(this);
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        HandleSharePref.saveLanguageCode(this, lang);
        Locale myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        this.getBaseContext().getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onActionPerformed(int action, boolean close) {
        switch (action) {
            case FRAGMENT_ACTIONS.GO_BACK:
                if (!close) {
                    finish();
                }
                break;
        }
    }
}
