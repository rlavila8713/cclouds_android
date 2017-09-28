package com.xedrux.cclouds.views.usuario;

/**
 * Created by reinier.leyva on 22/10/2015.
 */
public class ContactObject {

    long id;
    int displayMimeTypePriority;
    private String contactName;
    private String contactNo;
    private String image;
    private String email;
    private boolean selected;

    public ContactObject(long id) {
        this.id = id;
        contactNo="";
        contactName="";
        image="0";
        selected=false;
    }
    public ContactObject() {
        this.id = -1;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {

        return id;
    }


    public void setDisplay(String display, int priority){
        if(displayMimeTypePriority<priority){
            this.contactName=display;
            displayMimeTypePriority=priority;
        }
    }
    public String getName() {
        return contactName;
    }

    public void setName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return contactNo;
    }

    public void setNumber(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}