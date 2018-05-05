package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by Ajay Tiwari on 4/10/2018.
 */

public class FRTResponseAttendanceModel  implements Comparable<FRTResponseAttendanceModel> {

    private int task_tracking_id;
    private int issue_id;
    private String issue_desc;
    private String frt_full_name;
    private String attendance_date;
    private String checkin_time;
    private String checkout_time;
    private double latitude;

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

    private double longitude;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public int getTask_tracking_id() {
        return task_tracking_id;
    }

    public void setTask_tracking_id(int task_tracking_id) {
        this.task_tracking_id = task_tracking_id;
    }

    public int getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(int issue_id) {
        this.issue_id = issue_id;
    }

    public String getIssue_desc() {
        return issue_desc;
    }

    public void setIssue_desc(String issue_desc) {
        this.issue_desc = issue_desc;
    }

    public String getFrt_full_name() {
        return frt_full_name;
    }

    public void setFrt_full_name(String frt_full_name) {
        this.frt_full_name = frt_full_name;
    }

    public String getAttendance_date() {
        return attendance_date;
    }

    public void setAttendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(String checkout_time) {
        this.checkout_time = checkout_time;
    }

    public String getTime_taken() {
        return time_taken;
    }

    public void setTime_taken(String time_taken) {
        this.time_taken = time_taken;
    }

    private String time_taken;


    @Override
    public int compareTo(@NonNull FRTResponseAttendanceModel o) {
        return o.getAttendance_date().compareTo(o.getAttendance_date());
    }
}
