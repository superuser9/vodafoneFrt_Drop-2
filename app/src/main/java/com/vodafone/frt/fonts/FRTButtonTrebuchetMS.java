package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by vishal on 20/11/17
 */
public class FRTButtonTrebuchetMS extends Button {

    public FRTButtonTrebuchetMS(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FRTButtonTrebuchetMS(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FRTButtonTrebuchetMS(Context context) {
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
