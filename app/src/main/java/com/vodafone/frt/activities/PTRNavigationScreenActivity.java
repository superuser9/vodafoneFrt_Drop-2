package com.vodafone.frt.activities;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRSOSAdapter;
import com.vodafone.frt.apis.ApiType;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.apis.JsonParser;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.callbacks.FRTCallbackString;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.DataRequestModel;
import com.vodafone.frt.models.GetUserDetailsReq;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestModelLogout;
import com.vodafone.frt.models.PTRRequestUserDetailModel;
import com.vodafone.frt.models.PTRResponseUserDetailModel;
import com.vodafone.frt.models.UserDataModel;
import com.vodafone.frt.network.AsyncThread;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.network.ReqRespBean;
import com.vodafone.frt.network.WEBAPI;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.Config;
import com.vodafone.frt.utility.Constants;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;
import com.vodafone.frt.utility.SharePrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vishal
 */

/**
 * -----This class is used for switching across new feasibility and summary of existing feasibility------
 */
public class PTRNavigationScreenActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, PTRSOSAdapter.CQCallBackItemsAvailable, FRTBroadcasting.CallBackBroadcast, FRTCallbackString {
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private final PTRNavigationScreenActivity frtNavigationActivity = this;
    private Intent svcintent = null;
    private LinearLayout manager_ll, patroller_ll, both_ll;
    private RelativeLayout MyTaskButton, AttendanceButton, ChatButton, SosButton, LogOutButton, LeaveManagementButton, taskAssignmentRL, SelfCheckInButton, MyPlannedTaskButton, TrackPattrollerButton;
    private FRTBroadcasting broadcasting;
    private BroadcastReceiver broadcastReceiver;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTTextviewTrebuchetMS loader;
    private FRTUtility frtUtility;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTWEBAPI frtwebapi;
    private FRTApp frtApp;
    private ProgressDialog pd;
    private ProgressDialog progressDialog;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTConstants frtConstants;
    private GoogleApiClient googleApiClient;
    private PTRResponseUserDetailModel frtResponseUserDetailModel;
    private Handler handler;
    private RelativeLayout othersButton;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private boolean gpsEnabled;

    private FRTLocationDb frtLocationDb;
    private FRTCallbackString frtCallbackString;
    private LocationManager locationManager;
    private LocationManager mLocationManager;
    private Intent intent;
    private double latitude;
    private double longitude;

    private final View.OnClickListener onClickChat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           try {


               // startActivity(new Intent(frtNavigationActivity, FRTPTRUserListActivity.class));
               // overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            int roleID=   Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                       Base64.decode(frtSharePrefUtil.getString(getString(R.string.role_id)).getBytes("UTF-16LE"), Base64.DEFAULT)));
                if (roleID==2){
                    startActivity(new Intent(frtNavigationActivity, FRTPTRUserListActivity.class));
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }
                else {

                    ArrayList<UserDataModel> dataModels = new ArrayList<>(1);
                    UserDataModel temp = new UserDataModel();
                    temp.setUser_id(frtResponseUserDetailModel.getManager_id());
                    temp.setUser_name(frtResponseUserDetailModel.getManager_name());
                    temp.setFull_name(frtResponseUserDetailModel.getUser_name());
                    dataModels.add(temp);
                    goToNextActivity(dataModels);
                }


            } catch (Exception ignored) {
                Log.e(frtNavigationActivity.getClass().getName(), ignored.toString());
            }
        }
    };


    private void goToNextActivity(ArrayList<UserDataModel> users){
        Intent mIntent = new Intent(PTRNavigationScreenActivity.this,UsersListActivity.class);
        mIntent.putExtra("data",users);

        startActivity(mIntent);
        finish();
    }

    private final View.OnClickListener onClickMyTask = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                if (frtLocationDb.offlineRouteEnded()) {
                    frtUtility.logInfo(frtNavigationActivity, "Clicked on MyTask --> got message " + getString(R.string.syncingData), frtConstants);
                    Toast.makeText(frtNavigationActivity, getString(R.string.syncingData), Toast.LENGTH_SHORT).show();
                    frtUtility.sendDataToServer(frtwebapi, frtLocationDb, frtConnectivityReceiver);
                } else {
                    frtUtility.logInfo(frtNavigationActivity, "Clicked on --> MyTask", frtConstants);
                    Intent intent = new Intent(frtNavigationActivity, PTRMyTaskActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };


    // selfCheckIn Task Activity
    private final View.OnClickListener onSelfCheckInTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> SelfCheckInTask", frtConstants);
                Intent intent = new Intent(frtNavigationActivity, PTRMyCheckInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), v);
            }
        }
    };

    private final View.OnClickListener onClickMyPlannedTask = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> MyPlannedTask", frtConstants);
                Intent intent = new Intent(frtNavigationActivity, PTRScheduledRouteActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };
    private final View.OnClickListener onClickTrackPattroller = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> TrackPattroller", frtConstants);
                Intent intent = new Intent(frtNavigationActivity, PTRTrackPatrollerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };
    private final View.OnClickListener onClickAttendence = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> Attendence", frtConstants);
                Intent intent = new Intent(frtNavigationActivity, PTRAttendenceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };
    private final View.OnClickListener onClickSos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> Sos", frtConstants);
                startActivity(new Intent(frtNavigationActivity, FRTSOSActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };

    private final View.OnClickListener onClickLeaveManagement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> LeaveManagement", frtConstants);
//                if (!frtResponseUserDetailModel.getRole_name().equals("Manager")) {
                Intent intent = new Intent(frtNavigationActivity, MGRAttendanceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
               /* } else {
                    Intent intent = new Intent(frtNavigationActivity, LeaveManagementActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }*/
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), v);
            }
        }
    };

    private final View.OnClickListener onClickTaskAssignement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> ManageTask", frtConstants);
                Intent intent = new Intent(frtNavigationActivity, MGRTaskAssignActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), v);
            }
        }
    };

    private final View.OnClickListener onClickLogout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                if (frtLocationDb.offlineRouteEnded()) {
                    frtUtility.logInfo(frtNavigationActivity, "Clicked on --> Logout got message " + getString(R.string.syncingData), frtConstants);
                    Toast.makeText(frtNavigationActivity, getString(R.string.syncingData), Toast.LENGTH_SHORT).show();
                    frtUtility.sendDataToServer(frtwebapi, frtLocationDb, frtConnectivityReceiver);
                } else {
                    frtUtility.logInfo(frtNavigationActivity, "Clicked on --> Logout ", frtConstants);
                    doLogout();
                }

            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };
    private final View.OnClickListener onClickOthers = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
                frtUtility.logInfo(frtNavigationActivity, "Clicked on --> Others ", frtConstants);
                startActivity(new Intent(frtNavigationActivity, OthersActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtnavigation_screen);
        callbackSetUp();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.d(this.getClass().getName(), "FCM_MESAGE" + message);
                }
            }
        };
        displayFirebaseRegId();

    }

    /**
     * This method initialize the callback and objects
     */
    private void callbackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtNavigationActivity;
        frtCallBackForIdFind = frtNavigationActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtNavigationActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void initAllViews() {
        MyTaskButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.MyTaskButton);
        TrackPattrollerButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.TrackPattrollerButton);
        MyPlannedTaskButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.MyPlannedTaskButton);
        AttendanceButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.AttendanceButton);
        ChatButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.ChatButton);
        taskAssignmentRL = (RelativeLayout) frtCallBackForIdFind.view(R.id.taskAssignmentRL);
        SosButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.SosButton);
        LogOutButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.LogOutButton);
        LeaveManagementButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.LeaveManagementButton);
        othersButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.othersButton);
        SelfCheckInButton = (RelativeLayout) frtCallBackForIdFind.view(R.id.SelfCheckInButton);
        SelfCheckInButton.setVisibility(View.GONE);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtNavigationActivity);
        frtUtility.settingStatusBarColor(frtNavigationActivity, R.color.colorPrimary);
        handler = new Handler();
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtNavigationActivity);
        broadcasting.setCallbackBroadcast(frtNavigationActivity);
        frtwebapi = new FRTWEBAPI();
        frtCallbackString = frtNavigationActivity;
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtNavigationActivity);
        frtConstants = new FRTConstants();
        frtApp = (FRTApp) getApplication();
        frtLocationDb = new FRTLocationDb(frtNavigationActivity);
        pd = new ProgressDialog(PTRNavigationScreenActivity.this);
        manager_ll = (LinearLayout) frtCallBackForIdFind.view(R.id.manager_ll);
        patroller_ll = (LinearLayout) frtCallBackForIdFind.view(R.id.patroller_ll);
        both_ll = (LinearLayout) frtCallBackForIdFind.view(R.id.both_ll);
//        getRefereshTokenData();
        setUpUi();
    }

    private void setUpUi() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (frtResponseUserDetailModel != null && frtResponseUserDetailModel.getRole_name() != null
                        && frtResponseUserDetailModel.getRole_name().equals("Manager")) {
                    TrackPattrollerButton.setVisibility(View.VISIBLE);
                    MyTaskButton.setVisibility(View.GONE);
                    MyPlannedTaskButton.setVisibility(View.GONE);
                    AttendanceButton.setVisibility(View.GONE);
                    LogOutButton.setVisibility(View.VISIBLE);
                    LeaveManagementButton.setVisibility(View.VISIBLE);
                    othersButton.setVisibility(View.VISIBLE);
                    SelfCheckInButton.setVisibility(View.GONE);
                    SosButton.setVisibility(View.VISIBLE);
                    ChatButton.setVisibility(View.VISIBLE);
                    taskAssignmentRL.setVisibility(View.VISIBLE);
                    manager_ll.setVisibility(View.VISIBLE);
                    patroller_ll.setVisibility(View.GONE);
                    both_ll.setVisibility(View.GONE);
                } else if (frtResponseUserDetailModel != null && frtResponseUserDetailModel.getRole_name() != null
                        && frtResponseUserDetailModel.getRole_name().equals("FRT")) {
                   ChatButton.setVisibility(View.VISIBLE);
                    TrackPattrollerButton.setVisibility(View.GONE);
                    MyTaskButton.setVisibility(View.VISIBLE);
                    MyPlannedTaskButton.setVisibility(View.INVISIBLE);
                    AttendanceButton.setVisibility(View.VISIBLE);
                    LogOutButton.setVisibility(View.VISIBLE);
                    LeaveManagementButton.setVisibility(View.VISIBLE);
                    SelfCheckInButton.setVisibility(View.GONE);
                    SosButton.setVisibility(View.VISIBLE);
                    othersButton.setVisibility(View.VISIBLE);
                    taskAssignmentRL.setVisibility(View.GONE);
                    manager_ll.setVisibility(View.GONE);
                    patroller_ll.setVisibility(View.GONE);
                    both_ll.setVisibility(View.VISIBLE);
                } else {

                    ChatButton.setVisibility(View.VISIBLE);
                    TrackPattrollerButton.setVisibility(View.GONE);
                    MyTaskButton.setVisibility(View.VISIBLE);
                    MyPlannedTaskButton.setVisibility(View.VISIBLE);
                    AttendanceButton.setVisibility(View.VISIBLE);
                    LogOutButton.setVisibility(View.VISIBLE);
                    LeaveManagementButton.setVisibility(View.VISIBLE);
                    SelfCheckInButton.setVisibility(View.VISIBLE);
                    SosButton.setVisibility(View.VISIBLE);
                    othersButton.setVisibility(View.VISIBLE);
                    taskAssignmentRL.setVisibility(View.GONE);
                    manager_ll.setVisibility(View.GONE);
                    patroller_ll.setVisibility(View.VISIBLE);
                    both_ll.setVisibility(View.VISIBLE);
                }
            }
        }, 1800);
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = broadcasting.getBroadcasting();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtNavigationActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtNavigationActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtNavigationActivity);
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
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                        frtSharePrefUtil.setString(getString(R.string.token_key), jsonObject.optString(getString(R.string.token_key)));
                        frtSharePrefUtil.setString(getString(R.string.tokentype_key), jsonObject.optString(getString(R.string.tokentype_key)));
                        frtSharePrefUtil.setString(getString(R.string.tokenexpiretime_key), String.valueOf(jsonObject.optInt(getString(R.string.tokenexpiretime_key), 0)));
                        frtSharePrefUtil.setString(getString(R.string.refresh_token_key), jsonObject.optString(getString(R.string.refresh_token_key)));
                        frtSharePrefUtil.setString(getString(R.string.globalsettings_key), jsonObject.optString(getString(R.string.globalsettings_key)));
                        frtSharePrefUtil.setString("currenttime", frtUtility.getCurrentTime());
                        getUserDetail();
                    }
                } catch (JSONException ignored) {
                }
            } else if (type.equals("getuserdetail")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jo = new JSONObject(String.valueOf(jsonObject.optJSONObject("results")));
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                        frtResponseUserDetailModel = new PTRResponseUserDetailModel();
                        frtResponseUserDetailModel.setUser_id(jo.optInt("user_id"));
                        frtResponseUserDetailModel.setUser_name(jo.optString("user_name"));
                        frtResponseUserDetailModel.setFull_name(jo.optString("full_name"));
                        frtResponseUserDetailModel.setEmail_id(jo.optString("email_id"));
                        frtResponseUserDetailModel.setIs_active(jo.optString("is_active"));
                        frtResponseUserDetailModel.setManager_id(jo.optString("manager_id"));
                        frtResponseUserDetailModel.setManager_name(jo.optString("manager_name"));
                        frtResponseUserDetailModel.setUser_img(jo.optString("user_img"));
                        frtResponseUserDetailModel.setRole_id(jo.optInt("role_id"));
                        frtResponseUserDetailModel.setPhone(jo.optString("manager_phone"));
                        frtResponseUserDetailModel.setCreatedBy(jo.optString("createdBy"));
                        frtResponseUserDetailModel.setModifiedBy(jo.optString("modifiedBy"));
                        frtResponseUserDetailModel.setCreated_on(jo.optString("created_on"));
                        frtResponseUserDetailModel.setModified_on(jo.optString("modified_on"));
                        frtResponseUserDetailModel.setRole_name(jo.optString("role_name"));


                        if (!TextUtils.isEmpty(String.valueOf(frtResponseUserDetailModel.getUser_id())))
                            frtSharePrefUtil.setString(getString(R.string.userkey), AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), (String.valueOf(frtResponseUserDetailModel.getUser_id())).getBytes("UTF-16LE")));

                        if (!TextUtils.isEmpty(String.valueOf(frtResponseUserDetailModel.getManager_id())))

                            frtSharePrefUtil.setString(getString(R.string.managerkey), AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), (String.valueOf(frtResponseUserDetailModel.getManager_id())).getBytes("UTF-16LE")));

                        if (!TextUtils.isEmpty(frtResponseUserDetailModel.getRole_name())) {
                            frtSharePrefUtil.setString(getString(R.string.userrole), frtResponseUserDetailModel.getRole_name());
                        }
                        if (!TextUtils.isEmpty(frtResponseUserDetailModel.getPhone()))

                            frtSharePrefUtil.setString(getString(R.string.manager_phone), AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), frtResponseUserDetailModel.getPhone().getBytes("UTF-16LE")));

                        if (!TextUtils.isEmpty(String.valueOf(frtResponseUserDetailModel.getRole_id())))

                            frtSharePrefUtil.setString(getString(R.string.role_id), AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), (String.valueOf(frtResponseUserDetailModel.getRole_id()).getBytes("UTF-16LE"))));


                        if (!TextUtils.isEmpty(frtResponseUserDetailModel.getRole_name()))
                            frtSharePrefUtil.setString(getString(R.string.role_name), frtResponseUserDetailModel.getRole_name());

                        if (!TextUtils.isEmpty(frtResponseUserDetailModel.getUser_name()))

                            frtSharePrefUtil.setString(getString(R.string.username_shared), AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), frtResponseUserDetailModel.getUser_name().getBytes("UTF-16LE")));

                        if (!jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase(getString(R.string.req_denied))) {
                            Intent intent = new Intent(frtNavigationActivity, PTRNavigationScreenActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                        } else {
                            frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(android.R.id.content));
                        }
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {

                        frtUtility.goToLogin(getString(R.string.req_denied));

                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {

//                        frtUtility.goToLogin(getString(R.string.session_exp));
                        getRefereshTokenData();
                        getUserDetail();
                    } else {
                        frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), AttendanceButton);
                    }
                } catch (JSONException | UnsupportedEncodingException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type.equals("logout")) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString(getString(R.string.status_key)).equals("OK")) {
                        frtSharePrefUtil.setBoolean(frtConstants.IS_LOGGED_IN, false);
                        frtUtility.clearPrerences();
                        frtSharePrefUtil.setBoolean("isLogout", true);
                        finish();
                        startActivity(new Intent(frtNavigationActivity, FRTLoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                    }
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase(getString(R.string.req_denied))) {
                        frtSharePrefUtil.setBoolean(frtConstants.IS_LOGGED_IN, false);
                        frtUtility.clearPrerences();
                        finish();
                        startActivity(new Intent(frtNavigationActivity, FRTLoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                        //todo
                        getRefereshTokenData();
                        /*frtUtility.goToLogin(getString(R.string.session_exp));
                        frtUtility.clearPrerences();*/

                    }
                } catch (JSONException ignored) {
                }
            }
            loader.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    private void getUserDetail() {
        @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestUserDetailModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpUserDetailModel());
        frtAsyncCommon.setContext(frtNavigationActivity);
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_USER_DETAIL), "getuserdetail");
        frtAsyncCommon.setFetchDataCallBack(frtNavigationActivity);
    }

    private PTRRequestUserDetailModel setUpUserDetailModel() {
        PTRRequestUserDetailModel cqRequestPauseReasonModel = new PTRRequestUserDetailModel();
        cqRequestPauseReasonModel.setUserName(frtSharePrefUtil.getString(frtConstants.USERNAME_KEY));
        return cqRequestPauseReasonModel;
    }

    /**
     * This method is used for setting the listener on views.
     */
    @Override
    public void commonListeners() {
        MyTaskButton.setOnClickListener(onClickMyTask);
        MyPlannedTaskButton.setOnClickListener(onClickMyPlannedTask);
        AttendanceButton.setOnClickListener(onClickAttendence);
        ChatButton.setOnClickListener(onClickChat);
        taskAssignmentRL.setOnClickListener(onClickTaskAssignement);
        SosButton.setOnClickListener(onClickSos);
        LogOutButton.setOnClickListener(onClickLogout);
        LeaveManagementButton.setOnClickListener(onClickLeaveManagement);

        othersButton.setOnClickListener(onClickOthers);
        SelfCheckInButton.setOnClickListener(onSelfCheckInTask);
        TrackPattrollerButton.setOnClickListener(onClickTrackPattroller);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("TAG", "Firebase reg id: " + regId);
    }

    @Override
    public void onBackPressed() {
        frtUtility.logInfo(frtNavigationActivity, "Clicked --> Back", frtConstants);
        frtUtility.exitApp();
    }

    @Override
    public void getBroadcast(boolean isBroadcasting) {
        if (isBroadcasting) {
            frtUtility.setSnackBar(getString(R.string.nointernet), AttendanceButton);
            frtUtility.hideDialog();
        } else {
            getRefereshTokenData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

    }


    @Override
    public void available(boolean isavailable) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @SuppressWarnings("deprecation")
    private void doLogout() {
        @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestModelLogout> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpLogoutModel());
        frtUtility.show(pd);
        frtAsyncCommon.setContext(frtNavigationActivity);
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGOUT), "logout");
        frtAsyncCommon.setFetchDataCallBack(frtNavigationActivity);
    }

    private PTRRequestModelLogout setUpLogoutModel() {
        PTRRequestModelLogout cqRequestModelLogout = new PTRRequestModelLogout();
        try {
            cqRequestModelLogout.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        } catch (Exception ignored) {
        }
        return cqRequestModelLogout;
    }


    String checkRouting;

    @Override
    public void setString(String name) {
        checkRouting = name;
    }

}