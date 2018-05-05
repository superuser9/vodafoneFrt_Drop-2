package com.vodafone.frt.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vodafone.frt.activities.PTRNavigationScreenActivity;
import com.vodafone.frt.services.FRTLocationTrackingService;

import java.util.List;

/**
 * Created by Ajay Tiwari on 3/23/2018.
 */

public class TimeChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            Log.e("DATE_CHANGE", "ACTION_DATE_CHANGED received");

            if (isAppForground(context)){
            // App is in Foreground
                context.startActivity(new Intent(context, PTRNavigationScreenActivity.class));
                context.stopService(new Intent(context, FRTLocationTrackingService.class));
            }
            else {
            // App is in Background
                context.stopService(new Intent(context, FRTLocationTrackingService.class));
            }


        }
    }




    public boolean isAppForground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }


}
