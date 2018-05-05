package com.vodafone.frt.utility;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.*;

import com.vodafone.frt.R;
import com.vodafone.frt.preferences.FRTSharePrefUtil;

import java.util.TimerTask;

/**
 * Created by vishal on 19/2/18
 */

public class TimerClass extends TimerTask {

    private static TimerClass timerClass = new TimerClass();
    private static FRTSharePrefUtil frtSharePrefUtil1;
    private static Context context1;
    private static String msg;

    private TimerClass() {
    }

    public static TimerClass getInstance(FRTSharePrefUtil frtSharePrefUtil, Context ctx, String message) {
        frtSharePrefUtil1 = frtSharePrefUtil;
        context1 = ctx;
        msg = message;
        return timerClass;
    }

    @Override
    public void run() {

        //String phoneNo = frtSharePrefUtil1.getString(context1.getString(R.string.manager_phone));
        String phoneNo = null;
        try {
            phoneNo = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, android.util.Base64.decode(frtSharePrefUtil1.getString(context1.getString(R.string.manager_phone)).getBytes("UTF-16LE"), android.util.Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, msg, null, null);
    }
}
