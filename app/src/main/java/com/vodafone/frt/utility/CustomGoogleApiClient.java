package com.vodafone.frt.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.vodafone.frt.db.FRTLocationDb;
import com.vodafone.frt.preferences.FRTSharePrefUtil;

/**
 * Created by Ashutosh Kumar on 23-Feb-18.
 */

public class CustomGoogleApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static CustomGoogleApiClient customGoogleApiClient = new CustomGoogleApiClient();
    private GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Context context;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTLocationDb frtLocationDb;

    public CustomGoogleApiClient() {
    }

    private CustomGoogleApiClient(Context ctx) {
        context=ctx;
        frtLocationDb=new FRTLocationDb(context);
        buildGoogleApiClient(context);
    }

    private synchronized void buildGoogleApiClient(Context context) {
        try {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(customGoogleApiClient)
                    .addOnConnectionFailedListener(customGoogleApiClient)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }
    public static CustomGoogleApiClient getGoogleClientInstance(){
        return customGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(60000);
            locationRequest.setSmallestDisplacement(20);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, customGoogleApiClient);
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
        if (frtLocationDb.getpendingandingressDataSize()<=0){
            frtSharePrefUtil.setObject(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }
}
