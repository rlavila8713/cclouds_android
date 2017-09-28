package com.xedrux.cclouds.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Isidro on 8/31/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cclouds.db";
    String RAW_CONTACTS_TABLE_CREATE = "CREATE TABLE raw_contacts (_id INTEGER PRIMARY KEY," +
            "is_restricted INTEGER DEFAULT 0,account_name STRING DEFAULT NULL, account_type STRING DEFAULT NULL," +
            " sourceid TEXT,version INTEGER NOT NULL DEFAULT 1,dirty INTEGER NOT NULL DEFAULT 0," +
            "deleted INTEGER NOT NULL DEFAULT 0,contact_id INTEGER," +
            "aggregation_mode INTEGER NOT NULL DEFAULT 0,aggregation_needed INTEGER NOT NULL DEFAULT 1," +
            "custom_ringtone TEXT,send_to_voicemail INTEGER NOT NULL DEFAULT 0," +
            "times_contacted INTEGER NOT NULL DEFAULT 0,last_time_contacted INTEGER,starred INTEGER NOT NULL DEFAULT 0," +
            "sync1 TEXT, sync2 TEXT, sync3 TEXT, sync4 TEXT );";
    public static final String DATA_TABLE_CREATE = "CREATE TABLE \"data\" (\n" +
            "\"_id\"  INTEGER NOT NULL,\n" +
            "\"mimetype\"  VARCHAR,\n" +
            "\"raw_contact_id\"  INTEGER,\n" +
            "\"is_primary\"  INTEGER,\n" +
            "\"is_super_primary\"  INTEGER,\n" +
            "\"data_version\"  INTEGER,\n" +
            "\"data1\"  TEXT,\n" +
            "\"data2\"  TEXT,\n" +
            "\"data3\"  TEXT,\n" +
            "\"data4\"  TEXT,\n" +
            "\"data5\"  TEXT,\n" +
            "\"data6\"  TEXT,\n" +
            "\"data7\"  TEXT,\n" +
            "\"data8\"  TEXT,\n" +
            "\"data9\"  TEXT,\n" +
            "\"data10\"  TEXT,\n" +
            "\"data11\"  TEXT,\n" +
            "\"data12\"  TEXT,\n" +
            "\"data13\"  TEXT,\n" +
            "\"data14\"  TEXT,\n" +
            "\"data15\"  TEXT,\n" +
            "\"data_sync1\"  TEXT,\n" +
            "\"data_sync2\"  TEXT,\n" +
            "\"data_sync3\"  TEXT,\n" +
            "\"data_sync4\"  TEXT,\n" +
            "PRIMARY KEY (\"_id\" ASC),\n" +
            "UNIQUE (\"_id\" ASC)\n" +
            ");\n";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     */

    public static File getDbFile(Context context) {
        return new File("/data/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATA_TABLE_CREATE);
        db.execSQL(RAW_CONTACTS_TABLE_CREATE);
    }

    public static void createDb(Context context) {
        try {
            DataBaseHelper dbh = new DataBaseHelper(context);
            SQLiteDatabase db = dbh.getWritableDatabase();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
