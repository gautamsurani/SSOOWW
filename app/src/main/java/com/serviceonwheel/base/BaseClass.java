package com.serviceonwheel.base;

import android.app.Activity;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.serviceonwheel.R;

public class BaseClass {
    private SimpleArcDialog mDialog;

    public void dismissProgressDialog(Activity context) {
        if (!context.isFinishing()) {
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        }
    }

    public void startProgressDialog(Activity context) {
        if (!context.isFinishing()) {
            if (mDialog == null) {
                int[] color = new int[2];
                color[0] = context.getResources().getColor(R.color.black);
                color[1] = context.getResources().getColor(R.color.black);

                mDialog = new SimpleArcDialog(context);
                ArcConfiguration arcConfiguration = new ArcConfiguration(context);
                arcConfiguration.setColors(color);
                mDialog.setConfiguration(arcConfiguration);
                mDialog.setCancelable(false);
            }
            mDialog.show();
        }
    }
}
