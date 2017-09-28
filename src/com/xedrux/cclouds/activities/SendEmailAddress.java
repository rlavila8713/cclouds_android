package com.xedrux.cclouds.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;
import com.xedrux.cclouds.views.usuario.ContactSynch;
import org.json.JSONObject;

/**
 * Created by df on 03/10/2015.
 */
public class SendEmailAddress extends ActionBarActivity {
    private ImageView btnSend;
    private String mUserEmail;
    private EditText userEmail;
    private ProgressBar progressBar;

    private userRegisterTask userRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        userEmail = (EditText) findViewById(R.id.registerByEmail);
        btnSend = (ImageView) findViewById(R.id.userRegisterByEmail);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToRegister();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void attemptToRegister() {
        if (userRegisterTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        mUserEmail = userEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mUserEmail)) {
            userEmail.setError(getResources().getText(R.string.errRequiredField));
            focusView = userEmail;
            cancel = true;
        } else if (!mUserEmail.matches("^[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z_+])*@([0-9a-zA-Z][-\\w]*" +
                "[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9}$")) {
            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_LONG).show();
            focusView = userEmail;
            cancel = true;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.errInetConnection, Toast.LENGTH_LONG)
                    .show();
            focusView = userEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } else {
            showProgress();
            userRegisterTask = new userRegisterTask();
            userRegisterTask.execute((Void) null);
        }
    }

    private void showProgress() {
        btnSend.setVisibility(View.INVISIBLE);
        userEmail.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        btnSend.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public class userRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private boolean email_exists;
        private JSONObject result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email_exists = false;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Utils.loadPreferences(getApplication());

            try {
                result = WebServiceHelper.getServerDataObject(String.format(URLS.REGISTER_EMAIL_URL, mUserEmail,Utils.getID(getApplicationContext())),
                        URLS.SERVER_ADDRESS + ACTIONS.ACTION_REGISTER_EMAIL);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            boolean success = false;

            if (result != null && result.length() > 0) {
                try {
                    success = result.getBoolean("success");
                    if(!success){
                        if (result.getBoolean("email_exists"))
                            email_exists = true;
                    }
                    else
                    {
                        Utils.setEmailRegistered(getApplicationContext(),true);
                        Utils.setEmailRegisteredValue(getApplicationContext(),result.getString("userEmail"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userRegisterTask = null;
//            showProgress(false);
            if (success) {
//                Utils.setEmailRegistered(getApplicationContext(),true);
                Toast.makeText(getApplicationContext(), "Email registered.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), ContactSynch.class));
                finish();
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                Toast t;
                if(email_exists){
                    t = Toast.makeText(getApplicationContext(),R.string.error_email_exists, Toast.LENGTH_LONG);
                    userEmail.setText("");
                    hideProgress();
                    t.show();
                }else{
                    t = Toast.makeText(getApplicationContext(),R.string.error_submitting_email_address, Toast.LENGTH_LONG);
                    t.show();
                    startActivity(new Intent(getApplicationContext(), ContactSynch.class));
                    finish();
                }
            }

        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
            hideProgress();
        }
    }
}
