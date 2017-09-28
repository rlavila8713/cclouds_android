package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import com.xedrux.cclouds.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by reinier.leyva on 22/10/2015.
 */

public class ContactsListTest extends ActionBarActivity {
    Context context = null;
    ContactsAdapter objAdapter;
    ListView lv = null;
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    ImageView btnOK = null;
    RelativeLayout rlPBContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_contact);
        rlPBContainer = (RelativeLayout) findViewById(R.id.pbcontainer);
        edtSearch = (EditText) findViewById(R.id.input_search);
        llContainer = (LinearLayout) findViewById(R.id.data_container);
        btnOK = (ImageView) findViewById(R.id.ok_button);

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getSelectedContacts();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                // When user changed the Text
                String text = edtSearch.getText().toString()
                        .toLowerCase(Locale.getDefault());
                objAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        lv = new ListView(context);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {

                CheckBox chk = (CheckBox) view
                        .findViewById(R.id.contactcheck);
                ContactObject bean = ContactListClass.phoneList
                        .get(position);
                if (bean.isSelected()) {
                    bean.setSelected(false);
                    chk.setChecked(false);
                } else {
                    bean.setSelected(true);
                    chk.setChecked(true);
                }

            }
        });

    new contactListTask().execute((Void)null);

    }

    private void getSelectedContacts() {
        Intent backIntent = new Intent();
        StringBuffer sb = new StringBuffer();
        for (ContactObject bean : ContactListClass.phoneList) {
            if (bean.isSelected()) {
                sb.append(bean.getNumber());
                sb.append(",");
            }
        }
        StringBuffer email = new StringBuffer();
        for (ContactObject bean : ContactListClass.phoneList) {
            if (bean.isSelected()) {
                email.append(bean.getEmail());
                email.append(",");
            }
        }
        String s = sb.toString().trim();
        String d = email.toString().trim();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, getResources().getString(R.string.user_selection),
                    Toast.LENGTH_SHORT).show();
        } else {
            s = s.substring(0, s.length() - 1);
            d = d.substring(0, d.length() - 1);
            backIntent.putExtra("phoneNumbers", s);
            backIntent.putExtra("emailAddress", d);

            setResult(Activity.RESULT_OK, backIntent);
            finish();
        }

    }
    public class contactListTask extends AsyncTask<Void, Void, Boolean> {
        private int userRol;
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*builder = new AlertDialog.Builder(Login.this);
            builder.setTitle(getResources().getString(R.string.appName));
            builder.setIcon(R.drawable.cclouds_icon);*/
            rlPBContainer.setVisibility(View.VISIBLE);
            edtSearch.setVisibility(View.GONE);
            btnOK.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ContactListClass.phoneList.clear();
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);

                try {
                    ContactListClass.phoneList.clear();
                } catch (Exception e) {

                }

                while (phones.moveToNext()) {
                       /* String email = "email";
                        String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }*/
                    String phoneName = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String phoneImage = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));


                    ContactObject cp = new ContactObject();

                    cp.setName(phoneName);
                    cp.setNumber(phoneNumber);
                    cp.setImage(phoneImage);
                    cp.setEmail("email_here");

                    ContactListClass.phoneList.add(cp);

                }
                phones.close();


            } catch (Exception e) {

               return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            lv = new ListView(context);

            lv.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            objAdapter = new ContactsAdapter(ContactsListTest.this,
                    ContactListClass.phoneList);
            lv.setAdapter(objAdapter);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    llContainer.addView(lv);
                }
            });


            Collections.sort(ContactListClass.phoneList,
                    new Comparator<ContactObject>() {
                        @Override
                        public int compare(ContactObject lhs,
                                           ContactObject rhs) {
                            return lhs.getName().compareTo(
                                    rhs.getName());
                        }
                    });

            rlPBContainer.setVisibility(View.GONE);
            edtSearch.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            /*userLoginTask = null;
            showProgress(false);*/
        }
    }

}
