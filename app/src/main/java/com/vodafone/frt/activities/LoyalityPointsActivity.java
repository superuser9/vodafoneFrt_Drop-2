package com.vodafone.frt.activities;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRLoyaltyPointsAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRGetUserLoyaltyStatusModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRMainLoyaltyStatusModel;
import com.vodafone.frt.models.PTRResponseLoyaltyStatus;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class LoyalityPointsActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind,
        FRTBroadcasting.CallBackBroadcast, FRTCallBackSetListeners, FRTAsyncCommon.FetchDataCallBack{
    private boolean checknet = false;
    private LinearLayout ivbackCheckIn;
    private int roleIdEncripted;
    private int userIdEncripted;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTBroadcasting broadcasting;
    private FRTWEBAPI frtwebapi;
    private FRTUtility frtUtility;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private LoyalityPointsActivity frtLoyalityPointsActivity = this;
    private FRTConnectivityReceiver frtCnnectivityReceiver;
    private ProgressDialog progressDialog;
    private ListView frtLoyalityInListView;
    private PTRLoyaltyPointsAdapter adapter;
    private FRTTextviewTrebuchetMS norecord;
    private FRTTextviewTrebuchetMS loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyality_points);
        getSupportActionBar().hide();
        callBackSetUp();
        //initViews();

    }

    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtLoyalityPointsActivity;
        frtCallBackForIdFind = frtLoyalityPointsActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtLoyalityPointsActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }



    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void commonListeners() {
        ivbackCheckIn.setOnClickListener(frtBackClick);
    }

    @Override
    public void initAllViews() {
        frtCnnectivityReceiver = new FRTConnectivityReceiver();
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
         frtLoyalityInListView = (ListView)frtCallBackForIdFind.view(R.id.frtLoyalityInListView);
        ivbackCheckIn = (LinearLayout) frtCallBackForIdFind.view(R.id.ivbackCheckIn);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtLoyalityPointsActivity);
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtLoyalityPointsActivity);
        adapter = new PTRLoyaltyPointsAdapter(frtLoyalityPointsActivity);


        try {
            roleIdEncripted  = Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.role_id)).getBytes("UTF-16LE"), Base64.DEFAULT)));
            userIdEncripted  = Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void getBroadcast(boolean isBroadcasting) {
        if (isBroadcasting) {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivbackCheckIn);
            frtUtility.hideDialog();
            checknet = true;
        } else {
            if (checknet)
                checknet = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoyaltyStatusRequest();
    }

    private void getLoyaltyStatusRequest() {
        if (frtCnnectivityReceiver.isConnected(getApplicationContext())) {
            progressDialog = new ProgressDialog(frtLoyalityPointsActivity);
            final FRTAsyncCommon<PTRGetUserLoyaltyStatusModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtLoyalityPointsActivity);
            frtAsyncCommon.setFrtModel(setUpLoyaltyStatusModel());

            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_LOYALTY_STATUS), "GetUserLoyaltyStatus");
            frtAsyncCommon.setFetchDataCallBack(frtLoyalityPointsActivity);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();

        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivbackCheckIn);
            progressDialog.dismiss();
        }
    }


    private PTRGetUserLoyaltyStatusModel setUpLoyaltyStatusModel(){
        PTRGetUserLoyaltyStatusModel frtGetUserLoyaltyStatusModel = new PTRGetUserLoyaltyStatusModel();
        if (!TextUtils.isEmpty(getString(R.string.userkey)))
        frtGetUserLoyaltyStatusModel.setUserid(userIdEncripted);
        frtGetUserLoyaltyStatusModel.setRoleId(roleIdEncripted);
        //frtGetUserLoyaltyStatusModel.setRoleId(270);
        return  frtGetUserLoyaltyStatusModel;
    }


    @Override
    public void getAsyncData(String response, String type) {
        progressDialog.dismiss();
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
            } else if (type.equals("GetUserLoyaltyStatus")) {
                try {
                    Log.d(this.getClass().getName(), "RESPONSE_LOYALTY_POINTS===" + response);
                    PTRMainLoyaltyStatusModel frtMainLoyaltyStatusModel = new Gson().fromJson(response, PTRMainLoyaltyStatusModel.class);
                    if (frtMainLoyaltyStatusModel.getResults().size() > 0) {
                        norecord.setVisibility(View.GONE);
                        frtLoyalityInListView.setVisibility(View.VISIBLE);
                        setAdapter(frtMainLoyaltyStatusModel.getResults());
                    } else if (frtMainLoyaltyStatusModel.getStatus().equalsIgnoreCase("ERROR")) {
                        frtUtility.setSnackBar(frtMainLoyaltyStatusModel.getError_message(), frtLoyalityInListView);
                    } else if (frtMainLoyaltyStatusModel.getStatus().equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (frtMainLoyaltyStatusModel.getStatus().equals(getString(R.string.session_exp))) {
                        /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else if (frtMainLoyaltyStatusModel.getStatus().equalsIgnoreCase("ZERO_RESULTS")) {
                        norecord.setText(frtMainLoyaltyStatusModel.getError_message());
                        norecord.setVisibility(View.VISIBLE);
                        frtLoyalityInListView.setVisibility(View.GONE);
                    } else {
                        //norecord.setText(getString(R.string.noLoyaltyPoints));
                        norecord.setText(frtMainLoyaltyStatusModel.getError_message());
                        norecord.setVisibility(View.VISIBLE);
                        frtLoyalityInListView.setVisibility(View.GONE);
                    }
                    loader.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            frtUtility.setSnackBar(getString(R.string.didNotResponseFromServer), frtLoyalityInListView);
            new Handler().postAtTime(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    norecord.setText(getString(R.string.noDataAvailable));
                    norecord.setVisibility(View.VISIBLE);
                    //  onBackPressed();
                }
            },1000);

        }
    }



    private void setAdapter(List<PTRResponseLoyaltyStatus> results) {
        adapter.setDataSet(results);
        adapter.setSelectionListener(frtLoyalityPointsActivity);
        adapter.notifyDataSetChanged();
        frtLoyalityInListView.setAdapter(adapter);
    }
    private void getRefereshTokenData() {
        if (frtCnnectivityReceiver.isConnected(frtLoyalityPointsActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtLoyalityPointsActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtLoyalityPointsActivity);
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

