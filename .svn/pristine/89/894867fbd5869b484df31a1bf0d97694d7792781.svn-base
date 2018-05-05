package com.vodafone.frt.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.vodafone.frt.R;
import com.vodafone.frt.adapters.ChatRecyclerAdapter;
import com.vodafone.frt.apis.ApiType;
import com.vodafone.frt.apis.JsonParser;
import com.vodafone.frt.interfaces.APIService;
import com.vodafone.frt.models.BaseMessageResp;
import com.vodafone.frt.models.ChatMessagesResp;
import com.vodafone.frt.models.DataRequestModel;
import com.vodafone.frt.models.MsgData;
import com.vodafone.frt.models.ReceiveMsgData;
import com.vodafone.frt.models.ReceiveMsgReq;
import com.vodafone.frt.models.SendMsgResp;
import com.vodafone.frt.network.ApiUtils;
import com.vodafone.frt.network.AsyncThread;
import com.vodafone.frt.network.ReqRespBean;
import com.vodafone.frt.network.WEBAPI;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.Constants;
import com.vodafone.frt.utility.DateUtils;
import com.vodafone.frt.utility.SharePrefUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ChatActivity extends AppCompatActivity {

    private final String TAG = ChatActivity.class.getSimpleName();

    private EditText et_message;
    private ImageView iv_send;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChatRecyclerAdapter mChatAdapter;
    private APIService mAPIService;
    private String userChatId,senderChatId;
    private ProgressBar progressBar,pb_send_msg;
    private Handler mPollingHandler;
    private AsyncThread mAsyncThread = null;
    private Timer mTimer = null;
    private MessageReceiver messageReceiver = null;
    IntentFilter receiveMsgFilter = null;
    private FRTSharePrefUtil frtSharePrefUtil;
    private LinearLayout ivback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();
        initWidgets();
        setListeners();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(getApplicationContext());
        mTimer = new Timer();
        senderChatId = getIntent().getStringExtra(Constants.SENDER_ID);
        try {
            userChatId = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAPIService = ApiUtils.getAPIService();
        setUpChatAdapter();
        setUpRecyclerView();

        TextView mTitle = (TextView) findViewById(R.id.textUserName);
        ivback = (LinearLayout) findViewById(R.id.ivbackCheckIn);
        ivback.setOnClickListener(cqBackClick);
        mTitle.setText(getIntent().getStringExtra(Constants.SENDER_NAME));
       // mTitle.setText(userChatId);

        //receiveMessage();
       // getSupportActionBar().setTitle(getIntent().getStringExtra(Constants.SENDER_NAME));
        receiveMsgFilter = new IntentFilter(Constants.RECIEVE_MESSAGE_ACTION);
        messageReceiver = new MessageReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.CURRENT_CHATING_USER = senderChatId;
        loadInitialMessageList(0);
        registerReceiver(messageReceiver,receiveMsgFilter);

    }

    private final View.OnClickListener cqBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        Constants.CURRENT_CHATING_USER = "";
        unregisterReceiver(messageReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.CURRENT_CHATING_USER = "";
        // if(mTimer != null)mTimer.cancel();
        try{
            unregisterReceiver(messageReceiver);
        }catch (Exception e){

        }
    }

    private void setListeners() {
        iv_send.setOnClickListener(mOnClickListener);
    }

    private void initWidgets(){
        et_message = (EditText)findViewById(R.id.et_message);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_chat);
        pb_send_msg = (ProgressBar)findViewById(R.id.pb_send_msg);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_send:
                    if(!et_message.getText().toString().isEmpty())
                        sendUserMessage(et_message.getText().toString().trim());
                    break;
            }
        }
    };
    private void setUpChatAdapter() {
        mChatAdapter = new ChatRecyclerAdapter(this, userChatId, senderChatId);
    }
    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        // Load more messages when user reaches the top of the current message list.
       /* mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    loadNextMessageList(Constants.MSG_LIST_LIMIT);
                }
                Log.v(TAG, "onScrollStateChanged");
            }
        });*/
    }
    private void showToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }


    private void receiveMessage(){

        mTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if(mChatAdapter != null && mChatAdapter.mMessageList != null) {
                           // refreshMessageList(mChatAdapter.mMessageList.size());
                        }
                    };
                });
            }
        }, Constants.POLLING_INTERVAL, Constants.POLLING_INTERVAL);
    }



    /*private void refreshMessageList(int numMessages) {
        ReceiveMsgData msgData = new ReceiveMsgData(userChatId,senderChatId,numMessages);
        String reqData = JsonParser.convertBeanToJson(msgData);
        ReceiveMsgReq receiveMsgReq = new ReceiveMsgReq(reqData);
        String reqJson = JsonParser.convertBeanToJson(receiveMsgReq);
        ReqRespBean mBean = new ReqRespBean();
        mBean.setApiType(ApiType.REFRESH_MSG);
        mBean.setRequestmethod(WEBAPI.POST);
        mBean.setUrl(WEBAPI.getWEBAPI(ApiType.RECEIVE_MSG_RESP));
        mBean.setMimeType(WEBAPI.contentTypeJson);
        mBean.setJson(reqJson);
        mBean.setmHandler(mHandler);
        mBean.setCtx(this);
        mAsyncThread = new AsyncThread();
        //mAsyncThread.initProgressDialog(ChatActivity.this,mOnCancelListener);
        mAsyncThread.execute(mBean);
        mAsyncThread = null;

    }*/


    private void loadInitialMessageList(int numMessages) {
        progressBar.setVisibility(View.VISIBLE);
        ReceiveMsgData msgData = new ReceiveMsgData(userChatId,senderChatId,numMessages);
        String reqData = JsonParser.convertBeanToJson(msgData);
        DataRequestModel receiveMsgReq = new DataRequestModel(reqData);
        String reqJson = JsonParser.convertBeanToJson(receiveMsgReq);
        ReqRespBean mBean = new ReqRespBean();
        mBean.setApiType(ApiType.RECEIVE_MSG_RESP);
        mBean.setRequestmethod(WEBAPI.POST);
        mBean.setUrl(WEBAPI.getWEBAPI(ApiType.RECEIVE_MSG_RESP));
        mBean.setMimeType(WEBAPI.contentTypeJson);
        mBean.setJson(reqJson);
        HashMap<String,String> header = new HashMap<String, String>(1);
        header.put("Authorization","bearer "+ frtSharePrefUtil.getString(getApplicationContext().getString(R.string.token_key)));
        mBean.setHeader(header);
        mBean.setmHandler(mHandler);
        mBean.setCtx(this);
        mAsyncThread = new AsyncThread();
        //mAsyncThread.initProgressDialog(ChatActivity.this,mOnCancelListener);
        mAsyncThread.execute(mBean);
        mAsyncThread = null;

    }
   /* private void refreshMessageList(int numMessages) {
        ReceiveMsgData msgData = new ReceiveMsgData(userChatId,senderChatId,numMessages);
        String reqData = JsonParser.convertBeanToJson(msgData);
        ReceiveMsgReq receiveMsgReq = new ReceiveMsgReq(reqData);
        String reqJson = JsonParser.convertBeanToJson(receiveMsgReq);
        ReqRespBean mBean = new ReqRespBean();
        mBean.setApiType(ApiType.REFRESH_MSG);
        mBean.setRequestmethod(WEBAPI.POST);
        mBean.setUrl(WEBAPI.getWEBAPI(ApiType.RECEIVE_MSG_RESP));
        mBean.setMimeType(WEBAPI.contentTypeJson);
        mBean.setJson(reqJson);
        mBean.setmHandler(mHandler);
        mBean.setCtx(this);
        mAsyncThread = new AsyncThread();
        //mAsyncThread.initProgressDialog(ChatActivity.this,mOnCancelListener);
        mAsyncThread.execute(mBean);
        mAsyncThread = null;

    }*/

    public void sendUserMessage(final String text) {
        pb_send_msg.setVisibility(View.VISIBLE);
        iv_send.setVisibility(View.GONE);
        MsgData msgData = new MsgData(userChatId, senderChatId,text, DateUtils.getCurrentDate());
        String data = JsonParser.convertBeanToJson(msgData);
        DataRequestModel sendMsgReq = new DataRequestModel(data);
        String reqjson = JsonParser.convertBeanToJson(sendMsgReq);

        ReqRespBean mBean = new ReqRespBean();
        mBean.setApiType(ApiType.SEND_MSG_RESP);
        mBean.setRequestmethod(WEBAPI.POST);
        mBean.setUrl(WEBAPI.getWEBAPI(ApiType.SEND_MSG_RESP));
        mBean.setMimeType(WEBAPI.contentTypeJson);
        mBean.setJson(reqjson);
        HashMap<String,String> header = new HashMap<String, String>(1);
        header.put("Authorization","bearer "+ frtSharePrefUtil.getString(getApplicationContext().getString(R.string.token_key)));
        mBean.setHeader(header);
        mBean.setmHandler(mHandler);
        mBean.setCtx(this);
        mAsyncThread = new AsyncThread();
        //mAsyncThread.initProgressDialog(ChatActivity.this,mOnCancelListener);
        mAsyncThread.execute(mBean);
        mAsyncThread = null;

    }


    DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if(mAsyncThread != null)mAsyncThread.cancel(true);
            mAsyncThread = null;
        }
    };

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj == null) {
                return false;
            } else {
                ReqRespBean mBean = (ReqRespBean) msg.obj;
                switch (mBean.getApiType()) {
                    case SEND_MSG_RESP:
                        pb_send_msg.setVisibility(View.GONE);
                        iv_send.setVisibility(View.VISIBLE);
                        SendMsgResp msgResp = (SendMsgResp) JsonParser.convertJsonToBean(ApiType.SEND_MSG_RESP,mBean.getJson());
                        if(msgResp != null) {
                            if(msgResp.getStatus().equals(Constants.OK)){
                                if (et_message != null) et_message.setText("");
                                if (mChatAdapter != null) mChatAdapter.addFirst(msgResp.getResults());
                            }else{
                                showToast(msgResp.getError_message());
                            }
                        }else{
                            showToast(getString(R.string.unable_to_send_msg));
                        }
                        break;

                    case RECEIVE_MSG_RESP:
                        progressBar.setVisibility(View.GONE);
                        ChatMessagesResp chatMessagesResp = (ChatMessagesResp) JsonParser.convertJsonToBean(ApiType.RECEIVE_MSG_RESP,mBean.getJson());
                        if(chatMessagesResp != null) {
                            if(chatMessagesResp.getStatus().equals(Constants.OK)){
                                if(mChatAdapter != null && chatMessagesResp.getResults() != null) {
                                    List<BaseMessageResp> messageRespList = chatMessagesResp.getResults();
                                   // Collections.reverse(messageRespList);
                                    mChatAdapter.setMessageList(messageRespList);
                                    /*for (BaseMessageResp message : chatMessagesResp.getResults()) {
                                        mChatAdapter.addFirst(message);
                                    }*/
                                }
                            }else{
                                // showToast(chatMessagesResp.getError_message());
                            }
                        }else{
                            // showToast(getString(R.string.unable_fetch_data));
                        }
                        break;

                    case REFRESH_MSG:
                        progressBar.setVisibility(View.GONE);
                        ChatMessagesResp newMsg = (ChatMessagesResp) JsonParser.convertJsonToBean(ApiType.RECEIVE_MSG_RESP,mBean.getJson());
                        if(newMsg != null) {
                            if(newMsg.getStatus().equals(Constants.OK)){
                                if(mChatAdapter != null && newMsg.getResults() != null) {
                                    List<BaseMessageResp> messageRespList = newMsg.getResults();
                                    // Collections.reverse(messageRespList);
                                    for (BaseMessageResp message : messageRespList) {
                                        boolean isExist = false;
                                        for (BaseMessageResp temp : mChatAdapter.mMessageList){
                                            if(temp.getId().equals(message.getId())) {
                                                isExist = true;
                                                break;
                                            }
                                        }
                                        if(!isExist){
                                            mChatAdapter.addFirst(message);
                                        }
                                    }
                                }
                            }else{
                                //  showToast(newMsg.getError_message());
                            }
                        }else{
                            // showToast(getString(R.string.unable_fetch_data));
                        }
                        break;

                    default:
                        break;
                }
            }
            return true;
        }
    });

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            BaseMessageResp newMsg = (BaseMessageResp) JsonParser.convertJsonToBean(ApiType.BASE_MESSAGE,data);
            if(newMsg != null) {
                if(mChatAdapter != null) {
                    mChatAdapter.addFirst(newMsg);
                    /*List<BaseMessageResp> messageRespList = newMsg.getResults();
                    Collections.reverse(messageRespList);
                    for (BaseMessageResp message : messageRespList) {
                        boolean isExist = false;
                        for (BaseMessageResp temp : mChatAdapter.mMessageList){
                            if(temp.getId().equals(message.getId())) {
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            mChatAdapter.addFirst(message);
                        }
                    }*/
                }
            }else{
                // showToast(getString(R.string.unable_fetch_data));
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            // This activity is at root of task, so launch main activity
            Intent mIntent = new Intent(this,PTRNavigationScreenActivity.class);
            startActivity(mIntent);
            finish();
        } else {
            // This activity isn't at root of task, so just finish()
            super.onBackPressed();
        }
     /*   if(isTaskRoot()){

        }*/
    }
}
