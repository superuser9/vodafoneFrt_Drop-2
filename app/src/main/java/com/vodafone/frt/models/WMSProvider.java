package com.vodafone.frt.models;

/**
 * Created by Lepton on 13-Mar-18.
 */
public class WMSProvider {
    //lat/lon (EPSG:4326 ) , XY in metres(EPSG:900913) "EPSG:3857"    XY pixels Z zoom "EPSG:4326";     XYZ from TMS

    public String crs = "EPSG:4326";
    public String url;
    public String layers;
    public String styles = "default";
    public String transparent = "true";
    
    public WMSProvider() {
    }
    
    public WMSProvider layers(String layers) {
        this.layers = layers;
        return this;
    }
    
    public WMSProvider url(String url) {
        this.url = url;
        return this;
    }
    
    public WMSProvider crs(String crs) {
        this.crs = crs;
        return this;
    }

    public WMSProvider styles(String styles) {
        this.styles = styles;
        return this;
    }
    
    public WMSProvider transparent(boolean transparent) {
        this.transparent = Boolean.toString(transparent);
        return this;
    }
}
