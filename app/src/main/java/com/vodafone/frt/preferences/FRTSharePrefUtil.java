package com.vodafone.frt.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.vodafone.frt.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Singleton class that is used for storing data
 */
public class FRTSharePrefUtil {

    private static FRTSharePrefUtil frtSharePrefUtil;
    private static SharedPreferences mSharedPreferences;

    /**
     * getting class instance
     *
     * @param ctx activity context
     * @return class instance
     */
    public static FRTSharePrefUtil getInstance(Context ctx) {
        if (frtSharePrefUtil != null)
            return frtSharePrefUtil;
        mSharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        frtSharePrefUtil = new FRTSharePrefUtil();
        return frtSharePrefUtil;
    }


    /**
     * setting string type value
     *
     * @param key     key
     * @param address value
     */
    public void setString(String key, String address) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key, address);
        mEditor.apply();
    }


    public void setLong(String key, long value) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, -1);
    }

    /**
     * getting value
     *
     * @param key key
     * @return string value
     */
    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }


    /**
     * setting boolean type value
     *
     * @param key  key
     * @param bool value
     */
    public void setBoolean(String key, boolean bool) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, bool);
        mEditor.apply();
    }

    /**
     * getting value
     *
     * @param key key
     * @return true/false
     */
    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public Object getObject() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("MyObject", "");
        return gson.fromJson(json, LatLng.class);
    }

    public void setObject(Object object) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        mEditor.putString("MyObject", json);
        mEditor.apply();
    }

    /**
     * Get parsed ArrayList of Integers from SharedPreferences at 'key'
     *
     * @return ArrayList of Integers
     */
    /*public List<Integer> getList(String key) {
        String[] myList = TextUtils.split(mSharedPreferences.getString(key, ""), "‚");
        List<String> arrayToList = new ArrayList<>(Arrays.asList(myList));
        List<Integer> newList = new ArrayList<>();
        Gson gson = new Gson();
        for (String item : arrayToList) {
//            Integer frtResponsePlannedRouteModel = gson.fromJson(item, PTRResponsePlannedRouteModel.class);
//            newList.add(frtResponsePlannedRouteModel);
        }
        return newList;
    }
*/
    /**
     * Put ArrayList of Integer into SharedPreferences with 'key' and save
     *
     * @param responsePlannedRouteModelList ArrayList of Integer to be added
     */
   /* public void setList(String key,List<Integer> responsePlannedRouteModelList) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        Integer[] myIntList = responsePlannedRouteModelList.toArray(new Integer[responsePlannedRouteModelList.size()]);
        mEditor.putString(key, TextUtils.join("‚", myIntList));
        mEditor.apply();
    }
*/

    /**
     * Get parsed ArrayList of Integers from SharedPreferences at 'key'
     *
     * @return ArrayList of Integers
     */
    public List<String> getList(String key) {
        String[] myList = TextUtils.split(mSharedPreferences.getString(key, ""), "‚‗‚");
        List<String> arrayToList = new ArrayList<>(Arrays.asList(myList));
        List<String> newList = new ArrayList<>();
        Gson gson = new Gson();
        for (String item : arrayToList) {
            String string = gson.fromJson(item, String.class);
            newList.add(string);
        }
        return newList;
    }

    /**
     * Put ArrayList of Integer into SharedPreferences with 'key' and save
     *
     * @param string ArrayList of Integer to be added
     */
    public void setList(String key,List<String> string) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        String[] myStringList = string.toArray(new String[string.size()]);
        mEditor.putString(key, TextUtils.join("‚‗‚", myStringList));
        mEditor.apply();
    }

}