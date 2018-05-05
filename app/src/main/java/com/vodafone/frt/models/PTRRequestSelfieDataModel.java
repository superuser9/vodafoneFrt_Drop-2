package com.vodafone.frt.models;

import java.util.List;

/**
 * Created by vishal on 9/2/18
 */

public class PTRRequestSelfieDataModel {
    private int routeAssignmentId;
    private String action, latitude, longitude, mobileTime, remarks, imageData;

    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRouteAssignmentId() {
        return routeAssignmentId;
    }

    public void setRouteAssignmentId(int routeAssignmentId) {
        this.routeAssignmentId = routeAssignmentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getMobileTime() {
        return mobileTime;
    }

    public void setMobileTime(String mobileTime) {
        this.mobileTime = mobileTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}
