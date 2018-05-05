package com.vodafone.frt.apis;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vodafone.frt.models.BaseMessageResp;
import com.vodafone.frt.models.BaseModel;
import com.vodafone.frt.models.ChatMessagesResp;
import com.vodafone.frt.models.DataPayloadModel;
import com.vodafone.frt.models.SendMsgResp;
import com.vodafone.frt.models.SubordinatesRespModel;
import com.vodafone.frt.models.UserDetailsResp;


/**
 * Created by Naresh on 04-Apr-18.
 */

public class JsonParser {

    public static  final String TAG = JsonParser.class.getSimpleName();

    public static Object convertJsonToBean(ApiType apiType, String json){
        Object obj = null;
        if(apiType == null || json == null){
            Log.e(TAG,"apiType or json is null");
            return obj;
        }
        Gson mGson = new Gson();
        try {
            switch (apiType){
                case SEND_MSG_RESP:
                    obj =  mGson.fromJson(json, SendMsgResp.class);
                    break;
                case RECEIVE_MSG_RESP:
                    obj =  mGson.fromJson(json, ChatMessagesResp.class);
                    break;
                case REGISTER_USER:
                    obj =  mGson.fromJson(json, BaseModel.class);
                    break;
                case BASE_MESSAGE:
                    obj =  mGson.fromJson(json, BaseMessageResp.class);
                    break;
                case LOGIN:
                   // obj =  mGson.fromJson(json, LoginResponse.class);
                    break;
                case GET_USER_DETAILS:
                    obj =  mGson.fromJson(json, UserDetailsResp.class);
                    break;
                case GET_SUBORDINATE_DETAILS:
                    obj =  mGson.fromJson(json, SubordinatesRespModel.class);
                    break;
                case PAYLOAD_DATA:
                    obj =  mGson.fromJson(json, DataPayloadModel.class);
                    break;


            }
        } catch (JsonSyntaxException e) {
            Log.i(TAG,"Parsing Error:"+e);
        }
        return  obj;
    }

    public static String convertBeanToJson(Object bean){
        return new Gson().toJson(bean);
    }
}
