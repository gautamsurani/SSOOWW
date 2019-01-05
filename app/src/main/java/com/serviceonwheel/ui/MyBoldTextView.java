package com.serviceonwheel.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MyBoldTextView extends TextView {

    public MyBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/CALIBRIB_2.TTF");
            setTypeface(tf);
        }
    }
}
