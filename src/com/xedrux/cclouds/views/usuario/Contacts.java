package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.adapters.LanguageAdapter;
import com.xedrux.cclouds.asynctasks.ContactListSynchAsync;
import com.xedrux.cclouds.utils.SingletonPattern;

import java.util.ArrayList;

/**
 * Created by reinier.leyva on 18/01/2016.
 */
public class Contacts extends Activity implements AdapterView.OnItemClickListener {
    Context context = null;
    ContactsAdapter contactsAdapter;
    ArrayList<ContactObject>contactList;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.contacts_list);
        init();
    }

    public void init() {
        ListView list = (ListView) findViewById(R.id.list_contacts);
        new ContactListSynchAsync(context).execute((Void)null);
        contactList = SingletonPattern.getContactList();
        contactsAdapter = new ContactsAdapter(this,contactList);
        list.setAdapter(contactsAdapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
