package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by vishal on 22/11/17
 */

public class FRTEditTextTrebuchetMS extends EditText {


    public FRTEditTextTrebuchetMS(Context context) {
        super(context);
    }

    public FRTEditTextTrebuchetMS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FRTEditTextTrebuchetMS(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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