package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by vishal on 22/11/17
 */

public class FRTRadioButtonTrebuchetMS extends RadioButton {
    public FRTRadioButtonTrebuchetMS(Context context) {
        super(context);
    }

    public FRTRadioButtonTrebuchetMS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FRTRadioButtonTrebuchetMS(Context context, AttributeSet attrs, int defStyleAttr) {
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
