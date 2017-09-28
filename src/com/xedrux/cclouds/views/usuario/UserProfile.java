package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.models.Usuario;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by reinier on 28/08/2015.
 */
public class UserProfile extends Activity {

    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText userEmail;
    private EditText phoneNumber;

    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private String mUserEmail;
    private String mPhoneNumber;
    private String mUserSex;
    private int idUser;


    private Spinner userSex;
    private EditText DataBirth;
    private String SexRecieved;

    private RelativeLayout relativeLayoutContainer;

    private Context ctx;

    private View layout_main_child, layout_sexo, layout_phone_email, layout_login_child, layout_address_child;
    private ImageView arrow_main_layout, arrow_login_layout, arrow_address_layout, btnUpdateUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Initialization();
        Events();
//        if (Utils.isUserProfileUpdated(ctx)) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                relativeLayoutContainer.setVisibility(View.VISIBLE);
                fillUserProfileFields(ctx);
                relativeLayoutContainer.setVisibility(View.GONE);
            }
        });
//        }
    }

    private void fillUserProfileFields(Context ctx) {
        firstName.setText(Utils.getFirstName(ctx));
        lastName.setText(Utils.getLastName(ctx));
        username.setText(Utils.getUsername(ctx));
        userEmail.setText(Utils.getEmailRegisteredValue(ctx));
        if(!Utils.isUserProfileUpdated(ctx)) {
            phoneNumber.setText(Utils.getMyPhoneNumber1(ctx));
        }
        else
        {
            phoneNumber.setText(Utils.getPhoneNumber(ctx));
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSex.setAdapter(adapter);
        String sexSelected = Utils.getUserSex(ctx);
        if (sexSelected.toLowerCase().startsWith("m")) {
            userSex.setSelection(0);
        } else {
            userSex.setSelection(1);
        }
    }

    private void Initialization() {

        ctx = getApplicationContext();

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        username = (EditText) findViewById(R.id.username);
        userEmail = (EditText) findViewById(R.id.emailAddress);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        userSex = (Spinner) findViewById(R.id.userSex);

        btnUpdateUserProfile = (ImageView) findViewById(R.id.btnUpdateUserProfile);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.pbarcontainer);

        layout_main_child = (LinearLayout) findViewById(R.id.layout_main_child);
        layout_address_child = (LinearLayout) findViewById(R.id.layout_address_child);
        layout_login_child = (LinearLayout) findViewById(R.id.layout_login_child);
        layout_sexo = (LinearLayout) findViewById(R.id.layout_sexo);
        layout_phone_email = (LinearLayout) findViewById(R.id.layout_phone_email);

        arrow_main_layout = (ImageView) findViewById(R.id.arrow_main_layout);
        arrow_address_layout = (ImageView) findViewById(R.id.arrow_address_layout);
        arrow_login_layout = (ImageView) findViewById(R.id.arrow_login_layout);
    }

    private void Events() {

        btnUpdateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstName = firstName.getText().toString().trim();
                mLastName = lastName.getText().toString().trim();
                mPhoneNumber = phoneNumber.getText().toString();
                mUserEmail = userEmail.getText().toString();
                mUsername = username.getText().toString();
                mUserSex = userSex.getSelectedItem().toString();
                idUser = Utils.getIdUserRegistered(getApplication());
                UpdateUserProfileTask updateUserProfileObject = new UpdateUserProfileTask();
                updateUserProfileObject.execute(mFirstName, mLastName, mUserSex, mPhoneNumber, mUserEmail, mUsername);
            }
        });

        arrow_main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_main_child.getVisibility() == View.GONE) {
                    layout_main_child.setVisibility(View.VISIBLE);
                    layout_sexo.setVisibility(View.VISIBLE);
                    layout_phone_email.setVisibility(View.VISIBLE);
                    arrow_main_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                } else {
                    layout_main_child.setVisibility(View.GONE);
                    layout_sexo.setVisibility(View.GONE);
                    layout_phone_email.setVisibility(View.GONE);
                    arrow_main_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
            }
        });
        arrow_login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_login_child.getVisibility() == View.GONE) {
                    layout_login_child.setVisibility(View.VISIBLE);
                    arrow_login_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                } else {
                    layout_login_child.setVisibility(View.GONE);
                    arrow_login_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
            }
        });
        arrow_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_address_child.getVisibility() == View.GONE) {
                    layout_address_child.setVisibility(View.VISIBLE);
                    arrow_address_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                } else {
                    layout_address_child.setVisibility(View.GONE);
                    arrow_address_layout.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
            }
        });
    }

    public class UpdateUserProfileTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            relativeLayoutContainer.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            JSONObject serverResponse;
            try {
                serverResponse = WebServiceHelper.getServerDataObject(String.format(URLS.UPDATE_USER_PROFILE, params[0], params[1], params[2], params[3], params[4], params[5]),
                        URLS.SERVER_ADDRESS + String.format(ACTIONS.ACTION_USER_PROFILE, Utils.getIdUserRegistered(getApplication())));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserProfile.this, "getServerDataObjectException", Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }

            boolean success = false;
            if (serverResponse != null && serverResponse.length() > 0) {
                try {
                    success = serverResponse.getBoolean("success");
                    Utils.setUserProfileUpdated(getApplicationContext(), success);
                    Utils.setFirstName(getApplicationContext(), params[0]);
                    Utils.setLastName(getApplicationContext(), params[1]);
                    Utils.setUserSex(getApplicationContext(), params[2]);
                    Utils.setPhoneNumber(getApplicationContext(), params[3]);
                    Utils.setEmailRegisteredValue(getApplicationContext(), params[4]);
                    Utils.setUsername(getApplicationContext(), params[5]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), result ? getResources().getString(R.string.profile_updated):getResources().getString(R.string.profile_no_updated), Toast.LENGTH_LONG).show();
            relativeLayoutContainer.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }
}
