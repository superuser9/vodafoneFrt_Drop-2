package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vishal on 20/11/17
 */

public class FRTTextviewTrebuchetMS extends TextView {

    public FRTTextviewTrebuchetMS(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FRTTextviewTrebuchetMS(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FRTTextviewTrebuchetMS(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!this.isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Trebuchet MS.ttf");
            setTypeface(tf, Typeface.NORMAL);
        }
    }
}