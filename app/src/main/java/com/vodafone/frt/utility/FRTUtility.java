package com.vodafone.frt.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.vodafone.frt.BuildConfig;
import com.vodafone.frt.R;
import com.vodafone.frt.activities.FRTLoginActivity;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.models.CommonSaveLeaveDetailModel;
import com.vodafone.frt.models.FRTCheckInOutDetailsModel;
import com.vodafone.frt.models.FRTRequestFrtAttendanceModel;
import com.vodafone.frt.models.MGRRequestAttendanceModel;
import com.vodafone.frt.models.MGRRequestGetRouteIssueDetails;
import com.vodafone.frt.models.MGRRequestUpdateLeaveStatus;
import com.vodafone.frt.models.PTRFeedbackModel;
import com.vodafone.frt.models.PTRGETSelfCheckInRequestModel;
import com.vodafone.frt.models.PTRGetUserLoyaltyStatusModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestActualRouteModel;
import com.vodafone.frt.models.PTRRequestAttendanceModel;
import com.vodafone.frt.models.PTRRequestGetBroadCastDetailModel;
import com.vodafone.frt.models.PTRRequestGetHaltPointsModel;
import com.vodafone.frt.models.PTRRequestGetPtrollerLocationModel;
import com.vodafone.frt.models.PTRRequestModelLogin;
import com.vodafone.frt.models.PTRRequestModelLogout;
import com.vodafone.frt.models.PTRRequestMyTaskModel;
import com.vodafone.frt.models.PTRRequestPauseReasonModel;
import com.vodafone.frt.models.PTRRequestPlannedRouteModel;
import com.vodafone.frt.models.PTRRequestSOSModel;
import com.vodafone.frt.models.PTRRequestSaveHaltPointsModel;
import com.vodafone.frt.models.PTRRequestSaveIssuesRaisedModel;
import com.vodafone.frt.models.PTRRequestSaveRouteAction;
import com.vodafone.frt.models.PTRRequestSaveRouteLocationmodel;
import com.vodafone.frt.models.PTRRequestSaveWeatherModel;
import com.vodafone.frt.models.PTRRequestScheduleRouteModel;
import com.vodafone.frt.models.PTRRequestSelfieDataModel;
import com.vodafone.frt.models.PTRRequestTrackPatrollerModel;
import com.vodafone.frt.models.PTRRequestUploadRouteTrackingDocModel;
import com.vodafone.frt.models.PTRRequestUserDetailModel;
import com.vodafone.frt.models.PTRResponseActualRouteModel;
import com.vodafone.frt.models.PTRResponsePlannedRouteModel;
import com.vodafone.frt.models.PTRSaveSelfCheckInModel;
import com.vodafone.frt.models.RequestAssignRejectIssueDetailModel;
import com.vodafone.frt.models.RequestGetSubOrdinateDetailsModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.nlopez.smartlocation.SmartLocation;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by vishal on 16/11/17
 */
public class FRTUtility<T> {
    @SuppressLint("StaticFieldLeak")
    private static final FRTUtility frtUtility = new FRTUtility();
    private final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private boolean checkList = false;
    private Context context;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTConstants frtConstants;
    private FRTApp frtApp;
    private GoogleMap googleMap;
    private int routeColor;
    private List<T> latlnglist;
    private int cameraId;
    private Bitmap bitmap;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private String encoded;

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @SuppressWarnings("deprecation")
    private ProgressDialog progressDialog;
    private LatLngBounds.Builder b;
    private LatLngBounds bounds;
    private boolean isOneTime;
    private Bitmap taken_image;
    private Bitmap round_Image;

    public FRTUtility() {
    }

    public static FRTUtility getUtilityInstance() {
        return frtUtility != null ? frtUtility : new FRTUtility();
    }

    public void setLatLngList(List<T> latLngList, boolean checkLists) {
        this.latlnglist = latLngList;
        checkList = checkLists;
    }

    public void setContext(Context ctx) {
        try {
            context = ctx;
            frtApp = (FRTApp) ((Activity) context).getApplicationContext();
            frtConstants = new FRTConstants();
        } catch (Exception ignored) {
        }
    }

    /**
     * Sets messages according to user actions
     *
     * @param message meesage to show
     * @param view    view instance
     */
    public void setSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbargreen));
        snackbar.show();
    }

    /**
     * checking mobile gps
     *
     * @param activity activity
     */
    @SuppressWarnings("deprecation")
    public void checkGps(Activity activity) {
        // Check Location (API dependant)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String provider = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            }
            assert provider != null;
            if (!(provider.contains(context.getString(R.string.gpsprovider)) && provider.contains(context.getString(R.string.networkprovider)))) {
                showLocationDialog(activity);
            }
        } else {
            int mode;
            try {
                mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                mode = Settings.Secure.LOCATION_MODE_OFF;
            }
            if (mode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                showLocationDialog(activity);
            }
        }
    }

    public boolean isGpsEnabled() {
        return (SmartLocation.with(context).location().state().locationServicesEnabled() &&
                SmartLocation.with(context).location().state().isAnyProviderAvailable());
    }

    /**
     * showing location dialog when gps off
     *
     * @param ctx activity context
     */
    public void showLocationDialog(final Activity ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(R.string.ASLocationMessage);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ctx.finish();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    //ashutosh
    public double getShortestDeviationpath(List<PTRResponsePlannedRouteModel> plannedRouteList, LatLng currentLoaction) {

        ArrayList<Double> list = new ArrayList<>();
        for (int i = 0; i < plannedRouteList.size() - 1; i++) {
            list.add(calculationByDistance(new LatLng(plannedRouteList.get(i).getLat(), plannedRouteList.get(i).getLng()), currentLoaction));
        }
        return Collections.min(list);
    }


    public double getShortestDistanceBetweenHaltPointAndCurrentLocation(List<LatLng> plannedRouteList, LatLng currentLoaction) {

        ArrayList<Double> list = new ArrayList<>();
        for (int i = 0; i < plannedRouteList.size() - 1; i++) {
            list.add(calculationByDistance(new LatLng(plannedRouteList.get(i).latitude, plannedRouteList.get(i).longitude),
                    currentLoaction));
        }
        return Collections.min(list);
    }

    /**
     * Login name value pair composition
     *
     * @param params               name-value pair list instance
     * @param frtRequestModelLogin login model instance
     * @return name-value pair list
     */
    @SuppressWarnings("deprecation")
    private List<NameValuePair> prepareDataLogin(List<NameValuePair> params, PTRRequestModelLogin frtRequestModelLogin) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair("username", frtRequestModelLogin.getUsername());
        params.add(basicNameValuePair);
        BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair("password", frtRequestModelLogin.getPassword());
        params.add(basicNameValuePair1);
        BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair("grant_type", frtRequestModelLogin.getGrant_type());
        params.add(basicNameValuePair2);
        BasicNameValuePair basicNameValuePair3 = new BasicNameValuePair("osName", frtRequestModelLogin.getOsName());
        params.add(basicNameValuePair3);
        BasicNameValuePair basicNameValuePair4 = new BasicNameValuePair("osVersion", frtRequestModelLogin.getOsVersion());
        params.add(basicNameValuePair4);
        BasicNameValuePair basicNameValuePair5 = new BasicNameValuePair("macAddress", frtRequestModelLogin.getMacAddress());
        params.add(basicNameValuePair5);
        BasicNameValuePair basicNameValuePair6 = new BasicNameValuePair("fcmkey", frtRequestModelLogin.getFcmkey());
        params.add(basicNameValuePair6);
        BasicNameValuePair basicNameValuePair7 = new BasicNameValuePair("osType", frtRequestModelLogin.getOsType());
        params.add(basicNameValuePair7);
        BasicNameValuePair basicNameValuePair8 = new BasicNameValuePair("appVersion", frtRequestModelLogin.getAppVersion());
        params.add(basicNameValuePair8);
        logInfo(context, "Login request " + params.toString(), frtConstants);
        return params;
    }

    private JSONObject prepareSaveRouteAction(PTRRequestSaveRouteAction frtModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("assignedRouteId", frtModel.getAssignedRouteId());
            jsonObject.put("action", frtModel.getAction());
            jsonObject.put("latitude", frtModel.getLatitude());
            jsonObject.put("longitude", frtModel.getLongitude());
            jsonObject.put("mobileTime", frtModel.getMobileTime());
            jsonObject.put("reasonId", frtModel.getReasonId());
            jsonObject.put("remark", frtModel.getRemark());
//            List<Map<String, String>> mapList = frtModel.getBase64encodedstringList();
//            for (int i = 0; i < mapList.size(); i++) {
//                switch (i) {
//                    case 0:
//                        JSONObject jsonObject1 = new JSONObject();
//                        jsonObject1.put("imageData", mapList.get(i).get("image1"));
//                        jsonArray.put(jsonObject1);
//                        break;
//                    case 1:
//                        JSONObject jsonObject2 = new JSONObject();
//                        jsonObject2.put("imageData", mapList.get(i).get("image2"));
//                        jsonArray.put(jsonObject2);
//                        break;
//                    case 2:
//                        JSONObject jsonObject3 = new JSONObject();
//                        jsonObject3.put("imageData", mapList.get(i).get("image3"));
//                        jsonArray.put(jsonObject3);
//                        break;
//                    case 3:
//                        JSONObject jsonObject4 = new JSONObject();
//                        jsonObject4.put("imageData", mapList.get(i).get("image4"));
//                        jsonArray.put(jsonObject4);
//                        break;
//                    default:
//                        break;
//                }
//            }
            for (String image : frtModel.getBase64encodedstringList()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("imageData", image);
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("images", jsonArray);
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "SaveRouteAction request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    @SuppressWarnings("deprecation")
    private List<NameValuePair> prepareLoginRefreshTokenService(List<NameValuePair> params, PTRLoginRefreshRequestModel frtModel) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair("refresh_token", frtModel.getRefresh_token());
        params.add(basicNameValuePair);
        BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair("grant_type", frtModel.getGrant_type());
        params.add(basicNameValuePair1);
        logInfo(context, "RefreshToken request " + params.toString(), frtConstants);
        return params;
    }

    private JSONObject prepareDataPauseReason(PTRRequestPauseReasonModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("reasonType", cqModel.getReasonType());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "PauseReason request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareUserDetail(PTRRequestUserDetailModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userName", cqModel.getUserName());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "UserDetail request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataPlannedRoute(PTRRequestPlannedRouteModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeId", cqModel.getRouteId());
            jsonObject.put("routeRefId", cqModel.getRouteRefId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "PlannedRoute request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareAttendanceData(PTRRequestAttendanceModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("patrollerId", cqModel.getPatrollerId());
            jsonObject.put("fromDate", cqModel.getFromDate());
            jsonObject.put("toDate", cqModel.getToDate());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "Attendance request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }



    private JSONObject prepareFRTAttendanceData(FRTRequestFrtAttendanceModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("frtId", cqModel.getFrtId());
            jsonObject.put("fromDate", cqModel.getFromDate());
            jsonObject.put("toDate", cqModel.getToDate());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "Attendance request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }


    private JSONObject prepareGetHaltPointsData(PTRRequestGetHaltPointsModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeId", cqModel.getRouteId());
            jsonObject.put("routeRefId", cqModel.getRouteRefId());
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        logInfo(context, "GetHaltPoints request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareSaveWeatherData(PTRRequestSaveWeatherModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            jsonObject.put("humidity", cqModel.getHumidity());
            jsonObject.put("temperature", cqModel.getTemperature());
            jsonObject.put("precipitation", cqModel.getPrecipitation());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }/* catch (Exception OutOfMemoryError) {
            setSnackBar(context.getString(R.string.maxUploadDocumentSize), ((Activity) context).findViewById(android.R.id.content));
        }*/
        logInfo(context, "SaveWeatherData request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareUploadDocData(PTRRequestUploadRouteTrackingDocModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jsonObject.put("fileName", cqModel.getFileName());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            jsonObject.put("remarks", cqModel.getRemarks());
            jsonObject.put("fileData", cqModel.getFileData());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        } catch (Exception OutOfMemoryError) {
            setSnackBar(context.getString(R.string.maxUploadDocumentSize), ((Activity) context).findViewById(android.R.id.content));
        }
        logInfo(context, "UploadDoc request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareSaveRouteIssueData(PTRRequestSaveIssuesRaisedModel frtModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("routeAssignmentId", frtModel.getRouteAssignmentId());
            jsonObject.put("userId", frtModel.getUserId());
            jsonObject.put("managerId", frtModel.getManagerId());
            jsonObject.put("latitude", frtModel.getLatitude());
            jsonObject.put("longitude", frtModel.getLongitude());
            jsonObject.put("mobileTime", frtModel.getMobileTime());
            jsonObject.put("issueTypeId", frtModel.getIssueTypeId());
            jsonObject.put("remark", frtModel.getRemark());
            JSONArray jsonArray1 = new JSONArray();
            for (String image : frtModel.getImages()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("imageData", image);
                jsonArray1.put(jsonObject1);
            }
            jsonObject.put("images", jsonArray1);
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        String request = jo.toString();
        Log.d("REQUEST", request);
        frtUtility.logInfo(context, "SaveRouteIssueDetail request " + jo.toString(), frtConstants);
        return jo;
    }

    private JSONObject prepareSaveHaltRoute(PTRRequestSaveHaltPointsModel frtModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("routeAssignmentId", frtModel.getRouteAssignmentId());
            jsonObject.put("action", frtModel.getAction());
            jsonObject.put("latitude", frtModel.getLatitude());
            jsonObject.put("longitude", frtModel.getLongitude());
            jsonObject.put("dateTime", frtModel.getDateTime());
            jsonObject.put("userId", frtModel.getUserId());
            jsonObject.put("remark", frtModel.getRemark());
            jsonObject.put("haltTrackingId", frtModel.getHaltTrackingId());
            JSONArray jsonArray1 = new JSONArray();
            for (String image : frtModel.getImages()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("imageData", image);
                jsonArray1.put(jsonObject1);
            }
            jsonObject.put("images", jsonArray1);
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        String request = jo.toString();
        Log.d("REQUEST", request);
//        frtUtility.logInfo(context, request, new FRTConstants());
        frtUtility.logInfo(context, "SaveHaltRoute request " + jo.toString(), frtConstants);
        return jo;
    }

    private JSONObject prepareSubordinateData(PTRRequestTrackPatrollerModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("roleName", cqModel.getRoleName());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "Subordinate request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareScheduledRouteData(PTRRequestScheduleRouteModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getuserId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "ScheduledRoute request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataActualRoute(PTRRequestActualRouteModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "ActualRoute request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDatalLogout(PTRRequestModelLogout cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "Logout request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataTaskDetail(PTRRequestMyTaskModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "TaskDetail request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareSaveLeaveDetail(CommonSaveLeaveDetailModel cqModel) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("roleId", cqModel.getRoleId());
            jsonObject.put("fromDate", cqModel.getFromDate());
            jsonObject.put("toDate", cqModel.getToDate());
            jsonObject.put("reason", cqModel.getReason());

            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "SaveLeaveDetail request " + jo.toString(), frtConstants);
        Log.d(this.getClass().getName(), "LEAVE_JSON" + jo.toString());
        return jo;
    }


    private JSONObject prepareGetRouteIssueDetail(MGRRequestGetRouteIssueDetails cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());

            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "GetRouteIssueDetail request " + jo.toString(), frtConstants);
        Log.d(this.getClass().getName(), "JSON_TO_GET_ROUTE_ISSUE_DETAILS" + jo.toString());
        return jo;
    }


    private JSONObject prepareGetSubordinateDetails(RequestGetSubOrdinateDetailsModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("roleName", cqModel.getRoleName());
            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getName(), "JSON_TO_ASSIGN_REJECT" + jo.toString());
        return jo;
    }


    private JSONObject prepareAssignRejectIssue(RequestAssignRejectIssueDetailModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("issuesId", cqModel.getIssueIdString());
            jsonObject.put("status", cqModel.getStatus());
            jsonObject.put("frtUserId", cqModel.getFrtUserId());
            jsonObject.put("assignDate", cqModel.getAssignDate());
            jsonObject.put("remark", cqModel.getRemark());
            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        frtUtility.logInfo(context, "AssignRejectIssue request " + jo.toString(), frtConstants);
//        Log.d(this.getClass().getName(),"JSON_TO_ASSIGN_REJECT" + jo.toString());
        Log.d(this.getClass().getName(), "JSON_UpdateRouteIssueDetail" + jo.toString());
        return jo;
    }


    private JSONObject prepareUpdateLEaveDetails(MGRRequestUpdateLeaveStatus cqModel) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            //jsonObject.put("leaveId",cqModel.getLeaveId());
            jsonObject.put("leaveIds", cqModel.getLeaveIds());
            jsonObject.put("managerId", cqModel.getManagerId());
            jsonObject.put("status", cqModel.getStatus());
            jsonObject.put("remark", cqModel.getRemark());
            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getName(), "JSON_UpdateLeaveDetail" + jo.toString());
        return jo;
    }


    private JSONObject prepareRejectIssue(RequestAssignRejectIssueDetailModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("issuesId", cqModel.getIssueIdRejected());
            jsonObject.put("status", cqModel.getStatus());
            jsonObject.put("frtUserId", cqModel.getFrtUserId());
            jsonObject.put("assignDate", cqModel.getAssignDate());
            jsonObject.put("remark", cqModel.getRemark());
            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getName(), "JSON_UpdateRouteIssueDetail" + jo.toString());
        return jo;
    }


    private JSONObject prepareGetUserLeaveDetail(MGRRequestAttendanceModel cqModel) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("roleId", cqModel.getRoleId());
            jsonObject.put("subordinateId", cqModel.getSubordinateId());

            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "GetUserLeaveDetail request " + jo.toString(), frtConstants);
        Log.d(this.getClass().getName(), "JSON_GET_USER_LEAVE_DETAIL" + jo.toString());
        return jo;
    }


    private JSONObject prepareSaveTaskTracking(FRTCheckInOutDetailsModel cqModel) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("taskTrackingId", cqModel.getTaskTrackingId());
            jsonObject.put("issueId", cqModel.getIssueId());
            jsonObject.put("action", cqModel.getAction());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("dateTime", cqModel.getDateTime());
            jsonObject.put("remark", cqModel.getRemark());
            for (String image : cqModel.getBase64encodedstringList()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("imageData", image);
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("images", jsonArray);
            //jsonObject.put("imageData", cqModel.getImageData());
            jo.put("data", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "SaveTaskTracking request " + jo.toString(), frtConstants);
        return jo;
    }


    private JSONObject prepareDataSaveRouteLocation(PTRRequestSaveRouteLocationmodel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("batteryPercentage", cqModel.getBatteryPercentage());
            jsonObject.put("deviationFromPlanned", cqModel.getDeviationFromPlanned());
            jsonObject.put("distFromLastLoc", cqModel.getDistFromLastLoc());
            jsonObject.put("drivingSpeed", cqModel.getDrivingSpeed());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            jsonObject.put("networkProvider", cqModel.getNetworkProvider());
            jsonObject.put("isInsideBuffer", cqModel.getIsInsideBuffer());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "SaveRouteLocation request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataSelfCheckInRequest(PTRGETSelfCheckInRequestModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserid());
            object.put("data", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "SelfCheckIn request " + object.toString(), frtConstants);
        Log.d("REQUEST_CHECKIN", object.toString());
        return object;
    }


    private JSONObject prepareDatSaveSelfCheckInRoute(PTRSaveSelfCheckInModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jsonObject.put("managerId", cqModel.getManagerId());
            jsonObject.put("routeName", cqModel.getRouteName());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("remarks", cqModel.getRemarks());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            jsonObject.put("plannedStartTime", cqModel.getPlannedStartTime());
            jsonObject.put("plannedEndTime", cqModel.getPlannedEndTime());

            object.put("data", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "SaveSelfCheckInRoute request " + object.toString(), frtConstants);
        Log.d("REQUEST_CHECKIN_ROUTE", object.toString());
        return object;
    }

    private JSONObject prepareDataGetSosDetail(PTRRequestSOSModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "GetSosDetail request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataGetBroadCastDetail(PTRRequestGetBroadCastDetailModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserId());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "GetBroadCastDetail request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }


    private JSONObject prepareDataSavefeedback(PTRFeedbackModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserid());
            jsonObject.put("feedbackText", cqModel.getFeedbackText());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            object.put("data", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "Savefeedback request " + object.toString(), frtConstants);
        Log.d("REQUEST_FEEDBACK", object.toString());
        return object;
    }

    private JSONObject prepareDataLoyaltyStatus(PTRGetUserLoyaltyStatusModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserid());
            jsonObject.put("roleId", cqModel.getRoleId());

            object.put("data", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frtUtility.logInfo(context, "LoyaltyStatus request " + object.toString(), frtConstants);
        Log.d("REQUEST_LOYALITY", object.toString());
        return object;
    }


    private JSONObject prepareDataPatrollerCurrentLocation(PTRRequestGetPtrollerLocationModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("userId", cqModel.getUserid());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "PatrollerCurrentLocation request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareDataBulkTrackLocation(List<PTRRequestSaveRouteLocationmodel> cqModelList) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        try {
            for (int i = 0; i < cqModelList.size(); i++) {
                PTRRequestSaveRouteLocationmodel frtRequestSaveRouteLocationmodel = cqModelList.get(i);
                String json = frtRequestSaveRouteLocationmodel.getJsontoupload();
                // TODO: parse JSON
                // TODO: set Sequence number
                // TODO: create json again
                JSONObject jsonObject2 = new JSONObject(json);
                jsonObject2.put("seqId", "" + frtRequestSaveRouteLocationmodel.getSeqId());
//                jsonObject2.remove(frtRequestSaveRouteLocationmodel.getStatus());
                jsonArray.put(jsonObject2);
            }
            jsonObject.put("trackingData", jsonArray);
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "BulkTrackLocation request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private JSONObject prepareGetSelfieData(PTRRequestSelfieDataModel cqModel) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        try {
            jsonObject.put("routeAssignmentId", cqModel.getRouteAssignmentId());
            jsonObject.put("action", cqModel.getAction());
            jsonObject.put("latitude", cqModel.getLatitude());
            jsonObject.put("longitude", cqModel.getLongitude());
            jsonObject.put("mobileTime", cqModel.getMobileTime());
            jsonObject.put("remarks", cqModel.getRemarks());
            jsonObject.put("imageData", cqModel.getImageData());
            jo.put("data", jsonObject.toString());
        } catch (JSONException ignored) {
        }
        frtUtility.logInfo(context, "GetSelfieData request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    private <T> JSONObject preparesaverouteofflineData(T frtRequestSaveRouteLocationmodelList) {
        JSONObject jo = new JSONObject();
        PTRRequestSaveRouteLocationmodel frtRequestSaveRouteLocationmodel = ((List<PTRRequestSaveRouteLocationmodel>) frtRequestSaveRouteLocationmodelList).get(0);
        String json = frtRequestSaveRouteLocationmodel.getJsontoupload();
        try {
            jo.put("data", json);
        } catch (Exception e) {
        }
        frtUtility.logInfo(context, "saverouteofflineData request " + jo.toString(), frtConstants);
        Log.d("REQUEST", jo.toString());
        return jo;
    }

    @SuppressWarnings("deprecation")
    public <T> Object prepareData(String action, Object cqModel, List<NameValuePair> params, T frtRequestSaveRouteLocationmodelList, String url) {
        String response = null;
        switch (action) {
            case "login":
                prepareDataLogin(params, (PTRRequestModelLogin) cqModel);
                break;
            case "loginrefresh":
                prepareLoginRefreshTokenService(params, (PTRLoginRefreshRequestModel) cqModel);
                break;
            case "saverouteaction":
                response = doPostRequest(url, prepareSaveRouteAction((PTRRequestSaveRouteAction) cqModel), action);
                break;
            case "saverouteofflineaction":
                /*JSONObject jo = new JSONObject();
                PTRRequestSaveRouteLocationmodel frtRequestSaveRouteLocationmodel = ((List<PTRRequestSaveRouteLocationmodel>) frtRequestSaveRouteLocationmodelList).get(0);
                String json = frtRequestSaveRouteLocationmodel.getJsontoupload();
                try {
                    jo.put("data", json);
                } catch (Exception e) {
                }
                response = doPostRequest(url, jo, action);*/
                response = doPostRequest(url, preparesaverouteofflineData(frtRequestSaveRouteLocationmodelList), action);
                break;
            case "getreasondetail":
                response = doPostRequest(url, prepareDataPauseReason((PTRRequestPauseReasonModel) cqModel), action);
                break;
            case "getuserdetail":
                response = doPostRequest(url, prepareUserDetail((PTRRequestUserDetailModel) cqModel), action);
                break;
            case "getplannedroute":
                response = doPostRequest(url, prepareDataPlannedRoute((PTRRequestPlannedRouteModel) cqModel), action);
                break;
            case "getactualroute":
                response = doPostRequest(url, prepareDataActualRoute((PTRRequestActualRouteModel) cqModel), action);
                break;
            case "gettaskdetail":
                response = doPostRequest(url, prepareDataTaskDetail((PTRRequestMyTaskModel) cqModel), action);
                break;
            case "saveroute":
                response = doPostRequest(url, prepareDataSaveRouteLocation((PTRRequestSaveRouteLocationmodel) cqModel), action);
                break;
            case "getAttendance":
                response = doPostRequest(url, prepareAttendanceData((PTRRequestAttendanceModel) cqModel), action);
                break;

            case "GetFRTAttendance":
                response = doPostRequest(url, prepareFRTAttendanceData((FRTRequestFrtAttendanceModel) cqModel), action);
                break;

            case "getscheduledroute":
                response = doPostRequest(url, prepareScheduledRouteData((PTRRequestScheduleRouteModel) cqModel), action);
                break;
            case "logout":
                response = doPostRequest(url, prepareDatalLogout((PTRRequestModelLogout) cqModel), action);
                break;
            case "snaptoroadgoogle":
                response = doPostRequest(url, null, action);
                break;
            case "getsubordinate":
                response = doPostRequest(url, prepareSubordinateData((PTRRequestTrackPatrollerModel) cqModel), action);
                break;
            case "getcurrentlocation":
                response = doPostRequest(url, prepareDataPatrollerCurrentLocation((PTRRequestGetPtrollerLocationModel) cqModel), action);
                break;
            case "sos":
                response = doPostRequest(url, prepareDataGetSosDetail((PTRRequestSOSModel) cqModel), action);
                break;
            case "saveroutebulk":
                response = doPostRequest(url, prepareDataBulkTrackLocation((List<PTRRequestSaveRouteLocationmodel>) frtRequestSaveRouteLocationmodelList), action);
                break;
            case "getHaltPoints":
                response = doPostRequest(url, prepareGetHaltPointsData((PTRRequestGetHaltPointsModel) cqModel), action);
                break;
            case "sendselfiedata":
                response = doPostRequest(url, prepareGetSelfieData((PTRRequestSelfieDataModel) cqModel), action);
                break;
            case "saveHaltPoints":
                response = doPostRequest(url, prepareSaveHaltRoute((PTRRequestSaveHaltPointsModel) cqModel), action);
                break;
            case "UploadDoc":
                response = doPostRequest(url, prepareUploadDocData((PTRRequestUploadRouteTrackingDocModel) cqModel), action);
                break;
            case "SaveWeather":
                response = doPostRequest(url, prepareSaveWeatherData((PTRRequestSaveWeatherModel) cqModel), action);
                break;
            case "SaveRouteIssueDetail":
                response = doPostRequest(url, prepareSaveRouteIssueData((PTRRequestSaveIssuesRaisedModel) cqModel), action);
                break;
            case "getCheckInRequest":
                response = doPostRequest(url, prepareDataSelfCheckInRequest((PTRGETSelfCheckInRequestModel) cqModel), action);
                break;
            case "saveSelfCheckIn":
                response = doPostRequest(url, prepareDatSaveSelfCheckInRoute((PTRSaveSelfCheckInModel) cqModel), action);
                break;
            case "savefeedback":
                response = doPostRequest(url, prepareDataSavefeedback((PTRFeedbackModel) cqModel), action);
                break;
            case "GetBroadCastDetail":
                response = doPostRequest(url, prepareDataGetBroadCastDetail((PTRRequestGetBroadCastDetailModel) cqModel), action);
                break;
            case "GetUserLoyaltyStatus":
                response = doPostRequest(url, prepareDataLoyaltyStatus((PTRGetUserLoyaltyStatusModel) cqModel), action);
                break;
            case "GetUserFeedbackMgr":
                response = doPostRequest(url, prepareDataLoyaltyStatus((PTRGetUserLoyaltyStatusModel) cqModel), action);
                break;

            case "gettaskdetailForFrt":
                response = doPostRequest(url, prepareDataTaskDetail((PTRRequestMyTaskModel) cqModel), action);
                break;

            case "SaveTaskTracking":
                response = doPostRequest(url, prepareSaveTaskTracking((FRTCheckInOutDetailsModel) cqModel), action);
                break;
            case "SaveLeaveDetail":
                response = doPostRequest(url, prepareSaveLeaveDetail((CommonSaveLeaveDetailModel) cqModel), action);
                break;

            case "GetLeaveDetail":
                response = doPostRequest(url, prepareGetUserLeaveDetail((MGRRequestAttendanceModel) cqModel), action);
                break;

            case "GetRouteIssueDetail":
                response = doPostRequest(url, prepareGetRouteIssueDetail((MGRRequestGetRouteIssueDetails) cqModel), action);
                break;

            case "UpdateRouteIssueDetail":
                response = doPostRequest(url, prepareAssignRejectIssue((RequestAssignRejectIssueDetailModel) cqModel), action);
                break;


            case "GetSubordinateDetails":
                response = doPostRequest(url, prepareGetSubordinateDetails((RequestGetSubOrdinateDetailsModel) cqModel), action);
                break;

            case "UpdateRouteIssueDetailRejected":
                response = doPostRequest(url, prepareRejectIssue((RequestAssignRejectIssueDetailModel) cqModel), action);
                break;

            case "UpdateLeaveDetail":
                response = doPostRequest(url, prepareUpdateLEaveDetails((MGRRequestUpdateLeaveStatus) cqModel), action);
                break;

        }
        if (action.equals("login") || action.equals("loginrefresh"))
            return action;
        else
            return response;
    }


    private String doPostRequest(String url, JSONObject jsonObject, String action) {
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(context);
        client.setConnectTimeout(40, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(40, TimeUnit.SECONDS);    // socket timeout
        client.setWriteTimeout(40, TimeUnit.SECONDS);    // socket timeout
        Response response;
        String responses = null;
        Request request;
        try {
            if (!action.equals("snaptoroadgoogle")) {
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                request = new Request.Builder()
                        .header("Authorization", "bearer" + " " + frtSharePrefUtil.getString(context.getString(R.string.token_key)))
                        .header("Content-Type", "application/json")
                        .url(url)
                        .post(body)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .build();
            }
            response = client.newCall(request).execute();
            responses = response.body().string();
        } catch (Exception ignored) {
        }

        return responses;
    }

    /**
     * This method gave us the input query
     *
     * @param params name value pair list
     * @return the name value pair composite string
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("deprecation")
    public String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            try {
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(this.getClass().getName(), "EXCEPTION==" + e.getMessage());
            }

        }
        Log.d("REQUEST", result.toString());
        return result.toString();
    }

    /**
     * This method is used for showing dialog
     */
    @SuppressWarnings("deprecation")
    public void showDialog() {
        try {
            if (progressDialog == null) {
                dialogPresent();
            }
        } catch (Exception ignored) {
        }
    }


    public ProgressDialog dialogPresent() {
        try {
            progressDialog = new ProgressDialog(context);
            show(progressDialog);
        } catch (Exception ex) {
        }
        return progressDialog;
    }

    public void show(ProgressDialog progressDialog) {
        progressDialog.setMessage(context.getString(R.string.loaderttext));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    /**
     * This method is used for hiding dialog
     */
    public void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                dialogHide();
            }
        } catch (Exception ignored) {
        }
    }

    public void dialogHide() {

        try {
            progressDialog.dismiss();
            progressDialog = null;
        } catch (Exception ex) {
        }
    }

    /**
     * Common Structure for dialog
     *
     * @param dialog   dialog instance
     * @param layoutid layout id
     */
    public Dialog dialogBasicStructure(Dialog dialog, int layoutid) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutid);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((int) 0.9f));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window.getAttributes().windowAnimations = R.style.DialogAnimation_slideinout;
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * setting status bar color
     *
     * @param baseActivity actvity instance
     * @param color        color value
     */
    @SuppressWarnings("deprecation")
    public void settingStatusBarColor(Activity baseActivity, int color) {
        Window window = baseActivity.getWindow();
        if (Build.VERSION.SDK_INT >= 19)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= 21)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= 23)
            window.setStatusBarColor(baseActivity.getResources().getColor(color, null));
        else if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(baseActivity.getResources().getColor(color));
        }
    }

    /**
     * This method returns the sdk build version number
     *
     * @return build version number
     */
    public String getAppVersion() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        assert info != null;
        return String.format(context.getString(R.string.version), info.versionName);
    }

    public void showSoftKeyboard(EditText edt, Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(INPUT_METHOD_SERVICE);
        edt.requestFocus();
        imm.showSoftInput(edt, 0);
    }

    public void hideSoftKeyboard(EditText edt, Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }


//    public void removeMarkers(List<Marker> mMarkers) {
//        try {
//            if (mMarkers.size() > 0) {
//                for (Marker marker : mMarkers) {
//                    marker.remove();
//                }
//                mMarkers.clear();
//            }
//        } catch (Exception ignored) {
//        }
//    }

    public void exitApp() {
        try {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeIntent);
        } catch (Exception ignored) {
        }
    }

//    public void setParametersForRoute(LatLng latLngSource, LatLng latLngDest) {
//        String url = getUrl(latLngSource, latLngDest);
//        Log.d("onMapClick", url);
//        boolean isGettingParametersToUpload = true;
//        try {
//            downloadUrl(url);
//        } catch (IOException ignored) {
//        }
//    }

    /**
     * Method will create a webview
     * <p>
     * //     * @param webgif webview instance
     * //     * @param url    url
     */
//    @SuppressLint("SetJavaScriptEnabled")
//    public WebView createWebView(WebView webgif, String url) {
//        if (Build.VERSION.SDK_INT >= 17)
//            webgif.getSettings().setJavaScriptEnabled(true);
//        webgif.getSettings().setUseWideViewPort(false);
//        Log.d("url", url);
//        webgif.setScrollContainer(false);
//        webgif.loadData(url, "text/html", "UTF-8");
//        MyWebClient myWebViewClient = new MyWebClient();
//        webgif.setWebViewClient(myWebViewClient);
//        return webgif;
//    }
    private GnssStatus.Callback getGpsCallbackOnNougatAndAbove() {
        GnssStatus.Callback locationGnssCallback = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationGnssCallback = new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    checkGps((Activity) context);
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                }

                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                }
            };
        }
        return locationGnssCallback;
    }

    @SuppressWarnings("deprecation")
    private GpsStatus.Listener locationGpsListenerBelowNougat() {
        GpsStatus.Listener locationGpsListener = null;
        try {
            locationGpsListener = new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int i) {
                    try {
                        if (i == GpsStatus.GPS_EVENT_STOPPED)
                            checkGps((Activity) context);
                    } catch (Exception ignored) {
                    }
                }
            };
        } catch (Exception ignored) {
        }
        return locationGpsListener;
    }

    @SuppressWarnings("deprecation")
    public void handleGps(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT < 24) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.addGpsStatusListener(locationGpsListenerBelowNougat());
        } else
            locationManager.registerGnssStatusCallback(getGpsCallbackOnNougatAndAbove());
    }

    public boolean isLogoutStatus(String s) {
        if (s.equals(context.getString(R.string.req_denied))) {
            return true;
        } else if (s.equals(context.getString(R.string.session_exp))) {
            return true;
        }
        return false;
    }

    public void goToActivity(FRTLocationTrackingService frtLocationTrackingService, Class<T> frtActivitycls) {
        Intent intent = new Intent(frtLocationTrackingService, frtActivitycls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        frtLocationTrackingService.startActivity(intent);
    }

    public void drawRoute(LatLng latLngSource, LatLng latLngDest, List<Marker> markers, int color, LatLng userCurrentLatLon, int startmarker, int endmarker) {
        routeColor = color;
        if (markers.size() > 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }
        Marker startMarker = null, endMarker;
//        if (!isOneTime() && checkList)
        startMarker = googleMap.addMarker(new MarkerOptions().position(latLngSource).icon(BitmapDescriptorFactory.fromResource(startmarker)));
        endMarker = googleMap.addMarker(new MarkerOptions().position(latLngDest).icon(BitmapDescriptorFactory.fromResource(endmarker)));
        markers.add(startMarker);
        markers.add(endMarker);
        // Getting URL to the Google Directions API
        String url = getUrl(latLngSource, latLngDest);
        Log.d("routeurl", url);
        FetchUrl fetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        fetchUrl.execute(url);
        b = new LatLngBounds.Builder();
        b.include(userCurrentLatLon);
        if (checkList) {
            //noinspection unchecked
            for (PTRResponsePlannedRouteModel frtResponsePlannedRouteModel : (List<PTRResponsePlannedRouteModel>) latlnglist) {
                b.include(new LatLng(frtResponsePlannedRouteModel.getLat(), frtResponsePlannedRouteModel.getLng()));
            }
        } else {
            //noinspection unchecked
            for (PTRResponseActualRouteModel frtResponseActualRouteModel : (List<PTRResponseActualRouteModel>) latlnglist) {
                b.include(new LatLng(frtResponseActualRouteModel.getLat(), frtResponseActualRouteModel.getLng()));
            }
        }
        bounds = b.build();
        if (!frtApp.isOneTime()) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 40);
            googleMap.animateCamera(cu);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        //noinspection TryFinallyCanBeTryWithResources
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();
        } catch (Exception ignored) {
            Log.e("tagdownloadurl", ignored.toString());
        } finally {
            try {
                assert iStream != null;
                iStream.close();
                assert urlConnection != null;
                urlConnection.disconnect();
            } catch (IOException ignored) {
            }
        }
        return data;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    public void setGoogleMap(GoogleMap gm) {
        googleMap = gm;
    }

    /**
     * This method returns the sdk build version number
     *
     * @return build version number
     */
    public String getSdkBuildVersionNumber() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion + "";
    }

    /**
     * This method returns the Mac address
     *
     * @return build version number
     */
    public String getMacAddress() {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        @SuppressLint("HardwareIds") String address = info.getMacAddress();
        return address;
    }

    public String getBase64FromString(String input) {
        byte[] data;
        String base64 = null;
        try {
            data = input.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            //  e.printStackTrace();
        }
        return base64;
    }


    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

   /* public String getDecodeBase64ToString(String encodedBytes){
        byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
        try {
            String  decodeString =new String(decodedBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // System.out.println("decodedBytes: " + new String(decodedBytes, UTF_8));
    }*/


    public String hashWith256(String textToHash) {
        MessageDigest digest = null;
        byte[] hash = new byte[0];
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(textToHash.getBytes());
            encoded = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim();
           /* digest = MessageDigest.getInstance("SHA-256");
             hash = digest.digest(textToHash.getBytes("UTF-8"));
             encoded = Base64.encodeToString(hash, Base64.DEFAULT);*/
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } /*catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        return encoded;
    }


    public static String sha256Encryption(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("sha-256");
            byte[] bytes = md5.digest((string).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    @SuppressLint("SimpleDateFormat")
    public String getDateAndTime(Date d, boolean isUpload) {
        System.out.println("Start time => " + d.getTime());
        SimpleDateFormat df;
        if (isUpload)
            df = new SimpleDateFormat("dd-MMM-yy HH:mm:ss", Locale.ENGLISH);
        else
            df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        return df.format(d.getTime());
    }

    public String getLocationDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
        return format.format(new Date(time));
    }

    public String getDateAndTime(boolean isUpload) {
        return getDateAndTime(Calendar.getInstance().getTime(), isUpload);
    }

    public String getNetworkProvider() {
        // Get System TELEPHONY service reference
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // Get carrier name (Network Operator Name)
        return TextUtils.isEmpty(tManager.getNetworkOperatorName()) ? "" : tManager.getNetworkOperatorName();
    }

    public String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public void goToLogin(final String status) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCodeForLoginActivityTransition(status);
                }
            }, frtConstants.KEY_SPLASH_TIME);
        } catch (Exception ignored) {
        }
    }

    private void sendCodeForLoginActivityTransition(String status) {
        try {
            if (status.equals(context.getString(R.string.req_denied)))
                startLoginActivity(context.getString(R.string.reqdeniedmessage));
            else
                startLoginActivity(context.getString(R.string.sessionexpired));
            clearPrerences();
        } catch (Exception ignored) {
        }
    }

    private void startLoginActivity(String message) {
        try {
            Intent intent = new Intent(context, FRTLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("keyLogoutMesage", message);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } catch (Exception ignored) {
        }
    }

    public void clearPrerences() {
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(context);
        frtSharePrefUtil.setString(frtConstants.USERNAME_KEY, "");
        frtSharePrefUtil.setBoolean(frtConstants.IS_LOGGED_IN, false);
        frtSharePrefUtil.setString(context.getString(R.string.tokenexpiretime_key), "");
        frtSharePrefUtil.setString(context.getString(R.string.token_key), "");
//        frtSharePrefUtil.setString(context.getString(R.string.tokenexpiretime_key), "");
        frtSharePrefUtil.setString(context.getString(R.string.tokentype_key), "");
        frtSharePrefUtil.setString(context.getString(R.string.reasonname), null);
        frtSharePrefUtil.setObject("");
//        frtSharePrefUtil.setString("planneddata", "");
    }

    public String formatDate(String inputDate) {
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String outputDateStr = null;
        try {
            Date date = inputFormat.parse(inputDate);
            outputDateStr = outputFormat.format(date);
        } catch (ParseException ignored) {
        }
        return outputDateStr;
    }

    public double calculationByDistance(PTRRequestSaveRouteLocationmodel start, PTRRequestSaveRouteLocationmodel end) {
        LatLng l1 = new LatLng(start.getLatitudeDouble(), start.getLongitudeDouble());
        LatLng l2 = new LatLng(end.getLatitudeDouble(), end.getLongitudeDouble());
        return calculationByDistance(l1, l2);
    }

   /* public double calculationByDistance(PTRRequestSaveRouteLocationmodel end, PTRRequestSaveRouteLocationmodel start) {
        double dLat = Math.toRadians(end.getLatitudeDouble() - start.getLatitudeDouble());
        double dLon = Math.toRadians(end.getLongitudeDouble() - start.getLongitudeDouble());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(start.getLatitudeDouble()))
                * Math.cos(Math.toRadians(end.getLatitudeDouble())) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }
*/

    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }


    public double calculationByDistance(LatLng start, LatLng end) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = start.latitude;
        double lat2 = end.latitude;
        double lon1 = start.longitude;
        double lon2 = end.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c * 1000;
        if (Double.isNaN(valueResult)) {
            valueResult = 0.0;
        }
        return valueResult;
    }

    public void createTrackingTimeoutHandler() {
        try {
            Thread thread = new Thread() {
                public void run() {
                    Looper.prepare();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do Work
                            frtSharePrefUtil = FRTSharePrefUtil.getInstance(context);
                            Log.d("Location Time out", "yes");
                            frtSharePrefUtil.setString(context.getString(R.string.reasonname), "Timeout");
                            handler.removeCallbacks(this);
                            Looper.myLooper().quit();
                        }
                    }, 24 * 60 * 60 * 1000);
                    Looper.loop();
                }
            };
            thread.start();
        } catch (Exception ignored) {
        }
    }

    public int getBatteryPercentage(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    public boolean isTwoDatesDifferenceLessthanThirtyOne(String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date firstDate = sdf.parse(startDate);
        Date secondDate = sdf.parse(endDate);
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff < 31;
    }

    public boolean isTimeDifference(Date startTime, Date endTime) {
        if (endTime.after(startTime)) { //Same way you can check with after() method also.
            return false;

        } else if (endTime.equals(startTime))
            return false;
        else {
            return true;
        }
    }

    // calculate hours difference between two date
    public String isTimeDiffHours(Date startTime, Date endTime) {
        String diff;
        long mills = startTime.getTime() - endTime.getTime();
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) ((mills / (1000 * 60)) % 60);

        diff = hours + ":" + mins;
        return diff;
    }

    public boolean isTimeStartEndDifference(Date startTime, Date endTime) {
        if (endTime.before(startTime)) { //Same way you can check with after() method also.
            return false;

        } else if (endTime.equals(startTime))
            return false;
        else {
            return true;
        }
    }

    public boolean dateValidation(String startDate, String endDate, String dateFormat) {
        try {

            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return false;
            if (date1.equals(startingDate))
                return false;
            else
                return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * This function would capture the tracking data and push to server. This function would be called every x seconds
     *
     * @param frtwebapi               frtwebapi instance
     * @param frtLocationDb           db instance
     * @param frtConnectivityReceiver connectivity instance
     */
    public void sendDataToServer(FRTWEBAPI frtwebapi, FRTLocationDb frtLocationDb, FRTConnectivityReceiver frtConnectivityReceiver) {
        try {
            // commented as work in progress
            while (true) {
                offlineDataProcessed = false;
                if (!frtConnectivityReceiver.isConnected(context)) {
                    return;
                }
                List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.TRACKINGDATA);
                if (frtRequestSaveRouteLocationmodelList.size() <= 0) {
                    break;
                }

                int l = (frtRequestSaveRouteLocationmodelList.size() > 100) ? 100 : frtRequestSaveRouteLocationmodelList.size();

                sendDataToServerOffline(frtRequestSaveRouteLocationmodelList.subList(0, l), frtwebapi, frtLocationDb, frtConstants.TRACKINGDATA);
                while (!offlineDataProcessed) {
                    Thread.sleep(500);
                }
            }
            // send ended route
            List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.ROUTEEND);
            if (frtRequestSaveRouteLocationmodelList.size() > 0) {
                //TODO: Send saveroute action data and mark that synched
                // TODO: Also truncate the table
                sendDataToServerOffline(frtRequestSaveRouteLocationmodelList.subList(0, 1), frtwebapi, frtLocationDb, frtConstants.ROUTEEND);

            }
        } catch (Exception ignored) {
            Log.e("tagsenddata", ignored.toString());
        }
    }

    private static boolean offlineDataProcessed = false;

    private void sendDataToServerOffline(final List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList, final FRTWEBAPI frtwebapi, final FRTLocationDb frtLocationDb, String dataType) {
        try {
            FRTAsyncCommon<List<PTRRequestSaveRouteLocationmodel>> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(context);
            frtAsyncCommon.setFrtModelList(frtRequestSaveRouteLocationmodelList);
            final List<Integer> idList = new ArrayList<>();
            for (PTRRequestSaveRouteLocationmodel model : frtRequestSaveRouteLocationmodelList) {
                idList.add(model.getSeqId());
                Log.d("PANKAJ", "SENDING ID " + model.getSeqId());
            }
            if (frtConstants.ROUTEEND.equals(dataType)) {
                frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.SAVE_ROUTE_ACTION), "saverouteofflineaction");
            } else if (frtConstants.TRACKINGDATA.equals(dataType)) {
                frtAsyncCommon.execute(frtwebapi.getWEBAPI(FRTAPIType.SAVE_ROUTE_BULK), "saveroutebulk");
            }
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    logInfo(context, "type: " + type + "response: " + response, frtConstants);
                    if (response != null) {
                        Log.d("PANKAJ", "RESPONSE RECEIVED");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("OK")) {

                                if (type.equals("saverouteofflineaction")) {
                                    frtSharePrefUtil.setString("keytracktoend", "");
                                    frtLocationDb.updateSyncCompletedTrackingById(idList, frtConstants.SYNC_STATUS_SYNCED, true);
                                    //TODO: Truncate table
                                    frtLocationDb.truncateLocationTrackingTable();
                                }
//                                frtSharePrefUtil.setList(idList);
                                // TODO: Mark records synched
                                frtLocationDb.deleteTrackingById(idList);
                                Log.d("PANKAJ", "RECORDS DELETED");
                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.error))) {
                                Log.e("ERRORDATA", "Something went wrong with server or client parameters.");
                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.req_denied))) {
                                goToLogin(context.getString(R.string.req_denied));
                            } else if (jsonObject.optString(context.getString(R.string.status_key)).equals(context.getString(R.string.session_exp))) {
                                /*goToLogin(context.getString(R.string.session_exp));*/
                                getRefereshTokenData(frtwebapi);
                            }
                        } catch (Exception ignored) {
                            Log.e("service", ignored.toString());
                        }
                    }

                    offlineDataProcessed = true;
                }
            });
        } catch (Exception ignored) {
            Log.e("service", ignored.toString());
        }
    }

    public void setIsOneTime(boolean isOneTime) {
        this.isOneTime = isOneTime;
    }

    public boolean isOneTime() {
        return isOneTime;
    }

    public String saveImageAsBase64(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        if (bitmap.getWidth() > bitmap.getHeight())
            matrix.postRotate(0);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int h = (height < 700) ? height : 700;
        int w = (width < 700) ? width : 700;
//        if (height > h)
//            height = h;
//        int w = (h == height) ? width : (width * (h / height));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2 ,bitmap.getHeight()/2 , true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        int size = bytes.size() / 1024;
        byte[] byteArray = bytes.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
    }

    public void deleteGalleyfile(Context context,long captured_img_date) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" +"Camera"+"/");
        File list[] = file.listFiles();
        if(list.length >0){
            for( int i=0; i< list.length; i++)
            {
                if(captured_img_date - list[i].lastModified()<10000)
                {
                    if(list[i].isFile() && list[i].exists()){
                        list[i].delete();
                        MediaScannerConnection.scanFile(context,new String[]{list[i].toString()}, null, null);
                    }

                }
            }
        }

    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        OutputStream outStream = null;

        File file = new File(bmp + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, bmp + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + bmp);
        }
        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    /*public void compressBitmap(File file, int sampleSize, int quality) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            FileInputStream inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream("location to save");
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.close();
            long lengthInKb = photo.length() / 1024; //in kb
            if (lengthInKb > SIZE_LIMIT) {
                compressBitmap(file, (sampleSize*2), (quality/4));
            }

            selectedBitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    private class MyWebClient extends WebViewClient {
        /**
         * override url loading for api level below 21
         *
         * @param view webview instance
         * @param url  requested url
         * @return true/false
         */
        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask0", jsonData[0].toString());
                FRTDataParser parser = new FRTDataParser();
                Log.d("ParserTask1", parser.toString());
                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask2", routes.toString());
            } catch (Exception ignored) {
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.width(16);
            //this is the color of route
            rectOptions.color(routeColor);
            for (int i = 0; i < latlnglist.size(); i++) {
                if (checkList) {
                    @SuppressWarnings("unchecked") Double lati = ((List<PTRResponsePlannedRouteModel>) latlnglist).get(i).getLat();
                    @SuppressWarnings("unchecked") Double longi = ((List<PTRResponsePlannedRouteModel>) latlnglist).get(i).getLng();
                    LatLng latlng = new LatLng(lati,
                            longi);
                    rectOptions.add(latlng);
                } else {
                    @SuppressWarnings("unchecked") Double lati = ((List<PTRResponseActualRouteModel>) latlnglist).get(i).getLat();
                    @SuppressWarnings("unchecked") Double longi = ((List<PTRResponseActualRouteModel>) latlnglist).get(i).getLng();
                    LatLng latlng = new LatLng(lati,
                            longi);
                    rectOptions.add(latlng);
                }
            }
            googleMap.addPolyline(rectOptions);
//            setIsOneTime(true);
        }
    }


    public void logInfo(Context mContext, String message, FRTConstants frtConstants) {
       /* try {
            deleteContentOfLogFileIfAccordingToSize(mContext, frtConstants);
            if (BuildConfig.DEBUG) {
                String tabSeprator = "\t";
                String separator = "\r\n";
                message = separator + getCurrentDateTimeForLog() + tabSeprator + message;
                generateLogOnDevice(mContext, message, frtConstants);
            }
        } catch (Exception ex) {
            //logException(ex);
        }*/
    }

    public void deleteContentOfLogFileIfAccordingToSize(Context mContext, FRTConstants frtConstants) {
        File root = new File(mContext.getExternalFilesDir(null).getPath());
        String fileName = frtConstants.LOG_FILE_NAME;
        File file = new File(root, fileName);
        if (file.exists()) {
            if (file.length() > frtConstants.LOG_FILE_SIZE) {
                clearContentOfLogFile(mContext, frtConstants);
                String message = "Message:";
                message = message + "\t" + "Previous Log Deleted!";
                logInfo(mContext, message, frtConstants);
            }
        }
    }

    public void clearContentOfLogFile(Context mContext, FRTConstants frtConstants) {
        File root = new File(mContext.getExternalFilesDir(null).getPath());
        String fileName = frtConstants.LOG_FILE_NAME;
        File file = new File(root, fileName);
        if (file.exists()) {
            try {
                new RandomAccessFile(file, "rw").setLength(0);
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    }

    public void generateLogOnDevice(Context mContext, String message, FRTConstants frtConstants) {
        try {
            File root = new File(mContext.getExternalFilesDir(null).getPath());
            File file = new File(root, frtConstants.LOG_FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append(message + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // logException(e);
        }
    }

    public String getCurrentDateTimeForLog() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public Date getDateFromString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateString);
            System.out.println(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public Date getDateFromStringType2(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateString);
            System.out.println(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final Activity context, final FRTConstants frtConstants) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.CAMERA}, frtConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.CAMERA}, frtConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /* public static String getFileNameFromFile(String filePath) throws Exception {

         File fl = new File(filePath);
         String ret = fl.getName();
         return ret;
     }*/
//    public static String[] getFileNameFromFile(String filePath) throws Exception {
//        String[] ret = new String[2];
//        File fl = new File(filePath);
//        ret[0] = fl.getName();
//        ret[1] = String.valueOf(fl.length() / 1024);
//        return ret;
//<<<<<<< .mine
//    }*/
    public String[] getFileNameFromFile(String filePath) throws Exception {
        String[] ret = new String[2];
        File fl = new File(filePath);
        ret[0] = fl.getName();
        ret[1] = String.valueOf(fl.length() / 1024);
        return ret;

    }

    /**
     * reduces the size of the image
     *
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String getFileSize(File file) {
        String modifiedFileSize = null;
        double fileSize = 0.0;
        if (file.isFile()) {
            fileSize = (double) file.length();//in Bytes

            if (fileSize < 1024) {
                modifiedFileSize = String.valueOf(fileSize).concat("B");
            } else if (fileSize > 1024 && fileSize < (1024 * 1024)) {
                modifiedFileSize = String.valueOf(Math.round((fileSize / 1024 * 100.0)) / 100.0).concat("KB");
            } else {
                modifiedFileSize = String.valueOf(Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0).concat("MB");
            }
        } else {
            modifiedFileSize = "Unknown";
        }

        return modifiedFileSize;
    }

    public float getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float pxWidth = outMetrics.widthPixels;
        return pxWidth;
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;

        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height
     * <p>
     * The function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);

        int roundedInSampleSize;
        if (initialInSampleSize <= 8) {
            roundedInSampleSize = 1;
            while (roundedInSampleSize < initialInSampleSize) {
                // Shift one bit to left
                roundedInSampleSize <<= 1;
            }
        } else {
            roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
        }

        return roundedInSampleSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final double height = options.outHeight;
        final double width = options.outWidth;

        final long maxNumOfPixels = reqWidth * reqHeight;
        final int minSideLength = Math.min(reqHeight, reqWidth);

        int lowerBound = (maxNumOfPixels < 0) ? 1 :
                (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 :
                (int) Math.min(Math.floor(width / minSideLength),
                        Math.floor(height / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public int getExifOrientation(String path) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            return 0;
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }


    public Bitmap setCameraPicOrientation(String mCurrentPhotoPath) {
        int rotate = 0;
        //android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        //android.hardware.Camera.getCameraInfo(cameraId, info);
        // int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        // int degrees = 0;
        try {
            //File imageFile = new File(mCurrentPhotoPath);
            ExifInterface exif = new ExifInterface(
                    mCurrentPhotoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* if(android.os.Build.VERSION.SDK_INT > 13 && info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix   rotateRight = new Matrix();
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);

            rotateRight.postConcat(matrixMirrorY);

            rotateRight.preRotate(270);
        }*/


        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        int targetW = 640;
        int targetH = 640;


        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
            /* Associate the Bitmap to the ImageView */

        // imageView.setImageBitmap(bitmap);
        return bitmap;

    }

    public Bitmap cameraRoateImage(String mCurrentPhotoPath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {


            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;


            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;

                return rotatedBitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        if (source.getWidth() > 0 && source.getHeight() > 0) {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        }
        return bitmap;
    }

    public void sendMessageOnPause(String mngrNumber, String text) {
        SmsManager.getDefault().sendTextMessage(mngrNumber, null, text, null, null);
    }

    public void setLocale(final Context ctx, String lang, final Activity login, boolean b) {
        Locale myLocale = new Locale(lang);
        Resources res = ctx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        if (b) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent refresh = new Intent(ctx, login.getClass());
                    login.startActivity(refresh);
                    login.finish();
                    login.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }
            }, 700);
        }
    }


    // camera permission on button click
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void getRefereshTokenData(FRTWEBAPI frtwebapi) {
        /*if (frtConnectivityReceiver.isConnected(context)) {*/
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
                            frtSharePrefUtil.setString(context.getString(R.string.token_key), jsonObject.optString(context.getString(R.string.token_key)));
                            frtSharePrefUtil.setString(context.getString(R.string.tokentype_key), jsonObject.optString(context.getString(R.string.tokentype_key)));
                            frtSharePrefUtil.setString(context.getString(R.string.tokenexpiretime_key), String.valueOf(jsonObject.optInt(context.getString(R.string.tokenexpiretime_key), 0)));
                            frtSharePrefUtil.setString(context.getString(R.string.refresh_token_key), jsonObject.optString(context.getString(R.string.refresh_token_key)));
                            frtSharePrefUtil.setString(context.getString(R.string.globalsettings_key), jsonObject.optString(context.getString(R.string.globalsettings_key)));
                            frtSharePrefUtil.setString("currenttime", frtUtility.getCurrentTime());

                        } catch (JSONException ignored) {
                        }
                    }
                }
            }
        });
        // }
        /*else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(android.R.id.content));
        }*/
    }

    private PTRLoginRefreshRequestModel setUpLoginRefreshRequestModel() {
        PTRLoginRefreshRequestModel cqRequestModelLogin = new PTRLoginRefreshRequestModel();
        cqRequestModelLogin.setRefresh_token(frtSharePrefUtil.getString(context.getString(R.string.refresh_token_key)));
        cqRequestModelLogin.setGrant_type();
        return cqRequestModelLogin;
    }


   /* String userIdEncr = AESEncriptDecript.encrypt(AESEncriptDecript.KEY_SHA.getBytes("UTF-16LE"), (String.valueOf(frtResponseUserDetailModel.getUser_id())).getBytes("UTF-16LE"));
                            frtSharePrefUtil.setString(getString(R.string.userkey),
    userIdEncr);*/
}