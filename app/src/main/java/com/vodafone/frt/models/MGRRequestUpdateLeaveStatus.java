package com.vodafone.frt.models;

/**
 * Created by Ajay Tiwari on 4/2/2018.
 */

public class MGRRequestUpdateLeaveStatus {
    private String leaveIds;
    private int leaveId,managerId;
    private String status;

    public String getLeaveIds() {
        return leaveIds;
    }

    public void setLeaveIds(String leaveIds) {
        this.leaveIds = leaveIds;
    }



    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    private String remark;
}
