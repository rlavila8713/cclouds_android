package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.adapters.ContactsArrayAdapter;
import com.xedrux.cclouds.models.Contacts;
import com.xedrux.cclouds.utils.Utils;

import java.util.List;

/**
 * Created by reinier on 14/08/2015.
 */

public class ContactsListActivity
        extends ListActivity {
    private MenuItem contactSearch, contactSynchronize;
    private String serverAddress, username, password, error;
    private List<Contacts> contactsList;
    private ContactsArrayAdapter adapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);
        contactsList = Utils.getContactsList(getApplicationContext());
        adapter = new ContactsArrayAdapter(this, contactsList);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        /*Por ahora solo esta implementado para que se le evie el sms al contacto seleccionado
        * no para multiples selecciones. **/
        System.out.println("el onclick se ejectuta");
        Object contact = this.getListView().getAdapter().getItem(position);
        Intent backIntent = new Intent();
        backIntent.putExtra("phoneNumber",((Contacts)contact).getMobileNumber());
        setResult(Activity.RESULT_OK,backIntent);
        finish();
    }
}
