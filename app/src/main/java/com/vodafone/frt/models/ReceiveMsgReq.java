package com.vodafone.frt.models;

public class ReceiveMsgReq {
    private String data;

    public ReceiveMsgReq(String data){
        this.data = data;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
