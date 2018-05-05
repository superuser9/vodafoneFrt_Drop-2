package com.vodafone.frt.models;

public class RegisterUserReq {
    private String data;

    public RegisterUserReq(String data){
        this.data = data;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
