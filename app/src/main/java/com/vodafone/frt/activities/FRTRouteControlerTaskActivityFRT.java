package com.vodafone.frt.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTEditTextTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.FRTCheckInOutDetailsModel;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.WMSProvider;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTUtility;
import com.vodafone.frt.utility.WMSTileProviderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/19/2018.
 */

public class FRTRouteControlerTaskActivityFRT extends FRTRouteControlerTaskBaseActivityFRT implements FRTCallBackInitViews, FRTCallBackSetListeners,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, FRTAsyncCommon.FetchDataCallBack {

    private String base64Image;
    private String mCurrentPhotoPath;
    private Point mSize;
    private Bitmap  bitmapSetView;
    private int imageViewID;
    List<String> frtListImagesBase64 = new ArrayList<>();
    private FRTRouteControlerTaskActivityFRT frtRouteControlerTaskActivityFRT = this;
    private double latitude,longitude;
    private String checkinReason;
    private LatLng checkedInMarkerPosition =null;
    private double minDistanceHaultPoint;
    private String markerId = "";
    private double radius;
    private int radiusOriginal;
    private FRTEditTextTrebuchetMS tv_checkInComment;
    private LatLng clickedMarkerPosition = null;
    String title = "";
    private double latitudeModel;
    private ProgressDialog progressDialog;
    private double longitudeModel;
    private String remarksComment;
    boolean isTilesVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appPermissions();
    }



    /**
     * This method is used for authorising necessary permissions for initializing google map android
     */
    @SuppressLint("InlinedApi")
    private void appPermissions() {
        frtConstants = new FRTConstants();
        if (ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(frtRouteControlerTaskActivityFRT, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.CAMERA}, frtConstants.LOCATION_PERMISSION);
        } /*else if () {
            ActivityCompat.requestPermissions(frtRouteControllerTaskActivity, new String[]{Manifest.permission.CAMERA}, frtConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }*/ else {
            initializeScreen();
        }
    }

    private void initializeScreen() {
        callBackSetUp();
    }


    private void callBackSetUp() {
        frtCallBackInitViews = frtRouteControlerTaskActivityFRT;
        frtCallBackSetListeners = frtRouteControlerTaskActivityFRT;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    private synchronized void buildGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(frtRouteControlerTaskActivityFRT)
                    .addConnectionCallbacks(frtRouteControlerTaskActivityFRT)
                    .addOnConnectionFailedListener(frtRouteControlerTaskActivityFRT)
                    .addApi(LocationServices.API)
                    //.addApi(Awareness.API)
                    .build();
            googleApiClient.connect();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(60000);
            locationRequest.setSmallestDisplacement(20);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, frtRouteControlerTaskActivityFRT);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            locationCurrent = location;
             latitude = location.getLatitude();
             longitude = location.getLongitude();
            userCurrentLatLon = new LatLng(latitude, longitude);

          /*  if ((intent.hasExtra("case") && intent.getStringExtra("case").equals("PatrollerLocation"))) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (frtApp.getLatLong() != null) {
                                marker = googleMap.addMarker(new MarkerOptions().position(frtApp.getLatLong())
                                        .icon(BitmapDescriptorFactory.defaultMarker()));
                            }
                            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(frtApp.getLatLong(), 20);
                            googleMap.animateCamera(cu);
                        } catch (Exception ignored) {
                        }
                    }
                }, 3000);
            }*/
        } catch (Exception ignored) {
        }
    }

    @Override
    public void commonListeners() {
        ivback.setOnClickListener(frtBackClick);
        imgsattelite.setOnClickListener(onClickSattelite);
        imghybrid.setOnClickListener(onClickHybrid);
        imgnormal.setOnClickListener(onClickNormal);
        imgtraffic.setOnClickListener(onClickTraffic);


        iv_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTilesVisible){
                    if(tileOverlay != null) {
                        tileOverlay.remove();
                    }
                    iv_toggle.setColorFilter(ContextCompat.getColor(frtRouteControlerTaskActivityFRT, R.color.grey));
                }else{
//                    setUpMap();
                    setUpCustomMap();
                    iv_toggle.setColorFilter(ContextCompat.getColor(frtRouteControlerTaskActivityFRT, R.color.green));
                }
                isTilesVisible = isTilesVisible ? false:true;
            }
        });
    }



    private WMSProvider[] providers = {
            new WMSProvider()
//                    .url("http://111.93.46.11:3028/cgi-bin/mapserv.exe?MAP=D:/apps/SmartPatroller/mapfiles/NetworkEntitiesNoLabel.map")
                     .url("https://frt.vodafone.in:8086/cgi-bin/mapserv.exe?MAP=C:/LeptonApps/PROD/Mapfile/mapfiles/NetworkEntitiesNoLabel.map")
                    .layers("Cable")
    };



    private View.OnClickListener onClickTraffic = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isClickedtraffic) {
                googleMap.setTrafficEnabled(!isClickedtraffic);
                isClickedtraffic = true;
            } else {
                googleMap.setTrafficEnabled(!isClickedtraffic);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isClickedtraffic = false;
            }
        }
    };
    private View.OnClickListener onClickNormal = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isSatteliteClicked || isHybridClicked) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    };
    private View.OnClickListener onClickSattelite = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isSatteliteClicked && !isHybridClicked) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                isSatteliteClicked = true;
            } else {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isSatteliteClicked = false;
            }
        }
    };
    private View.OnClickListener onClickHybrid = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isHybridClicked && !isSatteliteClicked) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                isHybridClicked = true;
            } else {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isHybridClicked = false;
            }
            googleMap.setTrafficEnabled(false);
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


     //   frtRequestSaveRouteLocationmodelDb = new PTRRequestSaveRouteLocationmodel();
        frtCnnectivityReceiver = new FRTConnectivityReceiver();
        frtConstants = new FRTConstants();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtRouteControlerTaskActivityFRT);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        frtUtility.checkGps(frtRouteControlerTaskActivityFRT);
        frtUtility.settingStatusBarColor(frtRouteControlerTaskActivityFRT, R.color.colorPrimary);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtRouteControlerTaskActivityFRT);
        frtWEBAPI = new FRTWEBAPI();
        handler = new Handler();
        frtLocationDb = new FRTLocationDb(frtRouteControlerTaskActivityFRT);
        frtApp = (FRTApp) getApplication();
        frtApp.setCtx(frtRouteControlerTaskActivityFRT);
        intent = getIntent();
        mSensorService = new FRTLocationTrackingService();
        initUi();
        setUpMap();
    }



    @SuppressLint("SetTextI18n")
    private void initUi() {
        if ((getIntent().hasExtra("case") && getIntent().getStringExtra("case").equals("PatrollerLocation"))) {
            relbottom.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tv_zoom_level.setLayoutParams(params);
        }


        if ((getIntent().hasExtra("case") && getIntent().getStringExtra("case").equals("AttendanceFrt"))) {
            relbottom.setVisibility(View.GONE);
            routetitle.setText(getIntent().getStringExtra("routetitle"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tv_zoom_level.setLayoutParams(params);
        }



        try {
            if (intent.hasExtra("keyparcel")) {
                frtResponseRouteCommonModel = getIntent().getParcelableExtra("keyparcel");
                if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getIssue_type()))
                    routetitle.setText(frtResponseRouteCommonModel.getIssue_type());
                else
                    routetitle.setText("");
                if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getCheckin_remarks()))
                    checkinRemarks.setText(frtResponseRouteCommonModel.getCheckin_remarks());
                else
                    checkinRemarks.setText("");
                if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getCheckin_time()))
                    checkInTimeTextView.setText(frtResponseRouteCommonModel.getCheckin_time());
                else
                    checkInTimeTextView.setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(this.getClass().getName(),"EXCEPTION==" + ex.getMessage());
        }

        if ((getIntent().hasExtra("case") && getIntent().getStringExtra("case").equals("Assigned"))) {
            title = frtResponseRouteCommonModel.getIssue_type();
            relbottom.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tv_zoom_level.setLayoutParams(params);
            checkinRemarks.setVisibility(View.GONE);
        }


        else if ((getIntent().hasExtra("case") && getIntent().getStringExtra("case").equals("Inprogress"))) {
            relbottom.setVisibility(View.VISIBLE);

            checkinRemarks.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(frtSharePrefUtil.getString("issueType")))
                routetitle.setText(frtSharePrefUtil.getString("issueType"));
            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getIssue_type()))
                routetitle.setText(frtResponseRouteCommonModel.getIssue_type());
            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getCheckin_remarks()))
                checkinRemarks.setText(frtResponseRouteCommonModel.getCheckin_remarks());

            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getCheckin_time()))
                checkInTimeTextView.setText(frtResponseRouteCommonModel.getCheckin_time());

            if (!TextUtils.isEmpty(frtSharePrefUtil.getString("checkInRemarks")))
                checkinRemarks.setText(frtSharePrefUtil.getString("checkInRemarks"));

            if (!TextUtils.isEmpty(frtSharePrefUtil.getString("checkinTime")))
                checkInTimeTextView.setText(frtSharePrefUtil.getString("checkinTime"));

        }

        if (getIntent().hasExtra("case")) {
            frtSharePrefUtil.setString("case", intent.getStringExtra("case"));
            if (getIntent().getStringExtra("case").equals("Completed") || getIntent().getStringExtra("case").equals("Pending") || frtLocationDb.offlineRouteEnded()) {
                checkoutRemarksLL.setVisibility(View.VISIBLE);
                checkoutLL.setVisibility(View.VISIBLE);
                //checkOutTimeTextView.setText(frtResponseRouteCommonModel.getCheckout_time());
                checkOutTimeTextView.setText(frtSharePrefUtil.getString("CheckOutTimeShrd"));
                checkOutRemarksTextView.setText(frtResponseRouteCommonModel.getCheckout_remarks());
                frtUtility.setSnackBar(getString(R.string.routecompleted), ivback);
            }
        }

        if (frtSharePrefUtil.getString("caseSharedP") != null && frtSharePrefUtil.getString("caseSharedP").equals("Inprogress")){
            relbottom.setVisibility(View.VISIBLE);
            checkinRemarks.setVisibility(View.VISIBLE);
            //routetitle.setText(frtResponseRouteCommonModel.getIssue_type());
            checkinRemarks.setText(frtSharePrefUtil.getString("CHECKIN_TEXT"));
            checkInTimeTextView.setText(frtUtility.getDateAndTime(false));
        }

    }

    private GoogleMap.InfoWindowAdapter onMapSetInfoWindow = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return prepareInfoView(marker);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return prepareInfoView(marker);
        }

        private View prepareInfoView(Marker marker){
            LinearLayout infoView = null;
            infoView = new LinearLayout(frtRouteControlerTaskActivityFRT);
            LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            infoView.setOrientation(LinearLayout.HORIZONTAL);
            infoView.setLayoutParams(infoViewParams);
            LinearLayout subInfoView = new LinearLayout(frtRouteControlerTaskActivityFRT);
            LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subInfoView.setOrientation(LinearLayout.VERTICAL);
            subInfoView.setLayoutParams(subInfoViewParams);
            subInfoView.setBackgroundColor(getResources().getColor(R.color.white));
            subInfoView.setPadding(15,15,15,15);

            if (getIntent().getStringExtra("case").equals("AttendanceFrt")) {
                TextView tv_frt_name, tvIssueType;
                tv_frt_name = new TextView(frtRouteControlerTaskActivityFRT);
                tvIssueType = new TextView(frtRouteControlerTaskActivityFRT);
                tv_frt_name.setText(getString(R.string.frtName) + " : " +getIntent().getStringExtra("frtName"));
                tvIssueType.setText(getString(R.string.issueType) +" : " +getIntent().getStringExtra("routetitle"));
                subInfoView.addView(tv_frt_name);
                subInfoView.addView(tvIssueType);
                infoView.addView(subInfoView);
            }
            return infoView;
        }
    };



    private final GoogleMap.OnMarkerClickListener onMarkerClick = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            if (getIntent().getStringExtra("case").equals("AttendanceFrt")){
                marker.showInfoWindow();

            }
            else {


                if (frtResponseRouteCommonModel.getCheckin_radius() != 0) {
                    radius = frtResponseRouteCommonModel.getCheckin_radius();
                } else {
                    radius = Double.parseDouble(frtSharePrefUtil.getString("getCheckin_radius"));
                }
                marker = marker;


                boolean isCurrentCheckIn = false, isCurrentCheckOut = false;

                if ((frtSharePrefUtil.getString("case") != null && frtSharePrefUtil.getString("case").equals("PatrollerLocation"))) {
                    marker.showInfoWindow();
                }

                // if (frtSharePrefUtil.getString("caseSharedP") != null && frtSharePrefUtil.getString("caseSharedP").equals("Inprogress")){
                if (getIntent().getStringExtra("case").equalsIgnoreCase("Inprogress")) {
                    title = frtResponseRouteCommonModel.getIssue_type();
                    clickedMarkerPosition = marker.getPosition();
                    double latitude = Double.parseDouble(frtSharePrefUtil.getString("latitudeAssigned"));
                    double longitude = Double.parseDouble(frtSharePrefUtil.getString("longitudeAssigned"));
                    if (!frtSharePrefUtil.getString("case").equals("Pending") && !frtSharePrefUtil.getString("case").equals("Completed") && ((frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start") || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume") || !frtSharePrefUtil.getString("case").equals("Assigned"))))
                        if (clickedMarkerPosition.equals(new LatLng(latitude
                                , longitude)))
                            taskTrackingId = frtResponseRouteCommonModel.getTask_tracking_id();
                    String checkinout = frtSharePrefUtil.getString("FRTCHECKINOUT");
                    if (checkinout.equalsIgnoreCase("CheckInOut")) {
                        frtUtility.setSnackBar("Already checked out", ivback);
                    } else {
                        isCheckIn = false;
                        remark = "CHECKOUT";
                        showDialogCheckinOutPint(title);


                    }
                } else if (frtSharePrefUtil.getString("caseSharedP") != null && frtSharePrefUtil.getString("caseSharedP").equals("Inprogress")) {
                    title = frtResponseRouteCommonModel.getIssue_type();
                    clickedMarkerPosition = marker.getPosition();
                    double latitude = Double.parseDouble(frtSharePrefUtil.getString("latitudeAssigned"));
                    double longitude = Double.parseDouble(frtSharePrefUtil.getString("longitudeAssigned"));
                    if (!frtSharePrefUtil.getString("case").equals("Pending") && !frtSharePrefUtil.getString("case").equals("Completed") && ((frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start") || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume") || !frtSharePrefUtil.getString("case").equals("Assigned"))))
                        if (clickedMarkerPosition.equals(new LatLng(latitude
                                , longitude)))
                            taskTrackingId = frtResponseRouteCommonModel.getTask_tracking_id();
                    String checkinout = frtSharePrefUtil.getString("FRTCHECKINOUT");
                    if (checkinout.equalsIgnoreCase("CheckInOut")) {
                        frtUtility.setSnackBar("Already checked out", ivback);
                    } else {
                        isCheckIn = false;
                        remark = "CHECKOUT";
                        showDialogCheckinOutPint(title);
                    }

                    relbottom.setVisibility(View.VISIBLE);
                    tv_zoom_level.setVisibility(View.VISIBLE);

                  /*  RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv_zoom_level.getLayoutParams();
                    params.addRule(RelativeLayout.ABOVE);
                    params.addRule(RelativeLayout.ABOVE, R.id.relbottom);

                    tv_zoom_level.setLayoutParams(params);*/
                }

                else if (getIntent().getStringExtra("case").equalsIgnoreCase("Assigned")) {
                    title = frtResponseRouteCommonModel.getIssue_type();
                    if (/*isPreMarkerCheckIn*/checkedInMarkerPosition != null) {
                        if (!checkedInMarkerPosition.equals(clickedMarkerPosition))
                            frtUtility.setSnackBar("Already checked in a location", ivback);
                    } else {

                        if (!String.valueOf(latitude).isEmpty() && !String.valueOf(longitude).isEmpty()) {
                            try {
                                minDistanceHaultPoint = frtUtility.calculationByDistance(marker.getPosition(), userCurrentLatLon);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (minDistanceHaultPoint <= radius) {
                                remark = "CHECKIN";
                                isCheckIn = true;
                                showDialogCheckinOutPint(title);
                            } else {
                                frtUtility.setSnackBar("You are outside of the checkin area", ivback);
                            }
                        } else {
                            frtUtility.setSnackBar(getResources().getString(R.string.notgetting_responseFromServer), ivback);
                        }

                    }
                } else {
                    frtUtility.setSnackBar(getString(R.string.complete), ivback);
                }
            }
            return false;

        }
    };

    private void setUpCustomMap() {
        TileProvider tileProvider = WMSTileProviderFactory.getTileProvider(providers[0]);
        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }


    private final OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap gm) {
            googleMap = gm;
            buildGoogleApiClient();
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            googleMap.setInfoWindowAdapter(onMapSetInfoWindow);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            frtUtility.setGoogleMap(googleMap);


            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    tv_zoom_level.setText("Zoom level: "+(int)googleMap.getCameraPosition().zoom);
                    if(isTilesVisible) {
                        if (tileOverlay != null)
                            tileOverlay.remove();
                        setUpCustomMap();
                    }
                }

            });

            latitudeModel = ParseDouble(frtSharePrefUtil.getString("latitudeAssigned"));
            longitudeModel = ParseDouble(frtSharePrefUtil.getString("longitudeAssigned"));
            // latitudeModel = Double.parseDouble(frtSharePrefUtil.getString("latitudeAssigned"));
             //longitudeModel =Double.parseDouble(frtSharePrefUtil.getString("longitudeAssigned"));
            if (longitudeModel !=0 && latitudeModel != 0) {
                // here is marker Adding code
                plotMarkerOnMap();
            }
            else {
               if (getIntent().getStringExtra("case").equals("AttendanceFrt")) {
                   plotMarkerOnMapAttendance();
               }
            }

             googleMap.setOnMarkerClickListener(onMarkerClick);
            if (ActivityCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(frtRouteControlerTaskActivityFRT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
    };

    private void plotMarkerOnMap() {

        if(marker!=null){
            marker.remove();
        }

       if (frtSharePrefUtil.getString("caseSharedP") != null && frtSharePrefUtil.getString("caseSharedP").equals("Inprogress")){
           marker=googleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeModel, longitudeModel)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
           CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longitudeModel), 18);
           googleMap.animateCamera(cu);
        }
       else if (getIntent().getStringExtra("case").equals("Inprogress")){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeModel, longitudeModel)).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longitudeModel), 17);
            googleMap.animateCamera(cu);
        }

       else if (getIntent().getStringExtra("case").equals("AttendanceFrt")){

           double lat = getIntent().getDoubleExtra("latitude",0.0);
           double longit = getIntent().getDoubleExtra("longitude",0.0);
           String statusMarkerColor = getIntent().getStringExtra("status");
           if (statusMarkerColor.equalsIgnoreCase("InProgress")){
               googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit)).
                       icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
               CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longit), 17);
               googleMap.animateCamera(cu);
           }
           else  if (statusMarkerColor.equalsIgnoreCase("Completed")){
               googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit))
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
               CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longit), 17);
               googleMap.animateCamera(cu);
           }
           else {
               marker= googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit))
                       .icon(BitmapDescriptorFactory.defaultMarker()));
               CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longit), 17);
               googleMap.animateCamera(cu);
           }

       }

        else if (getIntent().getStringExtra("case").equals("Completed")){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeModel, longitudeModel))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longitudeModel), 17);
            googleMap.animateCamera(cu);
        }
        else if (getIntent().getStringExtra("case").equals("Assigned"))
        {
           marker= googleMap.addMarker(new MarkerOptions().position(new LatLng(latitudeModel, longitudeModel))
                   .icon(BitmapDescriptorFactory.defaultMarker()));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longitudeModel), 17);
            googleMap.animateCamera(cu);

        }
    }




    private void plotMarkerOnMapAttendance() {
        if(marker!=null){
            marker.remove();
        }

        else if (getIntent().getStringExtra("case").equals("AttendanceFrt")){

            double lat = getIntent().getDoubleExtra("latitude",0.0);
            double longit = getIntent().getDoubleExtra("longitude",0.0);
            String statusMarkerColor = getIntent().getStringExtra("status");
            if (statusMarkerColor.equalsIgnoreCase("InProgress")){
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit)).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeModel, longit), 17);
                googleMap.animateCamera(cu);
            }
            else  if (statusMarkerColor.equalsIgnoreCase("Completed")){
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longit), 17);
                googleMap.animateCamera(cu);
            }
            else {
                marker= googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longit))
                        .icon(BitmapDescriptorFactory.defaultMarker()));
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longit), 17);
                googleMap.animateCamera(cu);
            }

        }

    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

    private void setUpMap() {
        supportMapFragment = new SupportMapFragment();
        frtApp.setIsOneTime(false);
        if (frameLayout != null) {
            getSupportFragmentManager().beginTransaction().add(frameLayout.getId(), supportMapFragment).commit();
            supportMapFragment.getMapAsync(onMapReadyCallback);
        }
    }


    private void showDialogCheckinOutPint(String title) {
        onClickCheckInMarker(title);
    }


    private void onClickCheckInMarker(String title) {
        if (frtLocationDb.offlineRouteEnded() == true)
            return;

        isEnd = true;
        String name = "";
        dialog = new Dialog(frtRouteControlerTaskActivityFRT);
        if (isCheckIn) {
            dialog = frtUtility.dialogBasicStructure(dialog, R.layout.halt_point_dialog_checkin_frt);
            name = "CheckIn " + title;
        } else {
            dialog = frtUtility.dialogBasicStructure(dialog, R.layout.halt_point_dialog_checkout_frt);
            name = "CheckOut " + title;
        }

        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.no);
        FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.yes);
        FRTTextviewTrebuchetMS tv_name = dialog.findViewById(R.id.tv_name);
        tv_name.setText(name);
        tvno.setOnClickListener(onClickNo);
        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                    showCamerDialog(null);
            }
        });
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    @SuppressWarnings("deprecation")
    private void scheduleLocationUpload() {
        try {
            svcintent = new Intent(frtRouteControlerTaskActivityFRT, mSensorService.getClass());
            startService(svcintent);
        } catch (Exception ignored) {
        }
    }


    private final View.OnClickListener onClickNo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.hide();
        }
    };



    public void startCamera(final String imageType) {
        long cameraFirstLoadTime;
        if (!flagCamera) {
            cameraFirstLoadTime = frtConstants.KEY_CAEMRA_OPEN_TIME;
            flagCamera = true;
        } else
            cameraFirstLoadTime = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraIntent(imageType);
            }
        }, cameraFirstLoadTime);
    }

    public void cameraIntent(String imageType) {
        Intent intent;
        String manufaturer = Build.MANUFACTURER;
         if (imageType.equals("checkin")) {
            if (Build.VERSION.SDK_INT >= 20) {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            } else {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            }
            dispatchTakePictureIntent(intent);
            startActivityForResult(intent, frtConstants.REQUEST_CAMERA_CHECKIN);
        } else if (imageType.equals("checkout")) {
            if (Build.VERSION.SDK_INT >= 20) {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            } else {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            }
            dispatchTakePictureIntent(intent);
            startActivityForResult(intent, frtConstants.REQUEST_CAMERA_CHECKOUT);
        }
    }

    private Intent intentForSamsung(String manufaturer, int sdkInt) {
        if (manufaturer.equals("samsung"))
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        else {
            if (sdkInt >= 20)
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            else
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return intent;
    }
    long captured_img_date;
    private void dispatchTakePictureIntent(Intent takePictureIntent) {
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                captured_img_date= photoFile.lastModified();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    photoURI = Uri.fromFile(photoFile);
                } else {
                    photoURI = FileProvider.getUriForFile(frtRouteControlerTaskActivityFRT,
                            "com.example.android.fileprovider",
                            photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == frtConstants.REQUEST_CAMERA_CHECKIN) {
                onCaptureImageResult(data, "checkin");
            }
            if (requestCode == frtConstants.REQUEST_CAMERA_CHECKOUT) {
                onCaptureImageResult(data, "checkout");
            }
        }
    }

    private void onCaptureImageResult(Intent data, String imagemode) {
        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);
        Bitmap bitmap = frtUtility.decodeSampledBitmapFromPath(mCurrentPhotoPath, mSize.x, mSize.y);
        try {
            bitmapSetView = frtUtility.setCameraPicOrientation(mCurrentPhotoPath);
            bitmap = frtUtility.getResizedBitmap(bitmap, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }

         if (imagemode.equals("checkin"))
            showCamerDialog(bitmapSetView);
        else if (imagemode.equals("checkout"))
            showCamerDialog(bitmapSetView);
    }

    private void showCamerDialog(Bitmap thumbnail) {

        Dialog dialog = new Dialog(frtRouteControlerTaskActivityFRT);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_camera_frt);
        tv_checkInComment = dialog.findViewById(R.id.tv_checkInComment);
        final ImageView iv_capture1 = dialog.findViewById(R.id.iv_capture1);
        final ImageView iv_capture2 = dialog.findViewById(R.id.iv_capture2);
        final ImageView iv_capture3 = dialog.findViewById(R.id.iv_capture3);
        final ImageView iv_capture4 = dialog.findViewById(R.id.iv_capture4);
        final FRTTextviewTrebuchetMS titlesaverouteaction = dialog.findViewById(R.id.titlesaverouteaction);
        if (remark.equals("CHECKOUT")){
            frtSharePrefUtil.setString("CHECKIN_TEXT", "");
        }
        titlesaverouteaction.setText(remark);
        if (thumbnail == null) {
            frtListImagesBase64.clear();
            iv_capture1.setImageResource(R.mipmap.ic_add);
        } else if (thumbnail != null) {
            base64Image = frtUtility.saveImageAsBase64(thumbnail);
            frtListImagesBase64.clear();
            frtListImagesBase64.add(base64Image);
            frtUtility.deleteGalleyfile(frtRouteControlerTaskActivityFRT,captured_img_date);
            if (!frtSharePrefUtil.getString("CHECKIN_TEXT").isEmpty()) {
                tv_checkInComment.setText(frtSharePrefUtil.getString("CHECKIN_TEXT"));
            }
        }
        switch (imageViewID) {
            case R.id.iv_capture1:
                if (thumbnail != null) {
                    iv_capture1.setImageBitmap(thumbnail);
                    thumbnail1 = thumbnail;
                    iv_capture1.setImageBitmap(thumbnail1);
                }
                break;

            case R.id.iv_capture2:
                if (thumbnail != null)
                    iv_capture2.setImageBitmap(thumbnail);
                thumbnail2 = thumbnail;
                break;

            case R.id.iv_capture3:
                if (thumbnail != null)
                    iv_capture3.setImageBitmap(thumbnail);
                thumbnail3 = thumbnail;
                break;

            case R.id.iv_capture4:
                if (thumbnail != null)
                    iv_capture4.setImageBitmap(thumbnail);
                thumbnail4 = thumbnail;
                break;

        }
        final Dialog finalDialog = dialog;
        iv_capture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkInOutComment = tv_checkInComment.getText().toString();
                if (!checkInOutComment.matches("")) {
                    frtSharePrefUtil.setString("CHECKIN_TEXT", tv_checkInComment.getText().toString());
                }
                imageViewID = view.getId();
                frtListImagesBase64.clear();
                if (remark.equals("CHECKIN")) {
                    startCamera("checkin");
                    finalDialog.hide();
                } else if (remark.equals("CHECKOUT")) {
                    startCamera("checkout");
                    finalDialog.hide();
                }
            }
        });

        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.cancel);
        final FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.ok);
        tvyes.setEnabled(true);
        tvno.setOnClickListener(onClickNo);
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FRTRouteControlerTaskActivityFRT.this.base64Image = null;
                bitmapSetView = null;
                clearThumbnail();
                imageViewID = -1;
                finalDialog.hide();
                frtListImagesBase64.clear();
            }
        });


        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tv_checkInComment.getText().toString().length() >0) {
                    String commentValue = frtSharePrefUtil.getString("CHECKIN_TEXT");
                    if (!commentValue.matches("")){
                        tv_checkInComment.setText(frtSharePrefUtil.getString("CHECKIN_TEXT"));
                    }
                    else {
                        frtSharePrefUtil.setString("CHECKIN_TEXT", tv_checkInComment.getText().toString());
                        tv_checkInComment.setText(frtSharePrefUtil.getString("CHECKIN_TEXT"));
                    }
                    if (frtListImagesBase64.size() > 0) {
                        tvyes.setEnabled(false);
                        switch (remark) {

                            case "CHECKIN":
                                tvyes.setEnabled(false);
                                frtUtility.dialogPresent();
                                saveCheckInOutTaskDetailsForFRT(true);
                                frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                                finalDialog.hide();

                                break;
                            case "CHECKOUT":
                                tvyes.setEnabled(false);
                                frtUtility.dialogPresent();

                                saveCheckInOutTaskDetailsForFRT(false);
                                frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                                finalDialog.hide();
                                break;
                        }
                        finalDialog.hide();
                    } else {

                        tvyes.setEnabled(true);
                        frtUtility.setSnackBar(getString(R.string.imagerequired), ivback);
                    }
                }
                else {
                    frtUtility.setSnackBar(getString(R.string.enterRemarks), ivback);
                }
            }
        });

        if (!remark.equals("CHECKIN") || !remark.equals("CHECKOUT"))
            try {
                dialog.show();
            }catch (Exception e){}

    }


    private void clearThumbnail() {
        thumbnail1 = null;
        thumbnail2 = null;
        thumbnail3 = null;
        thumbnail4 = null;
    }



    private void saveCheckInOutTaskDetailsForFRT(final boolean isCheckIn) {
        if (frtCnnectivityReceiver.isConnected(frtRouteControlerTaskActivityFRT)){
             progressDialog = new ProgressDialog(frtRouteControlerTaskActivityFRT);
            frtUtility.show(progressDialog);
            final FRTAsyncCommon<FRTCheckInOutDetailsModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControlerTaskActivityFRT);
            frtAsyncCommon.setFrtModel(setUpSaveTaskTrackingModel(isCheckIn));
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SAVE_TASK_TRACKING_FRT), "SaveTaskTracking");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    progressDialog.dismiss();
                    if (response != null){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                        if (jsonObject.optString("status").equals("OK")){
                            Log.d(this.getClass().getName(),"RESPONSE_SUCCESS" + response);
                            frtUtility.dialogHide();
                            isSaved = true;
                            bitmapSetView = null;
                            if (isCheckIn) {
                                frtListImagesBase64.clear();
                                frtUtility.setSnackBar(getString(R.string.haltpointcheckin), ivback);
                                frtSharePrefUtil.setString("caseSharedP","Inprogress");
                                initUi();
                                plotMarkerOnMap();


                                //tv_zoom_level.invalidate();

                            } else {
                                frtUtility.setSnackBar(getString(R.string.haltpointcheckout), ivback);
                              handler.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      frtListImagesBase64.clear();
                                      frtSharePrefUtil.setString("caseSharedP","null");
                                      frtSharePrefUtil.setString("CHECKIN_PERFORM","null");
                                      frtSharePrefUtil.setString("FRTCHECKINOUT","CheckInOut");
                                      frtSharePrefUtil.setString("CHECKIN_TEXT", "");

                                      startActivity(new Intent(frtRouteControlerTaskActivityFRT,PTRMyTaskActivity.class));
                                      finish();
                                  }
                              },2000);

                            }
                            isSaved = false;
                        }

                        else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            getRefereshTokenData();
                        }
                        else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")){
                            frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                        }
                        else
                            frtUtility.setSnackBar(jsonObject.optString("error_message"), findViewById(R.id.header));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("TAG","EXCEPTION===" + e.getMessage());
                        }
                    }
                    else {
                        frtUtility.setSnackBar(getString(R.string.servernotResponding), findViewById(R.id.header));
                    }
                }
            });

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private FRTCheckInOutDetailsModel setUpSaveTaskTrackingModel(boolean isCheckIn) {
        FRTCheckInOutDetailsModel frtCheckInOutDetailsModel = new FRTCheckInOutDetailsModel();
        try {
        frtCheckInOutDetailsModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
        frtCheckInOutDetailsModel.setTaskTrackingId(Integer.parseInt(frtSharePrefUtil.getString("tasktrackingid")));
        frtCheckInOutDetailsModel.setIssueId(Integer.parseInt(frtSharePrefUtil.getString("issueId")));
            frtCheckInOutDetailsModel.setDateTime(frtUtility.getDateAndTime(false));
        if (userCurrentLatLon != null)
        {
            frtCheckInOutDetailsModel.setLatitude(String.valueOf(userCurrentLatLon.latitude));
            frtCheckInOutDetailsModel.setLongitude(String.valueOf(userCurrentLatLon.longitude));
        }

        if (isCheckIn)
            frtCheckInOutDetailsModel.setAction("checkin");
        else
            frtCheckInOutDetailsModel.setAction("checkout");
        frtCheckInOutDetailsModel.setBase64encodedstringList(frtListImagesBase64);
        if (isCheckIn)
                        frtCheckInOutDetailsModel.setRemark(tv_checkInComment.getText().toString());
        else
            frtCheckInOutDetailsModel.setRemark(tv_checkInComment.getText().toString());
        frtSharePrefUtil.setString("CHECKIN_TIME",frtUtility.getDateAndTime(false));
        frtSharePrefUtil.setString("CHECKIN_TEXT" , tv_checkInComment.getText().toString());
       // frtCheckInOutDetailsModel.setRemark(checkinReason);
        }catch (Exception e){
            e.printStackTrace();
        }
        return frtCheckInOutDetailsModel;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void getRefereshTokenData() {
        if (frtCnnectivityReceiver.isConnected(frtRouteControlerTaskActivityFRT)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControlerTaskActivityFRT);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtRouteControlerTaskActivityFRT);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frtSharePrefUtil.setString("CHECKIN_TEXT", "");
    }
}