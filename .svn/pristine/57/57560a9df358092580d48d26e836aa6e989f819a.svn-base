package com.vodafone.frt.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.vodafone.frt.R;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.fonts.FRTButtonTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRResponseActualRouteModel;
import com.vodafone.frt.models.PTRResponseGetHaltPointsModel;
import com.vodafone.frt.models.PTRResponsePlannedRouteModel;
import com.vodafone.frt.models.PTRResponsePtrollerLocationModel;
import com.vodafone.frt.models.PTRResponseRouteCommonModel;
import com.vodafone.frt.models.PTRResponseSaveRouteAction;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;
import com.vodafone.frt.utility.FRTUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishal on 12/12/17
 */
public class PTRRouteControllerTaskBaseActivity extends AppCompatActivity implements FRTCallBackForIdFind {
    boolean check = false, isEnd = false, isTrack = false, flagSession = false, flagIsPlannedOnce = false, flagCamera = false, haltPointClicked = false, isCheckIn = false,
            isSaved = false, isSatteliteClicked = false, isHybridClicked = false, isClickedtraffic = false;
    Camera camera;
    Dialog dialog;
    private final PTRRouteControllerTaskBaseActivity frtRouteControllerTaskBaseActivity = this;
    FRTConnectivityReceiver frtCnnectivityReceiver;
    FRTCallBackInitViews frtCallBackInitViews;
    FRTCallBackSetListeners frtCallBackSetListeners;
    FrameLayout frameLayout;
    FRTConstants frtConstants;
    FRTUtility frtUtility;
    FRTSharePrefUtil frtSharePrefUtil;
    FRTWEBAPI frtWEBAPI;
    FRTLocationDb frtLocationDb;
    PTRResponsePtrollerLocationModel frtResponsePtrollerLocationModel;
    FRTButtonTrebuchetMS startButton, pauseresumeButton, endButton;
    FRTLocationTrackingService mSensorService;
    FRTTextviewTrebuchetMS routetitle;
    FRTTextviewTrebuchetMS starttime;
    FRTTextviewTrebuchetMS endtime;
    FRTTextviewTrebuchetMS actualstarttime;
    FRTTextviewTrebuchetMS actualendtime;
    FRTApp frtApp;
    PTRResponseRouteCommonModel frtResponseRouteCommonModel;
    GoogleMap googleMap;
    GoogleApiClient googleApiClient;
    Handler handler;
    LinearLayout ivback;
    Intent intent;
    Intent svcintent;
    int finalI = 0, trackId = 0, cameraId;
    LocationRequest locationRequest;
    LocationManager locationManager;
    Location locationCurrent;
    List<PTRResponseSaveRouteAction> frtResponseSaveRouteActionList;
    Marker marker;
    ArrayList<PTRResponsePlannedRouteModel> frtResponsePlannedRouteModelList;
    List<PTRResponseActualRouteModel> frtResponseActualRouteModelList;
    final List<Marker> markerList = new ArrayList<>();
    final List<Marker> markerList1 = new ArrayList<>();
    //    List<Map<String, String>> frtListImagesBase64 = new ArrayList<>();
    List<String> frtListImagesBase64 = new ArrayList<>();
    LatLng userCurrentLatLon;
    LinearLayout llactualstart, llactualend;
    Point mSize;
    RelativeLayout relbottom;
    SupportMapFragment supportMapFragment;
    String remark, base64Image, comment;
    ImageView upload_docs, selfie, imgsattelite, imghybrid, imgnormal, imgtraffic,raise_issue;
    int imageViewID, clickedHaltMarkerPosition;
    Bitmap thumbnail1, thumbnail2, thumbnail3, thumbnail4;
    List<PTRResponseGetHaltPointsModel> frtResponseGetHaltPointsModelArrayList;
    List<LatLng> radiusList;
    double minDistanceHaultPoint;
     FRTTextviewTrebuchetMS speedDb1,distanceText,time,speedDbLocation;
    ImageView iv_toggle;
    TextView tv_zoom_level;
    TileOverlay tileOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_route_controller_task);
        callBackSetUpBase();
    }

    private void callBackSetUpBase() {
        FRTCallBackForIdFind frtCallBackForIdFind = frtRouteControllerTaskBaseActivity;
        frameLayout = (FrameLayout) frtCallBackForIdFind.view(R.id.fl);
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        startButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.startButton);
        pauseresumeButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.pauseresumeButton);
        endButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.endButton);
        routetitle = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.routetitle);
        starttime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.starttime);
        endtime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.endtime);
        relbottom = (RelativeLayout) frtCallBackForIdFind.view(R.id.relbottom);
        llactualstart = (LinearLayout) frtCallBackForIdFind.view(R.id.llactualstart);
        llactualend = (LinearLayout) frtCallBackForIdFind.view(R.id.llactualend);
        actualstarttime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.actualstarttime);
        actualendtime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.actualendtime);

        upload_docs = (ImageView) frtCallBackForIdFind.view(R.id.upload_docs);
        selfie = (ImageView) frtCallBackForIdFind.view(R.id.selfie);
        imgsattelite = (ImageView) frtCallBackForIdFind.view(R.id.imgsattelite);
        imghybrid = (ImageView) frtCallBackForIdFind.view(R.id.imghybrid);
        imgnormal = (ImageView) frtCallBackForIdFind.view(R.id.imgnormal);
        imgtraffic = (ImageView) frtCallBackForIdFind.view(R.id.imgtraffic);
        raise_issue= (ImageView) frtCallBackForIdFind.view(R.id.raise_issue);
        // testing data
        speedDb1 =(FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.speedDb1);
       // distanceText =(FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.distanceText);
        time =(FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.time);
        speedDbLocation =(FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.speedDbLocation);
//        imghybrid.setVisibility(View.GONE);
        imgnormal.setVisibility(View.GONE);
        iv_toggle = (ImageView)findViewById(R.id.iv_toggle);
        tv_zoom_level = (TextView)findViewById(R.id.tv_zoom_level);
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }
}
