package com.vodafone.frt.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRAttendanceAdapter;
import com.vodafone.frt.adapters.FRTCheckInAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRGETSelfCheckInRequestModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRMainSelfCheckModel;
import com.vodafone.frt.models.PTRResponseSelfCheckInModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.fragments.CheckInDialogFragment;
import com.vodafone.frt.services.GPSTracker;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PTRMyCheckInActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast, PTRAttendanceAdapter.SelectionViewAttendanceListener, CheckInDialogFragment.EditDialogListener {
    private boolean checknet = false;
    private BroadcastReceiver broadcastReceiver;
    private FRTCheckInAdapter adapter;
    private FRTBroadcasting broadcasting;
    private PTRMyCheckInActivity frtMyCheckInActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTUtility frtUtility;
    private int mYear, mMonth, mDay;
    private ImageView ivserchattend;
    private LinearLayout ivbackCheckIn;
    private FRTTextviewTrebuchetMS norecord;
    private FRTTextviewTrebuchetMS loader;
    private FRTWEBAPI frtwebapi;
    private ImageView addCheckInImg;
    private FRTSharePrefUtil frtSharePrefUtil;
    private ListView frtcheckInListView;
    private int userUserid;
    private GPSTracker gps;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtmy_check_in);
        callBackSetUp();
    }


    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtMyCheckInActivity;
        frtCallBackForIdFind = frtMyCheckInActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtMyCheckInActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }



    @SuppressWarnings("deprecation")
    @Override
    public void initAllViews() {
       // frtCnnectivityReceiver = new FRTConnectivityReceiver();
        ivbackCheckIn = (LinearLayout) frtCallBackForIdFind.view(R.id.ivbackCheckIn);
        addCheckInImg =(ImageView)frtCallBackForIdFind.view(R.id.addCheckInImg);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        frtcheckInListView = (ListView)frtCallBackForIdFind.view(R.id.frtcheckInListView);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtMyCheckInActivity);
        frtUtility.settingStatusBarColor(frtMyCheckInActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtMyCheckInActivity);
        broadcasting.setCallbackBroadcast(frtMyCheckInActivity);
        adapter = new FRTCheckInAdapter(frtMyCheckInActivity);
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtMyCheckInActivity);
       initdata();
       try {
           userUserid = Integer.parseInt( AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT)));
       } catch (Exception e){
           e.printStackTrace();
           Log.d(this.getClass().getName(),"EXCEPTION== NULL_KEY" + e.getMessage());
       }
    }


    private void initdata() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        String end = df1.format(c.getTime());
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.DATE, -7);
        String start = df1.format(c.getTime());
    }

    @Override
    public void commonListeners() {
        ivbackCheckIn.setOnClickListener(frtBackClick);
        addCheckInImg.setOnClickListener(imageAddFormCLick);

    }

    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private final View.OnClickListener imageAddFormCLick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (frtConnectivityReceiver.isConnected(frtMyCheckInActivity)) {
                if (latitude != 0.0 && longitude != 0.00) {
                    DialogFragment newFragment = CheckInDialogFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("LATITUDE_PASS", latitude);
                    bundle.putDouble("LONGITUDE_PASS", longitude);
                    newFragment.setArguments(bundle);
                    newFragment.show(getFragmentManager(), "dialog");
                    // Intent intent = new Intent(PTRMyCheckInActivity.this, CheckInDialogActivity.class);
                    //startActivity(intent);
                } else {
                    Toast.makeText(frtMyCheckInActivity, getString(R.string.notGettingLocation), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), PTRNavigationScreenActivity.class));
                    finish();
                }
            }
            else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }

        }
    };

    @Override
    public View view(int id) {
         return findViewById(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PTRMyCheckInActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

        } else {
            // Toast.makeText(this, "You need have granted permission", Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(this, PTRMyCheckInActivity.this);
            // Check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // Log.d("CHECK_IN","LOC_CHECKIN"+ latitude);
                // \n is for new line
                //   Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }
        getSelfCheckInRequest();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Intent previewMessage = new Intent(PTRMyCheckInActivity.this, PTRMyCheckInActivity.class);
        startActivity(previewMessage);
        //TabGroupActivity parentActivity = (TabGroupActivity)getParent();
        //parentActivity.startChildActivity("StampiiStore", previewMessage);
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gps != null) {
            gps.stopUsingGPS();
        }
        //Log.d("TAG","LOCAT_DESTROY"+ latitude);
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
    protected void onDestroy() {
        super.onDestroy();
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
    public void onClickItem(int position) {
    }



    private void getSelfCheckInRequest(){
        if (frtConnectivityReceiver.isConnected(getApplicationContext())) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRGETSelfCheckInRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtMyCheckInActivity);
            frtAsyncCommon.setFrtModel(setUpCheckInModel());

            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GETSELF_CHECKIN_REQUEST), "getCheckInRequest");
            frtAsyncCommon.setFetchDataCallBack(frtMyCheckInActivity);

        }else {
           // frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
            frtUtility.setSnackBar(getString(R.string.nointernet), ivbackCheckIn);
           // Toast.makeText(frtMyCheckInActivity, "You are not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }



    private PTRGETSelfCheckInRequestModel setUpCheckInModel() {
        PTRGETSelfCheckInRequestModel cqRequestPauseReasonModel = new PTRGETSelfCheckInRequestModel();
        //todo:set petrollerId
        if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.userkey))))
            cqRequestPauseReasonModel.setUserid(userUserid);
        return cqRequestPauseReasonModel;
    }

    @Override
    public void getAsyncData(String response, String type) {
        if (response != null){

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
            } else if (type.equals("getCheckInRequest")) {
                try {
                    Log.d(this.getClass().getName(), "RESPONSE_CHECKIN===" + response);
                    PTRMainSelfCheckModel frtMainSelfCheckModel = new Gson().fromJson(response, PTRMainSelfCheckModel.class);
                    if (frtMainSelfCheckModel.getResults().size() > 0) {
                        norecord.setVisibility(View.GONE);
                        frtcheckInListView.setVisibility(View.VISIBLE);
                        setAdapter(frtMainSelfCheckModel.getResults());

                    } else if (frtMainSelfCheckModel.getStatus().equalsIgnoreCase("UNKNOWN_ERROR")){
                        frtUtility.setSnackBar(frtMainSelfCheckModel.getError_message(), findViewById(R.id.header));
                    }
                    else if (frtMainSelfCheckModel.getStatus().equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (frtMainSelfCheckModel.getStatus().equals(getString(R.string.session_exp))) {
               /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else {
                        norecord.setText(getString(R.string.nopetrollercheckIn));
                        norecord.setVisibility(View.VISIBLE);
                        frtcheckInListView.setVisibility(View.GONE);
                    }
                    loader.setVisibility(View.GONE);
                } catch (IllegalStateException | JsonSyntaxException exception) {
                    exception.printStackTrace();
                }
            }
        }else {
            frtUtility.setSnackBar(getString(R.string.didNotResponseFromServer), frtcheckInListView);
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

    private void setAdapter(List<PTRResponseSelfCheckInModel> results) {
        adapter.setDataSet(results);
        adapter.setSelectionListener(frtMyCheckInActivity);
        adapter.notifyDataSetChanged();
        frtcheckInListView.setAdapter(adapter);
    }

    @Override
    public void updateResult(String route,
                             String startDate,
                             String endDate,
                             String startTime, String endTime, String remarks) {
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    gps = new GPSTracker(this, PTRMyCheckInActivity.this);
                    // Check if GPS enabled
                    if (gps.canGetLocation()) {
                         latitude = gps.getLatitude();
                         longitude = gps.getLongitude();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }
                } /*else {
                }*/
                return;
            }
        }
    }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtMyCheckInActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtMyCheckInActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtMyCheckInActivity);
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
