package com.vodafone.frt.models;

/**
 * Created by vishal on 14/12/17
 */

public class PTRResponsePlannedRouteModel {
    private double lat, lng;
    private String JSON;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }
}
