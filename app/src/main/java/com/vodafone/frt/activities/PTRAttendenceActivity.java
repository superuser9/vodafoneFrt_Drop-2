package com.vodafone.frt.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.adapters.FRTAttendanceAdapter;
import com.vodafone.frt.adapters.PTRAttendanceAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.fragments.ApplyLeaveDialogFragment;
import com.vodafone.frt.models.FRTRequestFrtAttendanceModel;
import com.vodafone.frt.models.FRTResponseAttendanceModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestAttendanceModel;
import com.vodafone.frt.models.PTRResponseAttendanceModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vishal on 9/12/17
 */
public class PTRAttendenceActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast, PTRAttendanceAdapter.SelectionViewAttendanceListener ,FRTAttendanceAdapter.SelectionViewAttendanceListener{
    private boolean checknet = false;
    private BroadcastReceiver broadcastReceiver;
    private PTRAttendanceAdapter adapter;
    private FRTAttendanceAdapter frtAttendanceAdapter;
    private FRTBroadcasting broadcasting;
    private final PTRAttendenceActivity frtAttendenceActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTUtility frtUtility;
    private int mYear, mMonth, mDay;
    private ImageView ivserchattend;
    private LinearLayout ivback;
    private ListView attendanceListView;
    private List<PTRResponseAttendanceModel> modelList;
    private List<FRTResponseAttendanceModel> frtResponseAttendanceModelList;
    private FRTTextviewTrebuchetMS staartdate;
    private FRTTextviewTrebuchetMS enddate;
    private FRTTextviewTrebuchetMS norecord;
    private FRTTextviewTrebuchetMS loader;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    private ImageView addApplyLeaveImg;
    private String userIdEncripted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtattendence);
        callBackSetUp();
    }
    // comments

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtAttendenceActivity;
        frtCallBackForIdFind = frtAttendenceActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtAttendenceActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initAllViews() {

        addApplyLeaveImg =(ImageView)frtCallBackForIdFind.view(R.id.addApplyLeaveImg);
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        attendanceListView = (ListView) frtCallBackForIdFind.view(R.id.frtaddressListView);
        staartdate = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.staartdate);
        enddate = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.enddate);
        FRTTextviewTrebuchetMS staartdateheader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.staartdateheader);
        FRTTextviewTrebuchetMS enddateheader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.enddateheader);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        ivserchattend = (ImageView) frtCallBackForIdFind.view(R.id.ivserchattend);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtAttendenceActivity);
        frtUtility.settingStatusBarColor(frtAttendenceActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtAttendenceActivity);
        broadcasting.setCallbackBroadcast(frtAttendenceActivity);
        adapter = new PTRAttendanceAdapter(frtAttendenceActivity);
        frtAttendanceAdapter = new FRTAttendanceAdapter(frtAttendenceActivity);
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtAttendenceActivity);
        staartdateheader.setText(Html.fromHtml(getString(R.string.startdate)));
        enddateheader.setText(Html.fromHtml(getString(R.string.enddate)));
        initdata();
        if (frtSharePrefUtil.getString(getString(R.string.role_name)).equals("FRT")){
            getFRTAttendance();
        }
        else {
            getAttendance();
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
        staartdate.setText(start);
        enddate.setText(end);
    }

    //code by Ashutosh
    @SuppressWarnings("deprecation")
    private void getAttendance() {
        try {
            if (frtConnectivityReceiver.isConnected(frtAttendenceActivity)) {
                if (frtUtility.isTwoDatesDifferenceLessthanThirtyOne(staartdate.getText().toString(), enddate.getText().toString())) {
                    FRTAsyncCommon<PTRRequestAttendanceModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
                    frtAsyncCommon.setFrtModel(setUpAttendanceModel());
                    frtAsyncCommon.setContext(frtAttendenceActivity);
                    frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.ATTENDANCE), "getAttendance");
                    frtAsyncCommon.setFetchDataCallBack(frtAttendenceActivity);
                } else {
                    frtUtility.setSnackBar(getString(R.string.max31days), attendanceListView);
                }
            }
            else {
                frtUtility.setSnackBar(getString(R.string.nointernet), attendanceListView);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG","EXCEPTION==" + e.getMessage());
        }
    }


    // code by ajay
    private void getFRTAttendance() {
        try {
            if (frtConnectivityReceiver.isConnected(frtAttendenceActivity)) {
                if (frtUtility.isTwoDatesDifferenceLessthanThirtyOne(staartdate.getText().toString(), enddate.getText().toString())) {
                    FRTAsyncCommon<FRTRequestFrtAttendanceModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
                    frtAsyncCommon.setFrtModel(setUpAttendanceModelForFRT());
                    frtAsyncCommon.setContext(frtAttendenceActivity);
                    frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.FRT_ATTENDANCE), "GetFRTAttendance");
                    frtAsyncCommon.setFetchDataCallBack(frtAttendenceActivity);
                } else {
                    frtUtility.setSnackBar(getString(R.string.max31days), attendanceListView);
                }
            }
            else {
                frtUtility.setSnackBar(getString(R.string.nointernet), attendanceListView);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG","EXCEPTION==" + e.getMessage());
        }
    }



    // for Patroller attendance model
    private PTRRequestAttendanceModel setUpAttendanceModel() {
        PTRRequestAttendanceModel cqRequestPauseReasonModel = new PTRRequestAttendanceModel();
        //todo:set petrollerId
        try {
            userIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(userIdEncripted))
            cqRequestPauseReasonModel.setPatrollerId(Integer.parseInt(userIdEncripted));
        cqRequestPauseReasonModel.setFromDate(staartdate.getText().toString());
        cqRequestPauseReasonModel.setToDate(enddate.getText().toString());
        return cqRequestPauseReasonModel;
    }

    // for FRT Attendance model
    private FRTRequestFrtAttendanceModel setUpAttendanceModelForFRT(){
        FRTRequestFrtAttendanceModel frtRequestFrtAttendanceModel = new FRTRequestFrtAttendanceModel();
        try {
            userIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(userIdEncripted))
            frtRequestFrtAttendanceModel.setFrtId(Integer.parseInt(userIdEncripted));
        frtRequestFrtAttendanceModel.setFromDate(staartdate.getText().toString());
        frtRequestFrtAttendanceModel.setToDate(enddate.getText().toString());

        return frtRequestFrtAttendanceModel;
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
            } else if (type.equals("getAttendance")) {
                modelList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.optJSONArray("results");
                    if (jsonObject.optString(getString(R.string.status_key)).equals("OK") && jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            PTRResponseAttendanceModel frtResponseAttendanceModel = new PTRResponseAttendanceModel();
                            frtResponseAttendanceModel.setRoute_assigned_id(jsonObject1.optInt("route_assigned_id"));
                            frtResponseAttendanceModel.setRoute__id(jsonObject1.optInt("route_id"));
                            frtResponseAttendanceModel.setRoute_ref_id(jsonObject1.optInt("route_ref_id"));
                            if (!jsonObject1.optString("patroller_name").equals("null"))
                                frtResponseAttendanceModel.setPatroller_name(jsonObject1.optString("patroller_name"));
                            else
                                frtResponseAttendanceModel.setPatroller_name("");
                            frtResponseAttendanceModel.setAttendance_date(jsonObject1.optString("attendance_date"));
                            frtResponseAttendanceModel.setRoute_name(jsonObject1.optString("route_name"));
                            frtResponseAttendanceModel.setPlanned_start_time(jsonObject1.optString("planned_start_time"));
                            frtResponseAttendanceModel.setPlanned_end_time(jsonObject1.optString("planned_end_time"));
                            frtResponseAttendanceModel.setActual_start_time(jsonObject1.optString("actual_start_time"));
                            frtResponseAttendanceModel.setActual_end_time(jsonObject1.optString("actual_end_time"));
                            frtResponseAttendanceModel.setPlanned_time_diff(jsonObject1.optString("planned_time_diff"));
                            frtResponseAttendanceModel.setTime_taken(jsonObject1.optString("time_taken"));
                            modelList.add(frtResponseAttendanceModel);
                        }
                        norecord.setVisibility(View.GONE);
                        attendanceListView.setVisibility(View.VISIBLE);
                        setAdapter();
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                    /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else {
                        norecord.setText(getString(R.string.nopetrollerattendence));
                        norecord.setVisibility(View.VISIBLE);
                        attendanceListView.setVisibility(View.GONE);
                    }
                    loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                }
            }
            else if (type.equalsIgnoreCase("GetFRTAttendance")){
                frtResponseAttendanceModelList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.optJSONArray("results");
                    if (jsonObject.optString(getString(R.string.status_key)).equals("OK") && jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            FRTResponseAttendanceModel frtResponseAttendanceModel = new FRTResponseAttendanceModel();
                            frtResponseAttendanceModel.setTask_tracking_id(jsonObject1.optInt("task_tracking_id"));
                            frtResponseAttendanceModel.setIssue_id(jsonObject1.optInt("issue_id"));
                            if (!jsonObject1.optString("frt_full_name").equals("null"))
                                frtResponseAttendanceModel.setFrt_full_name(jsonObject1.optString("frt_full_name"));
                            else
                                frtResponseAttendanceModel.setFrt_full_name("");
                            frtResponseAttendanceModel.setAttendance_date(jsonObject1.optString("attendance_date"));
                            frtResponseAttendanceModel.setIssue_desc(jsonObject1.optString("issue_desc"));
                            frtResponseAttendanceModel.setCheckin_time(jsonObject1.optString("checkin_time"));
                            frtResponseAttendanceModel.setCheckout_time(jsonObject1.optString("checkout_time"));
                            frtResponseAttendanceModel.setTime_taken(jsonObject1.optString("time_taken"));
                            frtResponseAttendanceModel.setLatitude(jsonObject1.optDouble("latitude"));
                            frtResponseAttendanceModel.setLongitude(jsonObject1.optDouble("longitude"));
                            frtResponseAttendanceModel.setStatus(jsonObject1.optString("status"));
                            frtResponseAttendanceModelList.add(frtResponseAttendanceModel);
                        }
                        norecord.setVisibility(View.GONE);
                        attendanceListView.setVisibility(View.VISIBLE);
                        setAdapterForFRT();
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                        getRefereshTokenData();
                    }  else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {
                        norecord.setText(jsonObject.optString("error_message").toString());
                        norecord.setVisibility(View.VISIBLE);
                        attendanceListView.setVisibility(View.GONE);
                        // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                       // Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        norecord.setText(getString(R.string.nopetrollerattendence));
                        norecord.setVisibility(View.VISIBLE);
                        attendanceListView.setVisibility(View.GONE);
                    }
                    loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    loader.setVisibility(View.GONE);
                }
            }

        } else {
            frtUtility.setSnackBar(getString(R.string.notgetting_responseFromServer), attendanceListView);
        }
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        ivback.setOnClickListener(frtBackClick);
        staartdate.setOnClickListener(frtStartCLick);
        enddate.setOnClickListener(frtEndCLick);
        ivserchattend.setOnClickListener(frtSearchCLick);
        addApplyLeaveImg.setOnClickListener(frtApplyLeaveClick);
    }

    private final View.OnClickListener frtApplyLeaveClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment newFragment = ApplyLeaveDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
    };

    private final View.OnClickListener frtSearchCLick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           // getAttendance();
            if (frtSharePrefUtil.getString(getString(R.string.role_name)).equals("FRT")){
                getFRTAttendance();
            }
            else {
                getAttendance();
            }

        }
    };

    private final View.OnClickListener frtStartCLick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            // Launch Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(frtAttendenceActivity, onFromDateSet, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }
    };
    private final DatePickerDialog.OnDateSetListener onFromDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            String fromdate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            try {
                staartdate.setText(frtUtility.formatDate(fromdate));
            } catch (Exception ex) {
            }
        }
    };
    private final View.OnClickListener frtEndCLick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            // Launch Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(frtAttendenceActivity, onEndDateSet, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }
    };
    private final DatePickerDialog.OnDateSetListener onEndDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            String enddates = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            try {
                enddate.setText(frtUtility.formatDate(enddates));
            } catch (Exception ex) {
            }
        }
    };
    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtAttendenceActivity)) {
            super.onBackPressed();
            Intent intent = new Intent(frtAttendenceActivity, PTRNavigationScreenActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }

    private void setAdapter() {
        adapter.setDataSet(modelList);
        adapter.setSelectionListener(frtAttendenceActivity);
        adapter.notifyDataSetChanged();
        attendanceListView.setAdapter(adapter);
    }
    private void setAdapterForFRT(){
        frtAttendanceAdapter.setDataSet(frtResponseAttendanceModelList);
        frtAttendanceAdapter.setSelectionListener(frtAttendenceActivity);
        frtAttendanceAdapter.notifyDataSetChanged();
        attendanceListView.setAdapter(frtAttendanceAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                getAttendance();
            checknet = false;
        }
    }

    @Override
    public void onClickItem(int position) {

        if (frtSharePrefUtil.getString(getString(R.string.role_name)).equals("FRT")){
            Intent intent = new Intent(frtAttendenceActivity, FRTRouteControlerTaskActivityFRT.class);
            intent.putExtra("routetitle", frtResponseAttendanceModelList.get(position).getIssue_desc());
           // intent.putExtra("tasktrackingid", frtResponseAttendanceModelList.get(position).getTask_tracking_id());
           // intent.putExtra("issueid", frtResponseAttendanceModelList.get(position).getIssue_id());
            intent.putExtra("frtName", frtResponseAttendanceModelList.get(position).getFrt_full_name());
            intent.putExtra("latitude", frtResponseAttendanceModelList.get(position).getLatitude());
            intent.putExtra("longitude", frtResponseAttendanceModelList.get(position).getLongitude());
            intent.putExtra("status", frtResponseAttendanceModelList.get(position).getStatus());
            intent.putExtra("case", "AttendanceFrt");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        }else {

            Intent intent = new Intent(frtAttendenceActivity, PTRRouteControllerTaskActivity.class);
            intent.putExtra("routetitle", modelList.get(position).getRoute_name());
            intent.putExtra("routeassignedId", modelList.get(position).getRoute_assigned_id());
            intent.putExtra("routeId", modelList.get(position).getRoute__id());
            intent.putExtra("routeRefId", modelList.get(position).getRoute_ref_id());
            intent.putExtra("case", "Attendance");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        }
    }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtAttendenceActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtAttendenceActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtAttendenceActivity);
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
