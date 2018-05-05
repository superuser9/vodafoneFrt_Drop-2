package com.vodafone.frt.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRPauseReasonAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTButtonTrebuchetMS;
import com.vodafone.frt.fonts.FRTEditTextTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestPauseReasonModel;
import com.vodafone.frt.models.PTRResponsePauseReasonModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishal
 */
public class PTRPauseReasonsActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast, PTRPauseReasonAdapter.FRTCallbackListItemChecked {
    private boolean flagSession = false, check = false, checknet = false;
    private BroadcastReceiver broadcastReceiver;
    private PTRPauseReasonsActivity frtPauseReasonsActivity = this;
    FRTCallBackInitViews frtCallBackInitViews;
    FRTCallBackForIdFind frtCallBackForIdFind;
    FRTCallBackSetListeners frtCallBackSetListeners;
    private FRTBroadcasting broadcasting;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTEditTextTrebuchetMS othereditText;
    private FRTButtonTrebuchetMS saveButton;
    private FRTTextviewTrebuchetMS specifyTitleTextView;
    FRTTextviewTrebuchetMS loader;
    private List<PTRResponsePauseReasonModel> reasonsList;
    private int selPos = -1;
    private ListView listView;
    private LinearLayout ivback;
    private FRTUtility frtUtility;
    FRTWEBAPI frtWEBAPI;
    private PTRPauseReasonAdapter.FRTCallbackListItemChecked frtCallbackListItemChecked;
    FRTApp frtApp;
    private ScrollView scrollView;
    private FRTSharePrefUtil frtSharePrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtpause_reasons);
        callBackSetUp();
    }

    private void callBackSetUp() {
        frtCallBackInitViews = frtPauseReasonsActivity;
        frtCallBackForIdFind = frtPauseReasonsActivity;
        frtCallBackSetListeners = frtPauseReasonsActivity;
        frtCallbackListItemChecked = frtPauseReasonsActivity;
        frtWEBAPI = new FRTWEBAPI();
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    private void setAdapter() {
        PTRPauseReasonAdapter adapter = new PTRPauseReasonAdapter(frtPauseReasonsActivity);
        adapter.notifyDataSetChanged();
        adapter.setDataSet(reasonsList);
        adapter.setItemCallBack(frtCallbackListItemChecked);
        listView.setAdapter(adapter);
    }

    @Override
    public void initAllViews() {
        listView = (ListView) findViewById(R.id.remarksListView);
        othereditText = (FRTEditTextTrebuchetMS) findViewById(R.id.othereditText);
        scrollView =(ScrollView)findViewById(R.id.scrollView);
        saveButton = (FRTButtonTrebuchetMS) findViewById(R.id.SaveButton);
        specifyTitleTextView = (FRTTextviewTrebuchetMS) findViewById(R.id.specifyTitleTextView);
        loader = (FRTTextviewTrebuchetMS) findViewById(R.id.loader);
        ivback = (LinearLayout) findViewById(R.id.ivback);
        frtApp = (FRTApp) frtPauseReasonsActivity.getApplication();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtPauseReasonsActivity);
        frtUtility.settingStatusBarColor(frtPauseReasonsActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtPauseReasonsActivity);
        broadcasting.setCallbackBroadcast(frtPauseReasonsActivity);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtPauseReasonsActivity);

        getPauseReasons();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        saveButton.setOnClickListener(onClickSave);
        ivback.setOnClickListener(frtBackClick);
    }

    private View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private View.OnClickListener onClickSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (check) {
                handleSaveReason();
            } else if (othereditText.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(othereditText.getText().toString())) {

                handleSaveReason();
            } else {
                frtUtility.setSnackBar(getString(R.string.pausereason), listView);
            }
        }
    };

    private void handleSaveReason() {
        Intent intent = new Intent(frtPauseReasonsActivity, PTRRouteControllerTaskActivity.class);
        intent.putExtra("reasonresp", "Resume");
        if (!TextUtils.isEmpty(reasonsList.get(selPos).getDescription()) && selPos != reasonsList.size() - 1)
            intent.putExtra("remark", reasonsList.get(selPos).getDescription());
        else if (!TextUtils.isEmpty(othereditText.getText().toString()))
            intent.putExtra("remark", othereditText.getText().toString());
        else
            intent.putExtra("remark", "none");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtPauseReasonsActivity)) {
            super.onBackPressed();
            Intent intent = new Intent(frtPauseReasonsActivity, PTRRouteControllerTaskActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }

    @SuppressWarnings("deprecation")
    private void getPauseReasons() {
        FRTAsyncCommon<PTRRequestPauseReasonModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpLoginModel());
        frtAsyncCommon.setContext(frtPauseReasonsActivity);
        frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.PAUSE_REASON_DETAIL), "getreasondetail");
        frtAsyncCommon.setFetchDataCallBack(frtPauseReasonsActivity);
    }

    private PTRRequestPauseReasonModel setUpLoginModel() {
        PTRRequestPauseReasonModel cqRequestPauseReasonModel = new PTRRequestPauseReasonModel();
        cqRequestPauseReasonModel.setReasonType(getString(R.string.reason_pause));
        return cqRequestPauseReasonModel;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            } else if (type.equals("getreasondetail")) {
                JSONObject jsonObject;
                JSONArray jsonArray;
                try {
                    Log.d(this.getClass().getName(), "ONPAUSEACTIVITYRESPONSE" + response);
                    jsonObject = new JSONObject(response);
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                        jsonArray = jsonObject.optJSONArray("results");
                        reasonsList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            PTRResponsePauseReasonModel model = new PTRResponsePauseReasonModel();
                            model.setReason_id(jsonObject1.optString(getString(R.string.reason_id)));
                            model.setDescription(jsonObject1.optString(getString(R.string.description)));
                            model.setReason_type(jsonObject1.optString(getString(R.string.reason_type)));
                            reasonsList.add(model);
                        }
                        PTRResponsePauseReasonModel model = new PTRResponsePauseReasonModel();
                        model.setReason_id("0");
                        model.setDescription("Others");
                        reasonsList.add(model);
                        setAdapter();
                    }
                    else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                        /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else {
                        frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), listView);
                    }
                    loader.setVisibility(View.GONE);
                    saveButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    //   e.printStackTrace();
                }
            }
        } else {
            frtUtility.setSnackBar("Did not get any response from server", listView);
        }
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flagSession = true;
    }

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
            checknet = true;
        } else {
            if (checknet)
                getPauseReasons();
            checknet = false;
        }
    }

    @Override
    public void onSelect(boolean flag, int position) {
        selPos = position;
        if (position != reasonsList.size() - 1) {
            othereditText.setVisibility(View.GONE);
            specifyTitleTextView.setVisibility(View.GONE);
            frtApp.setReasonId(String.valueOf(reasonsList.get(position).getReason_id()));
            check = true;
        } else {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            othereditText.setVisibility(View.VISIBLE);
            specifyTitleTextView.setVisibility(View.VISIBLE);
            frtApp.setReasonId(String.valueOf("0"));
            check = false;

        }
    }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtPauseReasonsActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtPauseReasonsActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtPauseReasonsActivity);
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

}
