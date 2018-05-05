package com.vodafone.frt.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/19/2018.
 */

public class FRTCheckInOutDetailsModel {

    private int userId,taskTrackingId,issueId;
    private String action;
    private String latitude,longitude;
    private String dateTime;
    private String remark;
    private List<String> base64encodedstringList = new ArrayList<>();

    public List<String> getBase64encodedstringList() {
        return base64encodedstringList;
    }

    public void setBase64encodedstringList(List<String> base64encodedstringList) {
        this.base64encodedstringList = base64encodedstringList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskTrackingId() {
        return taskTrackingId;
    }

    public void setTaskTrackingId(int taskTrackingId) {
        this.taskTrackingId = taskTrackingId;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
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

   /* public List<String> getImageData() {
        return imageData;
    }

    public void setImageData(List<String> imageData) {
        this.imageData = imageData;
    }

    private List<String> imageData;*/

}
