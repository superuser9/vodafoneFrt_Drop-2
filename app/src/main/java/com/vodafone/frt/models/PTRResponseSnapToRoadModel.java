package com.vodafone.frt.models;

/**
 * Created by qss on 10/1/18
 */

public class PTRResponseSnapToRoadModel {

    private String placeId;
        private String latitude;
        private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

}
