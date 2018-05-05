package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Vishal
 */
public class FrtFaIcon {

    public FrtFaIcon() {
    }

    public Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/fontawesome-webfont.ttf");
    }
}
