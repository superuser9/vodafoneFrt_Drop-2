package com.vodafone.frt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.vodafone.frt.R;
import com.vodafone.frt.webview.WebViewHelpDesk;

public class PTRFAQActivity extends AppCompatActivity {

    private WebView webView = null;
    private LinearLayout ivbackCheckIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptrfaq);

        getSupportActionBar().hide();
        this.webView = (WebView) findViewById(R.id.helpDeskWebView);
        ivbackCheckIn =(LinearLayout)findViewById(R.id.ivbackCheckIn);
        ivbackCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewHelpDesk webViewClient = new WebViewHelpDesk(this);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl("file:///android_asset/html/faq.html");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
