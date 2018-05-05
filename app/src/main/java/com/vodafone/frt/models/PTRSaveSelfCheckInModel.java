package com.vodafone.frt.models;

/**
 * Created by Ajay Tiwari on 2/26/2018.
 */

public class PTRSaveSelfCheckInModel {

    private int userId;
    private int managerId;
    private String routeName;
    private String latitude;
    private String longitude;
    private String remarks;
    private String mobileTime;
    private String plannedStartTime;
    private String plannedEndTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMobileTime() {
        return mobileTime;
    }

    public void setMobileTime(String mobileTime) {
        this.mobileTime = mobileTime;
    }

    public String getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(String plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public String getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(String plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

}
