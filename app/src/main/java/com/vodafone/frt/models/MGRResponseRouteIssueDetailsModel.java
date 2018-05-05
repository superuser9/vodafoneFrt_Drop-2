package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by Ajay Tiwari on 3/29/2018.
 */

public class MGRResponseRouteIssueDetailsModel implements Comparable<MGRResponseRouteIssueDetailsModel>{

    private boolean selected;
    private int issue_id;
    private double longitude;
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public int getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(int issue_id) {
        this.issue_id = issue_id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPatroller_user_name() {
        return patroller_user_name;
    }

    public void setPatroller_user_name(String patroller_user_name) {
        this.patroller_user_name = patroller_user_name;
    }

    public String getFrt_user_name() {
        return frt_user_name;
    }

    public void setFrt_user_name(String frt_user_name) {
        this.frt_user_name = frt_user_name;
    }

    public String getPatroller_remark() {
        return patroller_remark;
    }

    public void setPatroller_remark(String patroller_remark) {
        this.patroller_remark = patroller_remark;
    }

    public String getManager_remark() {
        return manager_remark;
    }

    public void setManager_remark(String manager_remark) {
        this.manager_remark = manager_remark;
    }

    public String getAssigned_date() {
        return assigned_date;
    }

    public void setAssigned_date(String assigned_date) {
        this.assigned_date = assigned_date;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getModified_on() {
        return modified_on;
    }

    public void setModified_on(String modified_on) {
        this.modified_on = modified_on;
    }

    public String getMobile_checkin_time() {
        return mobile_checkin_time;
    }

    public void setMobile_checkin_time(String mobile_checkin_time) {
        this.mobile_checkin_time = mobile_checkin_time;
    }

    public String getMobile_checkout_time() {
        return mobile_checkout_time;
    }

    public void setMobile_checkout_time(String mobile_checkout_time) {
        this.mobile_checkout_time = mobile_checkout_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssue_desc() {
        return issue_desc;
    }

    public void setIssue_desc(String issue_desc) {
        this.issue_desc = issue_desc;
    }

    public String getCheckin_remarks() {
        return checkin_remarks;
    }

    public void setCheckin_remarks(String checkin_remarks) {
        this.checkin_remarks = checkin_remarks;
    }

    public String getCheckout_remarks() {
        return checkout_remarks;
    }

    public void setCheckout_remarks(String checkout_remarks) {
        this.checkout_remarks = checkout_remarks;
    }

    private double latitude;
    private String patroller_user_name;
    private String frt_user_name;
    private String patroller_remark;
    private String manager_remark;
    private String assigned_date;
    private String created_on;
    private String modified_on;
    private String mobile_checkin_time;
    private String mobile_checkout_time;
    private String status;
    private String issue_desc;
    private String checkin_remarks;
    private String checkout_remarks;

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    private String route_name;


    @Override
    public int compareTo(@NonNull MGRResponseRouteIssueDetailsModel o) {
        return getCreated_on().compareTo(getCreated_on());
    }
}
