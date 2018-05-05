package com.vodafone.frt.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by vishal on 6/12/17
 */

public class FRTCustomAutoCompleteTextView extends AutoCompleteTextView {
    public FRTCustomAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public FRTCustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FRTCustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
