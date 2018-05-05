package com.vodafone.frt.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRSOSAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestSOSModel;
import com.vodafone.frt.models.PTRResponseSOSModel;
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

/**
 * Created by vishal
 */
public class FRTSOSActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , PTRSOSAdapter.CQCallBackItemsAvailable, FRTBroadcasting.CallBackBroadcast, FRTAsyncCommon.FetchDataCallBack {
    private BroadcastReceiver broadcastReceiver;
    private FRTBroadcasting broadcasting;
    private final FRTSOSActivity frtsosActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private PTRSOSAdapter frtsosAdapter;
    private FRTTextviewTrebuchetMS norecord, loader;
    private FRTUtility frtUtility;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTWEBAPI frtwebapi;
    private FRTConstants frtConstants;
    private LinearLayout ivback;
    private ListView soslist;
    private List<PTRResponseSOSModel> frtResponseSOSModelList;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private FRTSharePrefUtil frtSharePrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtsos);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtsosActivity);
        callBackSetUp();
    }

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtsosActivity;
        frtCallBackForIdFind = frtsosActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtsosActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void initAllViews() {
        soslist = (ListView) frtCallBackForIdFind.view(R.id.lv);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecord);
        loader = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.loader);
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        frtConstants = new FRTConstants();
        frtsosAdapter = new PTRSOSAdapter(frtsosActivity);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtsosActivity);
        frtUtility.settingStatusBarColor(frtsosActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtsosActivity);
        broadcasting.setCallbackBroadcast(frtsosActivity);
        frtwebapi = new FRTWEBAPI();
        getSosData();
    }

    private void checkCallPermission() {
        //noinspection StatementWithEmptyBody
        if (checkPermission()) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private PTRRequestSOSModel setUpGetSoS() {
        PTRRequestSOSModel frtRequestSOSModel = new PTRRequestSOSModel();
        try {
            frtRequestSOSModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frtRequestSOSModel;
    }
    private void getSosData() {
        if (frtConnectivityReceiver.isConnected(frtsosActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestSOSModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtsosActivity);
            frtAsyncCommon.setFrtModel(setUpGetSoS());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.GET_SOS_DETAILS), "sos");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        }else {
                            JSONArray jsonArray = jsonObject.optJSONArray("results");
                            frtResponseSOSModelList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                PTRResponseSOSModel cqResponseSummaryModel = new PTRResponseSOSModel();
                                cqResponseSummaryModel.setSos_id(jsonObject1.optString("sos_id"));
                                cqResponseSummaryModel.setSos_desc(jsonObject1.optString("sos_desc"));
                                cqResponseSummaryModel.setPhone(jsonObject1.optString("phone"));
                                if(!jsonObject1.optString("phone").equalsIgnoreCase("null"))
                                frtResponseSOSModelList.add(cqResponseSummaryModel);
                            }
                            if (jsonObject.optString("status").equals("OK")) {
                                norecord.setVisibility(View.GONE);
                                soslist.setVisibility(View.VISIBLE);
                                createAdapter();
                            }/* else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            frtUtility.goToLogin(getString(R.string.session_exp));
                        }*/ else {
                                norecord.setVisibility(View.VISIBLE);
                                soslist.setVisibility(View.GONE);
                            }
                            loader.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), soslist);
        }
    }

    private void createAdapter() {
        Collections.sort(frtResponseSOSModelList);
        frtsosAdapter.setDataSet(frtResponseSOSModelList);
        Collections.sort(frtResponseSOSModelList);
        frtsosAdapter.notifyDataSetChanged();
        frtsosAdapter.setItemCallBackItemAvailable(frtsosActivity);
        soslist.setAdapter(frtsosAdapter);
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        soslist.setOnItemClickListener(onItemClickSos);
        ivback.setOnClickListener(cqBackClick);
    }

    private final View.OnClickListener cqBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
    private final AdapterView.OnItemClickListener onItemClickSos = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            checkCallPermission();
            String phoneNumber = frtResponseSOSModelList.get(position).getPhone();
            if (!TextUtils.isEmpty(phoneNumber)) {
                if (checkPermission()) {
                    String dial = "tel:" + phoneNumber;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
            } else {
                frtUtility.setSnackBar(getString(R.string.nocontactavailable), view);
            }
        }
    };

    @Override
    public void available(boolean isavailable) {
        if (isavailable) {
            norecord.setVisibility(View.GONE);
            soslist.setVisibility(View.VISIBLE);
        } else {
            norecord.setVisibility(View.VISIBLE);
            soslist.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtsosActivity)) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
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
        } else {
            getSosData();
        }
    }

    /**
     * Activity callback method
     *
     * @param requestCode  permission request code
     * @param permissions  permissions
     * @param grantResults grant results array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == frtConstants.LOCATION_PERMISSION)
            //noinspection StatementWithEmptyBody
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(frtsosActivity, R.string.sufficient_permissions, Toast.LENGTH_LONG).show();
                finish();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtsosActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtsosActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtsosActivity);
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
                    frtSharePrefUtil.setString(getString(R.string.token_key), jsonObject.optString(getString(R.string.token_key)));
                    frtSharePrefUtil.setString(getString(R.string.tokentype_key), jsonObject.optString(getString(R.string.tokentype_key)));
                    frtSharePrefUtil.setString(getString(R.string.tokenexpiretime_key), String.valueOf(jsonObject.optInt(getString(R.string.tokenexpiretime_key), 0)));
                    frtSharePrefUtil.setString(getString(R.string.refresh_token_key), jsonObject.optString(getString(R.string.refresh_token_key)));
                    frtSharePrefUtil.setString(getString(R.string.globalsettings_key), jsonObject.optString(getString(R.string.globalsettings_key)));
                    frtSharePrefUtil.setString("currenttime", frtUtility.getCurrentTime());

                } catch (JSONException ignored) {
                }
            } else {
                frtUtility.setSnackBar(getString(R.string.notgetting_responseFromServer), ivback);
            }
        }
    }
}