package com.vodafone.frt.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.services.FRTLocationTrackingService;

/**
 * Created by vishal on 16/1/18
 */
public class FRTLocationTrackReceiver extends BroadcastReceiver {
    private FRTSharePrefUtil frtSharePrefUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(context);
        frtSharePrefUtil.setString("planneddata", "");
        context.startService(new Intent(context, FRTLocationTrackingService.class));

    }
}

