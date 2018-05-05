package com.vodafone.frt.v2.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

/**
 * Created by SM-002 on 01-02-2018.
 */
@Entity(tableName = "locationentity")
public class LocationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "networkProvider")
    private String networkProvider;
    @ColumnInfo(name = "userId")
    private String userId;
    @ColumnInfo(name = "routeAssignmentId")
    private String routeAssignmentId;
    @ColumnInfo(name = "isInsideBuffer")
    private String isInsideBuffer;
    @ColumnInfo(name = "deviationFromPlanned")
    private String deviationFromPlanned;
    @ColumnInfo(name = "distFromLastLoc")
    private String distFromLastLoc;
    @ColumnInfo(name = "drivingSpeed")
    private String drivingSpeed;
    @ColumnInfo(name = "batteryPercentage")
    private String batteryPercentage;

    @ColumnInfo(name = "mobileTime")
    @TypeConverters({DateTimeConverter.class})
    private Date mobileTime;

    @ColumnInfo(name = "jsontoupload")
    private String jsontoupload;

    public Date getMobileTime() {
        return mobileTime;
    }

    public void setMobileTime(Date mobileTime) {
        this.mobileTime = mobileTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNetworkProvider() {
        return networkProvider;
    }

    public void setNetworkProvider(String networkProvider) {
        this.networkProvider = networkProvider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteAssignmentId() {
        return routeAssignmentId;
    }

    public void setRouteAssignmentId(String routeAssignmentId) {
        this.routeAssignmentId = routeAssignmentId;
    }

    public String getIsInsideBuffer() {
        return isInsideBuffer;
    }

    public void setIsInsideBuffer(String isInsideBuffer) {
        this.isInsideBuffer = isInsideBuffer;
    }

    public String getDeviationFromPlanned() {
        return deviationFromPlanned;
    }

    public void setDeviationFromPlanned(String deviationFromPlanned) {
        this.deviationFromPlanned = deviationFromPlanned;
    }

    public String getDistFromLastLoc() {
        return distFromLastLoc;
    }

    public void setDistFromLastLoc(String distFromLastLoc) {
        this.distFromLastLoc = distFromLastLoc;
    }

    public String getDrivingSpeed() {
        return drivingSpeed;
    }

    public void setDrivingSpeed(String drivingSpeed) {
        this.drivingSpeed = drivingSpeed;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getJsontoupload() {
        return jsontoupload;
    }

    public void setJsontoupload(String jsontoupload) {
        this.jsontoupload = jsontoupload;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
