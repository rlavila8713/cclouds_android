package com.xedrux.cclouds.constants;

/**
 * Created by reinier on 04/09/2015.
 */
public class URLS {
    /* TODO Definir aqui las urls que se usaran en el sistema**/
//    public static final String WEB_SERVICE_URL = "http://rlavila-001-site1.hostbuddy.com?r=site/db";
//    public static final String WEB_SERVICE_URL = "http://10.0.2.2/cclouds/web/index.php?r=site/db";

    public static  String SERVER_ADDRESS = "";
    public static  String WEB_SERVICE_URL = "";//this and the former are filled in Utils.loadPreferences
    public static final String LOGIN_URL = "?action=25&LoginForm[username]=%s&LoginForm[password]=%s";
    public static final String REGISTER_URL = "?action=26&RegisterForm[userEmail]=%s&RegisterForm[password]=%s&RegisterForm[passwordConfirm]=%s&RegisterForm[phoneNumber]=%s";
    public static final String REGISTER_EMAIL_URL = "?action=26&RegisterForm[userEmail]=%s&RegisterForm[username]=%s";
    public static final String REGISTER_PHONE_URL = "?action=26&RegisterForm[userPhoneNumber]=%s";
    public static final String GET_USER_PROFILE = "idUser=25";
    public static final String UPDATE_USER_PROFILE = "?action=27&UpdateForm[firstName]=%s&UpdateForm[lastName]=%s&UpdateForm[userSex]=%s&UpdateForm[phoneNumber]=%s&UpdateForm[emailAddress]=%s&UpdateForm[username]=%s";
}
