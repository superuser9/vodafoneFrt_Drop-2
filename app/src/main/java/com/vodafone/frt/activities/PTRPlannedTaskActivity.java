package com.vodafone.frt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.models.PTRRequestScheduleRouteDetailModel;
import com.vodafone.frt.models.PTRResponseScheduleRouteDetailModel;

import org.json.JSONException;
import org.json.JSONObject;

public class PTRPlannedTaskActivity extends AppCompatActivity implements FRTAsyncCommon.FetchDataCallBack, FRTCallBackForIdFind, FRTCallBackInitViews, FRTCallBackSetListeners {

    private PTRPlannedTaskActivity frtPlannedTaskActivity = this;
    FRTCallBackInitViews frtCallBackInitViews;
    FRTCallBackForIdFind frtCallBackForIdFind;
    FRTCallBackSetListeners frtCallBackSetListeners;
    private FRTWEBAPI frtwebapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtplanned_task);
        callBackSetUp();
    }

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        frtCallBackInitViews = frtPlannedTaskActivity;
        frtCallBackForIdFind = frtPlannedTaskActivity;
        frtCallBackSetListeners = frtPlannedTaskActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void initAllViews() {
        getUserDetail();
    }

    @SuppressWarnings("deprecation")
    private void getUserDetail() {
        FRTAsyncCommon<PTRRequestScheduleRouteDetailModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpUserDetailModel());
        frtAsyncCommon.setContext(frtPlannedTaskActivity);
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_USER_DETAIL), "getuserdetail");
        frtAsyncCommon.setFetchDataCallBack(frtPlannedTaskActivity);
    }

    private PTRRequestScheduleRouteDetailModel setUpUserDetailModel() {
        PTRRequestScheduleRouteDetailModel frtRequestScheduleRouteDetailModel = new PTRRequestScheduleRouteDetailModel();
//        frtRequestScheduleRouteDetailModel.setUserId(frtSharePrefUtil.getString(getString(R.string.userkey)));
        return frtRequestScheduleRouteDetailModel;
    }

    @Override
    public void getAsyncData(String response, String type) {

        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jo = new JSONObject(String.valueOf(jsonObject.optJSONObject("results")));
                if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                    PTRResponseScheduleRouteDetailModel frtResponseScheduleRouteDetailModel = new PTRResponseScheduleRouteDetailModel();
                    frtResponseScheduleRouteDetailModel.setAssignment_type(jo.optString("assignment_type"));
                    frtResponseScheduleRouteDetailModel.setCreated_by_name(jo.optString("created_by_name"));
                    frtResponseScheduleRouteDetailModel.setCreated_on(jo.optString("created_on"));
                    frtResponseScheduleRouteDetailModel.setDay_of_month(jo.optInt("day_of_mont"));
                    frtResponseScheduleRouteDetailModel.setEnd_date(jo.optString("end_date"));
                    frtResponseScheduleRouteDetailModel.setEnd_time(jo.optString("end_time"));
                    frtResponseScheduleRouteDetailModel.setIs_active(jo.optString("is_active"));
                    frtResponseScheduleRouteDetailModel.setModified_by_name(jo.optString("modified_by_name"));
                    frtResponseScheduleRouteDetailModel.setModified_on(jo.optString("modified_on"));
                    frtResponseScheduleRouteDetailModel.setPatroller_id(jo.optInt("ptroller_id"));
                    frtResponseScheduleRouteDetailModel.setPatroller_name(jo.optString("patroller_name"));
                    frtResponseScheduleRouteDetailModel.setRoute_id(jo.optInt("route_id"));
                    frtResponseScheduleRouteDetailModel.setRoute_name(jo.optString("route_name"));
                    frtResponseScheduleRouteDetailModel.setRoute_ref_id(jo.optInt("route_ref_id"));
                    frtResponseScheduleRouteDetailModel.setScheduled_id(jo.optInt("scheduled_id"));
                    frtResponseScheduleRouteDetailModel.setStart_date(jo.optString("start_date"));
                    frtResponseScheduleRouteDetailModel.setStart_time(jo.optString("start_time"));
                    frtResponseScheduleRouteDetailModel.setWorking_days(jo.optInt("working_days"));
                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
//                    frtUtility.goToLogin();
                } else {
//                    frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), AttendanceButton);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
//            frtUtility.setSnackBar(getString(R.string.userpasserror), AttendanceButton);
        }
    }

    @Override
    public void commonListeners() {

    }
}
