package com.vodafone.frt.fragments;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.FRTMyTaskActivityFRT;
import com.vodafone.frt.activities.MGRTaskAssignActivity;
import com.vodafone.frt.activities.PTRNavigationScreenActivity;
import com.vodafone.frt.adapters.FRTGetUserNameSpinnerAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.CommonSaveLeaveDetailModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.FRTResponseSubOrdinateModel;
import com.vodafone.frt.models.RequestAssignRejectIssueDetailModel;
import com.vodafone.frt.models.RequestGetSubOrdinateDetailsModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/29/2018.
 */

public class TaskAssignIssueFragment extends DialogFragment  implements  FRTAsyncCommon.FetchDataCallBack {

    private FRTUtility frtUtility;
    private static int requestType =0;
    private ProgressDialog progressDialog;
    private TaskAssignIssueFragment applyLeaveDialogFragment = this;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
   // List<Integer> integerList;
    private Spinner simpleSpinner;
    private Button cancelBtn;

    private int mYear;
    private int mMonth;
    private int mDay;
    private ArrayList<Integer> issueIdList;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private Button submitBtn;
    private String spinnerValue;
    private JSONArray jsonArray;
    private ArrayList<FRTResponseSubOrdinateModel> frtUserNameList;
    private FRTGetUserNameSpinnerAdapter adapter;
    private FRTTextviewTrebuchetMS staartdate;
    private EditText remarksTextView;
    private int frtUserId;
    private String issueIdText;

    public static TaskAssignIssueFragment newInstance(){
        return new TaskAssignIssueFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frtUtility = FRTUtility.getUtilityInstance();
        frtwebapi = new FRTWEBAPI();
       // integerList = new ArrayList<>();
        issueIdList = new ArrayList<>();
        //checkInDialogFragment= this;
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(getActivity());
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        Bundle bundle = getArguments();
        if (bundle != null){
            issueIdList = bundle.getIntegerArrayList("ISSUE_ARRAY_LIST");
            issueIdText = issueIdList.toString().replace("[", "").replace("]", "");
           // Log.d(this.getClass().getName(),"ISSUE_ID_LIST" + issueIdList);

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.task_assign_issue_dialog_fragment,container,false);
        setCancelable(false);
         simpleSpinner = (Spinner)dialogView.findViewById(R.id.simpleSpinner);

        submitBtn =(Button)dialogView.findViewById(R.id.submitBtn);
        submitBtn.setEnabled(true);
        remarksTextView = (EditText)dialogView.findViewById(R.id.remarksTextView);
        staartdate = (FRTTextviewTrebuchetMS) dialogView
                .findViewById(R.id.staartdate);
        staartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    // Launch Date Picker Dialog
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // String fromdate = dayOfMonth + "-" + (month + 1) + "-" + year;
                            String fromdate = (month + 1)  + "/" + dayOfMonth + "/" + year;
                            try {
                                staartdate.setText((fromdate));
                            } catch (Exception ex) {
                            }
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                    datePickerDialog.show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerValue.equalsIgnoreCase("Select FRT")){
                    Toast.makeText(getActivity(), R.string.pleaseSelectFrt, Toast.LENGTH_SHORT).show();
                }
                else if (staartdate.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), R.string.enterAssignedDate, Toast.LENGTH_SHORT).show();
                }
                else if (remarksTextView.getText().toString().length() <= 0) {
                    Toast.makeText(getActivity(), R.string.enterLeaveReason, Toast.LENGTH_SHORT).show();
                }
                else {
                     submitBtn.setEnabled(false);
                    requestType = 2;
                    toSaveLeaveDetail();
                }

            }
        });
         cancelBtn = (Button)dialogView.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

       getSubordinateDetails();
       // requestType =1;



        return dialogView;
    }



    private void getSubordinateDetails() {
        if (frtConnectivityReceiver.isConnected(getActivity())) {
        progressDialog = new ProgressDialog(getActivity());
        FRTAsyncCommon<RequestGetSubOrdinateDetailsModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(getSubordinateDetailModel());
        frtAsyncCommon.setContext(getActivity());
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_SUBORDINATE_DETAILS), "GetSubordinateDetails");
        frtAsyncCommon.setFetchDataCallBack(applyLeaveDialogFragment);
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        } else {
            progressDialog.dismiss();
            frtUtility.setSnackBar(getString(R.string.nointernet), submitBtn);

        }
    }

    private RequestGetSubOrdinateDetailsModel getSubordinateDetailModel() {
        RequestGetSubOrdinateDetailsModel rGSDM = new RequestGetSubOrdinateDetailsModel();

        try {
            rGSDM.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            rGSDM.setRoleName("FRT");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rGSDM;
    }


    private void toSaveLeaveDetail() {
        if (frtConnectivityReceiver.isConnected(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            FRTAsyncCommon<RequestAssignRejectIssueDetailModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setFrtModel(setAssignRejectDetailModel());
            frtAsyncCommon.setContext(getActivity());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.UPDATE_ASSIGN_ROUTE_ISSUE_DETAILS), "UpdateRouteIssueDetail");
            frtAsyncCommon.setFetchDataCallBack(applyLeaveDialogFragment);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();

        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), submitBtn);
            progressDialog.dismiss();
        }
    }

   //frtUtility.getDateAndTime(false)
    private RequestAssignRejectIssueDetailModel setAssignRejectDetailModel() {
        RequestAssignRejectIssueDetailModel requestAssignRejectIssueDetailModel = new RequestAssignRejectIssueDetailModel();
        try {
        requestAssignRejectIssueDetailModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        requestAssignRejectIssueDetailModel.setAssignDate(staartdate.getText().toString());
        requestAssignRejectIssueDetailModel.setFrtUserId(frtUserId);
            requestAssignRejectIssueDetailModel.setIssueIdString(issueIdText);
//        requestAssignRejectIssueDetailModel.setIssuesId(issueIdList);
        requestAssignRejectIssueDetailModel.setRemark(remarksTextView.getText().toString());
        requestAssignRejectIssueDetailModel.setStatus("Assigned");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(this.getClass().getName(),"EXCEPTION==" + e.getMessage());
        }
        return requestAssignRejectIssueDetailModel;
    }

        @Override
        public void getAsyncData(String response, String type) {
            progressDialog.dismiss();

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
                } //else if ( (requestType ==2)) {

                        else if ( (type.equalsIgnoreCase("UpdateRouteIssueDetail"))) {
                    JSONObject jsonObject = null;
                    try {
                        dismiss();
                        jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            Toast.makeText(getActivity(), getString(R.string.assign_successfully), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
                            startActivity(intent);
                            //getActivity().finish();
                            Log.d(this.getClass().getName(), "RESPONSE_SUCCESS" + response);
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {

                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            getRefereshTokenData();
                        } else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {
                            // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")) {
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        // frtUtility.setSnackBar(jsonObject.optString("error_message"), findViewById(R.id.header));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("TAG", "EXCEPTION===" + e.getMessage());
                    }
                }

                else if (type.equals("GetSubordinateDetails")){
                    // Getting Frt Name
                    try {
                        JSONObject   jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            jsonArray = jsonObject.optJSONArray("results");
                            frtUserNameList = new ArrayList<>();
                            FRTResponseSubOrdinateModel frtResponseSubOrdinateModel1 = new FRTResponseSubOrdinateModel();
                            frtResponseSubOrdinateModel1.setUser_name("select FRT");
                            frtUserNameList.add(0,frtResponseSubOrdinateModel1);
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
                            setAdapterSpinnerFrtData();
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {

                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            //  frtUtility.goToLogin(getString(R.string.session_exp));
                            PTRNavigationScreenActivity navigationScreenActivity = new PTRNavigationScreenActivity();
                            navigationScreenActivity.getRefereshTokenData();
                        } else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {
                            //Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
                            //startActivity(intent);
                            callTaskActivity();
                            // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")) {
                            Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
                            startActivity(intent);
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                            // frtUtility.setSnackBar(jsonObject.optString("error_message"), findViewById(R.id.header));
                           // Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
                           // startActivity(intent);
                            callTaskActivity();
                        }

                    } catch (JSONException e) {
                        callTaskActivity();
                        e.printStackTrace();
                        Log.d("TAG", "EXCEPTION===" + e.getMessage());
                    }
                }
            }
            else {

                //dismiss();
                //callTaskActivity();
                Toast.makeText(getActivity(), getString(R.string.servernotResponding), Toast.LENGTH_SHORT).show();
                callTaskActivity();
                //frtUtility.setSnackBar(getString(R.string.servernotResponding), findViewById(R.id.header));
            }
        }


        private void callTaskActivity(){
            Intent intent = new Intent(getActivity(), PTRNavigationScreenActivity.class);
            startActivity(intent);
        }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(getActivity())) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(getActivity());
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(applyLeaveDialogFragment);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), cancelBtn);
        }
    }

    private PTRLoginRefreshRequestModel setUpLoginRefreshRequestModel() {
        PTRLoginRefreshRequestModel cqRequestModelLogin = new PTRLoginRefreshRequestModel();
        cqRequestModelLogin.setRefresh_token(frtSharePrefUtil.getString(getString(R.string.refresh_token_key)));
        cqRequestModelLogin.setGrant_type();
        return cqRequestModelLogin;
    }


    private void setAdapterSpinnerFrtData() {
        adapter = new FRTGetUserNameSpinnerAdapter(getActivity(),frtUserNameList);
        simpleSpinner.setAdapter(adapter);
        simpleSpinner.setPrompt("Select FRT");
        simpleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (frtUserNameList.size()>0) {
                    if (position == 0) {
                        spinnerValue = "Select FRT";
                    } else {
                        //else {
                        FRTResponseSubOrdinateModel idss = frtUserNameList.get(position);
                        frtUserId = idss.getFrt_user_id();
                        spinnerValue = idss.getUser_name();
                        // spinnerValue = simpleSpinner.getSelectedItem().toString();
                        Log.d(this.getClass().getName(), "FRT_ID_SPINNER" + frtUserId);
                    }
                }
                else {
                    spinnerValue = "No Data Found";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
