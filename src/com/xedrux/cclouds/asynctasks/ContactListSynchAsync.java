package com.xedrux.cclouds.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import com.xedrux.cclouds.utils.SingletonPattern;
import com.xedrux.cclouds.views.usuario.ContactListClass;
import com.xedrux.cclouds.views.usuario.ContactObject;

import java.util.ArrayList;

/**
 * Created by reinier.leyva on 18/01/2016.
 */
public class ContactListSynchAsync extends AsyncTask<Void, Void, Boolean> {

    Context ctx;
    ArrayList<ContactObject> listaContactos;

    public ContactListSynchAsync(Context ctx) {
        this.ctx = ctx;
        listaContactos = new ArrayList<ContactObject>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        listaContactos.clear();
        Cursor phones = ctx.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

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

            listaContactos.add(cp);
        }
        phones.close();
        SingletonPattern.setContactList(listaContactos);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
