package com.xedrux.cclouds.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.xedrux.cclouds.utils.DataBaseHelper;
import com.xedrux.cclouds.utils.Utils;

import java.io.File;

/**
 * Created by reinier on 07/09/2015.
 */
public class ContactSynchAsync extends AsyncTask<Void,Void,Boolean> {
    private Context context;
    private String serverAddress;
    private SharedPreferences preferences;


    public ContactSynchAsync(Context context) {
        this.context = context;
        serverAddress = preferences.getString("serverAddress", "NA");
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File db_file = DataBaseHelper.getDbFile(context);
        return  true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
