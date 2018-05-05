package com.vodafone.frt.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vodafone.frt.services.FRTLocationUploaderService;

/**
 * Created by vishal on 14/12/17
 */
public class FRTLocationUploaderReceiver extends BroadcastReceiver {


    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FRTLocationUploaderService.class));
    }
}
