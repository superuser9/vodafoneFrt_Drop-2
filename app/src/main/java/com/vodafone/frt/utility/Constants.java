package com.vodafone.frt.utility;

import android.util.*;

import java.io.UnsupportedEncodingException;

public class Constants {


    public static final String BASEURL = "http://111.93.46.11:8093/";
    public static final int MSG_LIST_LIMIT = 50;
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_NAME = "SENDER_NAME";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";

    public static final String OK = "OK";
    public static final long POLLING_INTERVAL = 1000;
    public static final String FCM_TOKEN = "token";
    public static final String RECIEVE_MESSAGE_ACTION = "com.notification.clickaction";
    public static final String SERVER_TOKEN = "server_token";
    public static final String MGR_ID = "mgrId";
    public static String CURRENT_CHATING_USER = "";

    public static final String PATROLLER = "Patroller";
    public static final String FRT = "FRT";
    public static final String MANAGER_ID = "2";


    public static String getBase64EncodedString(String text){
        try {
            byte[] data = text.getBytes("UTF-8");
            return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
