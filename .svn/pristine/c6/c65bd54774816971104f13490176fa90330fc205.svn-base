package com.vodafone.frt.v2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vodafone.frt.R;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.v2.db.LocationEntity;

import java.util.List;

/**
 * Created by SM-002 on 01-02-2018.
 */

public class SyncService extends Service {
    private FRTApp frtApp;
    private FRTConstants frtConstants;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        frtApp = (FRTApp) getApplication();
        frtConstants = new FRTConstants();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        long dateTime = FRTSharePrefUtil.getInstance(frtApp).getLong(getString(R.string.location_time_key));
//                        List<LocationEntity> locationEntities = frtApp.getDbService().daoAccess().fetchData(dateTime);
                        sleep(5000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(frtConstants.RESTART_SERVICE_KEY);
        sendBroadcast(intent);
    }
}
