package com.vodafone.frt.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vodafone.frt.R;
import com.vodafone.frt.preferences.FRTSharePrefUtil;

public class OthersActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout helpDeskRelativeLayout,loyalityPointsRelativeLayout,
            bulletinRelativeLayout,supportRelativeLayout,feedbackRelativeLayout,faqRelativeLayout;
    private LinearLayout ivbackCheckIn;
    private FRTSharePrefUtil frtSharePrefUtil;
    private String userIdEncripted;
    private String roleIdEncripted;
    private String roleNameEncripted;
    private String roleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        getSupportActionBar().hide();

        try {
            frtSharePrefUtil = FRTSharePrefUtil.getInstance(getApplicationContext());
            roleName =frtSharePrefUtil.getString(getString(R.string.role_name));

          //roleNameEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA, Base64.decode(frtSharePrefUtil.getString(getString(R.string.role_name)).getBytes("UTF-16LE"), Base64.DEFAULT));
          /* roleIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                   Base64.decode(frtSharePrefUtil.getString(getString(R.string.role_id)).getBytes("UTF-16LE"), Base64.DEFAULT));
           userIdEncripted  = AESEncriptDecript.decrypt(AESEncriptDecript.KEY_SHA,
                   Base64.decode(frtSharePrefUtil.getString(getString(R.string.userkey)).getBytes("UTF-16LE"), Base64.DEFAULT));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        clickListner();
    }



    private void initViews() {
        ivbackCheckIn =(LinearLayout)findViewById(R.id.ivbackCheckIn);
        helpDeskRelativeLayout = (RelativeLayout)findViewById(R.id.helpDeskRelativeLayout);
        loyalityPointsRelativeLayout = (RelativeLayout)findViewById(R.id.loyalityPointsRelativeLayout);
        supportRelativeLayout = (RelativeLayout)findViewById(R.id.supportRelativeLayout);
        feedbackRelativeLayout = (RelativeLayout)findViewById(R.id.feedbackRelativeLayout);
        bulletinRelativeLayout =(RelativeLayout)findViewById(R.id.bulletinRelativeLayout);
        faqRelativeLayout =(RelativeLayout)findViewById(R.id.faqRelativeLayout);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(OthersActivity.this);
    }
    private void clickListner() {
        helpDeskRelativeLayout.setOnClickListener(this);
        loyalityPointsRelativeLayout.setOnClickListener(this);
        supportRelativeLayout.setOnClickListener(this);
        feedbackRelativeLayout.setOnClickListener(this);
        bulletinRelativeLayout.setOnClickListener(this);
        ivbackCheckIn.setOnClickListener(this);
        faqRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.helpDeskRelativeLayout:
                startActivity(new Intent(this,HelpDeskActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.loyalityPointsRelativeLayout:
                startActivity(new Intent(this,LoyalityPointsActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.supportRelativeLayout:

                startActivity(new Intent(this,SupportActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.feedbackRelativeLayout:
                if (roleName.equalsIgnoreCase("Manager")){
                    startActivity(new Intent(this,FRTMgrFeedbackActivity.class));
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }else
                startActivity(new Intent(this,PTRFeedbackActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

//                Intent intent = new Intent(OthersActivity.this, PTRFeedbackActivity.class);
//                startActivity(intent);
              // startActivity(new Intent(this,PTRFeedbackActivity.class));
               // overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.bulletinRelativeLayout:
                startActivity(new Intent(this,BulletinBoardActivity.class));
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.faqRelativeLayout:
                startActivity(new Intent(this,PTRFAQActivity.class));
                break;

            case R.id.ivbackCheckIn:
                onBackPressed();
                break;
        }
    }
}
