package com.vodafone.frt.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vishal on 19/12/17
 */

public class PTRResponseMyTaskModel implements Parcelable {
    private int route_assigned_id,
            route_id,
            route_ref_id,
            patroller_id,
            scheduled_assigment_id;
    private String
            patroller_name,
            route_name,
            planned_start_time,
            planned_end_time,
            actual_start_time,
            actual_end_time,
            status,
            created_by_name,
            created_on,
            modified_on,
            modified_by_name,
            self_check_in;
    public PTRResponseMyTaskModel() {
    }

    private PTRResponseMyTaskModel(Parcel in) {
        route_assigned_id = in.readInt();
        route_id = in.readInt();
        route_ref_id = in.readInt();
        patroller_id = in.readInt();
        scheduled_assigment_id = in.readInt();
        patroller_name = in.readString();
        route_name = in.readString();
        planned_start_time = in.readString();
        planned_end_time = in.readString();
        actual_start_time = in.readString();
        actual_end_time = in.readString();
        status = in.readString();
        created_by_name = in.readString();
        created_on = in.readString();
        modified_on = in.readString();
        modified_by_name = in.readString();
        self_check_in=in.readString();
    }

    public static final Creator<PTRResponseMyTaskModel> CREATOR = new Creator<PTRResponseMyTaskModel>() {
        @Override
        public PTRResponseMyTaskModel createFromParcel(Parcel in) {
            return new PTRResponseMyTaskModel(in);
        }

        @Override
        public PTRResponseMyTaskModel[] newArray(int size) {
            return new PTRResponseMyTaskModel[size];
        }
    };

    public int getRoute_assigned_id() {
        return route_assigned_id;
    }

    public void setRoute_assigned_id(int route_assigned_id) {
        this.route_assigned_id = route_assigned_id;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public int getRoute_ref_id() {
        return route_ref_id;
    }

    public void setRoute_ref_id(int route_ref_id) {
        this.route_ref_id = route_ref_id;
    }

    public int getPatroller_id() {
        return patroller_id;
    }

    public void setPatroller_id(int patroller_id) {
        this.patroller_id = patroller_id;
    }

    public String getPatroller_name() {
        return patroller_name;
    }

    public void setPatroller_name(String patroller_name) {
        this.patroller_name = patroller_name;
    }

    public int getScheduled_assigment_id() {
        return scheduled_assigment_id;
    }

    public void setScheduled_assigment_id(int scheduled_assigment_id) {
        this.scheduled_assigment_id = scheduled_assigment_id;
    }

    public String getPlanned_start_time() {
        return planned_start_time;
    }

    public void setPlanned_start_time(String planned_start_time) {
        this.planned_start_time = planned_start_time;
    }

    public String getPlanned_end_time() {
        return planned_end_time;
    }

    public void setPlanned_end_time(String planned_end_time) {
        this.planned_end_time = planned_end_time;
    }

    public String getActual_start_time() {
        return actual_start_time;
    }

    public void setActual_start_time(String actual_start_time) {
        this.actual_start_time = actual_start_time;
    }

    public String getActual_end_time() {
        return actual_end_time;
    }

    public void setActual_end_time(String actual_end_time) {
        this.actual_end_time = actual_end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public void setCreated_by_name(String created_by_name) {
        this.created_by_name = created_by_name;
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

    public String getModified_by_name() {
        return modified_by_name;
    }

    public String getSelf_check_in() {
        return self_check_in;
    }

    public void setSelf_check_in(String self_check_in) {
        this.self_check_in = self_check_in;
    }

    public void setModified_by_name(String modified_by_name) {
        this.modified_by_name = modified_by_name;


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(route_assigned_id);
        parcel.writeInt(route_id);
        parcel.writeInt(route_ref_id);
        parcel.writeInt(patroller_id);
        parcel.writeInt(scheduled_assigment_id);
        parcel.writeString(actual_end_time);
        parcel.writeString(actual_start_time);
        parcel.writeString(created_by_name);
        parcel.writeString(created_on);
        parcel.writeString(modified_by_name);
        parcel.writeString(modified_on);
        parcel.writeString(patroller_name);
        parcel.writeString(planned_end_time);
        parcel.writeString(status);
        parcel.writeString(planned_start_time);
        parcel.writeString(route_name);
        parcel.writeString(self_check_in);
    }
}
