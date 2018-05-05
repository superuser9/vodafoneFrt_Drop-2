package com.vodafone.frt.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestGetBroadCastDetailModel;
import com.vodafone.frt.models.PTRResponseGetBroadCastDetailModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;
import com.vodafone.frt.webview.WebViewHelpDesk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BulletinBoardActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners, FRTBroadcasting.CallBackBroadcast, FRTAsyncCommon.FetchDataCallBack {

    private BroadcastReceiver broadcastReceiver;
    private FRTBroadcasting broadcasting;
    private final BulletinBoardActivity frtBulletinBoardActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private WebView webView = null;
    private FRTTextviewTrebuchetMS norecord, loader;
    private FRTUtility frtUtility;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTWEBAPI frtwebapi;
    private FRTConstants frtConstants;
    private LinearLayout ivback;
    private List<PTRResponseGetBroadCastDetailModel> frtResponseGetBroadCastDetailModels;
    private FRTSharePrefUtil frtSharePrefUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_bulletin_board);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtBulletinBoardActivity);
        callBackSetUp();
    }

    @Override
    public void initAllViews() {
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        webView = (WebView) findViewById(R.id.helpDeskWebView);
        frtConstants = new FRTConstants();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtBulletinBoardActivity);
        frtUtility.settingStatusBarColor(frtBulletinBoardActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtBulletinBoardActivity);
        broadcasting.setCallbackBroadcast(frtBulletinBoardActivity);
        frtwebapi = new FRTWEBAPI();
        setWebView();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        ivback.setOnClickListener(cqBackClick);
    }

    private final View.OnClickListener cqBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = broadcasting.getBroadcasting();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void getBroadcast(boolean isBroadcasting) {
        if (isBroadcasting) {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivback);
            frtUtility.dialogHide();
        } else {
            getBroadcastData();
        }
    }

    private PTRRequestGetBroadCastDetailModel setUpGetBroadcast() {
        PTRRequestGetBroadCastDetailModel frtRequestGetBroadCastDetailModel = new PTRRequestGetBroadCastDetailModel();
        try {
            frtRequestGetBroadCastDetailModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frtRequestGetBroadCastDetailModel;
    }

    private void getBroadcastData() {
        if (frtConnectivityReceiver.isConnected(frtBulletinBoardActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestGetBroadCastDetailModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtBulletinBoardActivity);
            frtAsyncCommon.setFrtModel(setUpGetBroadcast());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_BROADCAST_DETAIL), "GetBroadCastDetail");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONObject jo = jsonObject.optJSONObject("results");
                            String summary = jo.optString("message_content");
                            norecord.setVisibility(View.GONE);
                            webView.loadData(summary, "text/html", null);
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else if (jsonObject.optString("status").equalsIgnoreCase(getString(R.string.unknownError))) {
                            frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), ivback);
                            norecord.setVisibility(View.VISIBLE);
                            norecord.setText(jsonObject.optString(getString(R.string.error_message_key)));
                        } else {
                            norecord.setVisibility(View.VISIBLE);
                        }
                        loader.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivback);
        }
    }

    /* @Override
     public void available(boolean isavailable) {
         if (isavailable) {
             norecord.setVisibility(View.GONE);
         } else {
             norecord.setVisibility(View.VISIBLE);
         }
     }*/
    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtBulletinBoardActivity)) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }


    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtBulletinBoardActivity;
        frtCallBackForIdFind = frtBulletinBoardActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtBulletinBoardActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == frtConstants.LOCATION_PERMISSION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(frtBulletinBoardActivity, R.string.sufficient_permissions, Toast.LENGTH_LONG).show();
                finish();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebViewHelpDesk webViewClient = new WebViewHelpDesk(this);
        webView.setWebViewClient(webViewClient);
//        webView.loadUrl("https://www.google.com");
    }

    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtBulletinBoardActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtBulletinBoardActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtBulletinBoardActivity);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(android.R.id.content));
        }
    }

    private PTRLoginRefreshRequestModel setUpLoginRefreshRequestModel() {
        PTRLoginRefreshRequestModel cqRequestModelLogin = new PTRLoginRefreshRequestModel();
        cqRequestModelLogin.setRefresh_token(frtSharePrefUtil.getString(getString(R.string.refresh_token_key)));
        cqRequestModelLogin.setGrant_type();
        return cqRequestModelLogin;
    }

    @Override
    public void getAsyncData(String response, String type) {

        if (response != null) {
            if (type.equals("loginrefresh")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    frtSharePrefUtil.setString(getString(R.string.token_key), jsonObject.optString(getString(R.string.token_key)));
                    frtSharePrefUtil.setString(getString(R.string.tokentype_key), jsonObject.optString(getString(R.string.tokentype_key)));
                    frtSharePrefUtil.setString(getString(R.string.tokenexpiretime_key), String.valueOf(jsonObject.optInt(getString(R.string.tokenexpiretime_key), 0)));
                    frtSharePrefUtil.setString(getString(R.string.refresh_token_key), jsonObject.optString(getString(R.string.refresh_token_key)));
                    frtSharePrefUtil.setString(getString(R.string.globalsettings_key), jsonObject.optString(getString(R.string.globalsettings_key)));
                    frtSharePrefUtil.setString("currenttime", frtUtility.getCurrentTime());

                } catch (JSONException ignored) {
                }
            }
        }
    }
}
