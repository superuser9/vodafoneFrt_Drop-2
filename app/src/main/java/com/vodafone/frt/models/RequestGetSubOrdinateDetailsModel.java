package com.vodafone.frt.models;

/**
 * Created by Ajay Tiwari on 3/30/2018.
 */

public class RequestGetSubOrdinateDetailsModel {
    private int userId;
    private String roleName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
