package com.vodafone.frt.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRUsersListAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestScheduleRouteModel;
import com.vodafone.frt.models.PTRResponseScheduleRouteModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PTRUsersListActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast, PTRUsersListAdapter.SelectionViewAttendanceListener {
    private BroadcastReceiver broadcastReceiver;
    private PTRUsersListAdapter adapter;
    private FRTBroadcasting broadcasting;
    private final PTRUsersListActivity frtUsersListActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTUtility frtUtility;
    private LinearLayout ivback;
    private ListView attendanceListView;
    private List<PTRResponseScheduleRouteModel> modelList;
    private FRTTextviewTrebuchetMS title;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTTextviewTrebuchetMS norecord, loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtusers_list);
        callBackSetUp();
    }

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtUsersListActivity;
        frtCallBackForIdFind = frtUsersListActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtUsersListActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void initAllViews() {
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        attendanceListView = (ListView) frtCallBackForIdFind.view(R.id.frtscheduledrouteListView);
        title = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.title);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtUsersListActivity);
        frtUtility.settingStatusBarColor(frtUsersListActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtUsersListActivity);
        broadcasting.setCallbackBroadcast(frtUsersListActivity);
        adapter = new PTRUsersListAdapter(frtUsersListActivity);
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtUsersListActivity);
        PTRUsersListAdapter.SelectionViewAttendanceListener frtSelectionViewAttendanceListener = frtUsersListActivity;
        getSceduleRoute();
    }


    //code by Ashutosh
    @SuppressWarnings("deprecation")
    private void getSceduleRoute() {
        @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestScheduleRouteModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpScheduledRouteModel());
        frtAsyncCommon.setContext(frtUsersListActivity);
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.SCHEDULED_ROUTE), "getscheduledroute");
        frtAsyncCommon.setFetchDataCallBack(frtUsersListActivity);
    }

    private PTRRequestScheduleRouteModel setUpScheduledRouteModel() {
        PTRRequestScheduleRouteModel cqRequestPauseReasonModel = new PTRRequestScheduleRouteModel();
        //todo:set userId
        try {
            cqRequestPauseReasonModel.setuserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        } catch (Exception ignored) {
        }
        return cqRequestPauseReasonModel;
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
            } else if (type.equals("getscheduledroute")) {
            modelList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jo = new JSONArray(String.valueOf(jsonObject.optJSONArray("results")));
                for (int i = 0; i < jo.length(); i++) {
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                        PTRResponseScheduleRouteModel frtResponseScheduleRouteModel = new PTRResponseScheduleRouteModel();
                        frtResponseScheduleRouteModel.setScheduled_id(jo.optJSONObject(i).optInt("scheduled_id"));
                        frtResponseScheduleRouteModel.setRoute_id(jo.optJSONObject(i).optInt("route_id"));
                        frtResponseScheduleRouteModel.setRoute_ref_id(jo.optJSONObject(i).optInt("route_ref_id"));
                        frtResponseScheduleRouteModel.setRoute_name(jo.optJSONObject(i).optString("route_name"));
                        frtResponseScheduleRouteModel.setPatroller_id(jo.optJSONObject(i).optInt("patroller_id"));
                        frtResponseScheduleRouteModel.setPatroller_name(jo.optJSONObject(i).optString("patroller_name"));
                        frtResponseScheduleRouteModel.setAssignment_type(jo.optJSONObject(i).optString("assignment_type"));
                        frtResponseScheduleRouteModel.setWorking_days(jo.optJSONObject(i).optString("working_days"));
                        frtResponseScheduleRouteModel.setDay_of_month(jo.optJSONObject(i).optInt("day_of_month"));
                        frtResponseScheduleRouteModel.setStart_date(jo.optJSONObject(i).optString("start_date"));
                        frtResponseScheduleRouteModel.setStart_time(jo.optJSONObject(i).optString("start_time"));
                        frtResponseScheduleRouteModel.setEnd_date(jo.optJSONObject(i).optString("end_date"));
                        frtResponseScheduleRouteModel.setEnd_time(jo.optJSONObject(i).optString("end_time"));
                        frtResponseScheduleRouteModel.setIs_active(jo.optJSONObject(i).optString("is_active"));
                        frtResponseScheduleRouteModel.setCreated_by_name(jo.optJSONObject(i).optString("created_by_name"));
                        frtResponseScheduleRouteModel.setCreated_on(jo.optJSONObject(i).optString("created_on"));
                        frtResponseScheduleRouteModel.setModified_by_name(jo.optJSONObject(i).optString("modified_by_name"));
                        frtResponseScheduleRouteModel.setModified_on(jo.optJSONObject(i).optString("modified_on"));
                        modelList.add(frtResponseScheduleRouteModel);
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                       /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else {
                        frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), attendanceListView);
                    }
                }
                if (jsonObject.optString("status").equals("OK") && modelList.size() > 0) {
                    norecord.setVisibility(View.GONE);
                    attendanceListView.setVisibility(View.VISIBLE);
                } else {
                    norecord.setVisibility(View.VISIBLE);
                    attendanceListView.setVisibility(View.GONE);
                }
                loader.setVisibility(View.GONE);
            } catch (JSONException ignored) {
            }
            setAdapter();
            setItemCount();
        }
        } else {
            frtUtility.setSnackBar(getString(R.string.servernotResponding), attendanceListView);
        }
    }

    private void setItemCount() {
        if (modelList != null)
            title.setText("Users List" /*+ " [" + modelList.size() + "]"*/);
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        ivback.setOnClickListener(frtBackClick);
    }


    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtUsersListActivity)) {
            super.onBackPressed();
            Intent intent = new Intent(frtUsersListActivity, PTRNavigationScreenActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }

    private void setAdapter() {
        Collections.sort(modelList, Collections.<PTRResponseScheduleRouteModel>reverseOrder());
        adapter.setDataSet(modelList);
        adapter.notifyDataSetChanged();
        adapter.setSelectionListener(frtUsersListActivity);
        attendanceListView.setAdapter(adapter);
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
            frtUtility.hideDialog();
        } else {
            getSceduleRoute();
        }
    }

    @Override
    public void onClickItem(int position) {
        Intent intent = new Intent(frtUsersListActivity, PTRChatActivity.class);
        intent.putExtra("routetitle", modelList.get(position).getRoute_name());
        intent.putExtra("routeId", modelList.get(position).getRoute_id());
        intent.putExtra("routeRefId", modelList.get(position).getRoute_ref_id());
        intent.putExtra("case", "PlannedTask");
        startActivity(intent);
        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
    }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtUsersListActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtUsersListActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtUsersListActivity);
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
