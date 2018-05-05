package com.vodafone.frt.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.vodafone.frt.models.FRTResponseRouteCommonModelFRT;
import com.vodafone.frt.models.PTRResponsePtrollerLocationModel;
import com.vodafone.frt.models.PTRResponseRouteCommonModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;
import com.vodafone.frt.utility.FRTUtility;

public class FRTRouteControlerTaskBaseActivityFRT extends AppCompatActivity implements FRTCallBackForIdFind {
    Dialog dialog;
    boolean check = false, isEnd = false, isTrack = false, flagSession = false, flagIsPlannedOnce = false, flagCamera = false, haltPointClicked = false, isCheckIn = false,
            isSaved = false, isSatteliteClicked = false, isHybridClicked = false, isClickedtraffic = false;
    private final FRTRouteControlerTaskBaseActivityFRT frtRouteControlerTaskBaseActivityFRT= this;
    FRTConnectivityReceiver frtCnnectivityReceiver;
    FRTCallBackInitViews frtCallBackInitViews;
    FRTCallBackSetListeners frtCallBackSetListeners;
    FrameLayout frameLayout;
    String remark, base64Image, comment;
    FRTConstants frtConstants;
    FRTUtility frtUtility;
    FRTSharePrefUtil frtSharePrefUtil;
    FRTWEBAPI frtWEBAPI;
    FRTLocationDb frtLocationDb;
    LatLng userCurrentLatLon;
    Bitmap thumbnail1, thumbnail2, thumbnail3, thumbnail4;
    PTRResponsePtrollerLocationModel frtResponsePtrollerLocationModel;
    FRTLocationTrackingService mSensorService;
    FRTTextviewTrebuchetMS routetitle;
    FRTTextviewTrebuchetMS checkinRemarks;
    FRTTextviewTrebuchetMS checkInTimeTextView,checkOutTimeTextView,checkOutRemarksTextView;
    FRTTextviewTrebuchetMS actualstarttime;
    FRTTextviewTrebuchetMS actualendtime;
    FRTApp frtApp;
    FRTResponseRouteCommonModelFRT frtResponseRouteCommonModel;
    GoogleMap googleMap;
    GoogleApiClient googleApiClient;
    Handler handler;
    LinearLayout ivback;
    Intent intent;
    Marker marker;
    Intent svcintent;
    int finalI = 0, taskTrackingId = 0, cameraId;
    LocationRequest locationRequest;
    LocationManager locationManager;
    Location locationCurrent;
     RelativeLayout relbottom;
     LinearLayout checkoutLL,checkoutRemarksLL;

    ImageView upload_docs,  imgsattelite, imghybrid, imgnormal, imgtraffic,raise_issue;
    SupportMapFragment supportMapFragment;
    ImageView iv_toggle;
    TextView tv_zoom_level;
    TileOverlay tileOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtroute_controler_task_base_frt);
        callBackSetUpBase();
    }

    private void callBackSetUpBase() {
        FRTCallBackForIdFind frtCallBackForIdFind =frtRouteControlerTaskBaseActivityFRT;

        frameLayout = (FrameLayout) frtCallBackForIdFind.view(R.id.fl);
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
       // startButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.startButton);
       // pauseresumeButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.pauseresumeButton);
       // endButton = (FRTButtonTrebuchetMS) frtCallBackForIdFind.view(R.id.endButton);
        routetitle = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.issueType);
        checkinRemarks = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.checkinRemarks);
        checkInTimeTextView = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.checkInTimeTextView);
        checkOutTimeTextView = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.checkOutTimeTextView);
        checkOutRemarksTextView = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.checkOutRemarksTextView);
        relbottom = (RelativeLayout) frtCallBackForIdFind.view(R.id.relbottom);
        checkoutLL = (LinearLayout)frtCallBackForIdFind.view(R.id.checkoutLL);
        checkoutRemarksLL= (LinearLayout)frtCallBackForIdFind.view(R.id.checkoutRemarksLL);
       // llactualstart = (LinearLayout) frtCallBackForIdFind.view(R.id.llactualstart);
      //  llactualend = (LinearLayout) frtCallBackForIdFind.view(R.id.llactualend);
      //  actualstarttime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.actualstarttime);
      //  actualendtime = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.actualendtime);

        iv_toggle = (ImageView)findViewById(R.id.iv_toggle);
        tv_zoom_level = (TextView)findViewById(R.id.tv_zoom_level);
        imgsattelite = (ImageView) frtCallBackForIdFind.view(R.id.imgsattelite);
        imghybrid = (ImageView) frtCallBackForIdFind.view(R.id.imghybrid);
        imgnormal = (ImageView) frtCallBackForIdFind.view(R.id.imgnormal);
        imgtraffic = (ImageView) frtCallBackForIdFind.view(R.id.imgtraffic);

        imgnormal.setVisibility(View.GONE);

    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }
}
