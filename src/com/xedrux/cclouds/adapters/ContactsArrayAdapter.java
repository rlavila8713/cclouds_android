package com.xedrux.cclouds.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.models.Contacts;
import android.widget.CheckBox;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by reinier on 15/08/2015.
 */
public class ContactsArrayAdapter extends ArrayAdapter<Contacts> {

    private List<Contacts> contactsList;
    private Activity context;

    public ContactsArrayAdapter(Activity context, List<Contacts> contactsList) {
        super(context, R.layout.row_contact, contactsList);
        this.contactsList = contactsList;
        this.context = context;
    }

    @Override
    public Contacts getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_contact, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.firstName = (TextView) view.findViewById(R.id.firstName);
            viewHolder.mobileNumber = (TextView) view.findViewById(R.id.phoneNumber);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        Contacts contacts = contactsList.get(position);
        holder.firstName.setText(contacts.getFirstName());
        holder.mobileNumber.setText(contacts.getMobileNumber());
        return view;
    }

    public LinkedList<Integer> getSelectedItems(){
        LinkedList<Integer> indexList= new LinkedList<Integer>();
        return indexList;
    }

    static class ViewHolder {
        protected TextView firstName;
        protected TextView mobileNumber;
        /*para seleccionar varios usuarios (evio de sms y email)*/
        protected CheckBox contactSelected;
    }
}
