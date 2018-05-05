package com.vodafone.frt.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.maps.model.TileOverlay;
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
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestActualRouteModel;
import com.vodafone.frt.models.PTRRequestGetHaltPointsModel;
import com.vodafone.frt.models.PTRRequestGetPtrollerLocationModel;
import com.vodafone.frt.models.PTRRequestPlannedRouteModel;
import com.vodafone.frt.models.PTRRequestSaveHaltPointsModel;
import com.vodafone.frt.models.PTRRequestSaveRouteAction;
import com.vodafone.frt.models.PTRRequestSaveRouteLocationmodel;
import com.vodafone.frt.models.PTRRequestSaveWeatherModel;
import com.vodafone.frt.models.PTRRequestSelfieDataModel;
import com.vodafone.frt.models.PTRRequestSnapToRoadModel;
import com.vodafone.frt.models.PTRRequestUploadRouteTrackingDocModel;
import com.vodafone.frt.models.PTRResponseActualRouteModel;
import com.vodafone.frt.models.PTRResponseGetHaltPointsModel;
import com.vodafone.frt.models.PTRResponsePlannedRouteModel;
import com.vodafone.frt.models.PTRResponsePtrollerLocationModel;
import com.vodafone.frt.models.PTRResponseSaveRouteAction;
import com.vodafone.frt.models.PTRResponseSnapToRoadModel;
import com.vodafone.frt.models.WMSProvider;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;
import com.vodafone.frt.services.SyncService;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTUtility;
import com.vodafone.frt.utility.ImageFilePath;
import com.vodafone.frt.utility.WMSTileProviderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vishal
 */
public class PTRRouteControllerTaskActivity extends PTRRouteControllerTaskBaseActivity implements FRTCallBackInitViews, FRTCallBackSetListeners,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, FRTAsyncCommon.FetchDataCallBack {
    private PTRRouteControllerTaskActivity frtRouteControllerTaskActivity = this;
    private String markerId = "";
    private FRTUtility frtUtility;
    private boolean isPreMarkerCheckIn = false;
    private int selectedMarkerPosition = 0, selectedCheckInPosition = 0;
    PTRRequestSaveRouteLocationmodel frtRequestSaveRouteLocationmodelDb;
    private LatLng checkedInMarkerPosition = null;
    boolean isTilesVisible;
    private GoogleMap.InfoWindowAdapter onMapSetInfoWindow = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return prepareInfoView(marker);
        }

        @SuppressLint("SetTextI18n")
        private View prepareInfoView(Marker marker) {
            //prepare InfoView programmatically
            String caseValue = (frtSharePrefUtil.getString("case"));
            LinearLayout infoView = null;
            if (caseValue != null && caseValue.equals("PatrollerLocation")) {
                infoView = new LinearLayout(frtRouteControllerTaskActivity);
                LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                infoView.setOrientation(LinearLayout.HORIZONTAL);
                infoView.setLayoutParams(infoViewParams);
                LinearLayout subInfoView = new LinearLayout(frtRouteControllerTaskActivity);
                LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                subInfoView.setOrientation(LinearLayout.VERTICAL);
                subInfoView.setLayoutParams(subInfoViewParams);
                TextView tv_tracking_id, tv_route_assignment_id, tv_patroller_id, tv_patroller_user_name, tv_patroller_full_name, tv_network_provider, tv_is_inside_buffer,
                        tv_deviation_from_planned, tv_dist_from_last_loc, tv_driving_speed, tv_battery_percentage, tv_mobile_time, tv_server_time;
                if (frtSharePrefUtil.getString("case") != null && frtSharePrefUtil.getString("case").equals("PatrollerLocation")) {
                    tv_tracking_id = new TextView(frtRouteControllerTaskActivity);
                    tv_tracking_id.setText(getString(R.string.trackid) + String.valueOf(frtResponsePtrollerLocationModel.getTracking_id()).trim());
                    tv_route_assignment_id = new TextView(frtRouteControllerTaskActivity);
                    tv_route_assignment_id.setText(getString(R.string.routassignid) + String.valueOf(frtResponsePtrollerLocationModel.getRoute_assignment_id()).trim());
                    tv_patroller_id = new TextView(frtRouteControllerTaskActivity);
                    tv_patroller_id.setText(getString(R.string.patrollerid) + String.valueOf(frtResponsePtrollerLocationModel.getPatroller_id()).trim());
                    tv_patroller_user_name = new TextView(frtRouteControllerTaskActivity);
                    tv_patroller_user_name.setText(getString(R.string.usernamee) + frtResponsePtrollerLocationModel.getPatroller_user_name().trim());
                    tv_patroller_full_name = new TextView(frtRouteControllerTaskActivity);
                    tv_patroller_full_name.setText(getString(R.string.patrofname) + frtResponsePtrollerLocationModel.getPatroller_full_name().trim());
                    tv_network_provider = new TextView(frtRouteControllerTaskActivity);
                    tv_network_provider.setText(getString(R.string.netprovider) + frtResponsePtrollerLocationModel.getNetwork_provider().trim());
                    tv_is_inside_buffer = new TextView(frtRouteControllerTaskActivity);
                    tv_is_inside_buffer.setText(getString(R.string.insidebuffr) + frtResponsePtrollerLocationModel.getIs_inside_buffer().trim());
                    tv_deviation_from_planned = new TextView(frtRouteControllerTaskActivity);
                    tv_deviation_from_planned.setText(getString(R.string.deviationplanned) + String.valueOf(frtResponsePtrollerLocationModel.getDeviation_from_planned()).trim());
                    tv_dist_from_last_loc = new TextView(frtRouteControllerTaskActivity);
                    tv_dist_from_last_loc.setText(getString(R.string.lastlocdist) + String.valueOf(frtResponsePtrollerLocationModel.getDist_from_last_loc()).trim());
                    tv_driving_speed = new TextView(frtRouteControllerTaskActivity);
                    tv_driving_speed.setText(getString(R.string.drivesped) + String.valueOf(frtResponsePtrollerLocationModel.getDriving_speed()).trim());
                    tv_battery_percentage = new TextView(frtRouteControllerTaskActivity);
                    tv_battery_percentage.setText(getString(R.string.batterpercent) + String.valueOf(frtResponsePtrollerLocationModel.getBattery_percentage()).trim() + "%");
                    tv_mobile_time = new TextView(frtRouteControllerTaskActivity);
                    tv_mobile_time.setText(getString(R.string.mobtime) + frtResponsePtrollerLocationModel.getMobile_time().trim());
                    tv_server_time = new TextView(frtRouteControllerTaskActivity);
                    tv_server_time.setText(getString(R.string.servertime) + frtResponsePtrollerLocationModel.getServer_time().trim());
                    subInfoView.addView(tv_tracking_id);
                    subInfoView.addView(tv_route_assignment_id);
                    subInfoView.addView(tv_patroller_id);
                    subInfoView.addView(tv_patroller_user_name);
                    subInfoView.addView(tv_patroller_full_name);
                    subInfoView.addView(tv_network_provider);
                    subInfoView.addView(tv_is_inside_buffer);
                    subInfoView.addView(tv_deviation_from_planned);
                    subInfoView.addView(tv_dist_from_last_loc);
                    subInfoView.addView(tv_driving_speed);
                    subInfoView.addView(tv_battery_percentage);
                    subInfoView.addView(tv_mobile_time);
                    subInfoView.addView(tv_server_time);
                }
                infoView.addView(subInfoView);
            } else {
                infoView = new LinearLayout(frtRouteControllerTaskActivity);
                LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                infoView.setOrientation(LinearLayout.HORIZONTAL);
                infoView.setLayoutParams(infoViewParams);
                LinearLayout subInfoView = new LinearLayout(frtRouteControllerTaskActivity);
                LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                subInfoView.setOrientation(LinearLayout.VERTICAL);
                subInfoView.setLayoutParams(subInfoViewParams);
                TextView tv_halt_name, tv_checkin_radius, tv_time_to_spend;
                tv_halt_name = new TextView(frtRouteControllerTaskActivity);
                tv_checkin_radius = new TextView(frtRouteControllerTaskActivity);
                tv_time_to_spend = new TextView(frtRouteControllerTaskActivity);
                for (PTRResponseGetHaltPointsModel frtResponseGetHaltPointsModel :
                        frtResponseGetHaltPointsModelArrayList) {
                    if (marker.getPosition().equals(new LatLng(Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lat()), Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lng())))) {
                        tv_halt_name.setText(getString(R.string.halt_name) + " " + frtResponseGetHaltPointsModel.getHalt_name());
                        tv_checkin_radius.setText(getString(R.string.checkin_radius) + " " + frtResponseGetHaltPointsModel.getCheckin_radius() + " meter");
                        tv_time_to_spend.setText(getString(R.string.time_to_spend) + " " + frtResponseGetHaltPointsModel.getTimeto_spend() + " minutes");
                    }
                }
                subInfoView.addView(tv_halt_name);
                subInfoView.addView(tv_checkin_radius);
                subInfoView.addView(tv_time_to_spend);
                infoView.addView(subInfoView);
            }
            return infoView;
        }
    };
    private final GoogleMap.OnMarkerClickListener onMarkerClick = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker mark) {
            double radius = 0;
            marker = mark;
            String title = "";
            boolean isCurrentCheckIn = false, isCurrentCheckOut = false;
            LatLng clickedMarkerPosition = marker.getPosition();
            if (/*trackId > 0 || */(frtSharePrefUtil.getString("case") != null
                    && frtSharePrefUtil.getString("case").equals("PatrollerLocation"))) {
                marker.showInfoWindow();
            } else if (getIntent().getStringExtra("case").equals("Attendance") || getIntent().getStringExtra("case").equals("PlannedTask")) {
                return true;
            } else {
                try {
                    for (PTRResponseGetHaltPointsModel frtResponseGetHaltPointsModel :
                            frtResponseGetHaltPointsModelArrayList) {
                        double lat = Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lat());
                        double lng = Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lng());
                        if (mark.getPosition().equals(new LatLng(lat, lng))) {
                            selectedMarkerPosition = frtResponseGetHaltPointsModel.getPosition();
                            title = frtResponseGetHaltPointsModel.getHalt_name();
                            radius = frtResponseGetHaltPointsModel.getCheckin_radius();
                            if (frtResponseGetHaltPointsModel.getCheckin_remarks().contains("checkin") && !frtResponseGetHaltPointsModel.getCheckout_remarks().contains("checkout")) {
                                isCurrentCheckIn = true;

                            } else if (frtResponseGetHaltPointsModel.getCheckin_remarks().contains("checkin") && frtResponseGetHaltPointsModel.getCheckout_remarks().contains("checkout")) {
                                isCurrentCheckOut = true;

                            }
                        }
                    }
                    if (frtSharePrefUtil.getString("case") != null) {
                        LatLng plannedSource = null, plannedDest = null, actualSource = null, actualDest = null;
                        if (frtResponseActualRouteModelList != null && frtResponseActualRouteModelList.size() > 0) {
                            actualSource = new LatLng(frtResponseActualRouteModelList.get(0).getLat(), frtResponseActualRouteModelList.get(0).getLng());
                            actualDest = new LatLng(frtResponseActualRouteModelList.get(frtResponseActualRouteModelList.size() - 1).getLat(),
                                    frtResponseActualRouteModelList.get(frtResponseActualRouteModelList.size() - 1).getLng());
                        }
                        if (frtResponsePlannedRouteModelList != null && frtResponsePlannedRouteModelList.size() > 0) {
                            plannedSource = new LatLng(frtResponsePlannedRouteModelList.get(0).getLat(),
                                    frtResponsePlannedRouteModelList.get(0).getLng());
                            plannedDest = new LatLng(frtResponsePlannedRouteModelList.get(frtResponsePlannedRouteModelList.size() - 1).getLat(),
                                    frtResponsePlannedRouteModelList.get(frtResponsePlannedRouteModelList.size() - 1).getLng());
                        }
                        if (!frtSharePrefUtil.getString("case").equals("Pending") && !frtSharePrefUtil.getString("case").equals("Completed") && ((frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Start") || frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume") || !frtSharePrefUtil.getString("case").equals("Assigned"))))
                           /* if ((getString(R.string.reasonname)).equals("Start") ||
                                   *//* frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Pause") ||*//*
                                    frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume") ||
                                    frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Inprogress")) */ {
                            if (!clickedMarkerPosition.equals(plannedSource) && !clickedMarkerPosition.equals(plannedDest)
                                    && !clickedMarkerPosition.equals(actualSource)
                                    && !clickedMarkerPosition.equals(actualDest) && !isSaved) {
                                if (frtResponseGetHaltPointsModelArrayList != null && frtResponseGetHaltPointsModelArrayList.size() > 0)
                                    for (PTRResponseGetHaltPointsModel frtResponseGetHaltPointsModel :
                                            frtResponseGetHaltPointsModelArrayList) {
                                        if (clickedMarkerPosition.equals(new LatLng(Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lat())
                                                , Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lng()))))
                                            trackId = frtResponseGetHaltPointsModel.getHalt_tracking_id();
                                    }
                                if (isCurrentCheckOut) {
                                    frtUtility.setSnackBar("Already checked out", endButton);
                                } else {
                                    if (isCurrentCheckIn) {
                                        isCheckIn = false;
                                        remark = "CHECKOUT";
                                        showDialogHaltPoint(title);

                                    } else {
                                        if (/*isPreMarkerCheckIn*/checkedInMarkerPosition != null) {
                                            if (!checkedInMarkerPosition.equals(clickedMarkerPosition))
                                                frtUtility.setSnackBar("Already checked in a location", endButton);
                                        } else {
                                            minDistanceHaultPoint = frtUtility.calculationByDistance(mark.getPosition(), userCurrentLatLon);
                                            if (minDistanceHaultPoint < radius) {
                                                remark = "CHECKIN";
                                                isCheckIn = true;
                                                showDialogHaltPoint(title);
                                            } else {
                                                frtUtility.setSnackBar("You are outside of the checkin area", endButton);
                                            }

                                        }
                                    }
                                }
                            }
                            if (marker.getPosition().equals(plannedSource) || marker.getPosition().equals(plannedDest) || marker.getPosition().equals(actualSource) || marker.getPosition().equals(actualDest)) {
                                marker.hideInfoWindow();
                                return true;
                            }
                            return false;
                        } else {
                            if (marker.getPosition() != plannedSource && marker.getPosition() != plannedDest && marker.getPosition() != actualSource && marker.getPosition() != actualDest) {
                                //  frtUtility.setSnackBar(getString(R.string.tasknotstatedyet), endButton);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.e(frtRouteControllerTaskActivity.getClass().getName(), ex.toString());
                }
            }
            return true;
        }
    };
    private Bitmap bitmapSetView;
    private File storageDir;

    private void showDialogHaltPoint(String title) {
        markerId = marker.getId();
        marker.showInfoWindow();
        onClickHaltMarker(title);
    }

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
                    tv_zoom_level.setText("Zoom level: " + (int) googleMap.getCameraPosition().zoom);
                    if (isTilesVisible) {
                        if (tileOverlay != null)
                            tileOverlay.remove();
                        setUpCustomMap();
                    }
                }

            });
            googleMap.setOnMarkerClickListener(onMarkerClick);
            if (ActivityCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public Intent svcintent;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPermissions();
    }

    private void scheduleLocationSync() {
        try {
            Intent a = new Intent(frtRouteControllerTaskActivity, SyncService.class);
            startService(a);
//                }
        } catch (Exception ignored) {
            Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
        }
    }

    /**
     * This method is used for authorising necessary permissions for initializing google map android
     */
    @SuppressLint("InlinedApi")
    private void appPermissions() {
        frtConstants = new FRTConstants();
        if (ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(frtRouteControllerTaskActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
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

//    private void setAllPlannedDataToDb(List<PTRResponsePlannedRouteModel> frtResponsePlannedRouteModelList) {
//        if (intent.hasExtra("case") && (intent.getStringExtra("case").equals("Assigned") || intent.getStringExtra("case").equals("Inprogress"))) {
////            for (PTRResponsePlannedRouteModel frtResponsePlannedRouteModel :
////                    frtResponsePlannedRouteModelList) {
////            }
//        }
//    }

    private void callBackSetUp() {
        frtCallBackInitViews = frtRouteControllerTaskActivity;
        frtCallBackSetListeners = frtRouteControllerTaskActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    private synchronized void buildGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(frtRouteControllerTaskActivity)
                    .addConnectionCallbacks(frtRouteControllerTaskActivity)
                    .addOnConnectionFailedListener(frtRouteControllerTaskActivity)
                    .addApi(LocationServices.API)
                    .addApi(Awareness.API)
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
            if (ContextCompat.checkSelfPermission(frtRouteControllerTaskActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, frtRouteControllerTaskActivity);
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
    public void initAllViews() {
        frtRequestSaveRouteLocationmodelDb = new PTRRequestSaveRouteLocationmodel();
        frtCnnectivityReceiver = new FRTConnectivityReceiver();
        frtConstants = new FRTConstants();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtRouteControllerTaskActivity);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        frtUtility.checkGps(frtRouteControllerTaskActivity);
        frtUtility.settingStatusBarColor(frtRouteControllerTaskActivity, R.color.colorPrimary);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtRouteControllerTaskActivity);
        frtWEBAPI = new FRTWEBAPI();
        handler = new Handler();
        frtLocationDb = new FRTLocationDb(frtRouteControllerTaskActivity);
        frtApp = (FRTApp) getApplication();
        frtApp.setCtx(frtRouteControllerTaskActivity);
        intent = getIntent();
        mSensorService = new FRTLocationTrackingService();
        initUi();
        setUpMap();
    }

    @SuppressLint("SetTextI18n")
    private void initUi() {
        if ((intent.hasExtra("case") && intent.getStringExtra("case").equals("PatrollerLocation"))) {
            relbottom.setVisibility(View.GONE);
            routetitle.setText(intent.getStringExtra("userName"));
//            selfie.setVisibility(View.GONE);
            showPatrollerCurrentLocationOnMap();
        }
        try {
            if (intent.hasExtra("keyparcel"))
                frtResponseRouteCommonModel = intent.getParcelableExtra("keyparcel");
            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getRoute()))
                routetitle.setText(frtResponseRouteCommonModel.getRoute());
            else
                routetitle.setText("");
            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getPlanned_start_time()))
                starttime.setText(frtResponseRouteCommonModel.getPlanned_start_time());
            else
                starttime.setText("");
            if (!TextUtils.isEmpty(frtResponseRouteCommonModel.getPlanned_end_time()))
                endtime.setText(frtResponseRouteCommonModel.getPlanned_end_time());
            else
                endtime.setText("");
            FRTLocationTrackingService frtLocationTrackingService = new FRTLocationTrackingService();

              /*  speedDb1.setText("Speed:" +frtRequestSaveRouteLocationmodelList.getDrivingSpeed());
                distanceText.setText("distance:" +frtRequestSaveRouteLocationmodelList.getDistFromLastLoc());
                time.setText("time:" +frtResponsePtrollerLocationModel.getMobile_time());
                speedDbLocation.setText("Servtime:" +frtResponsePtrollerLocationModel.getServer_time());*/


        } catch (Exception ex) {
        }
        if (intent.hasExtra("case")) {
            frtSharePrefUtil.setString("case", intent.getStringExtra("case"));
            if (intent.getStringExtra("case").equals("Inprogress") && (TextUtils.isEmpty(frtSharePrefUtil.getString("keytracktoend"))
                    || !frtSharePrefUtil.getString("keytracktoend").equals("true"))) {
                startButton.setVisibility(View.GONE);
                endButton.setVisibility(View.VISIBLE);
                selfie.setVisibility(View.VISIBLE);
                pauseresumeButton.setVisibility(View.VISIBLE);
                llactualend.setVisibility(View.GONE);
                upload_docs.setVisibility(View.VISIBLE);
                raise_issue.setVisibility(View.VISIBLE);
                selfie.setVisibility(View.VISIBLE);
                if (!frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Pause")) {
                    frtSharePrefUtil.setString(getString(R.string.reasonname), "Inprogress");
                    scheduleLocationUpload();
                    scheduleLocationSync();
                }
                if (frtApp.getActualStartTime() != null && !frtApp.getActualStartTime().equals("null"))
                    actualstarttime.setText(frtApp.getActualStartTime());
                else
                    actualstarttime.setText("");
                if (frtSharePrefUtil.getString("keypauseresume").equals("RESUME")) {
                    endButton.setEnabled(false);
                    endButton.setAlpha(0.5f);
                    pauseresumeButton.setText("RESUME");
                } else if (frtSharePrefUtil.getString("keypauseresume").equals("PAUSE"))
                    pauseresumeButton.setText("PAUSE");
            } else if (intent.getStringExtra("case").equals("Assigned")) {
                selfie.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.GONE);
                upload_docs.setVisibility(View.GONE);
                raise_issue.setVisibility(View.GONE);
                pauseresumeButton.setVisibility(View.GONE);
                llactualstart.setVisibility(View.GONE);
                llactualend.setVisibility(View.GONE);
//                selfie.setVisibility(View.GONE);
            } else {
                if (intent.hasExtra("case") && !intent.getStringExtra("case").equals("PatrollerLocation")) {
                    selfie.setVisibility(View.VISIBLE);
                } else {
                    selfie.setVisibility(View.GONE);
                }
//                selfie.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                endButton.setVisibility(View.GONE);
                upload_docs.setVisibility(View.GONE);
                raise_issue.setVisibility(View.GONE);
//                selfie.setVisibility(View.GONE);
                pauseresumeButton.setVisibility(View.GONE);
                if (frtApp.getActualStartTime() != null && !frtApp.getActualStartTime().equals("null"))
                    actualstarttime.setText(frtApp.getActualStartTime());
                else
                    actualstarttime.setText("");
                if (frtApp.getActualEndTime() != null && !frtApp.getActualEndTime().equals("null"))
                    actualendtime.setText(frtApp.getActualEndTime());
                else
                    actualendtime.setText("");
            }
            if (intent.getStringExtra("case").equals("Completed") || intent.getStringExtra("case").equals("Pending") || frtLocationDb.offlineRouteEnded()) {
                frtUtility.setSnackBar(getString(R.string.routecompleted), endButton);
                startButton.setVisibility(View.GONE);
                selfie.setVisibility(View.VISIBLE);
                pauseresumeButton.setVisibility(View.GONE);
                endButton.setVisibility(View.GONE);
                selfie.setVisibility(View.GONE);
                upload_docs.setVisibility(View.GONE);
                raise_issue.setVisibility(View.GONE);
            }
        }
        if (intent.hasExtra("routeassignedId")) {
            if (intent.hasExtra("routetitle"))
                routetitle.setText(intent.getStringExtra("routetitle"));
            getPlannedRouteData();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActualRouteData();
                }
            }, 5000);
            relbottom.setVisibility(View.GONE);
            selfie.setVisibility(View.GONE);
            upload_docs.setVisibility(View.GONE);
            raise_issue.setVisibility(View.GONE);
        }
        if ((intent.hasExtra("case") && intent.getStringExtra("case").equals("PlannedTask")) && intent.hasExtra("routeId") && intent.hasExtra("routeRefId")) {
            if (intent.hasExtra("routetitle"))
                routetitle.setText(intent.getStringExtra("routetitle"));
            getPlannedRouteData();
            relbottom.setVisibility(View.GONE);
            selfie.setVisibility(View.GONE);
            upload_docs.setVisibility(View.GONE);
            raise_issue.setVisibility(View.GONE);
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        Double speedDriver = intent.getDoubleExtra("speedDriver", 0);
        Double locationSpeed = intent.getDoubleExtra("LocationSpeed", 0);
        // speedDbLocation.setText(locationSpeed + "");
        //String time = intent.getStringExtra("time");
        speedDb1.setText("Speed:" + String.format("%.2f", speedDriver));

    }


    private void showPatrollerCurrentLocationOnMap() {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            frtApp.setCheckForDialog("Yes");
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestGetPtrollerLocationModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpGetPatrollerLocation());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.GETCURRENTLOCATION), "getcurrentlocation");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONObject jo = jsonObject.optJSONObject("results");
                            frtResponsePtrollerLocationModel = new PTRResponsePtrollerLocationModel();
                            frtResponsePtrollerLocationModel.setIs_inside_buffer(jo.optString("is_inside_buffer"));
                            frtResponsePtrollerLocationModel.setDeviation_from_planned(jo.optInt("deviation_from_planned"));
                            frtResponsePtrollerLocationModel.setNetwork_provider(jo.optString("network_provider"));
                            frtResponsePtrollerLocationModel.setRoute_assignment_id(jo.optInt("route_assignment_id"));
                            frtResponsePtrollerLocationModel.setPatroller_full_name(jo.optString("patroller_full_name"));
                            frtResponsePtrollerLocationModel.setPatroller_user_name(jo.optString("patroller_user_name"));
                            frtResponsePtrollerLocationModel.setDriving_speed(jo.optInt("driving_speed"));
                            frtResponsePtrollerLocationModel.setLatitude(jo.optDouble("latitude"));
                            frtResponsePtrollerLocationModel.setLongitude(jo.optDouble("longitude"));
                            frtResponsePtrollerLocationModel.setDist_from_last_loc(jo.optDouble("dist_from_last_loc"));
                            frtResponsePtrollerLocationModel.setPatroller_id(jo.optInt("patroller_id"));
                            frtResponsePtrollerLocationModel.setTracking_id(jo.optInt("tracking_id"));
                            frtResponsePtrollerLocationModel.setServer_time(jo.optString("server_time"));
                            frtResponsePtrollerLocationModel.setMobile_time(jo.optString("mobile_time"));
                            frtResponsePtrollerLocationModel.setBattery_percentage(jo.optInt("battery_percentage"));
                            routetitle.setText(frtResponsePtrollerLocationModel.getPatroller_full_name());
                            frtApp.setLatLong(new LatLng(frtResponsePtrollerLocationModel.getLatitude(), frtResponsePtrollerLocationModel.getLongitude()));
                        } else if (jsonObject.optString("status").equalsIgnoreCase("UNKNOWN_ERROR")) {
                            // frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), findViewById(R.id.header));
                            frtUtility.setSnackBar(jsonObject.optString("error_message").toString(), endButton);

                        }

                        else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        }

                        else
                            frtUtility.setSnackBar(jsonObject.optString("Message"), endButton);
                    } catch (Exception ignored) {
                        //Log.d(this)
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private PTRRequestGetPtrollerLocationModel setUpGetPatrollerLocation() {
        PTRRequestGetPtrollerLocationModel frtRequestGetPtrollerLocationModel = new PTRRequestGetPtrollerLocationModel();
        if (intent.hasExtra("userid"))
            frtRequestGetPtrollerLocationModel.setUserid(String.valueOf(intent.getIntExtra("userid", -1)));
        return frtRequestGetPtrollerLocationModel;
    }

    private void setUpMap() {
        supportMapFragment = new SupportMapFragment();
        frtApp.setIsOneTime(false);
        if (frameLayout != null) {
            getSupportFragmentManager().beginTransaction().add(frameLayout.getId(), supportMapFragment).commit();
            supportMapFragment.getMapAsync(onMapReadyCallback);
        }

        if ((intent.hasExtra("case") && !intent.getStringExtra("case").equals("PatrollerLocation"))) {
            handleTaskOnMap(true);
        }
    }

    private void handleTaskOnMap(boolean b) {
        long wait;
        if (b)
            wait = frtConstants.KEY_SPLASH_TIME;
        else
            wait = 100;
        handler.postDelayed(new Runnable() {
            Runnable runnable = this;

            @Override
            public void run() {
                if (intent.hasExtra("case") && handler != null) {
                    handleTaskTypesOnMap(runnable);
                }
            }
        }, wait);
    }

    private void handleTaskTypesOnMap(Runnable runnable) {
        if (intent.getStringExtra("case").equals("Completed") || intent.getStringExtra("case").equals("Pending")) {
            getPlannedRouteData();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActualRouteData();
                }
            }, 11000);
        } else if (intent.getStringExtra("case").equals("Inprogress")) {
//            Runnable runnable = this;
            getActualRouteData();
            if (!flagIsPlannedOnce) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                                    if (count < 1) {
                        getPlannedRouteData();
//                                        count++;
//                                    }
                    }
                }, 8000);
                flagIsPlannedOnce = true;
            }
            handler.postDelayed(runnable, 10000);
            Log.d("inner", "checkruninner");
        } else {
            getPlannedRouteData();
        }
        Log.d("outer", "checkrunouter");
    }

    private void getPlannedRouteData() {
        if (frtSharePrefUtil.getString("self_check_in").equals("No") || frtSharePrefUtil.getString("self_check_in").isEmpty())
            if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                frtApp.setCheckForDialog("Yes");
                @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestPlannedRouteModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
                frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
                frtAsyncCommon.setFrtModel(setUpPlannedRouteModel());
                frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.PLANNED_ROUTE), "getplannedroute");
                frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                                                        @Override
                                                        public void getAsyncData(String response, String type) {
//                                                            frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
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
                                                                    drawPlannedRoute();
                                                                    getHaltPoints();
                                                                    if (intent.hasExtra("case") && intent.getStringExtra("case").equals("Inprogress"))
                                                                        frtApp.setIsOneTime(true);
                                                                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                                                                    frtUtility.goToLogin(getString(R.string.req_denied));
                                                                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                                                                   /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                                                                    getRefereshTokenData();
                                                                } else
                                                                    frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                                                            } catch (Exception e) {
                                                            }
                                                        }
                                                    }

                );
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
            }
    }

    private void getHaltPoints() {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
//            frtApp.setCheckForDialog("Yes");
            final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
            frtUtility.show(progressDialog);
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestGetHaltPointsModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpGetHaltPointsModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.GET_HALT_POINTS), "getHaltPoints");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    try {
                        frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONArray jo = jsonObject.optJSONArray("results");
                            frtResponseGetHaltPointsModelArrayList = new ArrayList<>();
                            for (int i = 0; i < jo.length(); i++) {
                                PTRResponseGetHaltPointsModel frtResponseGetHaltPointsModel = new PTRResponseGetHaltPointsModel();
                                frtResponseGetHaltPointsModel.setHalt_tracking_id(jo.optJSONObject(i).optInt("halt_tracking_id"));
                                frtResponseGetHaltPointsModel.setHalt_id(jo.optJSONObject(i).optInt("halt_id"));
                                frtResponseGetHaltPointsModel.setPosition(i);
                                frtResponseGetHaltPointsModel.setHalt_name(jo.optJSONObject(i).optString("halt_name"));
                                frtResponseGetHaltPointsModel.setPlanned_lat(jo.optJSONObject(i).optString("planned_lat"));
                                frtResponseGetHaltPointsModel.setPlanned_lng(jo.optJSONObject(i).optString("planned_lng"));
                                frtResponseGetHaltPointsModel.setActual_lat(jo.optJSONObject(i).optString("actual_lat"));
                                frtResponseGetHaltPointsModel.setActual_lng(jo.optJSONObject(i).optString("actual_lng"));
//                                frtResponseGetHaltPointsModel.setPosition(i);
                                frtResponseGetHaltPointsModel.setMobile_checkin_time(jo.optJSONObject(i).optString("mobile_checkin_time"));
                                frtResponseGetHaltPointsModel.setMobile_checkout_time(jo.optJSONObject(i).optString("mobile_checkout_time"));
                                frtResponseGetHaltPointsModel.setCheckin_remarks(jo.optJSONObject(i).optString("checkin_remarks"));
                                frtResponseGetHaltPointsModel.setCheckout_remarks(jo.optJSONObject(i).optString("checkout_remarks"));
                                frtResponseGetHaltPointsModel.setCheckin_radius(jo.optJSONObject(i).optInt("checkin_radius"));
                                frtResponseGetHaltPointsModel.setTimeto_spend(jo.optJSONObject(i).optString("timeto_spend"));
                                frtResponseGetHaltPointsModelArrayList.add(frtResponseGetHaltPointsModel);
                            }
                            // drawPlannedRoute();
                            plotHaltPointMarkers(R.mipmap.haltpoint_red);
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else
                            frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    List<PTRResponseSnapToRoadModel> frtResponseSnapToRoadModelList;

    private void getSnapToRoadData() {
        FRTAsyncCommon<PTRRequestSnapToRoadModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(frtWEBAPI.getWEBAPI(FRTAPIType.SNAP_TO_ROAD)).append("path=");
        for (int i = 0; i < frtResponseActualRouteModelList.size(); i++) {
            stringBuilder.append(frtResponseActualRouteModelList.get(i).getLat());
            stringBuilder.append(",");
            stringBuilder.append(frtResponseActualRouteModelList.get(i).getLng());
            if (frtResponseActualRouteModelList.size() - 1 != i)
                stringBuilder.append("|");
        }
        stringBuilder.append("&key=").append(getString(R.string.google_map_road_key));
        frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SNAP_TO_ROAD), "snaptoroadgoogle", stringBuilder.toString());
        frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
            @Override
            public void getAsyncData(String response, String type) {
                if (response != null) {
                    JSONObject jo;
                    frtResponseSnapToRoadModelList = new ArrayList<>();
                    try {
                        jo = new JSONObject(response);
                        JSONArray ja = jo.optJSONArray("snappedPoints");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo1 = ja.optJSONObject(i);
                            JSONObject jo2 = jo1.optJSONObject("location");
                            PTRResponseSnapToRoadModel frtResponseSnapToRoadModel = new PTRResponseSnapToRoadModel();
                            frtResponseSnapToRoadModel.setLatitude(jo2.optString("latitude"));
                            frtResponseSnapToRoadModel.setLongitude(jo2.optString("longitude"));
                            frtResponseSnapToRoadModel.setPlaceId(jo1.optString("placeId"));
                            frtResponseSnapToRoadModelList.add(frtResponseSnapToRoadModel);
                        }
                        drawActualRoute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private PTRRequestGetHaltPointsModel setUpGetHaltPointsModel() {
        PTRRequestGetHaltPointsModel frtRequestGetHaltPointsModel = new PTRRequestGetHaltPointsModel();
        try {
            frtRequestGetHaltPointsModel.setRouteId(Integer.parseInt(frtSharePrefUtil.getString("routeId")));
            frtRequestGetHaltPointsModel.setRouteRefId(Integer.parseInt(frtSharePrefUtil.getString("routeRefId")));
            frtRequestGetHaltPointsModel.setRouteAssignmentId(Integer.parseInt(frtSharePrefUtil.getString("routeassigmentid")));

        } catch (Exception ex) {
        }
        return frtRequestGetHaltPointsModel;
    }

    private void drawPlannedRoute() {
        //noinspection unchecked
        frtUtility.setLatLngList(frtResponsePlannedRouteModelList, true);
        LatLng source = new LatLng(frtResponsePlannedRouteModelList.get(0).getLat(), frtResponsePlannedRouteModelList.get(0).getLng());
        LatLng dest = new LatLng(frtResponsePlannedRouteModelList.get(frtResponsePlannedRouteModelList.size() - 1).getLat(),
                frtResponsePlannedRouteModelList.get(frtResponsePlannedRouteModelList.size() - 1).getLng());
        Log.d("source dest", source + "" + dest + "");
        frtUtility.drawRoute(source, dest, markerList, Color.parseColor("#5AA1E4"), userCurrentLatLon, R.mipmap.plannedstart, R.mipmap.plannedend);
        frtUtility.dialogHide();
    }

    private PTRRequestPlannedRouteModel setUpPlannedRouteModel() {
        PTRRequestPlannedRouteModel frtRequestAssignedRouteModel = new PTRRequestPlannedRouteModel();
        try {
            if (intent.hasExtra("routeId")) {
                frtRequestAssignedRouteModel.setRouteId(intent.getIntExtra("routeId", -1));
                frtSharePrefUtil.setString("routeId", intent.getIntExtra("routeId", -1) + "");
            } else {
                frtRequestAssignedRouteModel.setRouteId(frtResponseRouteCommonModel.getRouteId());
                frtSharePrefUtil.setString("routeId", frtResponseRouteCommonModel.getRouteId() + "");
            }
            if (intent.hasExtra("routeRefId")) {
                frtRequestAssignedRouteModel.setRouteRefId(intent.getIntExtra("routeRefId", -1));
                frtSharePrefUtil.setString("routeRefId", intent.getIntExtra("routeRefId", -1) + "");
            } else {
                frtRequestAssignedRouteModel.setRouteRefId(frtResponseRouteCommonModel.getRouteRefId());
                frtSharePrefUtil.setString("routeRefId", frtResponseRouteCommonModel.getRouteRefId() + "");
            }
        } catch (Exception ex) {
        }
        return frtRequestAssignedRouteModel;
    }

    private void getActualRouteData() {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            frtApp.setCheckForDialog("Yes");
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestActualRouteModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpActualRouteModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.ACTUAL_ROUTE), "getactualroute");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONObject jo = jsonObject.optJSONObject("results");
                            JSONArray jsonArray = jo.optJSONArray("actual_route");
                            frtResponseActualRouteModelList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo1 = jsonArray.optJSONObject(i);
                                if (jo1.optString("T").equals("R")) {
                                    if (jo1.optDouble("lat") != 0.0 && jo1.optDouble("lng") != 0.0) {
                                        PTRResponseActualRouteModel frtResponseActualRouteModel = new PTRResponseActualRouteModel();
                                        frtResponseActualRouteModel.setLat(jo1.optDouble("lat"));
                                        frtResponseActualRouteModel.setLng(jo1.optDouble("lng"));
                                        frtResponseActualRouteModelList.add(frtResponseActualRouteModel);
                                    }
                                } else if (jo1.optString("T").equals("null")) {
                                    if (jo1.optDouble("lat") != 0.0 && jo1.optDouble("lng") != 0.0) {
                                        PTRResponseActualRouteModel frtResponseActualRouteModel = new PTRResponseActualRouteModel();
                                        frtResponseActualRouteModel.setLat(jo1.optDouble("lat"));
                                        frtResponseActualRouteModel.setLng(jo1.optDouble("lng"));
                                        frtResponseActualRouteModelList.add(frtResponseActualRouteModel);
                                    }
                                }
                            }
                            drawActualRoute();
//                            getSnapToRoadData();
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else
                            frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                    } catch (Exception ignored) {
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private void drawActualRoute() {
        //noinspection unchecked
//        if (intent.hasExtra("case") && intent.getStringExtra("case").equals("Inprogress")) {
        frtUtility.setLatLngList(frtResponseActualRouteModelList, false);
//        } else {
//            frtUtility.setLatLngList(frtResponseSnapToRoadModelList, false);
//        }
        LatLng source = new LatLng(frtResponseActualRouteModelList.get(0).getLat(),
                frtResponseActualRouteModelList.get(0).getLng());
        LatLng dest = new LatLng(frtResponseActualRouteModelList.get(frtResponseActualRouteModelList.size() - 1).getLat(),
                frtResponseActualRouteModelList.get(frtResponseActualRouteModelList.size() - 1).getLng());
        Log.d("source dest", source + "" + dest + "");
        if (frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Pause")) {
            frtUtility.drawRoute(source, dest, markerList1, Color.parseColor("#40D67E"), userCurrentLatLon, R.mipmap.actualstart, R.mipmap.actualpausedinprogress);
            frtUtility.dialogHide();
        } else if (frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume")) {
            frtUtility.drawRoute(source, dest, markerList1, Color.parseColor("#40D67E"), userCurrentLatLon, R.mipmap.actualstart, R.mipmap.actualpending);
            frtUtility.dialogHide();
        } else if (intent.hasExtra("case") && (intent.getStringExtra("case").equals("Pending") || intent.getStringExtra("case").equals("Inprogress"))) {
            frtUtility.drawRoute(source, dest, markerList1, Color.parseColor("#40D67E"), userCurrentLatLon, R.mipmap.actualstart, R.mipmap.actualpending);
            frtUtility.dialogHide();
        } else {
            frtUtility.drawRoute(source, dest, markerList1, Color.parseColor("#40D67E"), userCurrentLatLon, R.mipmap.actualstart, R.mipmap.actualend);
            frtUtility.dialogHide();
        }
    }

    private PTRRequestActualRouteModel setUpActualRouteModel() {
        PTRRequestActualRouteModel frtRequestActualRouteModel = new PTRRequestActualRouteModel();
        try {
            if (intent.hasExtra("routeassignedId"))
                frtRequestActualRouteModel.setRouteAssignmentId(intent.getIntExtra("routeassignedId", -1));
            else
                frtRequestActualRouteModel.setRouteAssignmentId(frtResponseRouteCommonModel.getRouteAssignmentId());
        } catch (Exception ignored) {
        }
        return frtRequestActualRouteModel;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void commonListeners() {
        ivback.setOnClickListener(frtBackClick);
        startButton.setOnClickListener(onClickStartButton);
        pauseresumeButton.setOnClickListener(onClickPauseResume);
        endButton.setOnClickListener(onClickEndButton);
        upload_docs.setOnClickListener(onClickuploadDocs);
        selfie.setOnClickListener(onClickUploadSelfie);
        imgsattelite.setOnClickListener(onClickSattelite);
        imghybrid.setOnClickListener(onClickHybrid);
        imgnormal.setOnClickListener(onClickNormal);
        imgtraffic.setOnClickListener(onClickTraffic);
        raise_issue.setOnClickListener(onClickRaiseIssue);

        iv_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTilesVisible) {
                    if (tileOverlay != null) {
                        tileOverlay.remove();
                    }
                    iv_toggle.setColorFilter(ContextCompat.getColor(frtRouteControllerTaskActivity, R.color.grey));
                } else {
//                    setUpMap();
                    setUpCustomMap();
                    iv_toggle.setColorFilter(ContextCompat.getColor(frtRouteControllerTaskActivity, R.color.green));
                }
                isTilesVisible = isTilesVisible ? false : true;
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
                // frtUtility.setSnackBar("Traffic view", endButton);
                googleMap.setTrafficEnabled(!isClickedtraffic);
                isClickedtraffic = true;
//                imgtraffic.setColorFilter(ContextCompat.getColor(frtRouteControllerTaskActpivity, R.color.green));
            } else {
                //frtUtility.setSnackBar("Normal map view", endButton);
                googleMap.setTrafficEnabled(!isClickedtraffic);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isClickedtraffic = false;
//                imgtraffic.clearColorFilter();
            }
        }
    };
    private View.OnClickListener onClickNormal = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isSatteliteClicked || isHybridClicked) {
                //frtUtility.setSnackBar("Normal map view", endButton);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    };
    private View.OnClickListener onClickSattelite = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isSatteliteClicked && !isHybridClicked) {
                //    frtUtility.setSnackBar("Sattelite map view", endButton);
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                isSatteliteClicked = true;
//                imgsattelite.setColorFilter(ContextCompat.getColor(frtRouteControllerTaskActivity, R.color.green));
            } else {
                //  frtUtility.setSnackBar("Normal map view", endButton);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isSatteliteClicked = false;
//                imgsattelite.clearColorFilter();
            }
        }
    };
    private View.OnClickListener onClickHybrid = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isHybridClicked && !isSatteliteClicked) {
                // frtUtility.setSnackBar("Hybrib map view", endButton);
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                isHybridClicked = true;
            } else {
                //  frtUtility.setSnackBar("Normal map view", endButton);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isHybridClicked = false;
            }
            googleMap.setTrafficEnabled(false);
        }
    };
    private final View.OnClickListener onClickUploadSelfie = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            frtUtility.logInfo(frtRouteControllerTaskActivity,"clicked on --> selfie ",frtConstants);
            remark="selfie";
            showCamerDialog(null);
        }
    };

    private void sendSelfieDataToServer(Bitmap bitmap/*String base64*/) {
        try {
            if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
                progressDialog.setMessage("Please wait...");
                frtUtility.show(progressDialog);
                try {
                    base64Image = frtUtility.saveImageAsBase64(bitmap)/*base64*/;
                    frtUtility.deleteGalleyfile(frtRouteControllerTaskActivity,captured_img_date);
                } catch (Exception ex) {
                }
                @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestSelfieDataModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
                frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
                frtAsyncCommon.setFrtModel(setUpSelfieRouteModel());
                frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.UPLOAD_ROUT_TRACKING_IMAGE), "sendselfiedata");
                frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                    @Override
                    public void getAsyncData(String response, String type) {
                        frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                        if (response != null) {
                            try {
                                progressDialog.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optString("status").equals("OK")) {
                                    frtSharePrefUtil.setString("base64", "");
                                   getWeather();
                                    frtUtility.setSnackBar(getString(R.string.selfieuploaded), endButton);
                                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                                    frtUtility.goToLogin(getString(R.string.req_denied));
                                } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                                    getRefereshTokenData();
                                }
                            else
                                frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(this.getClass().getName(), "EX==" + e.getMessage());
                            }
                        }
                    }
                });
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
            }
        } catch (Exception ignored) {
            Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
        }
    }

    /*private void deleteGalleyfile() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" +"Camera"+"/");
        File list[] = file.listFiles();
        if(list.length >0){
            for( int i=0; i< list.length; i++)
            {
                if(captured_img_date - list[i].lastModified()<10000)
                {
                    if(list[i].isFile() && list[i].exists()){
                        list[i].delete();
                        MediaScannerConnection.scanFile(this,new String[]{list[i].toString()}, null, null);
                    }

                }
            }
        }

    }*/

    private PTRRequestSelfieDataModel setUpSelfieRouteModel() {
        PTRRequestSelfieDataModel frtRequestSelfieDataModel = new PTRRequestSelfieDataModel();
        try {
            if (intent.hasExtra("routeassignedId"))
                frtRequestSelfieDataModel.setRouteAssignmentId(intent.getIntExtra("routeassignedId", -1));
            else
                frtRequestSelfieDataModel.setRouteAssignmentId(frtResponseRouteCommonModel.getRouteAssignmentId());
            frtRequestSelfieDataModel.setAction("selfie");
            frtRequestSelfieDataModel.setLatitude(String.valueOf(userCurrentLatLon.latitude));
            frtRequestSelfieDataModel.setLongitude(String.valueOf(userCurrentLatLon.longitude));
            frtRequestSelfieDataModel.setMobileTime(frtUtility.getDateAndTime(false));
            frtRequestSelfieDataModel.setRemarks("selfie");
            frtRequestSelfieDataModel.setImageData(base64Image);
            frtUtility.logInfo(frtRouteControllerTaskActivity,"Took selfie ",frtConstants);
        } catch (Exception ignored) {
        }
        return frtRequestSelfieDataModel;
    }

    private final View.OnClickListener onClickRaiseIssue = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                Intent intent = new Intent(frtRouteControllerTaskActivity, PTRRaiseIssueActivity.class);
                frtUtility.logInfo(frtRouteControllerTaskActivity, "clicked on --> Raise Issue ", frtConstants);
//                startActivityForResult(intent, frtConstants.RAISE_ISSUE);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), view);
            }
        }
    };

    private final View.OnClickListener onClickuploadDocs = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            frtUtility.logInfo(frtRouteControllerTaskActivity, "Clicked on --> uploadDocs", frtConstants);
            frtLocationDb.exportDB(getApplicationContext());
            getFilechooser();
            //Log.d(this.getClass().getName(), "EXPORT_DB" +  frtLocationDb.exportDB(getApplicationContext()));
        }
    };
    private final View.OnClickListener onClickEndButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            remark = "END";
            frtListImagesBase64.clear();
            frtSharePrefUtil.setString("keypauseresume", "");
            setUiAccordingToAction("END");
            if (frtLocationDb.getTrackings(frtConstants.ROUTEEND).size() <= 0)
                frtSharePrefUtil.setString("routeassigmentid", "");
            else
                frtSharePrefUtil.setString("keytracktoend", "true");
            frtSharePrefUtil.setString("firstsms", "");
        }

    };
    private final View.OnClickListener onClickPauseResume = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            frtListImagesBase64.clear();
            if (pauseresumeButton.getText().toString().equalsIgnoreCase(getString(R.string.resume))) {
                frtSharePrefUtil.setString("keypauseresume", "PAUSE");
                remark = "RESUME";
                saveRouteActionData("RESUME");
            } else {
                remark = "PAUSE";
                setUiAccordingToAction("PAUSE");
            }
        }
    };
    private final View.OnClickListener onClickNo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
            selfie.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
            pauseresumeButton.setVisibility(View.VISIBLE);
            endButton.setVisibility(View.VISIBLE);
            upload_docs.setVisibility(View.VISIBLE);
            raise_issue.setVisibility(View.VISIBLE);
            pauseresumeButton.setText(getString(R.string.pause));
        }
    };

    private final View.OnClickListener onClickyes = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isEnd) {
                dialog.dismiss();
                bitmapSetView = null;
                selfie.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                pauseresumeButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.VISIBLE);
                upload_docs.setVisibility(View.VISIBLE);
                raise_issue.setVisibility(View.VISIBLE);
                frtApp.setCheckForDialog("No");
                Intent intent = new Intent(frtRouteControllerTaskActivity, PTRPauseReasonsActivity.class);
                startActivityForResult(intent, frtConstants.PAUSE_CODE);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            } else {
                dialog.dismiss();
                 showCamerDialog(null);
               // startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
            }
        }
    };
    private final View.OnClickListener onClickStartButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            remark = "START";
            frtListImagesBase64.clear();
            showCamerDialog(null);
           // startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
        }
    };
    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private void saveRouteActionData(final String action) {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRRequestSaveRouteAction> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);

            final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
            frtUtility.show(progressDialog);

            frtAsyncCommon.setFrtModel(setUprouteActionModel(action));
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SAVE_ROUTE_ACTION), "saverouteaction");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                    try {
                        progressDialog.dismiss();
                        bitmapSetView = null;
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            frtResponseSaveRouteActionList = new ArrayList<>();
                            for (int i = 0; i < jsonObject.length(); i++) {
                                PTRResponseSaveRouteAction cqResponseSummaryModel = new PTRResponseSaveRouteAction();
                                cqResponseSummaryModel.setStatus(jsonObject.optString("status"));
                                cqResponseSummaryModel.setError_message(jsonObject.optString("error_message"));
                                cqResponseSummaryModel.setResults(jsonObject.optString("results"));
                                frtResponseSaveRouteActionList.add(cqResponseSummaryModel);
                            }
                            if (!action.equals("PAUSE") && !action.equals("END"))
                                setUiAccordingToAction(action);
                            else if (action.equals("PAUSE")) {
                                frtUtility.dialogHide();
                                frtSharePrefUtil.setString("keypauseresume", "RESUME");
                                frtSharePrefUtil.setString(getString(R.string.reasonname), "Pause");

                                String name = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.username_shared)).getBytes("UTF-16LE"), Base64.DEFAULT));
                                // frtUtility.sendMessageOnPause(frtSharePrefUtil.getString(getString(R.string.manager_phone)), "\"" + name + "\"" + " " + getString(R.string.sendMessageOnPause) + " " + "\"" + comment + "\"");

                                //  sms send commented by ajay on 5-APR-2018
                                /*frtUtility.sendMessageOnPause(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.manager_phone)).getBytes("UTF-16LE"), Base64.DEFAULT)),
                                        "\"" + name + "\"" + " " + getString(R.string.sendMessageOnPause) + " " + "\"" + comment + "\"");
                                */
                                getActualRouteData();
                                frtUtility.setSnackBar("Route paused", endButton);

                            }
                            frtResponseSaveRouteActionList.clear();

                            getWeather();
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                           /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else
                            frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                    } catch (Exception ignored) {
                        frtUtility.setSnackBar(getString(R.string.servernotResponding), endButton);
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private void setUiAccordingToAction(String action) {
        switch (action) {
            case "START": {
                startCode();
                frtSharePrefUtil.setString(getString(R.string.reasonname), "Start");
            }
            break;
            case "PAUSE": {
                showPauseRouteDialog();
            }
            break;
            case "RESUME": {
                endButton.setEnabled(true);
                endButton.setAlpha(1.0f);
                pauseresumeButton.setText(getString(R.string.pause));
                frtUtility.setSnackBar("Route resumed", endButton);
                frtSharePrefUtil.setString(getString(R.string.reasonname), "Resume");
//                handleTaskOnMap(false);
                getActualRouteData();
            }
            break;
            case "END": {
                showEndRouteDialog();
                frtSharePrefUtil.setString(getString(R.string.reasonname), "End");
            }
            break;
        }
    }

    private void startCode() {
        selfie.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        pauseresumeButton.setVisibility(View.VISIBLE);
        endButton.setVisibility(View.VISIBLE);
        upload_docs.setVisibility(View.VISIBLE);
        raise_issue.setVisibility(View.VISIBLE);
        if (googleApiClient != null) {
            if (ActivityCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(frtRouteControllerTaskActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            frtUtility.setSnackBar("Routing started", endButton);
        }
    }

    private void showPauseRouteDialog() {
        isEnd = false;
        dialog = new Dialog(frtRouteControllerTaskActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_pause);
        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.no);
        FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.yes);
        tvno.setOnClickListener(onClickNo);
        tvyes.setOnClickListener(onClickyes);
        dialog.show();
    }

    private void showEndRouteDialog() {
        isEnd = true;
        dialog = new Dialog(frtRouteControllerTaskActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.endroute_dialog);
        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.no);
        FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.yes);
        tvno.setOnClickListener(onClickNo);
        tvyes.setOnClickListener(onClickyes);
        dialog.show();
    }

    public void plotHaltPointMarkers(int haltmarker) {
        radiusList = new ArrayList<>();
        if (frtResponseGetHaltPointsModelArrayList.size() > 0) {
            for (int i = 0; i < frtResponseGetHaltPointsModelArrayList.size(); i++) {
                BitmapDescriptor bitmapDescriptor;
                PTRResponseGetHaltPointsModel frtResponseGetHaltPointsModel = frtResponseGetHaltPointsModelArrayList.get(i);
                double lat = Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lat());
                double lng = Double.parseDouble(frtResponseGetHaltPointsModel.getPlanned_lng());
                LatLng halt = new LatLng(lat, lng);

                if (frtResponseGetHaltPointsModel.getCheckin_remarks().contains("checkin") && frtResponseGetHaltPointsModel.getCheckout_remarks().contains("checkout")) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.haltpoint_green);
                } else if (frtResponseGetHaltPointsModel.getCheckin_remarks().contains("checkin")) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.haltpoint_yellow);
                    checkedInMarkerPosition = halt;
                } else {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(haltmarker);
                }

                googleMap.addMarker(new MarkerOptions().position(halt).
                        icon(bitmapDescriptor));
            }
        }
    }

    private void onClickHaltMarker(String title) {
        if (frtLocationDb.offlineRouteEnded() == true)
            return;

        isEnd = true;
        String name = "";
        dialog = new Dialog(frtRouteControllerTaskActivity);
        if (isCheckIn) {
            dialog = frtUtility.dialogBasicStructure(dialog, R.layout.halt_point_dialog_checkin);
            name = "CheckIn " + title;
        } else {
            dialog = frtUtility.dialogBasicStructure(dialog, R.layout.halt_point_dialog_checkout);
            name = "Checkout " + title;
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
                //commented code by ajay
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

    private void saveHaltRoute(final boolean isCheckIn) {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            frtApp.setCheckForDialog("Yes");
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestSaveHaltPointsModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpSaveHaltPointsModel(isCheckIn));
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SAVE_HALT_POINTS), "saveHaltPoints");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("OK")) {
                                frtUtility.dialogHide();
                                isSaved = true;
                                bitmapSetView = null;
                                getWeather();
                                if (isCheckIn) {
                                    frtListImagesBase64.clear();
                                    frtResponseGetHaltPointsModelArrayList.get(selectedMarkerPosition).setCheckin_remarks("checkin");
                                    frtUtility.setSnackBar(getString(R.string.haltpointcheckin), endButton);
                                    Map<String, Integer> map = new HashMap<>();
                                    map.put("keytrackid", trackId);
//                                    frtSharePrefUtil.setObject(map);
                                    updateMarker(true);
                                } else {
                                    frtListImagesBase64.clear();
                                    frtUtility.setSnackBar(getString(R.string.haltpointcheckout), endButton);
                                    frtResponseGetHaltPointsModelArrayList.get(selectedMarkerPosition).setCheckout_remarks("checkout");
                                    frtResponseGetHaltPointsModelArrayList.get(selectedCheckInPosition).setCheckout_remarks("checkout");
//                                    frtSharePrefUtil.setObject(null);
                                    updateMarker(false);
                                    isPreMarkerCheckIn = false;
                                }
                                isSaved = false;
                            } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                                frtUtility.dialogHide();
                                frtUtility.goToLogin(getString(R.string.req_denied));
                            } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                                frtUtility.dialogHide();
                                /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                                getRefereshTokenData();
                            } else {
                                frtUtility.dialogHide();
                                frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                            }

                        } catch (Exception e) {
                            frtUtility.dialogHide();
                            e.printStackTrace();
                        }
                    } else {
                        frtUtility.setSnackBar("Did not get any response from server", endButton);
                        frtUtility.dialogHide();
                    }

                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private void updateMarker(boolean isCheckIn) {
        BitmapDescriptor bitmapDescriptor = null;
        LatLng checkInPosition = marker.getPosition();
        marker.remove();
        if (isCheckIn) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.haltpoint_yellow);
            checkedInMarkerPosition = marker.getPosition();
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.haltpoint_green);
            checkedInMarkerPosition = null;
        }

        googleMap.addMarker(new MarkerOptions().position(checkInPosition).
                icon(bitmapDescriptor));
    }

    private PTRRequestSaveHaltPointsModel setUpSaveHaltPointsModel(boolean isCheckIn) {
        PTRRequestSaveHaltPointsModel frtRequestSaveHaltPointsModel = new PTRRequestSaveHaltPointsModel();
        try {
            frtRequestSaveHaltPointsModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            frtRequestSaveHaltPointsModel.setHaltTrackingId(trackId);
            frtRequestSaveHaltPointsModel.setRouteAssignmentId(Integer.parseInt(frtSharePrefUtil.getString("routeassigmentid")));
            if (userCurrentLatLon != null) {
                frtRequestSaveHaltPointsModel.setLatitude(String.valueOf(userCurrentLatLon.latitude));
                frtRequestSaveHaltPointsModel.setLongitude(String.valueOf(userCurrentLatLon.longitude));
            }
            frtRequestSaveHaltPointsModel.setDateTime(frtUtility.getDateAndTime(false));
            if (isCheckIn)
                frtRequestSaveHaltPointsModel.setAction("checkin");
            else
                frtRequestSaveHaltPointsModel.setAction("checkout");
            frtRequestSaveHaltPointsModel.setImages(/*com.vodafone.frt.utility.Base64.encodeFromFile(base64Image)*/frtListImagesBase64);
            if (isCheckIn) {
                frtRequestSaveHaltPointsModel.setRemark("checkin");
                frtUtility.logInfo(frtRouteControllerTaskActivity, "Clicked on --> checkin", frtConstants);
            } else {
                frtRequestSaveHaltPointsModel.setRemark("checkout");
                frtUtility.logInfo(frtRouteControllerTaskActivity, "Clicked on --> checkout", frtConstants);
            }
        } catch (Exception ex) {
        }
        return frtRequestSaveHaltPointsModel;
    }

    private void showCamerDialog(Bitmap thumbnail) {
        final LatLng position = null;
        final BitmapDescriptor bitmapDescriptor = null;
        Dialog dialog = new Dialog(frtRouteControllerTaskActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_camera);
        final ImageView iv_capture1 = dialog.findViewById(R.id.iv_capture1);
        final ImageView iv_capture2 = dialog.findViewById(R.id.iv_capture2);
        final ImageView iv_capture3 = dialog.findViewById(R.id.iv_capture3);
        final ImageView iv_capture4 = dialog.findViewById(R.id.iv_capture4);
        final FRTEditTextTrebuchetMS et_comment = dialog.findViewById(R.id.et_comment);
        final FRTTextviewTrebuchetMS titlesaverouteaction = dialog.findViewById(R.id.titlesaverouteaction);
        titlesaverouteaction.setText(remark);

        if (thumbnail == null) {
            frtListImagesBase64.clear();
            iv_capture1.setImageResource(R.mipmap.ic_add);
        } else if (thumbnail != null) {
            base64Image = frtUtility.saveImageAsBase64(thumbnail);
            frtListImagesBase64.clear();
            frtListImagesBase64.add(base64Image);
            frtUtility.deleteGalleyfile(frtRouteControllerTaskActivity,captured_img_date);
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
        final Dialog finalDialog1 = dialog;
        iv_capture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewID = view.getId();
                frtListImagesBase64.clear();
                if (remark.equals("CHECKIN")) {
                    startCamera("checkin");
                    finalDialog.hide();
                } else if (remark.equals("CHECKOUT")) {
                    startCamera("checkout");
                    finalDialog.hide();
                } else if (remark.equals("selfie")){
                    startCamera("selfie");
                    finalDialog.hide();
                }else {
                    finalDialog.dismiss();
                    finalDialog1.dismiss();
                    //calling Custom Camera for on pause on resume functionality
                    startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
                    //frtListImagesBase64.clear();

                    //clearThumbnail();

                   // startCamera("routeaction");
                   // finalDialog.hide();
                }
            }
        });
        iv_capture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewID = view.getId();
                finalDialog.hide();
                startCamera("routeaction");
            }
        });
        iv_capture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewID = view.getId();
                finalDialog.hide();
                startCamera("routeaction");
            }
        });
        iv_capture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewID = view.getId();
                finalDialog.hide();
                startCamera("routeaction");
            }
        });

        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.cancel);
        final FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.ok);
        tvyes.setEnabled(true);
        tvno.setOnClickListener(onClickNo);
        tvyes.setOnClickListener(onClickyes);
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTRRouteControllerTaskActivity.this.base64Image = null;
                bitmapSetView = null;
                clearThumbnail();
                imageViewID = -1;
                finalDialog.hide();
                frtListImagesBase64.clear();
            }
        });
//        final Dialog finalDialog1 = dialog;

//        final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frtListImagesBase64.size() > 0) {
                    tvyes.setEnabled(false);
                    //   clearThumbnail();
                    switch (remark) {
                        case "START":
                            try {
//                            remark = "START";
                                try {
                                    List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.TRACKINGDATA);
                                    if (frtRequestSaveRouteLocationmodelList.size() <= 0) {
                                        frtLocationDb.recreateLocationTracking();
                                    }
                                } catch (Exception ignored) {
                                    Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                                }
                                saveRouteActionData("START");
                                scheduleLocationUpload();
                                scheduleLocationSync();
                                frtUtility.createTrackingTimeoutHandler();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    iv_capture1.setBackground(null);
                                    iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                                }
                            } catch (Exception ignored) {
                                Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                            }
                            break;

                        case "PAUSE":

                            pauseresumeButton.setText("Resume");
//                        remark = data.getStringExtra("remark");
                            endButton.setEnabled(false);
                            endButton.setAlpha(0.5f);
                            saveRouteActionData("PAUSE");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                iv_capture1.setBackground(null);
                                iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                            }
                            break;
                        case "END":
//                            int pendingsize = frtLocationDb.trackingDataCount();
//                            if (pendingsize > 0) {
//                            if (!frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                            if (googleApiClient != null) {
                                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, frtRouteControllerTaskActivity);
                                frtUtility.setSnackBar("Routing end", endButton);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    iv_capture1.setBackground(null);
                                    iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                                }
                                googleApiClient = null;
                            }
                           /*Gson gson = new Gson();
                            String json = gson.toJson(setUprouteActionModel("END"));*/
                            String json = prepareSaveRouteActionOfflineData(setUprouteActionModel("END"));
                            frtLocationDb.addOfflineData(json, "ROUTEEND");
                            frtApp.setCheckForDialog("No");

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRNavigationScreenActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                                    finish();
                                }
                            }, 3000);

                           /* } else {
                                endtime.setText(String.format(Locale.ENGLISH, "%s", frtUtility.getDateAndTime(false)));
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        frtApp.setCheckForDialog("No");
                                        Intent intent = new Intent(frtRouteControllerTaskActivity, PTRMyTaskActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                                    }
                                }, frtConstants.ROUTE_END_TIME);

                                if (googleApiClient != null) {
                                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, frtRouteControllerTaskActivity);
                                    frtUtility.setSnackBar("Routing end", endButton);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        iv_capture1.setBackground(null);
                                        iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                                    }
                                    saveRouteActionData("END");
                                }
                            }*/

                            break;
                        case "CHECKIN":
                            tvyes.setEnabled(false);
                            frtUtility.dialogPresent();
                            saveHaltRoute(true);
                            frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                            finalDialog.hide();
                            finalDialog1.hide();

                            break;
                        case "CHECKOUT":
                            tvyes.setEnabled(false);
                            frtUtility.dialogPresent();
                            saveHaltRoute(false);
                            frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                            finalDialog.hide();
                           finalDialog1.hide();
                            break;

                        case "selfie":
                            tvyes.setEnabled(false);
//                            frtUtility.dialogPresent();
                            sendSelfieDataToServer(bitmapSetView);
                            finalDialog.hide();
//                            finalDialog1.hide();
                            break;
                    }
                    finalDialog.hide();
//                    finalDialog1.hide();
                } else {

                    tvyes.setEnabled(true);
                    frtUtility.setSnackBar(getString(R.string.imagerequired), et_comment);
                }
            }
        });
       /* iv_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_capture.getRotation() == 0)
                    iv_capture.setRotation(90);
                else if (iv_capture.getRotation() == 90)
                    iv_capture.setRotation(180);
                else if (iv_capture.getRotation() == 180)
                    iv_capture.setRotation(270);
                else if (iv_capture.getRotation() == 270)
                    iv_capture.setRotation(0);
            }
        });*/
        if (!remark.equals("CHECKIN") || !remark.equals("CHECKOUT"))
            dialog.show();
    }

    private void clearThumbnail() {
        thumbnail1 = null;
        thumbnail2 = null;
        thumbnail3 = null;
        thumbnail4 = null;
    }

    private void showFileUploadDialog(final String selectedFilePath) {
        dialog = new Dialog(frtRouteControllerTaskActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_file_upload);
        FRTTextviewTrebuchetMS txtselectedfile = dialog.findViewById(R.id.tv_selected_file);
        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.cancel);
        FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.ok);
        final FRTEditTextTrebuchetMS et_comment = dialog.findViewById(R.id.et_comment1);
        et_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_comment.setCursorVisible(true);
                et_comment.setFocusable(true);
                et_comment.setFocusableInTouchMode(true);
                frtUtility.showSoftKeyboard(et_comment, frtRouteControllerTaskActivity);
            }
        });
        txtselectedfile.setText("" + selectedFilePath);
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
//        final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String comment = et_comment.getText().toString();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String[] Base64Doc = frtUtility.getFileNameFromFile(selectedFilePath);
                            if (Integer.parseInt(Base64Doc[1]) < 1024) {
                                Base64Doc[1] = com.vodafone.frt.utility.Base64.encodeFromFile(selectedFilePath);
                                frtUtility.logInfo(frtRouteControllerTaskActivity, "File selected for uploading", frtConstants);
                                uploaddocs(Base64Doc[0], Base64Doc[1], comment);
//                                frtUtility.dialogPresent();
                            } else {
                                frtUtility.setSnackBar(getString(R.string.maxUploadDocumentSize), endButton);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1);

                frtUtility.hideSoftKeyboard(et_comment, frtRouteControllerTaskActivity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void uploaddocs(String selectedFileName, String base64Doc, String comment) {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
//            frtApp.setCheckForDialog("Yes");
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestUploadRouteTrackingDocModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
            frtUtility.show(progressDialog);

            frtAsyncCommon.setFrtModel(setUpUploadRouteTrackingDocModel(selectedFileName, base64Doc, comment));
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.UPLOAD_DOC), "UploadDoc");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
//                    frtUtility.dialogHide();

                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
                            frtUtility.setSnackBar(getString(R.string.datauploadedsuccessfully), endButton);
                            getWeather();

                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.maxlimit))) {
                            frtUtility.setSnackBar(getString(R.string.maxUploadDocumentSize), endButton);
                        } else
                            frtUtility.setSnackBar(jsonObject.optString("Message"), endButton);
                    } catch (Exception e) {
                        frtUtility.setSnackBar(getString(R.string.didnotgetanyresponse), endButton);
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private PTRRequestUploadRouteTrackingDocModel setUpUploadRouteTrackingDocModel(
            final String selectedFileName, String base64Doc, String comment) {
        final PTRRequestUploadRouteTrackingDocModel frtRequestUploadRouteTrackingDocModel = new PTRRequestUploadRouteTrackingDocModel();
        try {
            frtRequestUploadRouteTrackingDocModel.setRouteAssignmentId(Integer.parseInt(frtSharePrefUtil.getString("routeassigmentid")));
            if (userCurrentLatLon != null) {
                frtRequestUploadRouteTrackingDocModel.setLatitude(String.valueOf(userCurrentLatLon.latitude));
                frtRequestUploadRouteTrackingDocModel.setLongitude(String.valueOf(userCurrentLatLon.longitude));
            }
            frtRequestUploadRouteTrackingDocModel.setMobileTime(frtUtility.getDateAndTime(false));
            frtRequestUploadRouteTrackingDocModel.setFileName(selectedFileName);
            frtRequestUploadRouteTrackingDocModel.setFileData(base64Doc);
            frtRequestUploadRouteTrackingDocModel.setRemarks(comment);
        } catch (Exception ignored) {
        }
        return frtRequestUploadRouteTrackingDocModel;
    }


    private void getFilechooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        PackageManager packageManager = getBaseContext().getPackageManager();
        @SuppressLint("WrongConstant") List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);

        try {
            if (list.size() > 0) {
                startActivityForResult(Intent.createChooser(intent, "Select File"), frtConstants.DOCUMENT_UPLOAD);
            } else {
                downlaodFileManagerFromPlayStore();
            }
        } catch (Exception ignored) {
            Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString() + "getFilechooser");
        }
    }


    private void downlaodFileManagerFromPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rhmsoft.fm")));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.rhmsoft.fm")));
        }
    }

    private PTRRequestSaveRouteAction setUprouteActionModel(String action) {
        if (action.equals("RESUME") && frtListImagesBase64.size() > 0) {
            frtListImagesBase64.clear();
        }
        frtUtility.logInfo(frtRouteControllerTaskActivity, "Clicked on -->" + action, frtConstants);
        PTRRequestSaveRouteAction frtRequestSaveRouteAction = new PTRRequestSaveRouteAction();
        frtRequestSaveRouteAction.setAction(action);
        frtRequestSaveRouteAction.setAssignedRouteId(frtResponseRouteCommonModel.getRouteAssignmentId());
        if (userCurrentLatLon != null) {
            frtRequestSaveRouteAction.setLatitude(String.valueOf(userCurrentLatLon.latitude));
            frtRequestSaveRouteAction.setLongitude(String.valueOf(userCurrentLatLon.longitude));
        }
        frtRequestSaveRouteAction.setMobileTime(frtUtility.getDateAndTime(false));
        frtRequestSaveRouteAction.setReasonId(frtApp.getReasonId());

        if (action.equals("PAUSE")) {
            frtRequestSaveRouteAction.setRemark(comment);
        } else if (action.equals("END")) {
            frtRequestSaveRouteAction.setRemark("END");
        }


//        if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.reasonname))) && !frtSharePrefUtil.getString(getString(R.string.reasonname)).equals("Resume"))

        frtRequestSaveRouteAction.setBase64encodedstringList(frtListImagesBase64);
        //frtRequestSaveRouteAction.setBase64encodedstringList(frtListImagesBase64);
        return frtRequestSaveRouteAction;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationCurrent = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
//            Log.d("SPEED:", "" + location.getSpeed());
            userCurrentLatLon = new LatLng(latitude, longitude);

            if ((intent.hasExtra("case") && intent.getStringExtra("case").equals("PatrollerLocation"))) {
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
            }
        } catch (Exception ignored) {
        }
    }

    boolean activityPaused = false;

    @Override
    protected void onResume() {
        super.onResume();

        activityStartupSetup();
        try {
            if (frtSharePrefUtil != null && !TextUtils.isEmpty(frtSharePrefUtil.getString("base64")) || !activityPaused) {
                //  sendSelfieDataToServer(frtSharePrefUtil.getString("base64"));
                frtListImagesBase64.add(frtSharePrefUtil.getString("base64"));


                if (frtListImagesBase64.size() > 0) {
                    // tvyes.setEnabled(false);
                    clearThumbnail();
                    switch (remark) {
                        case "START":

                            byte [] encodeByteS=Base64.decode(frtSharePrefUtil.getString("base64"),Base64.DEFAULT);
                            Bitmap bitmapS= BitmapFactory.decodeByteArray(encodeByteS, 0, encodeByteS.length);
                            showCamerDialogEnd(bitmapS);

                          /*  try {
                                frtSharePrefUtil.setString("base64", "");
                                try {
                                    List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.TRACKINGDATA);
                                    if (frtRequestSaveRouteLocationmodelList.size() <= 0) {
                                        frtLocationDb.recreateLocationTracking();
                                    }
                                } catch (Exception ignored) {
                                    Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                                }
                                saveRouteActionData("START");
                                scheduleLocationUpload();
                                scheduleLocationSync();
                                frtUtility.createTrackingTimeoutHandler();
                            } catch (Exception ignored) {
                                Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                            }*/
                            break;

                        case "PAUSE":

                            // calling dialog box for setting image




                            byte [] encodeByte=Base64.decode(frtSharePrefUtil.getString("base64"),Base64.DEFAULT);
                            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            showCamerDialogEnd(bitmap);




                        /*

                            frtSharePrefUtil.setString("base64", "");
                            pauseresumeButton.setText("Resume");
                            endButton.setEnabled(false);
                            endButton.setAlpha(0.5f);
                            saveRouteActionData("PAUSE");
                            frtSharePrefUtil.setString("base64", "");*/
                            break;
                        case "END":

                            byte [] encodeByte1=Base64.decode(frtSharePrefUtil.getString("base64"),Base64.DEFAULT);
                            Bitmap bitmap1= BitmapFactory.decodeByteArray(encodeByte1, 0, encodeByte1.length);
                            showCamerDialogEnd(bitmap1);

                          /*  frtSharePrefUtil.setString("base64", "");
                            if (googleApiClient != null) {
                                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, frtRouteControllerTaskActivity);
                                frtUtility.setSnackBar("Routing end", endButton);
                                googleApiClient = null;
                            }

                            String json = prepareSaveRouteActionOfflineData(setUprouteActionModel("END"));
                            frtLocationDb.addOfflineData(json, "ROUTEEND");
                            frtApp.setCheckForDialog("No");

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRNavigationScreenActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                                    finish();
                                }
                            }, 3000);
*/
                            break;
                        case "CHECKIN":
                            // tvyes.setEnabled(false);
                            frtUtility.dialogPresent();
                            saveHaltRoute(true);
                            frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                            // finalDialog.hide();
                            // finalDialog1.hide();

                            break;
                        case "CHECKOUT":
                            //  tvyes.setEnabled(false);
                            frtUtility.dialogPresent();
                            saveHaltRoute(false);
                            frtSharePrefUtil.setBoolean(getString(R.string.checkinkey), isCheckIn);
                            // finalDialog.hide();
                            // finalDialog1.hide();
                            break;
                    }
                    //finalDialog.hide();
                    //finalDialog1.hide();
                } /*else {

                    tvyes.setEnabled(true);
                    frtUtility.setSnackBar(getString(R.string.imagerequired), et_comment);
                }*/


            } else {
                activityPaused = false;
            }
        } catch (Exception ignored) {
            Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString() + "resume");
        }

        registerReceiver(broadcastReceiver, new IntentFilter(
                FRTLocationTrackingService.BROADCAST_ACTION));
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStop();
    }


    private void activityStartupSetup() {
        if (check) {
            callBackSetUp();
            check = false;
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (requestCode == frtConstants.LOCATION_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    check = true;
                } else {
                    frtUtility.setSnackBar(getString(R.string.sufficient_permissions), endButton);
                    finish();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                super.onBackPressed();
                frtUtility.logInfo(frtRouteControllerTaskActivity, "Clicked on --> Back", frtConstants);
                try {
                    frtApp.setCheckForDialog("No");
                } catch (Exception ignored) {
                    Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                }
                if (intent.hasExtra("case") && intent.getStringExtra("case").equals("Attendance")) {
                    frtSharePrefUtil.setString("self_check_in", "");
                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRAttendenceActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                } else if (intent.hasExtra("case") && intent.getStringExtra("case").equals("PlannedTask")) {
                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRScheduledRouteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                } else if (intent.hasExtra("case") && intent.getStringExtra("case").equals("PatrollerLocation")) {
                    frtApp.setLatLong(null);
                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRTrackPatrollerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                } else {
                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRMyTaskActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                    frtApp.setActualStartTime("");
                    frtApp.setActualEndTime("");
                    frtSharePrefUtil.setString("base64", "");
                }
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
            }
        } catch (Exception ignored) {
            Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
        }
    }

    @SuppressWarnings("deprecation")
    private void scheduleLocationUpload() {
        try {
            svcintent = new Intent(frtRouteControllerTaskActivity, mSensorService.getClass());
            startService(svcintent);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (dialog != null)
                dialog.hide();
            activityPaused = true;
        } catch (Exception ignored) {
        }
        flagSession = true;

        if (!isChangingConfigurations()) {
            deleteTempFiles(getCacheDir());
        }
    }


    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == frtConstants.PAUSE_CODE) {
                comment = data.getStringExtra("remark");
               // remark="routeaction";
                //startCamera();
                frtListImagesBase64.clear();
                frtSharePrefUtil.setString("base64", "");
                showCamerDialog(null);
               // startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
            }
            if (requestCode == frtConstants.DOCUMENT_UPLOAD) {
                if (null == data) return;
                String selectedFilePath;
                Uri selectedFileUri = data.getData();

                try {
                    selectedFilePath = ImageFilePath.getPath(getApplicationContext(), selectedFileUri);
                    assert selectedFilePath != null;
                    File file = new File(selectedFilePath);
                    if (file.exists()) {
                        if (selectedFilePath.endsWith("pdf") || selectedFilePath.endsWith("doc") || selectedFilePath.endsWith("docx") ||
                                selectedFilePath.endsWith("xls") || selectedFilePath.endsWith("xlsx") || selectedFilePath.endsWith("jpg") ||
                                selectedFilePath.endsWith("jpeg") || selectedFilePath.endsWith("png") || selectedFilePath.endsWith("gif")) {
//                    selectedFilePath = ImageFilePath.getPath(getApplicationContext(), selectedFileUri);
                            showFileUploadDialog(selectedFilePath);
                        } else {
                            frtUtility.setSnackBar(getString(R.string.filetypenotsupported), endButton);
                        }

                    } else {
                        frtUtility.setSnackBar(getString(R.string.filenotexist), endButton);
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    frtUtility.setSnackBar(getString(R.string.documentnotsupported), endButton);
                }
            }
            if (requestCode == frtConstants.REQUEST_CAMERA_ROUTEACTION) {
                onCaptureImageResult(data, "routeaction");
            }
            if (requestCode == frtConstants.REQUEST_CAMERA_SELFIE) {
                onCaptureImageResult(data, "selfie");
            }
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
        Bitmap bitmap = frtUtility.decodeSampledBitmapFromPath(/*photoUri.getPath()*/mCurrentPhotoPath, mSize.x, mSize.y);
        try {
            bitmapSetView = frtUtility.setCameraPicOrientation(mCurrentPhotoPath);
            bitmap = frtUtility.getResizedBitmap(bitmap, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imagemode.equals("routeaction"))
            showCamerDialog(bitmapSetView);
        else if (imagemode.equals("selfie"))
             showCamerDialog(bitmapSetView);
          //  sendSelfieDataToServer(bitmapSetView);
        else if (imagemode.equals("checkin"))
            showCamerDialog(bitmapSetView);
        else if (imagemode.equals("checkout"))
            showCamerDialog(bitmapSetView);
       /* else if (imagemode.equals("SaveRouteIssue"))
            showCamerDialog(bitmapSetView);*/
    }

   /* @SuppressWarnings("deprecation")
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }*/

    public void startCamera(final String imageType) {
//        cameraPermissions(frtRouteControllerTaskActivity);
//            frtUtility.cameraIntent(frtRouteControllerTaskActivity);
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
        if (imageType.equals("routeaction")) {
            if (Build.VERSION.SDK_INT >= 20) {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            } else {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            }
            dispatchTakePictureIntent(intent);
            startActivityForResult(intent, frtConstants.REQUEST_CAMERA_ROUTEACTION);
        } else if (imageType.equals("selfie")) {

            if (Build.VERSION.SDK_INT >= 20) {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            } else {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            }
            //intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            // selfieCode();
            dispatchTakePictureIntent(intent);
            startActivityForResult(intent, frtConstants.REQUEST_CAMERA_SELFIE);

        } else if (imageType.equals("checkin")) {
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

    private void selfieCode() {
        // do we have a camera?
//        if (!getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            cameraId = findFrontFacingCamera();
//            if (cameraId < 0) {
//                Toast.makeText(this, "No front facing camera found.",
//            } else {
//                camera = Camera.open(cameraId);
//            }
//        }
//        camera.startPreview();
//        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));

        // commented by ajay for change camera
        // startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
        showCamerDialog(null);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, Camera2BasicActivity.newInstance())
//                .commit();
    }

    private Intent intentForSamsung(String manufaturer, int sdkInt) {
        if (manufaturer.equals("samsung"))
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        else  if (manufaturer.equalsIgnoreCase("Xiaomi"))
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        else  if (manufaturer.equalsIgnoreCase("Intex"))
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        else {
            if (sdkInt >= 20)
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            else
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return intent;
    }

    String mCurrentPhotoPath;
    long captured_img_date;
    private void dispatchTakePictureIntent(Intent takePictureIntent) {
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                captured_img_date= photoFile.lastModified();
//                storageDir.delete();
            } catch (IOException ex) {
                Log.d(this.getClass().getName(), "EXCEPTION FilePath" + ex.getMessage());
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {


                    Uri photoURI = null;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        try {

                            photoURI = Uri.fromFile(photoFile);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        photoURI = FileProvider.getUriForFile(frtRouteControllerTaskActivity,
                                "com.example.android.fileprovider",
                                photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //image.delete();
        return image;
    }


    /**
     * This method is used for authorising necessary permissions for initializing google map android
     */
    @SuppressLint("InlinedApi")
    void cameraPermissions(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, frtConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        handler = null;
        Log.i("MAINACT", "onDestroy!");

        super.onDestroy();
    }


    @SuppressLint("MissingPermission")
    private void getWeather() {
        final String[] weather_condition = new String[3];
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SnapshotApi snapshotApi = Awareness.SnapshotApi;
        snapshotApi.getWeather(googleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e("Weather", "Could not detect weather info");
                            //weather_condition[0] = "Could not detect weather info";
                            return;
                        }
                        try {
                            Weather weather = weatherResult.getWeather();
                            int condition = weather.getConditions()[0];
                            switch (condition) {
                                case 0:
                                    weather_condition[0] = "UNKNOWN";
                                    break;
                                case 1:
                                    weather_condition[0] = "CLEAR";
                                    break;
                                case 2:
                                    weather_condition[0] = "CLOUDY";
                                    break;
                                case 3:
                                    weather_condition[0] = "FOGGY";
                                    break;
                                case 4:
                                    weather_condition[0] = "HAZY";
                                    break;
                                case 5:
                                    weather_condition[0] = "ICY";
                                    break;
                                case 6:
                                    weather_condition[0] = "RAINY";
                                    break;
                                case 7:
                                    weather_condition[0] = "SNOWY";
                                    break;
                                case 8:
                                    weather_condition[0] = "STORMY";
                                    break;
                                case 9:
                                    weather_condition[0] = "WINDY";
                                    break;

                            }
                            weather_condition[1] = String.valueOf(weather.getTemperature(Weather.CELSIUS));
                            weather_condition[2] = String.valueOf(weather.getHumidity());
                            DecimalFormat doubleFormatter = new DecimalFormat("####0.0");
                            saveWeather(weather_condition[0], doubleFormatter.format(Double.parseDouble(weather_condition[1])) + " Â°C",
                                    doubleFormatter.format(Double.parseDouble(weather_condition[2])) + " %");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void saveWeather(String precipitation, String temperature, String humidity) {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            // frtApp.setCheckForDialog("Yes");
            final ProgressDialog progressDialog = new ProgressDialog(frtRouteControllerTaskActivity);
            frtUtility.show(progressDialog);
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestSaveWeatherModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpSaveWeatherModel(precipitation, temperature, humidity));
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SAVE_ROUTE_WEATHER), "SaveWeather");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    frtUtility.logInfo(frtRouteControllerTaskActivity,type+" Response "+response,frtConstants);
                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equals("OK")) {
//                            frtUtility.setSnackBar(getString(R.string.weather_condition_uploaded),endButton);
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                            frtUtility.goToLogin(getString(R.string.req_denied));
                        } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                            /*frtUtility.goToLogin(getString(R.string.session_exp));*/
                            getRefereshTokenData();
                        } else
                            frtUtility.setSnackBar(jsonObject.optString("error_message"), endButton);
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), endButton);
        }
    }

    private PTRRequestSaveWeatherModel setUpSaveWeatherModel(String
                                                                     precipitation, String
                                                                     temperature, String humidity) {
        PTRRequestSaveWeatherModel frtRequestSaveWeatherModel = new PTRRequestSaveWeatherModel();
        try {
            frtRequestSaveWeatherModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            frtRequestSaveWeatherModel.setRouteAssignmentId(Integer.parseInt(frtSharePrefUtil.getString("routeassigmentid")));
            if (userCurrentLatLon != null) {
                frtRequestSaveWeatherModel.setLatitude(String.valueOf(userCurrentLatLon.latitude));
                frtRequestSaveWeatherModel.setLongitude(String.valueOf(userCurrentLatLon.longitude));
            }
            frtRequestSaveWeatherModel.setMobileTime(frtUtility.getDateAndTime(false));
            frtRequestSaveWeatherModel.setHumidity(humidity);
            frtRequestSaveWeatherModel.setTemperature(temperature);
            frtRequestSaveWeatherModel.setPrecipitation(precipitation);
            //todo
        } catch (Exception ex) {
        }
        return frtRequestSaveWeatherModel;
    }

    private String prepareSaveRouteActionOfflineData(PTRRequestSaveRouteAction frtModel) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("assignedRouteId", frtModel.getAssignedRouteId());
            jsonObject.put("action", frtModel.getAction());
            jsonObject.put("latitude", frtModel.getLatitude());
            jsonObject.put("longitude", frtModel.getLongitude());
            jsonObject.put("mobileTime", frtModel.getMobileTime());
            jsonObject.put("reasonId", frtModel.getReasonId());
            jsonObject.put("remark", frtModel.getRemark());

            for (String image : frtModel.getBase64encodedstringList()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("imageData", image);
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("images", jsonArray);
        } catch (JSONException ignored) {
        }
        return jsonObject.toString();
    }
    private void getRefereshTokenData() {
        if (frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRouteControllerTaskActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtRouteControllerTaskActivity);
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
        } else {
            frtUtility.setSnackBar(getString(R.string.notgetting_responseFromServer), ivback);
        }
    }









    private void showCamerDialogEnd(Bitmap thumbnail) {
        Dialog dialog = new Dialog(frtRouteControllerTaskActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_camera);
        final ImageView iv_capture1 = dialog.findViewById(R.id.iv_capture1);
        final FRTEditTextTrebuchetMS et_comment = dialog.findViewById(R.id.et_comment);
        final FRTTextviewTrebuchetMS titlesaverouteaction = dialog.findViewById(R.id.titlesaverouteaction);
        titlesaverouteaction.setText(remark);

        if (thumbnail == null) {
            frtListImagesBase64.clear();
            iv_capture1.setImageResource(R.mipmap.ic_add);
        } else if (thumbnail != null) {
            frtListImagesBase64.clear();

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            String temp=Base64.encodeToString(b, Base64.DEFAULT);
            frtListImagesBase64.add(temp);
        }
        switch (imageViewID) {
            case R.id.iv_capture1:
                if (thumbnail != null) {
                    iv_capture1.setImageBitmap(thumbnail);
                    thumbnail1 = thumbnail;
                    iv_capture1.setImageBitmap(thumbnail1);
                }
                break;
        }
        final Dialog finalDialog = dialog;
        iv_capture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewID = view.getId();
                frtListImagesBase64.clear();
                frtSharePrefUtil.setString("base64", "");
              // else {
                    //calling Custom Camera for on pause on resume functionality
                    startActivity(new Intent(frtRouteControllerTaskActivity, Camera2BasicActivity.class));
                    finalDialog.dismiss();

               // }
            }
        });

        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.cancel);
        final FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.ok);
        tvyes.setEnabled(true);
       // tvno.setOnClickListener(onClickNo);
       // tvyes.setOnClickListener(onClickyes);
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frtSharePrefUtil.setString("base64", "");
                PTRRouteControllerTaskActivity.this.base64Image = null;
                clearThumbnail();
                frtListImagesBase64.clear();
                finalDialog.hide();
            }
        });
        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frtListImagesBase64.size() > 0) {
                    tvyes.setEnabled(false);
                    switch (remark) {
                        case "START":
                            frtSharePrefUtil.setString("base64", "");
                            try {
                                try {
                                    List<PTRRequestSaveRouteLocationmodel> frtRequestSaveRouteLocationmodelList = frtLocationDb.getTrackings(frtConstants.TRACKINGDATA);
                                    if (frtRequestSaveRouteLocationmodelList.size() <= 0) {
                                        frtLocationDb.recreateLocationTracking();
                                    }
                                } catch (Exception ignored) {
                                    Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                                }
                                saveRouteActionData("START");
                                scheduleLocationUpload();
                                scheduleLocationSync();
                                frtUtility.createTrackingTimeoutHandler();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    iv_capture1.setBackground(null);
                                    iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                                }
                            } catch (Exception ignored) {
                                Log.e(frtRouteControllerTaskActivity.getClass().getName(), ignored.toString());
                            }
                            break;

                        case "PAUSE":
                            frtSharePrefUtil.setString("base64", "");
                            finalDialog.dismiss();
                            pauseresumeButton.setText("Resume");
//                        remark = data.getStringExtra("remark");
                            endButton.setEnabled(false);
                            endButton.setAlpha(0.5f);
                            saveRouteActionData("PAUSE");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                iv_capture1.setBackground(null);
                                iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                            }
                            break;
                        case "END":
                            frtSharePrefUtil.setString("base64", "");
//                            int pendingsize = frtLocationDb.trackingDataCount();
//                            if (pendingsize > 0) {
//                            if (!frtCnnectivityReceiver.isConnected(frtRouteControllerTaskActivity)) {
                            if (googleApiClient != null) {
                                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, frtRouteControllerTaskActivity);
                                frtUtility.setSnackBar("Routing end", endButton);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    iv_capture1.setBackground(null);
                                    iv_capture1.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                                }
                                googleApiClient = null;
                            }
                           /*Gson gson = new Gson();
                            String json = gson.toJson(setUprouteActionModel("END"));*/
                            String json = prepareSaveRouteActionOfflineData(setUprouteActionModel("END"));
                            frtLocationDb.addOfflineData(json, "ROUTEEND");
                            frtApp.setCheckForDialog("No");

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(frtRouteControllerTaskActivity, PTRNavigationScreenActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
                                    finish();
                                }
                            }, 3000);


                            break;
                    }
                    finalDialog.dismiss();
                    finalDialog.hide();
                } else {
                    tvyes.setEnabled(true);
                    frtUtility.setSnackBar(getString(R.string.imagerequired), et_comment);
                }
            }
        });
            dialog.show();
    }












}