package com.vodafone.frt.app;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.vodafone.frt.v2.db.DbService;

import io.fabric.sdk.android.Fabric;


/**
 * Created by vishal on 14/12/17
 */
public class FRTApp extends Application {
    private Context ctx;
    private int tabPosition;
    private String checkForDialog;
    private String ReasonId;
    private StringBuilder textOfFile = new StringBuilder();
    private LatLng latLong;
    private DbService dbService;
    private String actualStartTime;
    private String actualEndTime;
    private boolean isOneTime;
    private String issueTypeId;

    public String getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(String issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public String getReasonId() {
        return ReasonId;
    }

    public void setReasonId(String reasonId) {
        ReasonId = reasonId;
    }

    public String getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public String getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(String actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public DbService getDbService() {
        return dbService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        dbService = Room.databaseBuilder(this,
                DbService.class, DbService.DATABASE_NAME).build();
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public String getCheckForDialog() {
        return checkForDialog;
    }

    public void setCheckForDialog(String checkForDialog) {
        this.checkForDialog = checkForDialog;
    }

    public String getTextOfFile(boolean clear) {
        String s = textOfFile.toString();
        if (clear)
            textOfFile = new StringBuilder();
        return s;
    }

    public LatLng getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLng latLong) {
        this.latLong = latLong;
    }

    public void setIsOneTime(boolean isOneTime) {
        this.isOneTime = isOneTime;
    }

    public boolean isOneTime() {
        return isOneTime;
    }
}