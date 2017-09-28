package com.xedrux.cclouds.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.DataBaseHelper;
import com.xedrux.cclouds.utils.SingletonPattern;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Isidro on 9/6/2015.
 */
public class NetworkActions extends IntentService {

    LocalBroadcastManager localBroadcastManager;
    public NetworkActions() {
        super("Cclouds_Network_Worker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        localBroadcastManager=LocalBroadcastManager.getInstance(getApplicationContext());
        if (SingletonPattern.isUpdate_needed(getApplicationContext())) {
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    Utils.loadPreferences(getApplication());
                    localBroadcastManager.sendBroadcast(new Intent("sync_comenzada"));
                /*if (getApplicationContext().getMainLooper().myLooper() == null) {
                    getApplicationContext().getMainLooper().prepare();
                }*/
                    File tmp = File.createTempFile("compressed", null, getFilesDir());
                    Utils.compressFile(DataBaseHelper.getDbFile(getApplicationContext()), tmp);
                    boolean result = WebServiceHelper.uploadFile1(tmp, URLS.WEB_SERVICE_URL, Utils.uNameValue, Utils.passwordValue, getApplicationContext());
                    tmp.delete();
                    SingletonPattern.uploading = false;
                    SingletonPattern.setUpdate_needed(getApplicationContext(), !result);
                    localBroadcastManager.sendBroadcast(new Intent("sync_terminada"));
                }
            }catch(IOException e){
                SingletonPattern.setUpdate_needed(getApplicationContext(), true);
                e.printStackTrace();
            }
            finally{
                localBroadcastManager.sendBroadcast(new Intent("sync_terminada"));
            }
        }
    }

}
