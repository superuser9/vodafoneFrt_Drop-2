package com.vodafone.frt.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.models.PTRFeedbackModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PTRFeedbackActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind,
        FRTBroadcasting.CallBackBroadcast, FRTCallBackSetListeners, FRTAsyncCommon.FetchDataCallBack {
    private boolean checknet = false;
    private PTRFeedbackActivity frtfeedbackActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private BroadcastReceiver broadcastReceiver;
    private FRTBroadcasting broadcasting;
    private FRTUtility frtUtility;
    private FRTConnectivityReceiver frtCnnectivityReceiver;
    private LinearLayout ivbackCheckIn;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    private EditText editTextFeedback;
    private Button buttonSubmit, buttonCancel;
    private String userIdEncripted;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        callBackSetUp();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  broadcastReceiver = broadcasting.getBroadcasting();
      //  registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    @Override
    public void getBroadcast(boolean isBroadcasting) {
        if (isBroadcasting) {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivbackCheckIn);
            frtUtility.hideDialog();
            checknet = true;
        } else {
            if (checknet)
                //  getAttendance();
                checknet = false;
        }
    }


    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtfeedbackActivity;
        frtCallBackForIdFind = frtfeedbackActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtfeedbackActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Ashu", "on resume");
    }

    @Override
    public void commonListeners() {
          ivbackCheckIn.setOnClickListener(frtBackClick);
        buttonSubmit.setOnClickListener(frtSubmitClick);
        //  buttonCancel.setOnClickListener(frtcancelClick);
    }

     private final View.OnClickListener frtcancelClick = new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             onBackPressed();
         }
     };
    private final View.OnClickListener frtSubmitClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //onBackPressed();

            if (editTextFeedback.getText().toString().equals("")) {
                editTextFeedback.setError("can't be empty");
            }else {
                getfeedbackRequest();
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
    public void initAllViews() {
        frtCnnectivityReceiver = new FRTConnectivityReceiver();
         ivbackCheckIn = (LinearLayout) frtCallBackForIdFind.view(R.id.ivbackCheckIn);
        editTextFeedback = (EditText) frtCallBackForIdFind.view(R.id.editTextFeedback);
        buttonSubmit = (Button) frtCallBackForIdFind.view(R.id.buttonSubmit);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtfeedbackActivity);
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        frtwebapi = new FRTWEBAPI();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtfeedbackActivity);
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }


    @Override
    protected void onStop() {
        super.onStop();
      //  unregisterReceiver(broadcastReceiver);
    }


    private void getfeedbackRequest() {
        if (frtCnnectivityReceiver.isConnected(getApplicationContext())) {
            progressDialog = new ProgressDialog(PTRFeedbackActivity.this);
            final FRTAsyncCommon<PTRFeedbackModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtfeedbackActivity);
            frtAsyncCommon.setFrtModel(setUpfeedbackModel());

            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.SAVE_USER_FEEDBACK), "savefeedback");
            frtAsyncCommon.setFetchDataCallBack(frtfeedbackActivity);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();

        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivbackCheckIn);
            progressDialog.dismiss();
        }
    }

    private PTRFeedbackModel setUpfeedbackModel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        PTRFeedbackModel frtFeedbackModel = new PTRFeedbackModel();
        try {
             userIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //todo:set petrollerId
        if (!TextUtils.isEmpty(userIdEncripted))

            frtFeedbackModel.setUserid(Integer.parseInt(userIdEncripted));
        frtFeedbackModel.setFeedbackText(editTextFeedback.getText().toString());
        frtFeedbackModel.setMobileTime(currentDateandTime);
        return frtFeedbackModel;
    }

    @Override
    public void getAsyncData(String response, String type) {
        try {
            progressDialog.dismiss();
            Log.d(this.getClass().getName(), "FEEDBACK_RESPONSE" + response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString(getString(R.string.status_key)).equals("OK")) {
                Toast.makeText(frtfeedbackActivity, R.string.feedback_thanks, Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(frtfeedbackActivity, OthersActivity.class);
                 startActivity(intent);
            }
            else if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("UNKNOWN_ERROR")){
                //jsonObject.optString(getString(R.string.error_message_key)
                        frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), ivbackCheckIn);

            }
            else {
                frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), ivbackCheckIn);
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

}
