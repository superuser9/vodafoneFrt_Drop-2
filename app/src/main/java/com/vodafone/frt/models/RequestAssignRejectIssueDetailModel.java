package com.vodafone.frt.models;

import java.util.ArrayList;

/**
 * Created by Ajay Tiwari on 3/29/2018.
 */

public class RequestAssignRejectIssueDetailModel {

    private int userId;
    private int frtUserId;

    public int getIssueIdRejected() {
        return issueIdRejected;
    }

    public void setIssueIdRejected(int issueIdRejected) {
        this.issueIdRejected = issueIdRejected;
    }

    private int issueIdRejected;
    private String issueIdString;

    public String getIssueIdString() {
        return issueIdString;
    }

    public void setIssueIdString(String issueIdString) {
        this.issueIdString = issueIdString;
    }

    private ArrayList<Integer>issuesId;
    private String status;
    private String assignDate;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFrtUserId() {
        return frtUserId;
    }

    public void setFrtUserId(int frtUserId) {
        this.frtUserId = frtUserId;
    }

    public ArrayList<Integer> getIssuesId() {
        return issuesId;
    }

    public void setIssuesId(ArrayList<Integer> issuesId) {
        this.issuesId = issuesId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    private String remark;

}
