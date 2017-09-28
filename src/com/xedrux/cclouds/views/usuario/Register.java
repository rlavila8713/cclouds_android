package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;
import org.json.JSONObject;

/**
 * Created by reinier on 03/09/2015.
 */
public class Register extends Activity {
    private boolean isConditionAccept;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userPasswordConfirm;
    private Button userRegister;
    private String mUserEmail;
    private String mUserPasword;
    private String mUserPaswordConfirm;
    private String mphoneNumber;


    private userRegisterTask userRegisterTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getApplicationContext(),
                R.xml.preferences, false);

        setContentView(R.layout.activity_register);


        userEmail = (EditText) findViewById(R.id.registerByEmail);
        userPassword = (EditText) findViewById(R.id.registerUserPassword);
        userPasswordConfirm = (EditText) findViewById(R.id.userPasswordConfirmation);
        userRegister = (Button) findViewById(R.id.userRegisterBtn);
        mphoneNumber = Utils.getID(getApplicationContext());


        userPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id,
                                          KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptToRegister();
                    return true;
                }
                return false;
            }
        });

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToRegister();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void attemptToRegister() {
        if (userRegisterTask != null) {
            return;
        }

//        Utils.hideSoftKeyboard(this);

        // Reset errors.
        userEmail.setError(null);
        userPassword.setError(null);
        userPasswordConfirm.setError(null);

        // Store values at the time of the login attempt.
        mUserEmail = userEmail.getText().toString();
        mUserPasword = userPassword.getText().toString();
        mUserPaswordConfirm = userPasswordConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mUserPasword)) {
            userPassword.setError(getResources().getText(R.string.errRequiredField));
            focusView = userPassword;
            cancel = true;
        } else if (mUserPasword.length() < 1) {
            userPassword.setError(getResources().getText(
                    R.string.errPassTooShort));
            focusView = userPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserPaswordConfirm)) {
            userPasswordConfirm.setError(getResources().getText(R.string.errRequiredField));
            focusView = userPasswordConfirm;
            cancel = true;
        } else if (mUserPaswordConfirm.length() < 1) {
            userPasswordConfirm.setError(getResources().getText(
                    R.string.errPassTooShort));
            focusView = userPasswordConfirm;
            cancel = true;
        } else if (mUserPaswordConfirm.compareTo(mUserPasword) < 0) {
            userPasswordConfirm.setError(getResources().getText(
                    R.string.errPassDoNotMatch));
            focusView = userPasswordConfirm;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserEmail)) {
            userEmail.setError(getResources().getText(R.string.errRequiredField));
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
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            mLoginStatusMessageView.setText(R.string.loginActProcess);
//            showProgress(true);
            userRegisterTask = new userRegisterTask();
            userRegisterTask.execute((Void) null);
        }
    }

    public class userRegisterTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Utils.loadPreferences(getApplication());
            JSONObject result;
            try {
                result = WebServiceHelper.getServerDataObject(String.format(URLS.REGISTER_URL, mUserEmail, mUserPasword, mUserPaswordConfirm, mphoneNumber),
                        URLS.SERVER_ADDRESS + ACTIONS.ACTION_REGISTER);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            boolean success = false;

            if (result != null && result.length() > 0) {
                try {
                    success = result.getBoolean("success");
//                    SingletonPattern.getInstance(getApplicationContext());
//                    savePreferences(!rememberPass);
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
                /*Intent intent = new Intent(Login.this, UsuariosListActivity.class);
                SingletonPattern.getInstance();
                Bundle bag = new Bundle();
                bag.putString("Username", mUser);
                bag.putString("Password", mPassword);
                SingletonPattern.setActiveUser(new Usuario(1, mUser, mPassword));
                intent.putExtras(bag);
                startActivity(intent);
                finish();*/

//                Utils.setConditionAccept(getApplication(), true);
                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                Toast t = Toast.makeText(getApplicationContext(),
                        R.string.errWrongUserAndPass, Toast.LENGTH_LONG);
                t.show();
            }
        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
//            showProgress(false);
        }
    }
}
