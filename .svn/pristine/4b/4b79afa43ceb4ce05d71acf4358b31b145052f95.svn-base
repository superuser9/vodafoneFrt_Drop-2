package com.vodafone.frt.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.vodafone.frt.R;


public class SharePrefUtil {

    public static SharedPreferences getSharePref(Context ctx){
        return ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public static boolean clearSharePref(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.clear();
        return mEditor.commit();
    }


    public static String getUserName(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Constants.USER_NAME, "");
    }

    public static boolean setUserName(Context ctx, String userName){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.USER_NAME, userName);
        return mEditor.commit();
    }

    public static String getUserId(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Constants.USER_ID, "");
    }

    public static boolean setUserId(Context ctx, String userId){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.USER_ID, userId);
        return mEditor.commit();
    }


    public static String getMgrId(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Constants.MGR_ID, "");
    }

    public static boolean setMgrId(Context ctx, String mgrId){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.MGR_ID, mgrId);
        return mEditor.commit();
    }


   /* public static boolean setToken(Context ctx, String refreshedToken) {
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.FCM_TOKEN, refreshedToken);
        return mEditor.commit();
    }

    public static String getToken(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Constants.FCM_TOKEN, "");
    }

    public static boolean setServerToken(Context ctx, String refreshedToken) {
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.SERVER_TOKEN, refreshedToken);
        return mEditor.commit();
    }

    public static String getServerToken(Context ctx){
        SharedPreferences mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(Constants.SERVER_TOKEN, "");
    }*/
}
