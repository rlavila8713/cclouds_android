package com.xedrux.cclouds.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.loopj.android.http.RequestHandle;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.services.NetworkActions;

import java.io.IOException;

/**
 * Created by Isidro on 9/1/2015.
 */
public class NetworkStateChangesReceiver extends BroadcastReceiver {
    String user, pass, WEB_SERVICE_URL; //TODO precibir estas variables en el constructor y pasarlas al métdo uploadfile
    public NetworkStateChangesReceiver(String user, String pass, String WEB_SERVICE_URL) {
        this.user = user;
        this.pass = pass;
        this.WEB_SERVICE_URL = WEB_SERVICE_URL;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connectivity = !intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
        Log.d("cclouds_service", "Connectivity? " + connectivity);
        Log.d("cclouds_service", "Connectivity receiver is working OK File???: " + DataBaseHelper.getDbFile(context).exists());
        if (connectivity) {

            if (SingletonPattern.isUpdate_needed(context)) {
                context.startService(new Intent(context, NetworkActions.class));
            }
        } else {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("connectivity_gone"));
        }
    }
}
