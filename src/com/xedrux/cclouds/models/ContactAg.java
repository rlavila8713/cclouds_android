package com.xedrux.cclouds.models;

import java.util.LinkedList;

/**
 * Created by Isidro on 10/19/2015.
 */
public class ContactAg {
    long id;
    String display;
    int displayMimeTypePriority;
    String accountType;
    String accountName;


    public ContactAg(long id) {
        this.id = id;
        displayMimeTypePriority=-1;
        display="";
    }

    public long getId() {
        return id;
    }

    public void setDisplay(String display, int priority){
        if(displayMimeTypePriority<priority){
            this.display=display;
            displayMimeTypePriority=priority;
        }
    }


    @Override
    public String toString() {
        return display;
    }
}
