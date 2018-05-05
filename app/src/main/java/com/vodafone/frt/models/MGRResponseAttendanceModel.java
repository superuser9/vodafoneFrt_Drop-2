package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by Ajay Tiwari on 3/20/2018.
 */

public class MGRResponseAttendanceModel  implements  Comparable<MGRResponseAttendanceModel>{
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

    public int getLeave_id() {
        return leave_id;
    }

    public void setLeave_id(int leave_id) {
        this.leave_id = leave_id;
    }

    private int leave_id;
    private String user_name,from_date,to_date,leave_reason,leave_status,manager_remark,created_on,approved_on,approved_by;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getLeave_reason() {
        return leave_reason;
    }

    public void setLeave_reason(String leave_reason) {
        this.leave_reason = leave_reason;
    }

    public String getLeave_status() {
        return leave_status;
    }

    public void setLeave_status(String leave_status) {
        this.leave_status = leave_status;
    }

    public String getManager_remark() {
        return manager_remark;
    }

    public void setManager_remark(String manager_remark) {
        this.manager_remark = manager_remark;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getApproved_on() {
        return approved_on;
    }

    public void setApproved_on(String approved_on) {
        this.approved_on = approved_on;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    private int created_by;

    @Override
    public int compareTo(MGRResponseAttendanceModel o) {
        return o.getCreated_on().compareTo(o.getCreated_on());
    }
}
