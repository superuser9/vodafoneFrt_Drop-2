package com.vodafone.frt.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.vodafone.frt.R;
import com.vodafone.frt.webview.WebViewHelpDesk;

public class HelpDeskActivity extends AppCompatActivity {

    private WebView webView = null;
    private LinearLayout ivbackCheckIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_desk);
        getSupportActionBar().hide();
        this.webView = findViewById(R.id.helpDeskWebView);
        ivbackCheckIn = findViewById(R.id.ivbackCheckIn);
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
        webView.loadUrl("file:///android_asset/html/helpdesk.html");
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
