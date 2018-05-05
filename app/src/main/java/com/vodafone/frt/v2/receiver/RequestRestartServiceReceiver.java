package com.vodafone.frt.v2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vodafone.frt.v2.service.LocationService;
import com.vodafone.frt.v2.service.SyncService;


/**
 * Created by Abhimanyu on 3/2/18
 */
public class RequestRestartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationService.class));
        context.startService(new Intent(context, SyncService.class));
    }
}

