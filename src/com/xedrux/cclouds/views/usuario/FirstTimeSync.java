package com.xedrux.cclouds.views.usuario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Isidro on 10/9/2015.
 */
public class FirstTimeSync extends ActionBarActivity {
    String token;
    boolean restarted = false;
    int serverC=0,deviceC=0;
    AlertDialog alertDialog;
    DownloadContactsFromServer downloadTask;
    DBActions dbActionsAsyncTask;
    AlertDialog.Builder builder;
    Context context;
    ContactsManager contactsManager;
    SharedPreferences preferences;

    ProgressBar progressBar;
    TextView textView;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            token=savedInstanceState.getString("token");
        }else {
            token = getIntent().getStringExtra("token");
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restarted = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.loadPreferences(getApplication());
        context=getApplicationContext();
        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        contactsManager= new ContactsManager(context);
        button = (Button) findViewById(R.id.try_again_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptDownload();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.network_status1);

        if (!restarted) {
            attemptDownload();
        }
        else {
            if (alertDialog == null || !alertDialog.isShowing()) {
                hideProgress();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("token",token);
    }

    private void attemptDownload(){
        if(downloadTask!=null)
            return;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (!Utils.isNetworkAvailable(context)) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.open_settings);
            builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setItems(R.array.networkOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = null;
                    switch (i) {
                        case 0:
                            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            break;
                        case 1:
                            intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            break;
                    }
                    if (intent!=null&&(intent.resolveActivity(getPackageManager()) != null)) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, String.format(getResources().getString(R.string.unable_to_start_activity), getResources().getStringArray(R.array.networkOptions)[i]), Toast.LENGTH_LONG).show();
                        hideProgress();
                    }
                }


            });
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
            v.vibrate(200);
        }else {
            showProgress();
            downloadTask = new DownloadContactsFromServer();
            builder=new AlertDialog.Builder(context);
            downloadTask.execute(URLS.WEB_SERVICE_URL);
        }

    }
    private void showProgress() {
        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgress() {
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private class DownloadContactsFromServer extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            serverC = 0;
            deviceC = 0;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                DataBaseHelper.createDb(context);
                File tmp= File.createTempFile("2decompress",null,context.getFilesDir());
                WebServiceHelper.downloadFile(tmp, strings[0],
                        Utils.getID(context), token) ;
                Utils.decompressFile(tmp,DataBaseHelper.getDbFile(context));
                tmp.delete();
                serverC = contactsManager.isThereAnyContactInTheServer()  ? 1 : 0;
                deviceC = contactsManager.isThereAnyContactInThisDevice() ? 2 : 0;
                System.out.println("serverC "+serverC+" deviceC "+deviceC);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                switch (serverC | deviceC) {//serverC=>Contacts from Server; deviceC=>Contacts in Device
                    case 1://(1|0)
                        askIfContactsAreToBeImported();
                        break;
                    case 0:
                    case 2:
                        dbActionsAsyncTask=new DBActions();
                        dbActionsAsyncTask.execute(1);
                        break;
                    case 3:
                        showDialogForContactsInBoth();
                        break;
                }
            }else{
                hideProgress();
            }
            downloadTask=null;
        }

        @Override
        protected void onCancelled() {
            downloadTask=null;
        }
    }

    public void askIfContactsAreToBeImported(){
        builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.contactsBackupOnServer);
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbActionsAsyncTask=new DBActions();
                dbActionsAsyncTask.execute(0);
            }
        });
        builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbActionsAsyncTask=new DBActions();
                dbActionsAsyncTask.execute(1);
            }
        });
        alertDialog=builder.create();
        alertDialog.show();
    }
    public void showDialogForContactsInBoth(){
        builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.contactsBackupOnBoth);
        builder.setItems(R.array.contactsSynchronizationByValues, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbActionsAsyncTask = new DBActions();
                dbActionsAsyncTask.execute(i);
            }
        });
        builder.setCancelable(false);
        alertDialog=builder.create();
        alertDialog.show();
    }

    private class DBActions extends AsyncTask<Integer, Void, Boolean>{
        //TODO en este m'etodo hay que escribir el codigo para tener en cuenta los errores y return false when needed
        @Override
        protected Boolean doInBackground(Integer... integers) {
            int choice=integers[0];
            switch (choice){
                case 0://replace device contacts
                    contactsManager.deleteDeviceContacts(true);
                    contactsManager.copyFromDB2Device();
                    break;
                case 1://replace server contacts
                    contactsManager.clearDB(true);
                    contactsManager.copyFromDevice2DB();
                    SingletonPattern.setUpdate_needed(context,true);
                    break;
                case 2://mix
                    contactsManager.copyFromDB2Device();
                    contactsManager.clearDB(true);
                    contactsManager.copyFromDevice2DB();
                    SingletonPattern.setUpdate_needed(context,true);
                    break;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            preferences.edit().putString("token",token).commit();
            startActivity(new Intent(context,ContactSynch.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbActionsAsyncTask!=null)
            System.out.println("Activity is being destroyed before  finishing to apply changes to contacts.");
    }
}