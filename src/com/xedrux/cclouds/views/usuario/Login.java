package com.xedrux.cclouds.views.usuario;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.Roles;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.models.Contacts;
import com.xedrux.cclouds.models.Usuario;
import com.xedrux.cclouds.utils.*;
import org.json.JSONObject;

import java.util.List;


public class Login extends Activity {
    /**
     * Called when the activity is first created.
     */
    private Button startSession;

    //    private TextView loginUser;
    private TextView loginPassword;
    private TextView mLoginStatusMessageView;
    private TextView userRegister;

    private CheckBox chkRememberPassword;

    private String mUser;
    private String mPassword;
    private String uNameValue;
    private String passwordValue;
    private String serverAddress;

    private boolean usingStoredPass;
    private boolean rememberPassword;

    private View mLoginFormView;
    private View mLoginStatusView;

    private UserLoginTask userLoginTask = null;

    private boolean isConditionAccept;
    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getApplicationContext(),
                R.xml.preferences, false);

        isConditionAccept = Utils.isConditionAccept(getApplication());

        if (isConditionAccept) {

            setContentView(R.layout.activity_log_in);
            loadPreferences();
            loginPassword = (TextView) findViewById(R.id.loginPassword);
            chkRememberPassword = (CheckBox) findViewById(R.id.rememberPassword);
            startSession = (Button) findViewById(R.id.startSession);
            userRegister = (TextView) findViewById(R.id.userRegister);

            mLoginFormView = findViewById(R.id.login_form);
            mLoginStatusView = findViewById(R.id.login_status);
            mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

            if (rememberPassword) {
                loginPassword.setText(passwordValue);
                chkRememberPassword.setChecked(rememberPassword);
            }

            loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id,
                                              KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

/*
            chkRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    if (checked) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("rememberPassword", true);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("rememberPassword", false);
                        editor.commit();
                        savePreferences(true);
                    }
                }
            });
*/

            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

        } else {
            startActivity(new Intent(this, TermsAndConditions.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        context = getApplicationContext();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showContacts() {
        List<Contacts> contactsList = Utils.getContactsList(getApplicationContext());
        for (int i = 0; i < 3; i++) {
            Toast.makeText(getApplicationContext(), contactsList.get(i).getFirstName(), Toast.LENGTH_LONG).show();
        }
    }

    private void savePreferences(boolean clear) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();

        uNameValue = mUser;
        passwordValue = mPassword;
        editor.putString("Username", mUser);
        editor.putString("Password", mPassword);
        if (clear) {
//            editor.putString("Username", "");
//            editor.putString("Password", "");
            editor.putBoolean("rememberPassword", false);
        } else {
//            uNameValue = mUser;
//            passwordValue = mPassword;
//            editor.putString("Username", mUser);
//            editor.putString("Password", mPassword);
            editor.putBoolean("rememberPassword", true);
        }
        editor.commit();
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        serverAddress = preferences.getString("serverAddress", "NA");
        uNameValue = preferences.getString("Username", "");
//        uNameValue = preferences.getString("Username",Utils.getID(getApplicationContext()));
        passwordValue = preferences.getString("Password", "");
        rememberPassword = preferences.getBoolean("rememberPassword", false);

        if (!passwordValue.equals("")) {
            usingStoredPass = true;
        }
    }

    public void attemptLogin() {
        if (userLoginTask != null) {
            return;
        }

        loginPassword.setError(null);
        mPassword = loginPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            loginPassword.setError(getResources().getText(R.string.errRequiredField));
            focusView = loginPassword;
            cancel = true;
        } else if (mPassword.length() < 1) {
            loginPassword.setError(getResources().getText(
                    R.string.errPassTooShort));
            focusView = loginPassword;
            cancel = true;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.errInetConnection, Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.loginActProcess);
            showProgress(true);
            userLoginTask = new UserLoginTask();
            userLoginTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private int userRol;
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new AlertDialog.Builder(Login.this);
            builder.setTitle(getResources().getString(R.string.appName));
            builder.setIcon(R.drawable.cclouds_icon);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            loadPreferences();
            if (!mPassword.equals(passwordValue) && usingStoredPass) {
                usingStoredPass = false;
            }
            JSONObject result;
            try {
                result = WebServiceHelper.getServerDataObject(String.format(URLS.LOGIN_URL, Utils.getID(getApplicationContext()), mPassword),
                        URLS.SERVER_ADDRESS + ACTIONS.ACTION_LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            boolean success = false;

            if (result != null && result.length() > 0) {
                try {
                    success = result.getBoolean("success");
                    if (success) {
                        userRol = result.getInt("idRol");
                        token = result.getString("token");
                        Utils.setEmailRegistered(context, result.getBoolean("email_registered"));
                        Utils.setEmailRegisteredValue(context, result.getString("userEmail"));

                        Utils.setIdUserRegistered(context, result.getInt("idUser"));
                        Utils.setUserRol(context, result.getInt("idRol"));
                        Utils.setUsername(context, result.getString("username"));
                        Utils.setPhoneNumber(context, result.getString("phoneNumber"));
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
            userLoginTask = null;
            showProgress(false);
            Intent userIntent = null;
            if (success) {
                if (userRol == Roles.ADMINISTRATOR) {
                    userIntent = new Intent(Login.this, UsuariosListActivity.class);
                } else if (userRol == Roles.CLIENT) {
                    userIntent = new Intent(Login.this, FirstTimeSync.class);
                    userIntent.putExtra("token", token);
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                preferences.edit().putString("token",token).commit();
                System.out.println("Login got the token from the server. This is it: " + token);
                SingletonPattern.getInstance();
                SingletonPattern.setActiveUser(new Usuario(userRol, mUser, mPassword));
                startActivity(userIntent);
                finish();
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                builder.setMessage(R.string.errWrongUserAndPass)
                        .setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setCancelable(false);
                dialog = builder.create();
                dialog.show();
            }
            savePreferences(!rememberPassword);
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress(false);
        }
    }
}
