package com.vodafone.frt.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.vodafone.frt.R;
import com.vodafone.frt.adapters.PTRRaiseIssueAdapter;
import com.vodafone.frt.apis.FRTAsyncCommon;
import com.vodafone.frt.apis.FRTWEBAPI;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.enums.FRTAPIType;
import com.vodafone.frt.fonts.FRTButtonTrebuchetMS;
import com.vodafone.frt.fonts.FRTEditTextTrebuchetMS;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.PTRLoginRefreshRequestModel;
import com.vodafone.frt.models.PTRRequestPauseReasonModel;
import com.vodafone.frt.models.PTRRequestSaveIssuesRaisedModel;
import com.vodafone.frt.models.PTRResponseIssueTypesModel;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.GPSTracker;
import com.vodafone.frt.utility.AESEncriptDecript;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ashutosh Kumar on 23-Feb-18.
 */

public class PTRRaiseIssueActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTAsyncCommon.FetchDataCallBack, FRTBroadcasting.CallBackBroadcast, PTRRaiseIssueAdapter.FRTCallbackListItemChecked {
    private boolean flagSession = false, check = false, checknet = false;
    private BroadcastReceiver broadcastReceiver;
    private PTRRaiseIssueActivity frtRaiseIssueActivity = this;
    FRTCallBackInitViews frtCallBackInitViews;
    FRTCallBackForIdFind frtCallBackForIdFind;
    FRTCallBackSetListeners frtCallBackSetListeners;
    private FRTBroadcasting broadcasting;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTEditTextTrebuchetMS othereditText;
    private FRTButtonTrebuchetMS saveButton;
    private FRTTextviewTrebuchetMS specifyTitleTextView;
    //FRTTextviewTrebuchetMS loader;
    private List<PTRResponseIssueTypesModel> reasonsList;
    private int selPos = -1;
    private ListView listView;
    private LinearLayout ivback;
    private FRTUtility frtUtility;
    FRTWEBAPI frtWEBAPI;
    private PTRRaiseIssueAdapter.FRTCallbackListItemChecked frtCallbackListItemChecked;
    List<String> frtListImagesBase64 = new ArrayList<>();
    FRTApp frtApp;
    FRTSharePrefUtil frtSharePrefUtil;
    private double latitude;
    private double longitude;
    private Location userCurrentLatLon;
    private LocationManager mLocationManager;
    private Intent intent;
    private FRTConstants frtConstants;
    private String mCurrentPhotoPath;
    private Handler handler;
    private boolean flagCamera = false;
    private int imageViewID;
    private Bitmap thumbnail1, bitmapSetView;
    private String base64Image;
    private Point mSize;
    private GPSTracker gps;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtraise_issue);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtRaiseIssueActivity);
        frtConstants = new FRTConstants();
        handler = new Handler();
        callBackSetUp();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PTRRaiseIssueActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

        } else {
            gps = new GPSTracker(this, PTRRaiseIssueActivity.this);
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            } else {
                gps.showSettingsAlert();
            }
        }

    }

    private void callBackSetUp() {
        frtCallBackInitViews = frtRaiseIssueActivity;
        frtCallBackForIdFind = frtRaiseIssueActivity;
        frtCallBackSetListeners = frtRaiseIssueActivity;
        frtCallbackListItemChecked = frtRaiseIssueActivity;
        frtWEBAPI = new FRTWEBAPI();
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    private void setAdapter() {
        PTRRaiseIssueAdapter adapter = new PTRRaiseIssueAdapter(frtRaiseIssueActivity);
        adapter.notifyDataSetChanged();
        adapter.setDataSet(reasonsList);
        adapter.setItemCallBack(frtCallbackListItemChecked);
        listView.setAdapter(adapter);
    }

    @Override
    public void initAllViews() {
        listView = (ListView) findViewById(R.id.remarksListView);
        othereditText = (FRTEditTextTrebuchetMS) findViewById(R.id.othereditText);
        saveButton = (FRTButtonTrebuchetMS) findViewById(R.id.SaveButton);
        specifyTitleTextView = (FRTTextviewTrebuchetMS) findViewById(R.id.specifyTitleTextView);
        // loader = (FRTTextviewTrebuchetMS) findViewById(R.id.loader);
        ivback = (LinearLayout) findViewById(R.id.ivback);
        frtApp = (FRTApp) frtRaiseIssueActivity.getApplication();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtRaiseIssueActivity);
        frtUtility.hideSoftKeyboard(othereditText, frtRaiseIssueActivity);
        frtUtility.settingStatusBarColor(frtRaiseIssueActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtRaiseIssueActivity);
        broadcasting.setCallbackBroadcast(frtRaiseIssueActivity);
        saveButton.setEnabled(true);
        getPauseReasons();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void commonListeners() {
        saveButton.setOnClickListener(onClickSave);
        ivback.setOnClickListener(frtBackClick);
        othereditText.setOnClickListener(onClickotheredit);
    }

    private View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private View.OnClickListener onClickSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //saveButton.setEnabled(false);
            if (frtConnectivityReceiver.isConnected(frtRaiseIssueActivity)) {
                if (selPos != -1)
                    showCamerDialog(null);
//                    SaveRouteIssueDetail();
                else
                    frtUtility.setSnackBar(getString(R.string.selectissue), findViewById(R.id.header));
            } else {
                frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
            }
        }
    };

   /* private void handleSaveReason() {
        Intent intent = new Intent(frtRaiseIssueActivity, PTRRouteControllerTaskActivity.class);
        intent.putExtra("reasonresp", "Resume");
        if (!TextUtils.isEmpty(othereditText.getText().toString()))
            intent.putExtra("remark", othereditText.getText().toString());
        else
            intent.putExtra("remark", " ");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
    }*/


    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtRaiseIssueActivity)) {
            super.onBackPressed();
            Intent intent = new Intent(frtRaiseIssueActivity, PTRRouteControllerTaskActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }

    private final View.OnClickListener onClickotheredit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            othereditText.setFocusable(true);
            othereditText.setFocusableInTouchMode(true);
            frtUtility.showSoftKeyboard(othereditText, frtRaiseIssueActivity);
        }
    };

    @SuppressWarnings("deprecation")
    private void getPauseReasons() {
        progressDialog = new ProgressDialog(frtRaiseIssueActivity);
        FRTAsyncCommon<PTRRequestPauseReasonModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
        frtAsyncCommon.setFrtModel(setUpLoginModel());
        frtAsyncCommon.setContext(frtRaiseIssueActivity);
        frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.GET_ISSUE_TYPE), "getissuetype");
        frtAsyncCommon.setFetchDataCallBack(frtRaiseIssueActivity);
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
    }

    private PTRRequestPauseReasonModel setUpLoginModel() {
        PTRRequestPauseReasonModel cqRequestPauseReasonModel = new PTRRequestPauseReasonModel();
        cqRequestPauseReasonModel.setReasonType(getString(R.string.reason_pause));
        return cqRequestPauseReasonModel;
    }
    //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void getAsyncData(String response, String type) {
        progressDialog.dismiss();
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
            } else if (type.equals("getissuetype")) {
                JSONObject jsonObject;
                JSONArray jsonArray;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.optString(getString(R.string.status_key)).equalsIgnoreCase("OK")) {
                        jsonArray = jsonObject.optJSONArray("results");
                        reasonsList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            PTRResponseIssueTypesModel model = new PTRResponseIssueTypesModel();
                            model.setType_id(jsonObject1.optInt(getString(R.string.type_id)));
                            model.setDescription(jsonObject1.optString(getString(R.string.description)));
                            reasonsList.add(model);
                        }
                        setAdapter();
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                        frtUtility.goToLogin(getString(R.string.req_denied));
                    } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                   /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                        getRefereshTokenData();
                    } else {
                        frtUtility.setSnackBar(jsonObject.optString(getString(R.string.error_message_key)), listView);
                    }
                    // loader.setVisibility(View.GONE);
                    specifyTitleTextView.setVisibility(View.VISIBLE);
                    othereditText.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    //   e.printStackTrace();
                }
            }
        } else {
            frtUtility.setSnackBar(getString(R.string.servernotResponding), listView);
        }
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        customGoogleApiClient = new CustomGoogleApiClient();
       /* mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.valueOf(frtSharePrefUtil.getString(getString(R.string.tracktime))),
                5, mLocationListener);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        flagSession = true;
        if (gps != null) {
            gps.stopUsingGPS();
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
            checknet = true;
        } else {
            if (checknet)
                getPauseReasons();
            checknet = false;
        }
    }

    @Override
    public void onSelect(boolean flag, int position) {
        selPos = position;

        frtApp.setIssueTypeId(String.valueOf(reasonsList.get(position).getType_id()));
        frtUtility.hideSoftKeyboard(othereditText, frtRaiseIssueActivity);
    }

    private void SaveRouteIssueDetail() {
        if (frtConnectivityReceiver.isConnected(frtRaiseIssueActivity)) {
            final ProgressDialog progressDialog = new ProgressDialog(frtRaiseIssueActivity);
            frtUtility.show(progressDialog);
            @SuppressWarnings("unchecked") final FRTAsyncCommon<PTRRequestSaveIssuesRaisedModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRaiseIssueActivity);
            frtAsyncCommon.setFrtModel(setUpSaveissuesraisedModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.SAVE_ROUTE_ISSUE_DETAIL), "SaveRouteIssueDetail");
            frtAsyncCommon.setFetchDataCallBack(new FRTAsyncCommon.FetchDataCallBack() {
                @Override
                public void getAsyncData(String response, String type) {
                    try {
                        progressDialog.dismiss();
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("OK")) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(frtRaiseIssueActivity, PTRRouteControllerTaskActivity.class);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }, 1000);
                                frtUtility.setSnackBar(getString(R.string.issues_raised), saveButton);

                            } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.req_denied))) {
                                frtUtility.goToLogin(getString(R.string.req_denied));
                            } else if (jsonObject.optString(getString(R.string.status_key)).equals(getString(R.string.session_exp))) {
                               /* frtUtility.goToLogin(getString(R.string.session_exp));*/
                                getRefereshTokenData();
                            } else
                                frtUtility.setSnackBar(jsonObject.optString("error_message"), saveButton);
                        }else {
                            frtUtility.setSnackBar(getString(R.string.servernotResponding), saveButton);
                        }
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), saveButton);
        }
    }

    private PTRRequestSaveIssuesRaisedModel setUpSaveissuesraisedModel() {
        PTRRequestSaveIssuesRaisedModel frtRequestSaveWeatherModel = new PTRRequestSaveIssuesRaisedModel();
        try {
            frtRequestSaveWeatherModel.setUserId(Integer.parseInt(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT))));
            frtRequestSaveWeatherModel.setManagerId(AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.managerkey)).getBytes("UTF-16LE"), Base64.DEFAULT)));
            frtRequestSaveWeatherModel.setRouteAssignmentId(Integer.parseInt(frtSharePrefUtil.getString("routeassigmentid")));
            if (latitude != 0.0 && longitude != 0.00) {
                frtRequestSaveWeatherModel.setLatitude(String.valueOf(latitude));
                frtRequestSaveWeatherModel.setLongitude(String.valueOf(longitude));
            }
            /*if (userCurrentLatLon != null) {
                frtRequestSaveWeatherModel.setLatitude(String.valueOf(latitude));
                frtRequestSaveWeatherModel.setLongitude(String.valueOf(longitude));
            }*/
            frtRequestSaveWeatherModel.setMobileTime(frtUtility.getDateAndTime(false));
            frtRequestSaveWeatherModel.setIssueTypeId(/*frtApp.getIssueTypeId()*/String.valueOf(selPos));

            frtRequestSaveWeatherModel.setRemark(othereditText.getText().toString());
            frtRequestSaveWeatherModel.setImages(frtListImagesBase64);
        } catch (Exception ex) {
        }
        return frtRequestSaveWeatherModel;
    }

    /*private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            userCurrentLatLon = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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
    };*/

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
        String manufaturer = android.os.Build.MANUFACTURER;
        if (imageType.equals(getString(R.string.raise_issue))) {
            if (Build.VERSION.SDK_INT >= 20) {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            } else {
                intent = intentForSamsung(manufaturer, Build.VERSION.SDK_INT);
            }
            dispatchTakePictureIntent(intent);
            startActivityForResult(intent, frtConstants.REQUEST_CAMERA_SAVE_ROUTE_ISSUE);
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
                    photoURI = FileProvider.getUriForFile(frtRaiseIssueActivity,
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
            if (requestCode == frtConstants.REQUEST_CAMERA_SAVE_ROUTE_ISSUE) {
                onCaptureImageResult(data, getString(R.string.raise_issue));
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
        if (imagemode.equals(getString(R.string.raise_issue)))
            showCamerDialog(bitmapSetView);
    }

    private void showCamerDialog(Bitmap thumbnail) {

        final LatLng position = null;
        final BitmapDescriptor bitmapDescriptor = null;
        Dialog dialog = new Dialog(frtRaiseIssueActivity);
        dialog = frtUtility.dialogBasicStructure(dialog, R.layout.dialog_camera);
        final ImageView iv_capture1 = dialog.findViewById(R.id.iv_capture1);
        final FRTTextviewTrebuchetMS titlesaverouteaction = dialog.findViewById(R.id.titlesaverouteaction);
        titlesaverouteaction.setText(getString(R.string.raise_issue));

        if (thumbnail == null) {
            frtListImagesBase64.clear();
            iv_capture1.setImageResource(R.mipmap.ic_add);
        } else if (thumbnail != null) {
            base64Image = frtUtility.saveImageAsBase64(thumbnail);
            frtListImagesBase64.clear();
            frtListImagesBase64.add(base64Image);
            frtUtility.deleteGalleyfile(frtRaiseIssueActivity,captured_img_date);
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
                startCamera(getString(R.string.raise_issue));
                finalDialog.hide();
            }
        });

        FRTTextviewTrebuchetMS tvno = dialog.findViewById(R.id.cancel);
        final FRTTextviewTrebuchetMS tvyes = dialog.findViewById(R.id.ok);
        tvyes.setEnabled(true);
        //tvno.setOnClickListener(onClickNo);
        //tvyes.setOnClickListener(onClickyes);
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PTRRaiseIssueActivity.this.base64Image = null;
                bitmapSetView = null;
                thumbnail1 = null;
                imageViewID = -1;
                finalDialog.hide();
                frtListImagesBase64.clear();
            }
        });
        final Dialog finalDialog1 = dialog;
        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frtListImagesBase64.size() > 0) {
                    tvyes.setEnabled(false);
                    thumbnail1 = null;
                    frtUtility.dialogPresent();
                    SaveRouteIssueDetail();
                    finalDialog.hide();
                    finalDialog1.hide();
                } else {
                    frtUtility.setSnackBar(getString(R.string.imagerequired), othereditText);
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(this, PTRRaiseIssueActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } /*else {

                }*/
                return;
            }
        }
    }
    private void getRefereshTokenData() {
        if (frtConnectivityReceiver.isConnected(frtRaiseIssueActivity)) {
            @SuppressWarnings("unchecked") FRTAsyncCommon<PTRLoginRefreshRequestModel> frtAsyncCommon = FRTAsyncCommon.getAsyncInstance();
            frtAsyncCommon.setContext(frtRaiseIssueActivity);
            frtAsyncCommon.setFrtModel(setUpLoginRefreshRequestModel());
            frtAsyncCommon.execute(frtWEBAPI.getWEBAPI(FRTAPIType.LOGIN), "loginrefresh");
            frtAsyncCommon.setFetchDataCallBack(frtRaiseIssueActivity);
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
}
