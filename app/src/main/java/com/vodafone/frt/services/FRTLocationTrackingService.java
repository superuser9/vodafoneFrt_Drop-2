package com.vodafone.frt.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestPlannedRouteModel;

import com.vodafone.frt.models.PTRRequestSaveRouteLocationmodel;
import com.vodafone.frt.models.PTRResponsePlannedRouteModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTUtility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This service would capture the user location and send it to server
 */
public class FRTLocationTrackingService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String CALL_STATE_IDLE = "smsSend";
    private String TAG = "FRTLocationTrackingService";
    private Intent intt;
    private FRTLocationTrackingService frtLocationTrackingService;
    private FRTWEBAPI frtwebapi;
    private FRTUtility frtUtility;
    private FRTSharePrefUtil frtSharePrefUtil;
    private PTRRequestSaveRouteLocationmodel locationTempModel = null;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTLocationDb frtLocationDb;
    private FRTConstants frtConstants;
    private GoogleApiClient googleApiClient;
    private MyServiceHandler myServiceHandler;
    private List<PTRResponsePlannedRouteModel> frtResponsePlannedRouteModelList = new ArrayList<>();
    private long trackwait, serverwait;
    private boolean flag = false;
    private FRTApp frtApp;
    private long tempTime;
    private final int ACCURACY = 50;
    private final int MINDISTANCE = 5;
    private final int SMS_SENDING_DELAY_IN_MINUTE = 30 * 60 * 1000;
    private final DecimalFormat doubleFormatter = new DecimalFormat("####0.000");
    private long lastSmsSentTime = 0L;
    //    private Timer timer;
    public static final String BROADCAST_ACTION = "com.senddatatoactivity.displayevent";
    private final Handler handler = new Handler();
    private double newUpdateDistance;

    private Intent intent;
    private double newLocationSpeed;
    private double latitudeSms;
    private double longitudeSms;

    /**
     * Constructor class
     */
    public FRTLocationTrackingService() {
        frtLocationTrackingService = this;
    }

    @Override
    public void onCreate() {
        intent = new Intent(this, FRTLocationTrackingService.class);

        frtwebapi = new FRTWEBAPI();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(getApplicationContext());
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtLocationTrackingService);
        frtApp = (FRTApp) getApplication();
        frtLocationDb = new FRTLocationDb(frtLocationTrackingService);
        frtwebapi = new FRTWEBAPI();
        frtConstants = new FRTConstants();
        frtConnectivityReceiver = new FRTConnectivityReceiver();
//        timer = new Timer();
        if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.tracktime))))
            trackwait = Long.valueOf(frtSharePrefUtil.getString(getString(R.string.tracktime)));
        if (trackwait >= 5000) {
            serverwait = trackwait;
        } else {
            serverwait = 5000;
        }
//        if (TextUtils.isEmpty(frtSharePrefUtil.getString("planneddata")))
//        HandlerThread handlerthread = new HandlerThread("MyThread", Process.THREAD_PRIORITY_BACKGROUND);
//        handlerthread.start();
//        Looper looper = handlerthread.getLooper();
        //  myServiceHandler = new MyServiceHandler(looper);


        // changing data to server
        intent = new Intent(BROADCAST_ACTION);
    }

    /**
     * OnStart command will be triggered when invoking service for first time
     *
     * @param intent  intent object which used to invoke service
     * @param flags   if any flag is used while starting service
     * @param startId any id attachning while starting the service
     * @return an integer value
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intt = intent;
        try {
            /*if(frtLocationDb.offlineRouteEnded() == true)
                return START_NOT_STICKY;*/
            getPlannedRouteData();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        // sending data to update ui
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
        // Make the service sticky so that ir runs even if it restarts
        return START_STICKY;
    }

    // send update speed ui on every 2 second
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 2000); // 2 seconds
        }
    };

    private void DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo");

        intent.putExtra("speedDriver", newUpdateDistance);
        intent.putExtra("LocationSpeed", newLocationSpeed);
        sendBroadcast(intent);
    }

    /**
     * Handler to upload the data every x seconds
     */
    private final class MyServiceHandler extends Handler {
        MyServiceHandler(Looper looper) {
            super(looper);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
//            Log.i(TAG, "HANDLE MESSAGE");
//            long t = Calendar.getInstance().getTime().getTime();
//            try {
//                Log.i(TAG, "FRTLocationTrackingService");
//                if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.reasonname))) && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Pause")
//                        && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("End") && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Timeout")) {
//                    if (frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start") || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume")
//                            || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Inprogress"))
////                        frtUtility.sendDataToServer(frtwebapi, frtLocationDb, frtConnectivityReceiver);
//                }
//                t = Calendar.getInstance().getTime().getTime() - t;
//                Thread.sleep((serverwait - t) <= 0 ? serverwait : (serverwait - t));
//            } catch (Exception e) {
//                Log.i(TAG, e.getMessage());
//            }
//            Log.d(TAG, "POSTED MESSAGE");
        }
    }

    /**
     * This is used to bind the intent with service
     *
     * @param intent intent given by activity to start service
     * @return binder instance
     */
    @Override
    public IBinder onBind(Intent intent) {
        startService(intt);
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    /**
     * This method will get triggered when service destroys and starting the service again
     */
    @Override
    public void onDestroy() {
        try {
            startService(intt);
        } catch (Exception ignored) {
        }
        frtSharePrefUtil.setString("planneddata", "");
        lastSmsSentTime = Calendar.getInstance().getTime().getTime();
        Intent intent = new Intent("com.vodafone.frt.receivers");
        sendBroadcast(intent);
        // remove call back update speed ui
        handler.removeCallbacks(sendUpdatesToUI);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationRequest locationRequest = new LocationRequest();
            //Invoking location based on time provided by server
            locationRequest.setInterval(trackwait);
            // locationRequest.setSmallestDisplacement(5);
            //Setting location accuracy to high
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(trackwait);
            //locationRequest.setSmallestDisplacement(10f);
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, frtLocationTrackingService);
            }
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
        }
    }

    /**
     * Here we are building the Google api client
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(frtLocationTrackingService)
                .addOnConnectionFailedListener(frtLocationTrackingService)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /**
     * Onlocationchanged get triggered when device location get changed
     *
     * @param location location obtained by GPS
     */
    @Override
    public void onLocationChanged(Location location) {
        latitudeSms = location.getLatitude();
        longitudeSms = location.getLongitude();
        if (frtSharePrefUtil.getBoolean("isLogout")/* || !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Inprogress")*/)
            return;
        LatLng userCurrentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
        frtUtility.logInfo(frtLocationTrackingService, "onLocation Changed called from LocationTrackingService", frtConstants);
        String deviation = null;
        try {
            if (frtResponsePlannedRouteModelList != null)
                deviation = doubleFormatter.format(frtUtility.getShortestDeviationpath(frtResponsePlannedRouteModelList, userCurrentLatLon));
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
        }
        Log.d("DEVIATION", "DEVIATION" + deviation);
        String bufferLimit = frtSharePrefUtil.getString(getString(R.string.Bufferlimit));
        //setting isinsidebuffer
        if (!TextUtils.isEmpty(deviation) && !TextUtils.isEmpty(bufferLimit)) {
            if (frtSharePrefUtil.getString("self_check_in").equals("No")) {
//                sendSmsCode(deviation, bufferLimit, frtSharePrefUtil.getString("firstsms"));
            }
        }
        Log.d("Time", "System.currentTimeMillis()::: " + System.currentTimeMillis() + " : " + location.getAccuracy());
        // ignore location if accuracy is less than 10 meter
        if (locationTempModel == null) {
            if (location.getAccuracy() < 80)
                flag = true;
        } else {
            flag = false;
        }
        //  sendMessage();
        if (!flag)
            if (location.getAccuracy() > ACCURACY)
                return;
        String userId = null;
        try {
            userId = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String routessignmentId = frtSharePrefUtil.getString("routeassigmentid");
        if (TextUtils.isEmpty(userId)) {
            // TODO: Stop location capturing and go to login screen
//            frtUtility.goToActivity(frtLocationTrackingService, FRTLoginActivity.class);
            return;
        } else if (TextUtils.isEmpty(routessignmentId)) {
            // TODO: Stop location capturing and go to Dashboard
//            frtUtility.goToActivity(frtLocationTrackingService, FRTNavigationScreenActivity.class);
            return;
        }
        PTRRequestSaveRouteLocationmodel locationModel = new PTRRequestSaveRouteLocationmodel();

        long actualTime;
        try {

            // testing data for speed

            //getting route assignmentid
            locationModel.setRouteAssignmentId(routessignmentId);
            //setting latitude
            locationModel.setLatitude("" + location.getLatitude());
            frtSharePrefUtil.setString("lati", location.getLatitude() + "");
            frtSharePrefUtil.setString("longi", location.getLongitude() + "");
            //setting longitude
            locationModel.setLongitude("" + location.getLongitude());
            //calculating mobile time
            Date dt = Calendar.getInstance().getTime();
            actualTime = dt.getTime();
            locationModel.setMobileTime(frtUtility.getDateAndTime(dt, true));
            //calculating distance
            if (locationTempModel != null) {
                double distance = frtUtility.calculationByDistance(locationTempModel, locationModel);
                if (distance < MINDISTANCE)
                    return;
                locationModel.setDistFromLastLoc(doubleFormatter.format(distance));
                //Calculating spped from last record by sequence number

                //  PTRRequestSaveRouteLocationmodel lastLocation = frtLocationDb.getLastLocationBySeqNumber();
                //  if (lastLocation != null) {
/*
                    Date lastCapturedDate = frtUtility.getDateFromString(lastLocation.getMobileTime());
                    long lastCapturedTime = lastCapturedDate.getTime();
                    double distanceTravelled = frtUtility.calculationByDistance(lastLocation, locationModel);*/

                // long time = ((actualTime - lastCapturedTime) / 1000);

                PTRRequestSaveRouteLocationmodel lastLocation = frtLocationDb.getLastLocationBySeqNumber();
                if (lastLocation != null) {
                    Date lastCapturedDate = frtUtility.getDateFromStringType2(lastLocation.getMobileTime());
                    long lastCapturedTime = lastCapturedDate.getTime();
                    double distanceTravelled = frtUtility.calculationByDistance(lastLocation, locationModel);
                    if (lastCapturedTime != actualTime) {
                        long time = ((actualTime - lastCapturedTime) / 1000);
                        time = (time <= 1) ? 1 : time;

                        double speed = (distanceTravelled/*distance*/ / time) * 3.6; // 3600/1000 (hours / KM)

                        if (speed <= 120) {
                            locationModel.setDrivingSpeed(doubleFormatter.format(speed));
                        }
                        else {
                            locationModel.setDrivingSpeed(doubleFormatter.format(130));
                        }
                        newUpdateDistance = (speed);
                    }

                   /* long time = ((actualTime - lastCapturedTime) / 1000);

                    time = (time <= 1) ? 1 : time;
                    double speed = (distanceTravelled*//*distance*//* / time) * 3.6; // 3600/1000 (hours / KM)
                    locationModel.setDrivingSpeed(doubleFormatter.format(speed));
                    newUpdateDistance = (speed);*/
                }


            } else {
                locationModel.setDrivingSpeed("0.0");
                locationModel.setDistFromLastLoc("0.0");
            }
            // set userid
            locationModel.setUserId(userId);

            //setting battery percentage
            locationModel.setBatteryPercentage(String.valueOf(frtUtility.getBatteryPercentage(getApplicationContext())));
//            LatLng userCurrentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
            try {
                locationModel.setDeviationFromPlanned(deviation);

               /* PTRResponseRouteCommonModel frtResponseRouteCommonModel = new PTRResponseRouteCommonModel();
                frtResponseRouteCommonModel.getSelf_check_in();
                if (!frtResponseRouteCommonModel.getSelf_check_in().equals("Yes")) {
                    locationModel.setDeviationFromPlanned(deviation);
                }else {
                    locationModel.setDeviationFromPlanned("0");
                }*/
            } catch (Exception ignored) {
                Log.e(TAG, ignored.toString());
            }
            //setting isinsidebuffer
            if (!TextUtils.isEmpty(deviation) && !TextUtils.isEmpty(bufferLimit)) {
                locationModel.setIsInsideBuffer(Float.parseFloat(locationModel.getDeviationFromPlanned()) >= Float.parseFloat(bufferLimit) ? "false" : "true");
            } else {
                locationModel.setIsInsideBuffer("false");
            }
            //getting provider
            // TODO: Change this
            if (!TextUtils.isEmpty(frtUtility.getNetworkProvider()))
                locationModel.setNetworkProvider(frtUtility.getNetworkProvider());
            else
                locationModel.setNetworkProvider(getString(R.string.nosim));
//            locationModel.setStatus("");
            //adding location model to db
            if (locationTempModel != locationModel/* && (TextUtils.isEmpty(frtSharePrefUtil.getString("lastsequencenumber"))
                    || !frtSharePrefUtil.getString("lastsequencenumber").equals(String.valueOf(frtLocationDb.getLastSequenceNumber())))*/) {
//                frtSharePrefUtil.setString("lastsequencenumber", String.valueOf(frtLocationDb.getLastSequenceNumber()));
                frtLocationDb.addLocationTracking(locationModel);
                locationTempModel = locationModel;
                tempTime = actualTime;
            }
            // adding addLocationTrackingTest speed set
            // TODO: It must send complete data to server before logout
            // TODO: If user logged out from server, it should send data to server from login screen
//            frtSharePrefUtil.setString("planneddata", "get");
            Log.d("PANKAJ", "LOCATION CAPTURED: " + locationModel.toString());
            frtUtility.logInfo(frtLocationTrackingService, "LOCATION CAPTURED: onLocation hanged " + locationModel.toString(), frtConstants);
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
        }
    }

    private void sendSmsCode(String deviationFromPlanned, String bufferLimit, String firstsms) {
        if (Float.parseFloat(deviationFromPlanned) >= Float.parseFloat(bufferLimit)) {
            try {
                if (!AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.manager_phone)).getBytes("UTF-16LE"), Base64.DEFAULT)).isEmpty()) {
                    String userName = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.username_shared)).getBytes("UTF-16LE"), Base64.DEFAULT));
                    // sendAlertSmsToManager(String.format(getString(R.string.outside_buffer_message_to_manager), frtSharePrefUtil.getString(getString(R.string.username_shared))), firstsms);
                    sendAlertSmsToManager(String.format(getString(R.string.outside_buffer_message_to_manager), userName) + " Latitude: " + latitudeSms + " Longitude: " + longitudeSms, firstsms);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sending message to looper to begin looping
     */
    private void sendMessage() {
        Message msg = new Message();
        msg.obj = "Looper Message";
        myServiceHandler.sendMessage(msg);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void getPlannedRouteData() {
        if (frtConnectivityReceiver.isConnected(frtLocationTrackingService)) {
            frtApp.setCheckForDialog("Yes");
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestPlannedRouteModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtLocationTrackingService);
            frtAsyncCommon.setFrtModel(setUpPlannedRouteModel());
            frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.PLANNED_ROUTE), "getplannedroute");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONObject jo = jsonObject.optJSONObject("results");
                            JSONArray jsonArray = jo.optJSONArray("planned_route");
                            frtResponsePlannedRouteModelList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo1 = jsonArray.optJSONObject(i);
                                if (jo1.optString("T").equals("R")) {
                                    PTRResponsePlannedRouteModel frtResponsePlannedRouteModel = new PTRResponsePlannedRouteModel();
                                    frtResponsePlannedRouteModel.setLat(jo1.optDouble("lat"));
                                    frtResponsePlannedRouteModel.setLng(jo1.optDouble("lng"));
                                    frtResponsePlannedRouteModelList.add(frtResponsePlannedRouteModel);
                                } else if (jo1.optString("T").equals("null")) {
                                    PTRResponsePlannedRouteModel frtResponsePlannedRouteModel = new PTRResponsePlannedRouteModel();
                                    frtResponsePlannedRouteModel.setLat(jo1.optDouble("lat"));
                                    frtResponsePlannedRouteModel.setLng(jo1.optDouble("lng"));
                                    frtResponsePlannedRouteModelList.add(frtResponsePlannedRouteModel);
                                }
                            }
                            buildGoogleApiClient();
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    private PTRRequestPlannedRouteModel setUpPlannedRouteModel() {
        PTRRequestPlannedRouteModel frtRequestAssignedRouteModel = new PTRRequestPlannedRouteModel();
        try {
            frtRequestAssignedRouteModel.setRouteId(Integer.parseInt(frtSharePrefUtil.getString("routeId")));
            frtRequestAssignedRouteModel.setRouteRefId(Integer.parseInt(frtSharePrefUtil.getString("routeRefId")));
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return frtRequestAssignedRouteModel;
    }

    public void sendAlertSmsToManager(final String msg, String firstsms) {
        try {
            // String phoneNo = frtSharePrefUtil.getString(getString(R.string.manager_phone));
            String phoneNo = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.manager_phone)).getBytes("UTF-16LE"), Base64.DEFAULT));
            //String phoneNo = "9811006556";
            SmsManager smsManager = SmsManager.getDefault();
            long currentTime = Calendar.getInstance().getTime().getTime();
            if (!TextUtils.isEmpty(firstsms) && ((currentTime - lastSmsSentTime)) > SMS_SENDING_DELAY_IN_MINUTE) {
                Log.e("loginnertimer", "logs " + currentTime);
                smsManager.sendTextMessage(phoneNo, null, msg, null, null);
                lastSmsSentTime = currentTime;
            } else if (TextUtils.isEmpty(firstsms)) {
                if (frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start")) {
                    frtSharePrefUtil.setString("firstsms", "true");
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    if (CALL_STATE_IDLE.equals(state)) {
                        SmsManager.getDefault().sendTextMessage(phoneNo, null, msg, null, null);
                        Log.e("log", "logsfirstSEoncd");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtLocationTrackingService)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtLocationTrackingService);
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