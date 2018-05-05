package com.vodafone.frt.models;

import java.util.List;

/**
 * Created by Ashutosh Kumar on 11-Feb-18.
 */

public class PTRRequestSaveHaltPointsModel {
    private int userId;
    private int haltTrackingId;
    private int routeAssignmentId;
    private String action;
    private String latitude;
    private String longitude;
    private String dateTime;
    private String remark;
    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHaltTrackingId() {
        return haltTrackingId;
    }

    public void setHaltTrackingId(int haltTrackingId) {
        this.haltTrackingId = haltTrackingId;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
