package com.vodafone.frt.utility;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A class with static util methods.
 */

public class DateUtils {

    // This class should not be initialized
    private DateUtils() {

    }


    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static int getHourOfDay(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    public static int getMinute(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("m", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     * @param millisFirst   The time in milliseconds of the first date.
     * @param millisSecond  The time in milliseconds of the second date.
     * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    /*public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }*/

    public static boolean hasSameDate(String millisFirst, String millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return millisFirst.equals(millisSecond);
    }

    public static String dateFormate(String value){
        String temp = "";
        try {
            value = "2018-04-05T16:39:03.7992229+05:30";
            SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = dt.parse(value);
            SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            temp = dt1.format(date);
            System.out.println(dt1.format(date));
        }catch (Exception e){
            Log.e("TAG",""+e);
        }
        return temp;
    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        Date mDate = new Date(new Date().getTime());
        return sdf.format(mDate);
    }

    public static String getDateInMillisecond(String dateString){
        //01-JAN-0001 12:00 AM
       /* SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm a",Locale.getDefault());
        try{
            Date date = sdf.parse(dateString);
            return date.getTime();
        }catch(Exception e){
            return 0;
        }*/
        try{
            return dateString.split(" ")[0];
            /*Calendar.getInstance().getTimeZone();
            Date date = sdf.parse(dateFormate);
            sdf = new SimpleDateFormat("HH:mm a",Locale.getDefault());
            sdf.setTimeZone(Calendar.getInstance().getTimeZone());
            String format = sdf.format(date);
            return format;*/
        }catch(Exception e){
            return dateString;
        }
    }


    public static String getTime(String dateFormate){
       // 12-Apr-2018 12:06 PM
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm a",Locale.getDefault());
        try{
            return dateFormate.split(" ")[1] + " " + dateFormate.split(" ")[2];
            /*Calendar.getInstance().getTimeZone();
            Date date = sdf.parse(dateFormate);
            sdf = new SimpleDateFormat("HH:mm a",Locale.getDefault());
            sdf.setTimeZone(Calendar.getInstance().getTimeZone());
            String format = sdf.format(date);
            return format;*/
        }catch(Exception e){
            return dateFormate;
        }
    }
}
