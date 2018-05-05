package com.vodafone.frt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.vodafone.frt.R;

public class SupportActivity extends AppCompatActivity {

    private LinearLayout ivbackCheckIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        getSupportActionBar().hide();
        ivbackCheckIn =(LinearLayout)findViewById(R.id.ivbackCheckIn);
        ivbackCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
