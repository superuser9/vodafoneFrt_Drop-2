package com.vodafone.frt.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Ajay Tiwari on 2/28/2018.
 */

public class WebViewHelpDesk extends WebViewClient {

    private Activity activity = null;

    public WebViewHelpDesk(Activity activity) {
        this.activity = activity;
    }


    /*@Override
    public boolean shouldOverrideUrlLoading(WebView view, String request) {

        if(request.indexOf("google.com") > -1 ) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request));
        activity.startActivity(intent);
        return true;
    }*/


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String request) {

        view.loadUrl(request);
        return true;
    }


}
