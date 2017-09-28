package com.xedrux.cclouds.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.*;

/**
 * Created by Isidro on 9/1/2015.
 */
public class CcloudsService extends Service {
    String user, pass;//TODO pensar donde pongo estos valores para no perderlos en caso de que el service sea destruido y luego creado otra vez por el OS
    NetworkStateChangesReceiver networkStateChangesReceiver;
    ContentObserver contentObserver;
    ServiceAsynTask asynTask;
    public CcloudsService() {
        super();
    }

    @Override
    public void onCreate() {
        asynTask=null;
        Utils.loadPreferences(getApplication());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(networkStateChangesReceiver);
//        getContentResolver().unregisterContentObserver(contentObserver);
        Log.d("cclouds_service", "OnDestroy has been called.");
        super.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1489, new Notification());
        if(asynTask==null) {
            asynTask=new ServiceAsynTask();
            asynTask.execute();
        }
        System.out.println("On Start command was called.");
        System.out.println("is content observer null? " + (contentObserver == null));

        return START_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceAsynTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("executing service set up!!--------------------------------------");
            ContactsManager contactsManager=new ContactsManager(getApplicationContext());
            if(!contactsManager.areContactsEqual())
                SingletonPattern.setUpdate_needed(getApplicationContext(),true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            networkStateChangesReceiver = new NetworkStateChangesReceiver(Utils.uNameValue,Utils.passwordValue,URLS.WEB_SERVICE_URL);//TODO pasar como argumentos el user y el pass
            contentObserver = new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange) {
                    Log.d("cclouds_service", "Contacts have change. :D");
                    ContactsManager cm = new ContactsManager(getApplicationContext());
                    SingletonPattern.setUpdate_needed(getApplicationContext(),false);
                    Utils.setDbLock(getApplicationContext(),true);
                    cm.clearDB(false);
                    cm.copyFromDevice2DB();
                    Utils.setDbLock(getApplicationContext(),false);
                    if (Utils.isNetworkAvailable(getApplicationContext())) {
                        //stet the alarm
                        Intent intent= new Intent(getApplicationContext(),NetworkActions.class);
                        AlarmManager alarmManager;
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+1000*15, PendingIntent.getService(
                                getApplicationContext(), 123456, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        SingletonPattern.setUpdate_needed(getApplicationContext(),true);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("sync_coming_soon"));
                    } else {
                        SingletonPattern.setUpdate_needed(getApplicationContext(),true);
                        Log.d("cclouds_service", "Database will be uploaded as soon as there is connection");
                    }
                    super.onChange(selfChange);
                }
            };

            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            registerReceiver(networkStateChangesReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
}
