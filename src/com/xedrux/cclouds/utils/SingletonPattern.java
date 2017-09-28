package com.xedrux.cclouds.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.xedrux.cclouds.models.Usuario;
import com.xedrux.cclouds.views.usuario.ContactObject;

import java.util.ArrayList;

public class SingletonPattern {

    private static SingletonPattern instance = null;
    private static Usuario activeUser;
    private static  Boolean update_needed=false;
    public static boolean uploading=false;
    public static boolean check_contact_changes=false;
    public static ArrayList<ContactObject>contactsList;

    protected SingletonPattern(Context context)
    {
        activeUser = new Usuario();
    }

    public static SingletonPattern getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new SingletonPattern(context);
        }
        return instance;
    }
    public static SingletonPattern getInstance()
    {
        return instance;
    }

    public static void setInstance(SingletonPattern instance)
    {
        SingletonPattern.instance = instance;
    }

    public static Usuario getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(Usuario activeUser) {
        SingletonPattern.activeUser = activeUser;
    }

    public static void  setContactList(ArrayList<ContactObject> value) {
        SingletonPattern.contactsList = value;
    }

    public static ArrayList<ContactObject> getContactList() {
        return  SingletonPattern.contactsList;
    }

   public static Boolean isUpdate_needed(Context context) {
        //leer este valor de las preferencias compartidas
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return preferences.getBoolean("shouldUpdate",false);
    }



    public static void setUpdate_needed(Context context,Boolean update_needed) {
        //guardar este valor en las preferencias compartidas
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        preferences.edit().putBoolean("shouldUpdate",update_needed).commit();
       // SingletonPattern.update_needed = update_needed;
    }
}
