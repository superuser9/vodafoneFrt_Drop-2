package com.vodafone.frt.models;

public class GetSubordinatesReq {

    private String userId;
    private String roleName;

    public GetSubordinatesReq(String userId, String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


}
