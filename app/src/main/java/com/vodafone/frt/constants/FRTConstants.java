package com.vodafone.frt.constants;

/**
 * Created by vishal on 16/11/17
 */
public class FRTConstants {
    public final String RESTART_SERVICE_KEY = "com.vodafone.frt.v2.db";
    public final float TRACKING_DISTANCE = 100;
    public final String IS_LOGGED_IN = "islogin";
    public final int PAUSE_CODE = 9;
    public final int RAISE_ISSUE = 6;
    public final long KEY_SPLASH_TIME = 3000;
    public final long KEY_CAEMRA_OPEN_TIME = 1500;
    public final long ROUTE_END_TIME = 3000;

   // public static final String

//    public final String SMART_INVENTORY_URL = "https://frt.vodafone.in:8086/cgi-bin/mapserv.exe?MAP=C:/LeptonApps/PROD/Mapfile/mapfiles/NetworkEntitiesNoLabel.map";
//    public final String SMART_INVENTORY_URL = "http://111.93.46.11:3028/cgi-bin/mapserv.exe?MAP=D:/apps/SmartPatroller/mapfiles/NetworkEntitiesNoLabel.map";
    //Production server drop1
//    public final String ROOT_URL = "http://111.93.46.11:8091";
    //Web Tesr=ting server
//    public final String ROOT_URL = "http://111.93.46.11:8092";
    //Production server drop2
//    public final String ROOT_URL = "http://111.93.46.11:8093";
    public final String ROOT_URL = "https://frt.vodafone.in:8085";




    //    public final String ROOT_URL = "http://122.15.135.191:8085";
    //SECURITY TESTING URL
//    public final String ROOT_URL = "http://10.113.244.109:8085";
    public final int LOCATION_PERMISSION = 200;
    public final String USERNAME_KEY = "passkey";
    public final int DOCUMENT_UPLOAD = 302;
    public final int REQUEST_CAMERA_ROUTEACTION = 11;
    public final int REQUEST_CAMERA_SELFIE = 12;
    public final int REQUEST_CAMERA_CHECKIN = 13;
    public final int REQUEST_CAMERA_CHECKOUT = 14;
    public final int REQUEST_CAMERA_SAVE_ROUTE_ISSUE = 15;

    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public final String LOG_FILE_NAME = "frt_log.txt";
    public final int LOG_FILE_SIZE = 9999999;//2097152; // 2mb(2*1024*1024)

    //Sync Status
    public final String SYNC_STATUS_PENDING = "pending";
    public final String SYNC_STATUS_IN_PROGRESS = "in_progress";
    public final String SYNC_STATUS_SYNCED = "synced";
    public final int LAST_LOCATION_SEQUENCE_NUMBER = 10;

    public final String ROUTEEND = "ROUTEEND";
    public final String TRACKINGDATA = "TRACKINGDATA";

}