package com.vodafone.frt.utility;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vodafone.frt.network.FRTConnectivityReceiver;


/**
 * Created by vishal on 1/12/17
 */

public class FRTBroadcasting {
    @SuppressLint("StaticFieldLeak")
    private static final FRTBroadcasting frtBroadcasting;

    static {
        frtBroadcasting = new FRTBroadcasting();
    }

    private FRTConnectivityReceiver frtConnectivityReceiver;
    private CallBackBroadcast callBackBroadcast;
    private Context ctx;

    private FRTBroadcasting() {
    }

    public void setContext(Context context) {
        ctx = context;
        frtConnectivityReceiver = new FRTConnectivityReceiver();
    }

    public void setCallbackBroadcast(CallBackBroadcast callBackBroadcast1) {
        callBackBroadcast = callBackBroadcast1;
    }

    public static FRTBroadcasting getBroadcastingInstance() {
        if (frtBroadcasting != null)
            return frtBroadcasting;
        else
            return new FRTBroadcasting();
    }

    public interface CallBackBroadcast {
        void getBroadcast(boolean isBroadcasting);
    }

    /**
     * Broacast receiver that checks network
     */
    public BroadcastReceiver getBroadcasting() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!frtConnectivityReceiver.isConnected(ctx)) {
                    callBackBroadcast.getBroadcast(true);
                } else {
                    callBackBroadcast.getBroadcast(false);
                }
            }
        };
    }
}
