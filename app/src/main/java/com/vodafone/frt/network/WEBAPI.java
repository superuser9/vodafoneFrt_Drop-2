package com.vodafone.frt.network;


import com.vodafone.frt.apis.ApiType;
import com.vodafone.frt.utility.Constants;

public class WEBAPI {


    public static String getWEBAPI(ApiType apiType) {
        String api = "";
        String separator = Constants.BASEURL.endsWith("/") ? "" : "/";
        switch (apiType) {
            case SEND_MSG_RESP:
                api = Constants.BASEURL + separator + "api/chat/SendMessage";
                break;
            case RECEIVE_MSG_RESP:
                api = Constants.BASEURL + separator + "api/chat/GetMessage";
                break;
            case REGISTER_USER:
                api = Constants.BASEURL + separator + "api/chat/addUpdateDeviceId";
                break;
            case LOGIN:
                api = Constants.BASEURL + separator + "token";
                break;
            case GET_USER_DETAILS:
                api = Constants.BASEURL + separator + "api/user/GetUserDetail";
                break;
            case GET_SUBORDINATE_DETAILS:
                api = Constants.BASEURL + separator + "api/user/GetSubordinateDetails";  //Patroller or FRT
                break;
        }
        return api;
    }

    public static final String GET = "get";
    public static final String POST = "post";

    public static final String contentTypeFormData = "application/x-www-form-urlencoded";
    public static final String contentTypeJson = "application/json";
}

