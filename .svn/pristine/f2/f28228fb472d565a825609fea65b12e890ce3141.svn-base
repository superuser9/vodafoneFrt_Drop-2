package com.vodafone.frt.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestSaveRouteLocationmodel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by SM-002 on 01-02-2018.
 */

public class SyncService extends Service {
    private FRTApp frtApp;
    private FRTLocationDb frtLocationDb;
    private FRTUtility frtUtility;
    private FRTWEBAPI frtwebapi;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTConstants frtConstants;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    //    long trackwait, serverwait;
    Context context;
    private static PendingIntent pi = null;
    //    final private static int FIVE_MINUTES = 1000 * 60 * 5;
    public Boolean isRunning = false;
    ScheduledFuture syncHandle;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        frtApp = (FRTApp) getApplication();
        frtLocationDb = new FRTLocationDb(frtApp);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(getApplicationContext());
        frtwebapi = new FRTWEBAPI();
        context = getApplicationContext();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(context);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        frtConstants = new FRTConstants();
        frtUtility.logInfo(context, "SYNC SERVICE STARTED", frtConstants);
        syncLocationPeriodically(5);
    }

    public void syncLocationPeriodically(long period) {
        syncHandle = scheduler.scheduleWithFixedDelay(syncLocation, 0, period, TimeUnit.SECONDS);
    }

    final Runnable syncLocation = new Runnable() {
        public void run() {
            try {
                if (frtConnectivityReceiver.isConnected(context)) {
                    frtUtility.logInfo(context, "DEVICE IS ONLINE", frtConstants);
                    if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.reasonname))) && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Pause")
                            && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("End") && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Timeout") || frtLocationDb.offlineRouteEnded()) {
                        if (frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start") || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume")
                                || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Inprogress") || frtLocationDb.offlineRouteEnded())
                            if (!isRunning) {
                                isRunning = true;
                                int size = frtLocationDb.trackingDataCount();
                                if (size > 0) {
                                    frtUtility.logInfo(context, "DATA AVAILABLE FOR SYNCING", frtConstants);
                                } else {
                                    frtUtility.logInfo(context, "NO DATA AVAILABLE FOR SYNCING", frtConstants);
                                }
                                if (size > 0) {
                                    frtUtility.logInfo(context, "SYNCING START", frtConstants);
                                    // Sync the tracking data
                                    List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.TRACKINGDATA);
                                    List<Integer> idList = new ArrayList<>();
                                    if (frtRequestSaveRouteLocationmodelList.size() > 0) {
                                        for (PTRRequestSaveRouteLocationmodel model : frtRequestSaveRouteLocationmodelList) {
                                            idList.add(model.getSeqId());
                                            Log.d("PANKAJ", "SENDING ID " + model.getSeqId());
                                        }
                                        TimeUnit.SECONDS.sleep(2);
                                        frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_IN_PROGRESS, true);
                                        String response = frtUtility.prepareData("saveroutebulk", null, null, frtRequestSaveRouteLocationmodelList, frtwebapi.getWEBAPI(FRTAPIType.SAVE_ROUTE_BULK)).toString();
                                        frtUtility.logInfo(context, "saveroutebulk response : " + response, frtConstants);
                                        try {
//                                            frtUtility.logInfo(context,"saveroutebulk response : "+ response, frtConstants);
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.optString("status").equals("OK")) {
//                                    frtSharePrefUtil.setList(idList);
                                                // frtLocationDb.deleteTrackingById(idList);
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_SYNCED, true);
                                                Log.d("PANKAJ", "RECORDS DELETED");
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.error))) {
                                                Log.e("ERRORDATA", "Something went wrong with server or client parameters.");
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.req_denied))) {
                                                frtUtility.goToLogin(context.getString(R.string.req_denied));
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.session_exp))) {
                                                /*frtUtility.goToLogin(context.getString(R.string.session_exp));*/
                                                getRefereshTokenData();
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            }
                                        } catch (Exception ignored) {
                                            Log.e("service", ignored.toString());
                                        }
                                    }
                                    // sync end route data
                                    List<PTRRequestSaveRouteLocationmodel> frtRequestEndRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.ROUTEEND);
                                    idList = new ArrayList<>();
                                    if (frtRequestEndRouteLocationmodelList.size() > 0) {
                                        for (PTRRequestSaveRouteLocationmodel model : frtRequestEndRouteLocationmodelList) {
                                            idList.add(model.getSeqId());
                                            Log.d("PANKAJ", "SENDING ID " + model.getSeqId());
                                        }
//                                        TimeUnit.SECONDS.sleep(2);
                                        frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_IN_PROGRESS, true);
                                        String response = frtUtility.prepareData("saverouteofflineaction", null, null, frtRequestEndRouteLocationmodelList, frtwebapi.getWEBAPI(FRTAPIType.SAVE_ROUTE_ACTION)).toString();
                                        frtUtility.logInfo(context, "saverouteofflineaction response : " + response, frtConstants);
                                        try {
                                            //  frtUtility.logInfo(context, response, frtConstants);
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.optString("status").equals("OK")) {
//                                    frtSharePrefUtil.setList(idList);
                                                // frtLocationDb.deleteTrackingById(idList);
                                                frtSharePrefUtil.setString("keytracktoend", "");
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_SYNCED, true);
                                                //TODO: Truncate table
                                                frtLocationDb.truncateLocationTrackingTable();
                                                frtUtility.logInfo(context, "truncate location tracking table", frtConstants);

                                                Log.d("PANKAJ", "RECORDS DELETED");
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.error))) {
                                                Log.e("ERRORDATA", "Something went wrong with server or client parameters.");
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.req_denied))) {
                                                frtUtility.goToLogin(context.getString(R.string.req_denied));
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.session_exp))) {
                                               /* frtUtility.goToLogin(context.getString(R.string.session_exp));*/
                                                getRefereshTokenData();
                                                frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_PENDING, true);
                                            }
                                        } catch (Exception ignored) {
                                            Log.e("service", ignored.toString());
                                        }
                                    }
                                }
                                isRunning = false;
                                frtUtility.logInfo(context, "SYNCING COMPLETED", frtConstants);
                            } else {
                                frtUtility.logInfo(context, "SYNCING IS IN PROGRESS", frtConstants);
                            }
                    } else {
                        isRunning = false;
                        frtUtility.logInfo(context, "DEVICE IS OFFLINE", frtConstants);
                    }
                }
            } catch (Exception e) {
                isRunning = false;
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        Intent intent = new Intent(frtConstants.RESTART_SERVICE_KEY);
        sendBroadcast(intent);
    }

    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(context)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(context);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
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
                        }
                    }
                }
            });
        } /*else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(android.R.id.content));
        }*/
    }

    private PTRLoginRefreshRequestModel setUpLoginRefreshRequestModel() {
        PTRLoginRefreshRequestModel cqRequestModelLogin = new PTRLoginRefreshRequestModel();
        cqRequestModelLogin.setRefresh_token(frtSharePrefUtil.getString(getString(R.string.refresh_token_key)));
        cqRequestModelLogin.setGrant_type();
        return cqRequestModelLogin;
    }
}
