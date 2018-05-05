package com.vodafone.frt.fragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.MGRTaskAssignActivity;
import com.vodafone.frt.activities.PTRMyCheckInActivity;
import com.vodafone.frt.activities.PTRNavigationScreenActivity;
import com.vodafone.frt.adapters.FRTGetUserNameSpinnerAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.enums.FRTAPIType;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/29/2018.
 */

public class RejectedTaskIssueFragment extends DialogFragment  implements  FRTAsyncCommon.FetchDataCallBack {

    private FRTUtility frtUtility;
    private ProgressDialog progressDialog;
    private RejectedTaskIssueFragment rejectedTaskIssueFragment = this;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    List<Integer> integerList;
    private Spinner simpleSpinner;
    private Button cancelBtn;


   // private ArrayList<Integer> issueIdList;
    private Button submitBtn;
    private JSONArray jsonArray;
    private ArrayList<FRTResponseSubOrdinateModel> frtUserNameList;
    private FRTGetUserNameSpinnerAdapter adapter;
    private EditText remarksTextView;
    private int issueIdList;
    private FRTConnectivityReceiver frtCnnectivityReceiver;

    public static RejectedTaskIssueFragment newInstance(){
        return new RejectedTaskIssueFragment();
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
        integerList = new ArrayList<>();
        //issueIdList = new ArrayList<>();
        //checkInDialogFragment= this;
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(getActivity());
        frtCnnectivityReceiver = new FRTConnectivityReceiver();


        Bundle bundle = getArguments();
        if (bundle != null){
            issueIdList = bundle.getInt("ISSUE_ID");

            Log.d(this.getClass().getName(),"ISSUE_ID" + issueIdList);



        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.task_rejected_dialog_fragment,container,false);
        setCancelable(false);
         simpleSpinner = (Spinner)dialogView.findViewById(R.id.simpleSpinner);
        submitBtn =(Button)dialogView.findViewById(R.id.submitBtn);
        submitBtn.setEnabled(true);
        remarksTextView = (EditText)dialogView.findViewById(R.id.remarksTextView);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*   if (remarksTextView.getText().toString().length() <= 0) {
                    Toast.makeText(getActivity(), R.string.enterLeaveReason, Toast.LENGTH_SHORT).show();
                }
                else {*/
                submitBtn.setEnabled(false);
                       toRejectedTaskDetail();
                 //  }
            }
        });


         cancelBtn = (Button)dialogView.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return dialogView;
    }





    private void toRejectedTaskDetail() {
        if (frtCnnectivityReceiver.isConnected(getActivity())) {
        progressDialog = new ProgressDialog(getActivity());
        FRTAsyncCommon<RequestAssignRejectIssueDetailModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setAssignRejectDetailModel());
        frtAsyncCommon.setContext(getActivity());
        frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.UPDATE_REJECTED_ISSUE_DETAIL), "UpdateRouteIssueDetailRejected");
        frtAsyncCommon.setFetchDataCallBack(rejectedTaskIssueFragment);
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), submitBtn);
            progressDialog.dismiss();
        }
    }

    private RequestAssignRejectIssueDetailModel setAssignRejectDetailModel() {
        RequestAssignRejectIssueDetailModel requestAssignRejectIssueDetailModel = new RequestAssignRejectIssueDetailModel();
        try {
            requestAssignRejectIssueDetailModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                    Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String currentDate = sdf.format(new Date());
            requestAssignRejectIssueDetailModel.setAssignDate(currentDate);
            requestAssignRejectIssueDetailModel.setFrtUserId(0);
            requestAssignRejectIssueDetailModel.setIssueIdRejected(issueIdList);
            requestAssignRejectIssueDetailModel.setRemark(remarksTextView.getText().toString());
            requestAssignRejectIssueDetailModel.setStatus("Rejected");

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
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(response);
                    dismiss();
                    if (jsonObject.optString("status").equals("OK")) {
                        Toast.makeText(getActivity(), getString(R.string.rejectedSuccessfully), Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
                       // startActivity(intent);

                        Log.d(this.getClass().getName(), "RESPONSE_SUCCESS" + response);
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {

                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                        ///frtUtility.goToLogin(getString(R.string.session_exp));
                        PTRNavigationScreenActivity navigationScreenActivity = new PTRNavigationScreenActivity();
                        navigationScreenActivity.getRefereshTokenData();
                    } else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {
                        // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                        Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        gotoTaskActivity();
                    } else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")) {
                        Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                        gotoTaskActivity();
                    } else
                        Toast.makeText(getActivity(), jsonObject.optString("error_message").toString(), Toast.LENGTH_SHORT).show();
                    gotoTaskActivity();
                    // frtUtility.setSnackBar(jsonObject.optString("error_message"), findViewById(R.id.header));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TAG", "EXCEPTION===" + e.getMessage());
                }
            }
        else {
            Toast.makeText(getActivity(), getString(R.string.servernotResponding), Toast.LENGTH_SHORT).show();
            gotoTaskActivity();
            //frtUtility.setSnackBar(getString(R.string.servernotResponding), findViewById(R.id.header));
        }
    }

 private void   gotoTaskActivity(){
        //Toast.makeText(getActivity(), getString(R.string.rejectedSuccessfully), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MGRTaskAssignActivity.class);
        startActivity(intent);
    }


}
