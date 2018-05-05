package com.vodafone.frt.interfaces;

import com.vodafone.frt.models.ChatMessagesResp;
import com.vodafone.frt.models.ReceiveMsgReq;
import com.vodafone.frt.models.SendMsgResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Naresh on 04-Apr-18.
 */

public interface APIService {

    /*@GET("playlistItems")
            @Query("part") String part,
    Call<PlayListResponse> getPlayList(
            @Query("key") String key,
            @Query("maxResults") String maxResults,
            @Query("playlistId") String playlistId

    );*/

     @POST("SendMessage")
     Call<SendMsgResp> sendMessages(@Body String msgreq);

    @POST("SendMessage")
    Call<ChatMessagesResp> receiveMessages(@Body ReceiveMsgReq req);

    /* @GET("/api/users?")
     Call<UserList> doGetUserList(@Query("page") String page);*/

     /*@FormUrlEncoded
     @POST("/api/users?")
     Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
   /* @POST("/posts")
    @FormUrlEncoded
    Call<BaseMessageResp> sendMessages(@Field("userID") String userId,
                                       @Field("receiverID") String receiverID,
                                       @Field("data") String message);

    @POST("/posts")
    @FormUrlEncoded
    Call<ChatMessagesResp> getChatMessages(@Field("userID") String userId,
                                           @Field("senderID") String senderID,
                                           @Field("index") int index);
*/


}




