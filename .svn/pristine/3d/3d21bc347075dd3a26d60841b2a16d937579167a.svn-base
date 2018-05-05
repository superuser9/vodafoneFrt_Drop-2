package com.vodafone.frt.v2.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import com.vodafone.frt.R;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.FRTUtility;
import com.vodafone.frt.v2.db.LocationEntity;

import java.util.Date;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by SM-002 on 01-02-2018.
 */

public class LocationService extends Service {
    private FRTApp frtApp;
    private FRTUtility frtUtility;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTConstants frtConstants;
    private LocationEntity locationEntity;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        frtApp = (FRTApp) getApplication();
        frtUtility = FRTUtility.getUtilityInstance();
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtApp);
        frtConstants = new FRTConstants();
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(frtConstants.TRACKING_DISTANCE);
        SmartLocation.with(frtApp)
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        String userId = frtSharePrefUtil.getString(getString(R.string.userkey));
                        String routessignmentId = frtSharePrefUtil.getString(getString(R.string.routeassigmentid_key));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (location.isFromMockProvider() || TextUtils.isEmpty(userId) || TextUtils.isEmpty(routessignmentId))
                                return;
                        }
                        Date date = new Date();
                        date.setTime(location.getTime());
                        String bufferLimit = frtSharePrefUtil.getString(getString(R.string.Bufferlimit));
                        locationEntity = new LocationEntity();
                        locationEntity.setLatitude(location.getLatitude());
                        locationEntity.setLongitude(location.getLongitude());
                        locationEntity.setMobileTime(date);
                        locationEntity.setDrivingSpeed(location.getSpeed() + "");
                        locationEntity.setBatteryPercentage(String.valueOf(frtUtility.getBatteryPercentage(getApplicationContext())));
                        locationEntity.setDistFromLastLoc("0.0");
                        locationEntity.setUserId(userId);
                        locationEntity.setRouteAssignmentId(routessignmentId);
                        locationEntity.setIsInsideBuffer("false");
                        locationEntity.setNetworkProvider(frtUtility.getNetworkProvider());
                        new Thread() {
                            public void run() {
                                frtApp.getDbService().daoAccess().insertRecord(locationEntity);
                            }
                        }.start();
                    }
                });
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(frtConstants.RESTART_SERVICE_KEY);
        sendBroadcast(intent);
    }
}