package com.vodafone.frt.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ajay Tiwari on 3/19/2018.
 */

public class FRTResponseRouteCommonModelFRT implements Parcelable ,Comparable<FRTResponseRouteCommonModelFRT>{
   private int task_tracking_id,issue_id,user_id,checkin_radius;
   private double latitude,longitude;
   private String checkin_remarks;
    private String checkout_remarks;
    private String issue_type;
    private String checkin_time;
    private String patroller_remark;
    private String checkout_time;

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

    private String manager_remark;

    public String getAssigned_date() {
        return assigned_date;
    }

    public void setAssigned_date(String assigned_date) {
        this.assigned_date = assigned_date;
    }

    private String assigned_date;


    public FRTResponseRouteCommonModelFRT(){

    }

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public int getCheckin_radius() {
        return checkin_radius;
    }

    public void setCheckin_radius(int checkin_radius) {
        this.checkin_radius = checkin_radius;
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

    public String getIssue_type() {
        return issue_type;
    }

    public void setIssue_type(String issue_type) {
        this.issue_type = issue_type;
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



    public FRTResponseRouteCommonModelFRT(Parcel in) {
        task_tracking_id = in.readInt();
        issue_id = in.readInt();
        user_id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        checkin_radius = in.readInt();
        checkin_remarks = in.readString();
        checkout_remarks = in.readString();
        issue_type = in.readString();
        checkin_time = in.readString();
        checkout_time = in.readString();
        assigned_date = in.readString();
        patroller_remark = in.readString();
        manager_remark= in.readString();
    }

    public static final Creator<FRTResponseRouteCommonModelFRT> CREATOR = new Creator<FRTResponseRouteCommonModelFRT>() {
        @Override
        public FRTResponseRouteCommonModelFRT createFromParcel(Parcel in) {
            return new FRTResponseRouteCommonModelFRT(in);
        }

        @Override
        public FRTResponseRouteCommonModelFRT[] newArray(int size) {
            return new FRTResponseRouteCommonModelFRT[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(task_tracking_id);
        dest.writeInt(issue_id);
        dest.writeInt(user_id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(checkin_radius);
        dest.writeString(checkin_remarks);
        dest.writeString(checkout_remarks);
        dest.writeString(issue_type);
        dest.writeString(checkin_time);
        dest.writeString(assigned_date);
        dest.writeString(checkout_time);
        dest.writeString(patroller_remark);
        dest.writeString(manager_remark);
    }

    @Override
    public int compareTo(@NonNull FRTResponseRouteCommonModelFRT frtResponseRouteCommonModelFRT) {
        return frtResponseRouteCommonModelFRT.getAssigned_date().compareTo(getAssigned_date());
    }
      /*  SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:MM");
        try {
            Date d1 = f.parse(getCheckin_time());
            Date d2 = f.parse(frtResponseRouteCommonModelFRT.getCheckin_time());
            long milliseconds1 = d1.getTime();
            long milliseconds2 = d2.getTime();
            long value=milliseconds1-milliseconds2;
        } catch (ParseException e) {
            e.printStackTrace();
        }





        if(getCheckin_time().compareTo(frtResponseRouteCommonModelFRT.getCheckin_time()) == -1)
            return 1;
        if(getCheckin_time().compareTo(frtResponseRouteCommonModelFRT.getCheckin_time()) == 1)
            return -1;
        else
            return 0;
    }*/
}
