package com.xedrux.cclouds.activities;

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
import android.widget.*;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.ContactsManager;
import com.xedrux.cclouds.utils.HandleSharePref;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;
import com.xedrux.cclouds.views.usuario.ContactSynch;
import com.xedrux.cclouds.views.usuario.Login;
import com.xedrux.cclouds.views.usuario.TermsAndConditions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Isidro on 10/6/2015.
 */
public class MainActivity extends ActionBarActivity {
    SharedPreferences preferences;
    Context context;
    RegisterTask registerTask;
    boolean restarted = false;

    ImageView button;
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    TextView network_status1,network_status2;
    ImageView network_status_image;

    ImageView search, img1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale myLocale = new Locale(HandleSharePref.getLanguageCode(this));
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restarted = true;
    }

    //este es un cambio
    @Override
    protected void onResume() {
        super.onResume();
        Utils.loadPreferences(getApplication());
        context = getApplicationContext();
        boolean isConditionAccept = Utils.isConditionAccept(getApplication());
        if (isConditionAccept) {

            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String token = preferences.getString("token", "");

            if (token.equals("")) {
                setContentView(R.layout.activity_main);
                button = (ImageView) findViewById(R.id.try_again_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptRegister();
                    }
                });
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                network_status1 = (TextView) findViewById(R.id.network_status1);
                network_status2 = (TextView) findViewById(R.id.network_status2);
                network_status_image = (ImageView) findViewById(R.id.network_status_image);
                if (!restarted)
                    attemptRegister();
                else {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        hideProgress();
                    }
                }

            } else {
                Intent userIntent = new Intent(this, ContactSynch.class);
                startActivity(userIntent);
                finish();
            }

        } else {
            startActivity(new Intent(this, TermsAndConditions.class));
            finish();
        }
    }

    public void attemptRegister() {
        if (registerTask != null)
            return;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        showProgress();
        if (Utils.getID(context) == null) {
            Toast.makeText(context, R.string.errNoPhoneNumber, Toast.LENGTH_LONG).show();
            //without phone number the app can't work so tell the user and finish, show dialog and finish
            v.vibrate(200);
            finish();
        }
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
                            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            break;
                    }
                    if (intent != null && (intent.resolveActivity(getPackageManager()) != null)) {
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
        } else {
            showProgress();
            registerTask = new RegisterTask();
//            Toast.makeText(this, "Enviando solicitud de registro a: " + URLS.SERVER_ADDRESS, Toast.LENGTH_LONG).show();
            registerTask.execute(String.format(URLS.REGISTER_PHONE_URL, Utils.getID(context)), URLS.SERVER_ADDRESS + ACTIONS.ACTION_REGISTER);
        }

    }

    private void showProgress() {
        button.setVisibility(View.INVISIBLE);
        network_status1.setVisibility(View.INVISIBLE);
        network_status2.setVisibility(View.INVISIBLE);
        network_status_image.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        network_status1.setVisibility(View.VISIBLE);
        network_status2.setVisibility(View.VISIBLE);
        network_status_image.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.user_exists);
        builder.setPositiveButton(R.string.log_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, Login.class));
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showSendEmailDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.wanna_provide_email);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, SendEmailAddress.class));
                finish();
            }
        });
        builder.setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(context, ContactSynch.class));
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public class RegisterTask extends AsyncTask<String, Void, Boolean> {

        private boolean user_exists;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            user_exists = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            JSONObject serverResponse;
            try {
                serverResponse = WebServiceHelper.getServerDataObject(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "getServerDataObjectException", Toast.LENGTH_LONG).show();
                    }
                });

                //            Toast.makeText(MainActivity.this,"getServerDataObjectException: "+e.getMessage(),Toast.LENGTH_LONG).show();
                return false;
            }
            boolean success = false;

            if (serverResponse != null && serverResponse.length() > 0) {
                try {
                    success = serverResponse.getBoolean("success");
                    if (success) {
                        preferences.edit()
                                .putString("token", serverResponse.getString("token"))
                                .putBoolean("shouldUpdate", true)
                                .commit();
                        ContactsManager contactsManager = new ContactsManager(context);
                        contactsManager.copyFromDevice2DB();
                        Utils.setFirstTimePreference(false, context);
                        Utils.setIdUserRegistered(context, serverResponse.getInt("idUser"));
                        Utils.setUserRol(context, serverResponse.getInt("idRol"));
                        Utils.setUsername(context, serverResponse.getString("username"));
                        Utils.setPhoneNumber(context, serverResponse.getString("phoneNumber"));
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "success is false", Toast.LENGTH_LONG).show();
                            }
                        });
                        if (serverResponse.getBoolean("user_exists"))
                            user_exists = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "JSONException", Toast.LENGTH_LONG).show();
                        }
                    });

                    return false;
                    //tell the user there has been an error in the server response and let him try again
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "response is null or empty", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (user_exists) {
                showDialog();
            } else {
                if (success) {
                    showSendEmailDialog();
                    /*startActivity(new Intent(context, ContactSynch.class));
                    finish();*/
                } else {
                    //hay que ver cuales son los  motivos por los que success puede ser falso
                    //show unable to start, show try again button, and hide progress bar, also show a toast showing the error
                    hideProgress();
                    Toast.makeText(MainActivity.this, "onPostExecute: success=false", Toast.LENGTH_LONG).show();
                }
            }
            registerTask = null;
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            hideProgress();
        }
    }


}