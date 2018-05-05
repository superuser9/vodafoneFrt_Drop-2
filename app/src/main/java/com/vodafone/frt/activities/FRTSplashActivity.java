package com.vodafone.frt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.vodafone.frt.R;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

/**
 * Created by vishal
 */
public class FRTSplashActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTBroadcasting.CallBackBroadcast {
    private final FRTSplashActivity frtsplashActivity = this;
    private FRTSharePrefUtil frtSharePrefUtil;
    FRTConstants frtConstants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        callbackSetUp();
    }

    private void callbackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtsplashActivity;
        frtCallBackInitViews.initAllViews();
    }


    @Override
    public void initAllViews() {
        FRTBroadcasting broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtsplashActivity);
        broadcasting.setCallbackBroadcast(frtsplashActivity);
        FRTUtility frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtsplashActivity);
        frtUtility.settingStatusBarColor(frtsplashActivity, android.R.color.white);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtsplashActivity);
        if (!TextUtils.isEmpty(frtSharePrefUtil.getString(getString(R.string.langstring))))
            frtUtility.setLocale(frtsplashActivity, frtSharePrefUtil.getString(getString(R.string.langstring)), frtsplashActivity, false);
        goToNextScreen();
    }

    private void goToNextScreen() {
        final FRTConstants cqConstants = new FRTConstants();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (frtSharePrefUtil.getBoolean(cqConstants.IS_LOGGED_IN)) {
                    Log.d(FRTSplashActivity.class.getSimpleName(), "NavigationScreenActivity");
                    Intent intent = new Intent(frtsplashActivity, PTRNavigationScreenActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                } else {
                    Log.d(FRTSplashActivity.class.getSimpleName(), "FRTLoginActivity");
                    Intent intent = new Intent(frtsplashActivity, FRTLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }
            }
        }, cqConstants.KEY_SPLASH_TIME);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        goToNextScreen();
    }

    @Override
    public void getBroadcast(boolean isBroadcasting) {
    }
}
