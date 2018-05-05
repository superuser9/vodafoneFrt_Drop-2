package com.vodafone.frt.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.adapters.FRTGetUserNameSpinnerAdapter;
import com.vodafone.frt.adapters.FRTPTRUserListAdapter;
import com.vodafone.frt.adapters.PTRUsersListAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.FRTResponseSubOrdinateModel;
import com.vodafone.frt.models.PTRRequestScheduleRouteModel;
import com.vodafone.frt.models.PTRResponseScheduleRouteModel;
import com.vodafone.frt.models.RequestGetSubOrdinateDetailsModel;
import com.vodafone.frt.models.UserDataModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.Constants;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FRTPTRUserListActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast,FRTPTRUserListAdapter.SelectionViewAttendanceListener{



    private BroadcastReceiver broadcastReceiver;
    private FRTPTRUserListAdapter adapter;
    private FRTBroadcasting broadcasting;
    private final FRTPTRUserListActivity frtptrUserListActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTUtility frtUtility;
    private LinearLayout ivback;
    private ListView attendanceListView;
    private FRTTextviewTrebuchetMS title;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTTextviewTrebuchetMS norecord, loader;
    private ProgressDialog progressDialog;
    private JSONArray jsonArray;
    private ArrayList<FRTResponseSubOrdinateModel> frtUserNameList;
    private String roleIdEncripted;
    private String managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frtptruser_list);
        getSupportActionBar().hide();
        callBackSetUp();
    }


    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtptrUserListActivity;
        frtCallBackForIdFind = frtptrUserListActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtptrUserListActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
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
    public void initAllViews() {

        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        attendanceListView = (ListView) frtCallBackForIdFind.view(R.id.frtscheduledrouteListView);
        title = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.title);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
       // loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtptrUserListActivity);
        frtUtility.settingStatusBarColor(frtptrUserListActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtptrUserListActivity);
        broadcasting.setCallbackBroadcast(frtptrUserListActivity);
        adapter = new FRTPTRUserListAdapter(frtptrUserListActivity);
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtptrUserListActivity);

        try {
            roleIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.role_id)).getBytes("UTF-16LE"), Base64.DEFAULT));
          managerId =  AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.managerkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
            if(roleIdEncripted.equals(Constants.MANAGER_ID)){
                getSubordinateDetails();


            }
        }
            catch (Exception e) {
            e.printStackTrace();
        }
       //else{

       // }


    }

    private void getSubordinateDetails() {
        if (frtConnectivityReceiver.isConnected(frtptrUserListActivity)) {
            progressDialog = new ProgressDialog(frtptrUserListActivity);
            FRTAsyncCommon<RequestGetSubOrdinateDetailsModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setFrtModel(getSubordinateDetailModel());
            frtAsyncCommon.setContext(frtptrUserListActivity);
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_SUBORDINATE_DETAILS), "GetSubordinateDetails");
            frtAsyncCommon.setFetchDataCallBack(frtptrUserListActivity);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
            frtUtility.setSnackBar(getString(R.string.nointernet), attendanceListView);

        }
    }

    private RequestGetSubOrdinateDetailsModel getSubordinateDetailModel() {
        RequestGetSubOrdinateDetailsModel rGSDM = new RequestGetSubOrdinateDetailsModel();

        try {
            rGSDM.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            //rGSDM.setRoleName("FRT");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rGSDM;
    }


    @Override
    public View view(int id) {
        return findViewById(id);
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
           // getSceduleRoute();
        }
    }


    @Override
    public void getAsyncData(String response, String type) {
        progressDialog.dismiss();

        if (type.equals("GetSubordinateDetails")){
            // Getting Frt Name
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("status").equals("OK")) {
                    jsonArray = jsonObject.optJSONArray("results");
                    frtUserNameList = new ArrayList<>();
                    //FRTResponseSubOrdinateModel frtResponseSubOrdinateModel1 = new FRTResponseSubOrdinateModel();
                   // frtResponseSubOrdinateModel1.setUser_name("select FRT");
                   // frtUserNameList.add(0,frtResponseSubOrdinateModel1);
                    //Toast.makeText(getActivity(), getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
                    // Log.d(this.getClass().getName(), "RESPONSE_SUCCESS" + response);


                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jo = jsonArray.optJSONObject(i);
                        FRTResponseSubOrdinateModel frtResponseSubOrdinateModel = new FRTResponseSubOrdinateModel();
                        frtResponseSubOrdinateModel.setFrt_user_id(jo.optInt("user_id"));
                        frtResponseSubOrdinateModel.setUser_name(jo.optString("user_name"));
                        frtResponseSubOrdinateModel.setFull_name(jo.optString("full_name"));
                        frtUserNameList.add( frtResponseSubOrdinateModel);

                    }
                    setAdapterFRTPTRUser();
                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {

                    frtUtility.goToLogin(getString(R.string.req_denied));
                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                    PTRNavigationScreenActivity navigationScreenActivity = new PTRNavigationScreenActivity();
                    navigationScreenActivity.getRefereshTokenData();
                } else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {

                    //callTaskActivity();
                    Toast.makeText(frtptrUserListActivity, jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                } else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")) {
                    Intent intent = new Intent(frtptrUserListActivity, PTRNavigationScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(frtptrUserListActivity, jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(frtptrUserListActivity, jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                    callTaskActivity();
                }

            } catch (JSONException e) {
                callTaskActivity();
                e.printStackTrace();
                Log.d("TAG", "EXCEPTION===" + e.getMessage());
            }
        }
    }

    private void setAdapterFRTPTRUser() {
       // adapter = new PTRUsersListAdapter(frtptrUserListActivity);
        //adapter.setAdapter(adapter);

       // Collections.sort(modelList, Collections.<FRTResponseSubOrdinateModel>reverseOrder());
        adapter.setDataSet(frtUserNameList);
        adapter.notifyDataSetChanged();
        adapter.setSelectionListener(frtptrUserListActivity);
        attendanceListView.setAdapter(adapter);
    }

    private void callTaskActivity(){
        Intent intent = new Intent(frtptrUserListActivity, PTRNavigationScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickItem(int position) {
        Intent intent = new Intent(frtptrUserListActivity, ChatActivity.class);
        String userIdString = String.valueOf(frtUserNameList.get(position).getFrt_user_id());
        intent.putExtra("userIdFRT_PTR", frtUserNameList.get(position).getFrt_user_id());
       // intent.putExtra("userName", frtUserNameList.get(position).getUser_name());
        intent.putExtra(Constants.SENDER_NAME,frtUserNameList.get(position).getUser_name());
        intent.putExtra(Constants.SENDER_ID,userIdString);
        //intent.putExtra("case", "ChatActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
    }
}
