package com.xedrux.cclouds.views.usuario;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.activities.ContactRestore;
import com.xedrux.cclouds.activities.SendEmailAddress;
import com.xedrux.cclouds.services.CcloudsService;
import com.xedrux.cclouds.utils.*;

import java.io.File;

/**
 * Created by reinier on 04/09/2015.
 */
public class ContactSynch extends ActionBarActivity {

    private String TAG = "contactSyncActivity";

    private SyncStatusReceiver syncStatusReceiver;
    private ImageView optionsButton;
    private int deviceC = 0, serverC = 0;
    private Context context;
    private ContactsManager contactsManager;
    private int userSelection = 0;
    private String serverAddress;
    private View syncStatusView;
    private View mainView;
    private ImageView startSynch;
    private int taskToDo;
    private File db_file;
    private boolean isFirstTime;
    private MenuItem contactSynchronize;
    private TextView mContactsSyncStatusMessageView;
    private TextView synch_status_message_front;
    private Animation sync_in_progress_anim;
    private ImageView curvedArrows;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_contact_synch);


        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        sync_in_progress_anim = AnimationUtils.loadAnimation(this, R.anim.anim_sync_in_progress);
        curvedArrows = (ImageView) findViewById(R.id.curvedArrows);
        contactsManager = new ContactsManager(getApplicationContext());
        //syncStatusView = findViewById(R.id.synch_status);
//        mainView = findViewById(R.id.synch_view);
//        mContactsSyncStatusMessageView = (TextView) findViewById(R.id.synch_status_message);
        synch_status_message_front = (TextView) findViewById(R.id.synch_status_message_front);
        optionsButton = (ImageView) findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SendEmailAddress.class));
            }
        });

    }

    private void sendEmail() {
        Intent contactListForEmail = new Intent(ContactSynch.this, ContactsListTest.class);
        startActivityForResult(contactListForEmail, 2);
    }

    private void sendSms() {
        //                 Cargar la lista de contactos para escoger uno/varios contactos
        Intent contactList = new Intent(ContactSynch.this, ContactsListTest.class);
        startActivityForResult(contactList, 1);
    }

    private void restoreContacts() {
        if (!Utils.isDbLocked(context)) {
            startActivity(new Intent(context, ContactRestore.class));
        } else {
            Toast.makeText(ContactSynch.this, R.string.db_in_use, Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.contact_restore:
                restoreContacts();
                break;
            case R.id.send_sms:
                sendSms();
                break;
            case R.id.send_email:
                sendEmail();
                break;
            case R.id.user_profile:
                Intent intentUserProfile = new Intent(ContactSynch.this, UserProfile.class);
                startActivity(intentUserProfile);
                break;
            case R.id.about:
                Intent intentAbout = new Intent(ContactSynch.this, About.class);
                startActivity(intentAbout);
                break;
            case R.id.settings:
                Intent intentPreferences = new Intent(ContactSynch.this, Preferences.class);
                startActivity(intentPreferences);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**/

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    String phoneNumber = data.getExtras().getString("phoneNumbers");
                    Intent smsIntent = Utils.sendSMS(phoneNumber, getResources().getString(R.string.sms_body));
                    startActivity(smsIntent);
                    break;
                case 2:
                    try {
                        String[] emailAddress = null;
                        String dataEmailAddress = data.getExtras().getString("emailAddress");
                        if (dataEmailAddress.length() > 0) {
                            emailAddress = dataEmailAddress.split(",");
                            Intent emailIntent = Utils.sendEmail(emailAddress, getResources().getString(R.string.email_body));
                            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email_using)));
                        } else {
                            Toast.makeText(ContactSynch.this,
                                    getResources().getString(R.string.constacts_no_has_email_address),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ContactSynch.this,
                                getResources().getString(R.string.no_email_client_installed),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
                /*............*/
        }
    }

    public boolean dbExists() {
        db_file = DataBaseHelper.getDbFile(getApplicationContext());
        if (db_file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
  /*  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            syncStatusView.setVisibility(View.VISIBLE);
            syncStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            syncStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mainView.setVisibility(View.VISIBLE);
            mainView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mainView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            syncStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

*/
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncStatusReceiver = new SyncStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sync_comenzada");
        filter.addAction("sync_terminada");
        filter.addAction("sync_coming_soon");
        filter.addAction("connectivity_gone");
        localBroadcastManager.registerReceiver(syncStatusReceiver, filter); //security issue here
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(syncStatusReceiver);
        syncStatusReceiver = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //verificar si el idioma cambio para cargar el layout nuevamente
        startService(new Intent(getApplicationContext(), CcloudsService.class));
        if (SingletonPattern.isUpdate_needed(context)) {
            if (Utils.isNetworkAvailable(this))
                synch_status_message_front.setText(R.string.sync_coming_soon);
            else
                synch_status_message_front.setText(R.string.contactsChanged);

        }
        if (Utils.isAnEmailRegistered(context)) {
            optionsButton.setVisibility(View.GONE);
        } else {
            optionsButton.setVisibility(View.VISIBLE);
        }

    }

    private class SyncStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("sync_comenzada")) {
                //comenzar animaci'on
                synch_status_message_front.setText(R.string.syncingNow);
                curvedArrows.setVisibility(View.VISIBLE);
                curvedArrows.startAnimation(sync_in_progress_anim);
            } else {
                //terminar animaci'on
                curvedArrows.clearAnimation();
                curvedArrows.setVisibility(View.GONE);
                if (intent.getAction().equals("sync_terminada")) {
                    if (SingletonPattern.isUpdate_needed(context)) {
                        synch_status_message_front.setText(R.string.contactsChanged);
                    } else
                        synch_status_message_front.setText(R.string.contactsSynced);
                } else if (intent.getAction().equals("sync_coming_soon")) {
                    synch_status_message_front.setText(R.string.sync_coming_soon);
                } else if (intent.getAction().equals("connectivity_gone")) {
                    if (SingletonPattern.isUpdate_needed(context)) {
                        synch_status_message_front.setText(R.string.contactsChanged);
                    } else
                        synch_status_message_front.setText(R.string.contactsSynced);
                }
            }
        }
    }
}

