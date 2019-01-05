package com.serviceonwheel.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyBoldEditText extends android.support.v7.widget.AppCompatEditText {

    public MyBoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBoldEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/CALIBRIB_2.TTF");
            setTypeface(tf);
        }
    }
}