package com.xedrux.cclouds.models;

import com.xedrux.cclouds.models.Contacts;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by reinier on 20/08/2015.
 */
@Root
public class ContactsList {
    @ElementList
    private List<Contacts> contactsList;

    @Attribute
    private String name;

    public ContactsList(List<Contacts> contactsList, String name) {
        this.contactsList = contactsList;
        this.name = name;
    }

    public List<Contacts> getContactsList() {
        return contactsList;
    }

    public String getName() {
        return name;
    }
}
