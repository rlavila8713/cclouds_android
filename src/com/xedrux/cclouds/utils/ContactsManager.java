package com.xedrux.cclouds.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import com.xedrux.cclouds.models.ContactAg;
import com.xedrux.cclouds.models.Contacts;
import com.xedrux.cclouds.views.usuario.ContactObject;

import java.util.*;
import java.util.zip.CRC32;


/**
 * Created by Isidro on 9/3/2015.
 */
public class ContactsManager {
    //purposes
    final int AVOID_DUPLICATION = 0;
    final int FIND_CHANGES = 1;
    private DataBaseHelper dataBaseHelper;
    private Context context;
    public ContactsManager(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
    }

    public LinkedList<ContactObject> getDeletedContacts(){
        SQLiteDatabase sqLiteDatabase=dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable= new RawContactsTable(sqLiteDatabase,context);
        Cursor raw_contacts=rawContactsTable.getDeletedContacts();
        DataTable dataTable=new DataTable(sqLiteDatabase,context);
        int idIndex=raw_contacts.getColumnIndex(ContactsContract.RawContacts._ID);
        LinkedList<ContactObject> id_list_index=new LinkedList<ContactObject>();
        while (raw_contacts.moveToNext()){
                ContactObject contactObject=new ContactObject(raw_contacts.getLong(idIndex));
                id_list_index.add(contactObject);
                setDisplay(dataTable.getDataRows(contactObject.getId()),
                    contactObject);
        }
        sqLiteDatabase.close();
        return id_list_index;
    }

    private void setDisplay(Cursor dataRows, ContactObject contactAg){
        while (dataRows.moveToNext()){
            int mimeTypePriority=getMimeTypePriority(dataRows.getString(dataRows.getColumnIndex(ContactsContract.Data.MIMETYPE)));
            if(mimeTypePriority>=0){
                contactAg.setDisplay(dataRows.getString(dataRows.getColumnIndex(ContactsContract.Data.DATA1)),mimeTypePriority);
            }
        }
    }

    private int getMimeTypePriority(String mimeType){
        HashMap<String,Integer> mimeTypePriorities=new HashMap<String, Integer>();
        mimeTypePriorities.put(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,10);
        mimeTypePriorities.put(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE,9);
        mimeTypePriorities.put(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,8);
        mimeTypePriorities.put(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,7);
        return mimeTypePriorities.containsKey(mimeType)?mimeTypePriorities.get(mimeType):-1;
    }
    public void restoreDeletedContacts(List<Long> list){

        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable=new RawContactsTable(sqLiteDatabase,context);
        rawContactsTable.undelete(list);
        sqLiteDatabase.close();

    }
    public void copyFromDevice2DB() {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable = new RawContactsTable(sqLiteDatabase, context);
        DataTable dataTable = new DataTable(sqLiteDatabase, context);
        rawContactsTable.copyFromContactsProvider2DataBase(dataTable);
//        dataTable.copyFromContactsProvider2DataBase();
        sqLiteDatabase.close();
    }

    public void copyFromDB2Device() {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable = new RawContactsTable(sqLiteDatabase, context);
        rawContactsTable.copyFromDataBase2ContactsProvider(createContactsHashSet(AVOID_DUPLICATION));
        sqLiteDatabase.close();
    }

    public boolean areContactsEqual() {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable = new RawContactsTable(sqLiteDatabase, context);
        boolean answer = rawContactsTable.compareDataBaseWithContactsProvider(createContactsHashSet(FIND_CHANGES));
        sqLiteDatabase.close();
        return answer;
    }

    public void deleteDeviceContacts(Boolean permanently) {
        Uri uri = permanently ? ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.
                CALLER_IS_SYNCADAPTER, "true").build() : ContactsContract.RawContacts.CONTENT_URI;
        context.getContentResolver().delete(uri, null, null);
    }

    public boolean isThereAnyContactInThisDevice() {
        String[] prj = {ContactsContract.Contacts._ID};
        Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, prj, null, null, null);
        boolean answer = c.getCount() > 0 ? true : false;
        c.close();
        return answer;

    }

    public boolean isThereAnyContactInTheServer() {
        String[] prj = {ContactsContract.Contacts._ID};
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor c = db.query(false, "raw_contacts", prj, null, null, null, null, null, null);
        boolean answer = c.getCount() > 0 ? true : false;
        c.close();
        return answer;

    }

    public void clearDB(boolean permanently) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        RawContactsTable rawContactsTable = new RawContactsTable(sqLiteDatabase, context);
        DataTable dataTable = new DataTable(sqLiteDatabase, context);
        if(permanently) {
            dataTable.clear();
            rawContactsTable.clear();
        }
        else
            rawContactsTable.markAllAsDeleted();
        sqLiteDatabase.close();

    }

    private Set<Long> createContactsHashSet(int purpose) {
        Set<Long> treeSet = new TreeSet<Long>();
        CRC32 crc32 = new CRC32();
        Cursor raw_contact_rows = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, ContactsContract.RawContacts.DELETED+"=0", null, null);
        while (raw_contact_rows.moveToNext()) {
            crc32.reset();
            computeRawContactHash(purpose, crc32, raw_contact_rows);
            String[] args = {String.valueOf(raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)))};
            Cursor data_rows = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=?", args, null);
            while (data_rows.moveToNext()) {
                computeDataRowHash(purpose, crc32, data_rows);
            }
            data_rows.close();
            treeSet.add(crc32.getValue());
            System.out.println("id: " + args[0] + " crc32: " + crc32.getValue());//<----------------------------------------delete
        }
        raw_contact_rows.close();
        return treeSet;
    }

    private void computeDataRowHash(int purpose, CRC32 crc32, Cursor data_rows) {
        switch (purpose) {
            case AVOID_DUPLICATION:
                computeDataHash2AvoidDuplication(crc32, data_rows);
                break;
            case FIND_CHANGES:
                computeDataHash2FindChanges(crc32, data_rows);
        }
    }

    private void computeRawContactHash(int purpose, CRC32 crc32, Cursor raw_contact_rows) {
        switch (purpose) {
            case AVOID_DUPLICATION:
                computeRawContactHash2AvoidDuplication(crc32, raw_contact_rows);
                break;
            case FIND_CHANGES:
                computeRawContactHash2FindChanges(crc32, raw_contact_rows);
                break;
        }
    }

    private byte[] getDataFieldBytes(Cursor data_row, int index){
        //TODO: if for some reason we update the min sdk to 11 or newer, data_rows.getType can be used here
        try{
            if(data_row.isNull(index))
                return "".getBytes();
            return  data_row.getString(index).getBytes();
        }catch (Exception e){
            return data_row.getBlob(index);
        }

    }

    private void computeDataHash2AvoidDuplication(CRC32 crc32, Cursor data_rows) {
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.MIMETYPE)) )
            crc32.update(data_rows.getString(data_rows.getColumnIndex(ContactsContract.Data.MIMETYPE)).getBytes());
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.IS_PRIMARY)) )//?
            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_PRIMARY)));
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)) )//?
            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)));
//        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.DATA_VERSION)) )
//            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.DATA_VERSION)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA1)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA2)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA3)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA4)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA5)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA6)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA7)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA8)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA9)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA10)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA11)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA12)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA13)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA14)));
        crc32.update(getDataFieldBytes(data_rows, data_rows.getColumnIndex(ContactsContract.Data.DATA15)));

    }

    private void computeDataHash2FindChanges(CRC32 crc32, Cursor data_rows) {
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.MIMETYPE)) )
            crc32.update(data_rows.getString(data_rows.getColumnIndex(ContactsContract.Data.MIMETYPE)).getBytes());
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.IS_PRIMARY)) )
            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_PRIMARY)));
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)) )
            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)));
        if (!data_rows.isNull(data_rows.getColumnIndex(ContactsContract.Data.DATA_VERSION)) )
            crc32.update(data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.DATA_VERSION)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA1)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA2)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA3)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA4)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA5)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA6)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA7)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA8)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA9)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA10)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA11)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA12)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA13)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA14)));
        crc32.update(getDataFieldBytes(data_rows,data_rows.getColumnIndex(ContactsContract.Data.DATA15)));


    }

    private void computeRawContactHash2AvoidDuplication(CRC32 crc32, Cursor raw_contact_rows) {
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DELETED)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DELETED)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.AGGREGATION_MODE)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.AGGREGATION_MODE)).getBytes());
    }

    private void computeRawContactHash2FindChanges(CRC32 crc32, Cursor raw_contact_rows) {
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SOURCE_ID)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SOURCE_ID)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.VERSION)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.VERSION)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DIRTY)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DIRTY)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DELETED)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DELETED)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.AGGREGATION_MODE)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.AGGREGATION_MODE)).getBytes());
        if (!raw_contact_rows.isNull(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.STARRED)) )
            crc32.update(raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.STARRED)).getBytes());
    }

    /**
     * Nested, private classes -------------------------------------------------------------------------------------
     * --------------------------------------------------------------------------------------------------------------
     */

    private class RawContactsTable {

        public String TABLE_NAME_RAW_CONTACTS = "raw_contacts";
        private SQLiteDatabase db;//this db should be initialized elsewhere and arrive here as a parameter
        private Context context;

        public RawContactsTable(SQLiteDatabase db, Context context) {
            this.db = db;
            this.context = context;
        }

        public void clear() {
            db.delete(TABLE_NAME_RAW_CONTACTS, null, null);
        }

        public Cursor getDeletedContacts(){
            return db.query(false,TABLE_NAME_RAW_CONTACTS,null,ContactsContract.RawContacts.DELETED+"=1",null,null,null,null,null);
        }

        public void undelete(List<Long> list){
            Iterator<Long> iterator=list.iterator();
            Long id;
            ContentValues values=new ContentValues();
            values.put(ContactsContract.RawContacts.DELETED,0);
            String[] prj={ContactsContract.RawContacts._ID};
            Uri uri=ContactsContract.RawContacts.CONTENT_URI;
            DataTable dataTable= new DataTable(db,context);
            ContentValues values1= new ContentValues();
            while(iterator.hasNext()){
                id=iterator.next();
                //undelete in db
                db.update(TABLE_NAME_RAW_CONTACTS,values,prj[0]+"="+id,null);
                //insert or update in contact provider
                Cursor raw_contact_row=context.getContentResolver().query(uri,prj,prj[0]+"="+id,null,null);
                if(raw_contact_row.getCount()>0){
                    //update
                    context.getContentResolver().update(uri,values,prj[0]+"="+id,null);
                }else{
                    //insert
                    values.clear();
                    FillValuesWithRawContactCommonFields(values1, raw_contact_row);
                    Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values1);
                    long rawContactId = ContentUris.parseId(rawContactUri);
                    dataTable.copyFromDataBase2ContactsProvider(
                            raw_contact_row.getLong(raw_contact_row.getColumnIndex(ContactsContract.RawContacts._ID)), rawContactId);
                }
            }
        }
        public void markAllAsDeleted(){
            ContentValues values = new ContentValues();
            values.put(ContactsContract.RawContacts.DELETED,1);
            db.update(TABLE_NAME_RAW_CONTACTS,values,null,null);
        }
        public void copyFromContactsProvider2DataBase(DataTable dataTable) {
            ContentValues values = new ContentValues();
            Cursor raw_contact_rows = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, ContactsContract.RawContacts.DELETED+"=0", null, null);
            String[] prj={ContactsContract.RawContacts._ID};
            while (raw_contact_rows.moveToNext()) {
                values.clear();
                long id=raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID));
                values.put(ContactsContract.RawContacts.CONTACT_ID, raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)));
                FillValuesWithRawContactCommonFields(values, raw_contact_rows);
                if(db.query(true,TABLE_NAME_RAW_CONTACTS,prj,prj[0]+"="+id,null,null,null,null,null).getCount()==0){
                    //insert
                    values.put(ContactsContract.RawContacts._ID, id);
                    db.insert(TABLE_NAME_RAW_CONTACTS,null, values);
                }else{
                    //update
                    db.update(TABLE_NAME_RAW_CONTACTS, values, ContactsContract.RawContacts._ID + "=" + id, null);
                }
                dataTable.copyFromContactsProvider2DataBase(id);
            }
            raw_contact_rows.close();
        }


        private void FillValuesWithRawContactCommonFields(ContentValues values, Cursor raw_contact_rows) {
            values.put(ContactsContract.RawContacts.AGGREGATION_MODE, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.AGGREGATION_MODE)));
            values.put(ContactsContract.RawContacts.DELETED, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DELETED)));
            values.put(ContactsContract.RawContacts.TIMES_CONTACTED, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.TIMES_CONTACTED)));
            values.put(ContactsContract.RawContacts.LAST_TIME_CONTACTED, raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.LAST_TIME_CONTACTED)));
            values.put(ContactsContract.RawContacts.STARRED, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.STARRED)));
//          por ahora este campo no se guarda  values.put(ContactsContract.RawContacts.CUSTOM_RINGTONE,raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.CUSTOM_RINGTONE)));
            values.put(ContactsContract.RawContacts.SEND_TO_VOICEMAIL, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SEND_TO_VOICEMAIL)));
            values.put(ContactsContract.RawContacts.ACCOUNT_NAME, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)));
            values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)));
//          este campo no esta presente en la version 2.3.3  values.put(ContactsContract.RawContacts.DATA_SET,raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DATA_SET)));
            values.put(ContactsContract.RawContacts.SOURCE_ID, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SOURCE_ID)));
            values.put(ContactsContract.RawContacts.VERSION, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.VERSION)));
            values.put(ContactsContract.RawContacts.DIRTY, raw_contact_rows.getInt(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.DIRTY)));
            values.put(ContactsContract.RawContacts.SYNC1, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SYNC1)));
            values.put(ContactsContract.RawContacts.SYNC2, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SYNC2)));
            values.put(ContactsContract.RawContacts.SYNC3, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SYNC3)));
            values.put(ContactsContract.RawContacts.SYNC4, raw_contact_rows.getString(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts.SYNC4)));
        }

        public boolean compareDataBaseWithContactsProvider(Set<Long> treeSet) {
            Cursor raw_contact_rows = db.query(false, TABLE_NAME_RAW_CONTACTS, null, null, null, null, null, null, null);
            CRC32 crc32 = new CRC32();
            boolean differencesFound = false;
            if(treeSet.size()!=raw_contact_rows.getCount())
                return false;
            while (raw_contact_rows.moveToNext()) {
                crc32.reset();
                System.out.println("Comparing raw contact");
                System.out.println("raw contact id: " + raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)));
                computeRawContactHash2FindChanges(crc32, raw_contact_rows);
                String[] args = {String.valueOf(raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)))};
                Cursor data_rows = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=?", args, null);
                while (data_rows.moveToNext()) {
                    computeDataHash2FindChanges(crc32, data_rows);
                }
                data_rows.close();
                System.out.println("crc32: " + crc32.getValue());
                if (treeSet.contains(crc32.getValue())) {
                    System.out.println("This is =, contact " + args[0]);
                    continue;
                }
                System.out.println("Differences found, contact " + args[0]);
                differencesFound = true;
                break;
            }
            raw_contact_rows.close();
            return !differencesFound;
        }
        public void copyFromDataBase2ContactsProvider(Set<Long> treeSet) {
            Cursor raw_contact_rows = db.query(false, TABLE_NAME_RAW_CONTACTS, null, ContactsContract.RawContacts.DELETED+"=0", null, null, null, null, null);
            ContentValues values = new ContentValues();
            DataTable dataTable = new DataTable(db, context);
            CRC32 crc32 = new CRC32();
            while (raw_contact_rows.moveToNext()) {
                crc32.reset();
                System.out.println("Inserting raw contact");
                System.out.println("raw contact id: " + raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)));
                computeRawContactHash2AvoidDuplication(crc32, raw_contact_rows);
                String[] args = {String.valueOf(raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)))};
                Cursor data_rows = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=?", args, null);
                while (data_rows.moveToNext()) {
                    computeDataHash2AvoidDuplication(crc32, data_rows);
                }
                data_rows.close();
                System.out.println("crc32: " + crc32.getValue());
                if (treeSet.contains(crc32.getValue())) {
                    System.out.println("Continue! Skipping contact " + args[0]);
                    continue;
                }
                System.out.println("Proceed to add! Inserting contact " + args[0]);
                ////-----------------------------------------------------
                values.clear();
                FillValuesWithRawContactCommonFields(values, raw_contact_rows);
                Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
                long rawContactId = ContentUris.parseId(rawContactUri);
                dataTable.copyFromDataBase2ContactsProvider(
                        raw_contact_rows.getLong(raw_contact_rows.getColumnIndex(ContactsContract.RawContacts._ID)), rawContactId);
            }
            raw_contact_rows.close();
        }
    }

    private class DataTable {
        String TABLE_NAME_DATA = "data";
        private SQLiteDatabase db;//this db should be initialized elsewhere and arrive here as a parameter
        private Context context;

        public DataTable(SQLiteDatabase db, Context context) {
            this.db = db;
            this.context = context;
        }

        public void clear() {
            db.delete(TABLE_NAME_DATA, null, null);
        }

        public void copyFromContactsProvider2DataBase(long raw_contact_id) {
            ContentValues values = new ContentValues();
            String[] args = {String.valueOf(raw_contact_id)};
//            System.out.println("------------------------------Table Data---------------------------------");
            Cursor data_rows = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=?", args, null);
            db.delete(TABLE_NAME_DATA,ContactsContract.Data.RAW_CONTACT_ID + "=?", args);
            while (data_rows.moveToNext()) {
                values.clear();
                values.put(ContactsContract.Data._ID, data_rows.getLong(data_rows.getColumnIndex(ContactsContract.Data._ID)));
                values.put(ContactsContract.Data.RAW_CONTACT_ID, data_rows.getLong(data_rows.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
                fillValuesWithCommonDataFields(data_rows, values);
                db.insert(TABLE_NAME_DATA, null, values);
            }
//            System.out.println("******************************Table Data*********************************");
            data_rows.close();
        }

        public Cursor getDataRows(Long raw_contact_id){
            Cursor data_rows = db.query(false, TABLE_NAME_DATA, null, ContactsContract.Data.RAW_CONTACT_ID + "="+String.valueOf(raw_contact_id),null
                    , null, null, null, null);
            System.out.println("Data Rows count: " +data_rows.getCount());
            return  data_rows;
        }

        public void copyFromDataBase2ContactsProvider(long raw_contact_id, long new_raw_contact_id) {
            String[] args = {String.valueOf(raw_contact_id)};
            Cursor data_rows = db.query(false, TABLE_NAME_DATA, null, ContactsContract.Data.RAW_CONTACT_ID + "=?", args
                    , null, null, null, null);
            ContentValues values = new ContentValues();
            while (data_rows.moveToNext()) {
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, new_raw_contact_id);
                fillValuesWithCommonDataFields(data_rows, values);
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            }

            data_rows.close();

        }

        private void putAppropriateType(String key, Cursor data_rows, ContentValues values){
            int index=data_rows.getColumnIndex(key);
            try{
                values.put(key,data_rows.getString(index));
            }catch (Exception e){
                values.put(key,data_rows.getBlob(index));
            }
        }

        private void fillValuesWithCommonDataFields(Cursor data_rows, ContentValues values) {
            values.put(ContactsContract.Data.MIMETYPE, data_rows.getString(data_rows.getColumnIndex(ContactsContract.Data.MIMETYPE)));
            values.put(ContactsContract.Data.IS_PRIMARY, data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_PRIMARY)));
            values.put(ContactsContract.Data.IS_SUPER_PRIMARY, data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)));
            values.put(ContactsContract.Data.DATA_VERSION, data_rows.getInt(data_rows.getColumnIndex(ContactsContract.Data.DATA_VERSION)));
            putAppropriateType(ContactsContract.Data.DATA1, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA2, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA3, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA4, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA5, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA6, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA7, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA8, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA9, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA10, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA11, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA12, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA13, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA14, data_rows, values);
            putAppropriateType(ContactsContract.Data.DATA15, data_rows, values);
            putAppropriateType(ContactsContract.Data.SYNC1, data_rows, values);
            putAppropriateType(ContactsContract.Data.SYNC2, data_rows, values);
            putAppropriateType(ContactsContract.Data.SYNC3, data_rows, values);
            putAppropriateType(ContactsContract.Data.SYNC4, data_rows, values);
        }
    }


}
