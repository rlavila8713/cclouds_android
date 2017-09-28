package com.xedrux.cclouds.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.constants.ACTIONS;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.models.Contacts;
import com.xedrux.cclouds.models.ContactsList;
import com.xedrux.cclouds.models.Usuario;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by reinier on 14/08/2015.
 */
public class Utils {

    //preferences
    public static Boolean isConditionAccepted = false;
    public static String serverAddress = "";
    public static String uNameValue = "";
    public static String passwordValue = "";
    public static boolean rememberPassword = false;
    public static boolean usingStoredPass = false;
    public static boolean isFirstTime = true;


    /**Metodo que retorna el numero de telefonico
     * asociado a la SIM CARD del telefono
     * */

    public static String getMyPhoneNumber1(Context context) {
        TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telemamanger.getLine1Number();
        return GetCountryZipCode(context, false)+phoneNumber;
    }

    public static String getID(Context context){
        TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telemamanger.getSubscriberId();
    }
    /**
     * Metodo que retorna el codigo del pais(CountryZipCode) en caso de que la variable
     * hasPlus sea verdadera retornara el signo (+) delante del CountryZipCode Ex:+(CountryZipCode)
     * en caso contrario hasPlus sea false solo se devolveran el CountryZipCodeEx:(CountryZipCode)*/

    public static String GetCountryZipCode(Context ctx, boolean hasPlus) {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] countryCodesArray = ctx.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < countryCodesArray.length; i++) {
            String[] ZIP_ID = countryCodesArray[i].split(",");
            if (ZIP_ID[1].trim().equals(CountryID.trim())) {
                CountryZipCode = ZIP_ID[0];
                break;
            }
        }
        return hasPlus?"+"+CountryZipCode:CountryZipCode;
    }

    public static List<Contacts> getContactsList(Context context) {

        List<Contacts> contactslistAux = new ArrayList<Contacts>();
        Contacts contacts = null;
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts = new Contacts(name, phoneNumber);
            contactslistAux.add(contacts);
        }
        phones.close();
        ContactsList contactsList = new ContactsList(contactslistAux, "contacts");
        return contactsList.getContactsList();
    }

    public static List<Usuario> getUsuariosList(Context context) {

        return null;
    }


    public static File exportContactToXMLFile(List<Contacts> contactsList) throws Exception {

        Serializer serializer = new Persister();
        File result = new File("contactList.xml");
        try {
            serializer.write(contactsList, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static int getNetworkConnectionType(Context context) {
        int networkType = -1;// -1 - Not connected, 0 - WiFi, 1 - 3G
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // mobile
            NetworkInfo.State mobile = cm.getNetworkInfo(0).getState();

            // wifi
            NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();
            if (wifi == NetworkInfo.State.CONNECTED
                    || wifi == NetworkInfo.State.CONNECTING) {
                networkType = 0;
            } else {
                if (mobile == NetworkInfo.State.CONNECTED
                        || mobile == NetworkInfo.State.CONNECTING) {
                    networkType = 1;
                } else {
                    networkType = -1;
                }
            }
        }
        return networkType;
    }

    public static void loadPreferences(Application application) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(application.getBaseContext());

        URLS.SERVER_ADDRESS = preferences.getString("serverAddress", "NA");
        URLS.WEB_SERVICE_URL = URLS.SERVER_ADDRESS + ACTIONS.ACTION_GET_DB;
//        isConditionAccepted=preferences.getBoolean("is");
//        uNameValue = preferences.getString("Username", "");
        uNameValue = getID(application.getApplicationContext());
        isFirstTime = preferences.getBoolean("isFirstTime", true);
//        passwordValue = preferences.getString("Password", "");
        passwordValue = preferences.getString("token", "");
        rememberPassword = preferences.getBoolean("rememberPassword", false);

        if (!passwordValue.equals("")) {
            usingStoredPass = true;
        }
    }

    public static Boolean isAnEmailRegistered(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getBoolean("email_registered", false);
    }

    public static Boolean setEmailRegistered(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putBoolean("email_registered", value).commit();
    }
    public static Boolean setEmailRegisteredValue(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("email_registered_value", value).commit();
    }

    public static String getEmailRegisteredValue(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("email_registered_value", "");
    }
    public static Boolean setUserRol(Context context, int value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putInt("userRol", value).commit();
    }

    public static int getUserRol(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getInt("userRol", -1);
    }
    public static Boolean setUsername(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("username", value).commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("username", "");
    }

    public static Boolean setFirstName(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("firstName", value).commit();
    }

    public static String getFirstName(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("firstName", "");
    }
    public static Boolean setLastName(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("lastName", value).commit();
    }

    public static String getLastName(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("lastName", "");
    }
    public static Boolean setPhoneNumber(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("phoneNumber", value).commit();
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("phoneNumber", "");
    }
    public static Boolean setUserSex(Context context, String value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putString("userSex", value).commit();
    }

    public static String getUserSex(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString("userSex", "M");
    }

    public static Boolean setIdUserRegistered(Context context, int value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putInt("idUser", value).commit();
    }

    public static int getIdUserRegistered(Application application) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(application.getBaseContext());
        return preferences.getInt("idUser",-1);
    }

    public static Boolean setUserProfileUpdated(Context ctx, Boolean value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.edit().putBoolean("userProfileUpdate",value).commit();
    }

    public static Boolean isUserProfileUpdated(Context ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("userProfileUpdate", false);
    }


    public static Boolean isDbLocked(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getBoolean("db_lock", false);
    }

    public static Boolean setDbLock(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.edit().putBoolean("db_lock", value).commit();
    }

    public static boolean isConditionAccept(Application application) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(application.getBaseContext());

        return preferences.getBoolean("isConditionAccept", false);
    }

    public static boolean setConditionAccept(Application application, boolean accept) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(application.getBaseContext());
        boolean isConditionAccept = preferences.getBoolean("isConditionAccept", false);
        SharedPreferences.Editor editor = preferences.edit();
        if (!isConditionAccept && accept) {
            editor.putBoolean("isConditionAccept", true);
        } else if (!accept) {
            editor.putBoolean("isConditionAccept", false);
        }

        editor.commit();

        return preferences.getBoolean("isConditionAccept", false);
    }

    public static void setFirstTimePreference(boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isFirstTime", value);

        editor.commit();
    }

    public static Intent sendSMS(String phoneNumber, String message) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        smsIntent.putExtra("sms_body", message);
        return smsIntent;
    }
    public static Intent sendEmail(String[] emailAddress, String message) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "cclouds");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        emailIntent.setType("message/rfc822");
        return emailIntent;
    }
    public static Intent linkInBrowser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        return i;
    }


    public static void decompressFile(File compressedFile, File outputFile){
        try {
            FileInputStream fis= new FileInputStream(compressedFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            Inflater inflater= new Inflater();
            InflaterInputStream inflaterInputStream= new InflaterInputStream(fis,inflater,1024);
            byte[] buffer =  new byte[1024];
            int length;
            while((length=inflaterInputStream.read(buffer))>0){
                fos.write(buffer,0,length);
            }
            fis.close();
            fos.flush();
            fos.close();
            System.out.println("decompressing done OK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void compressFile(File sourceFile, File compressedFile){
        try {
            FileInputStream fis= new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(compressedFile);
            Deflater deflater= new Deflater(Deflater.BEST_COMPRESSION);
            DeflaterOutputStream deflaterOutputStream= new DeflaterOutputStream(fos,deflater,1024);
            byte[] buffer= new byte[1024];
            int length=0;
            while((length=fis.read(buffer))>0){
                deflaterOutputStream.write(buffer,0,length);
            }
            fis.close();
            deflaterOutputStream.finish();
            deflaterOutputStream.close();
            System.out.println("compressing done OK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
