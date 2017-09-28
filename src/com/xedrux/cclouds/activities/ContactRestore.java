package com.xedrux.cclouds.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.xedrux.cclouds.utils.ContactsManager;
import com.xedrux.cclouds.views.usuario.ContactListClass;
import com.xedrux.cclouds.views.usuario.ContactObject;
import com.xedrux.cclouds.views.usuario.ContactsAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by reinier.leyva on 22/10/2015.
 */

public class ContactRestore extends ActionBarActivity {
    Context context = null;

    ContactsAdapter objAdapter;

    ListView lv = null;

    EditText edtSearch = null;
    LinearLayout llContainer = null;
    Button btnOK = null;
    LinkedList<ContactObject> contacts;

    RelativeLayout rlPBContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_contact);
        contacts= new LinkedList<ContactObject>();
        rlPBContainer = (RelativeLayout) findViewById(R.id.pbcontainer);

        edtSearch = (EditText) findViewById(R.id.input_search);
        llContainer = (LinearLayout) findViewById(R.id.data_container);
        btnOK = (Button) findViewById(R.id.ok_button);

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
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

        addContactsInList();

    }

    private void getSelectedContacts() {
        Intent backIntent = new Intent();
        LinkedList<Long> selectedContacts= new LinkedList<Long>();
        for (ContactObject bean : contacts) {
            if (bean.isSelected()) {
                selectedContacts.add(bean.getId());
            }
        }
        if (selectedContacts.isEmpty()) {
            Toast.makeText(context, "Select at least one Contact",
                    Toast.LENGTH_SHORT).show();
        } else {
            ContactsManager contactsManager= new ContactsManager(getApplicationContext());
            contactsManager.restoreDeletedContacts(selectedContacts);
            finish();
         /*   Toast.makeText(context, "Selected Contacts : " + s,
                    Toast.LENGTH_SHORT).show();*/

        }

    }

    private void addContactsInList() {
        // TODO Auto-generated method stub

        Thread thread = new Thread() {
            @Override
            public void run() {

                showPB();

                try {
                    contacts.clear();
                    ContactsManager contactsManager= new ContactsManager(getApplicationContext());
                    contacts.addAll(contactsManager.getDeletedContacts());

                    lv = new ListView(context);

                    lv.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            llContainer.addView(lv);
                        }
                    });

                    Collections.sort(contacts,
                            new Comparator<ContactObject>() {
                                @Override
                                public int compare(ContactObject lhs,
                                                   ContactObject rhs) {
                                    return lhs.getName().compareTo(
                                            rhs.getName());
                                }
                            });

                    objAdapter = new ContactsAdapter(ContactRestore.this,
                            contacts);
                    lv.setAdapter(objAdapter);
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

                } catch (Exception e) {

                    e.printStackTrace();

                }

                hidePB();

            }
        };
        thread.start();

    }

    void showPB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.GONE);
                btnOK.setVisibility(View.GONE);
            }
        });
    }

    void hidePB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.GONE);
                edtSearch.setVisibility(View.VISIBLE);
                btnOK.setVisibility(View.VISIBLE);
            }
        });
    }
}
