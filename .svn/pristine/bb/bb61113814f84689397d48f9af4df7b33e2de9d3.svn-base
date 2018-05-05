package com.vodafone.frt.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.vodafone.frt.R;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.network.FRTConnectivityReceiver;
import com.vodafone.frt.utility.FRTBroadcasting;
import com.vodafone.frt.utility.FRTUtility;

/**
 * Created by vishal
 */
public class PTRChatActivity extends AppCompatActivity implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners
        , FRTBroadcasting.CallBackBroadcast {
    private BroadcastReceiver broadcastReceiver;
    private final PTRChatActivity frtChatActivity = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTBroadcasting broadcasting;
    private FRTConnectivityReceiver frtConnectivityReceiver;
    private FRTUtility frtUtility;
    private LinearLayout ivback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
        setContentView(R.layout.activity_frtchat);
        callBackSetUp();
    }

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtChatActivity;
        frtCallBackForIdFind = frtChatActivity;
        FRTCallBackSetListeners frtCallBackSetListeners = frtChatActivity;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public View view(int id) {
        return findViewById(id);
    }

    @Override
    public void initAllViews() {
        ivback = (LinearLayout) frtCallBackForIdFind.view(R.id.ivback);
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtChatActivity);
        frtUtility.settingStatusBarColor(frtChatActivity, R.color.colorPrimary);
        frtConnectivityReceiver = new FRTConnectivityReceiver();
        broadcasting = FRTBroadcasting.getBroadcastingInstance();
        broadcasting.setContext(frtChatActivity);
        broadcasting.setCallbackBroadcast(frtChatActivity);
    }

    /**
     * This method is used for setting the listener on views.
     */
    @Override
    public void commonListeners() {
        ivback.setOnClickListener(frtBackClick);
    }

    private final View.OnClickListener frtBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        if (frtConnectivityReceiver.isConnected(frtChatActivity)) {
            super.onBackPressed();
            Intent intent = new Intent(frtChatActivity, PTRNavigationScreenActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
        } else {
            frtUtility.setSnackBar(getString(R.string.nointernet), findViewById(R.id.header));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = broadcasting.getBroadcasting();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void getBroadcast(boolean isBroadcasting) {
        if (isBroadcasting) {
            frtUtility.setSnackBar(getString(R.string.nointernet), ivback);
            frtUtility.hideDialog();
        }
    }
}
