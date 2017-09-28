package com.xedrux.cclouds.models;

/**
 * Created by reinier on 14/08/2015.
 */

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class Contacts {
    @Element
    private String firstName;
    @Element
    private String mobileNumber;

    private int id;
    private int idUser;
    private String lastName;
    private Character sex;
    private String birthDay;
    private String country;
    private String region;
    private String canton;
    private String parroquia;


    public Contacts(String firstName, String mobileNumber) {
        this.firstName = firstName;
        this.mobileNumber = mobileNumber;
    }

    public Contacts(String firstName, String lastName, String mobileNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }

    public Contacts(int id, int idUser, String firstName, String lastName, String mobileNumber, Character sex, String birthDay, String country, String region, String canton, String parroquia) {
        this.id = id;
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.sex = sex;
        this.birthDay = birthDay;
        this.country = country;
        this.region = region;
        this.canton = canton;
        this.parroquia = parroquia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getParroquia() {
        return parroquia;
    }

    public void setParroquia(String parroquia) {
        this.parroquia = parroquia;
    }


}


