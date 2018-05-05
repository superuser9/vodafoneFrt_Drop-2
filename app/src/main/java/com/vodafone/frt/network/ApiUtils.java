package com.vodafone.frt.network;


import com.vodafone.frt.interfaces.APIService;

/**
 * Created by Naresh on 04-Apr-18.
 */

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "http://192.168.1.6:8091/api/chat/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
