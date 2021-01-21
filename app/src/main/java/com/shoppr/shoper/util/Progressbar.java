package com.shoppr.shoper.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.shoppr.shoper.R;


public class Progressbar {
    public Dialog mDialog;


    public void showProgress(Context mContext) {
        if (mContext != null) {
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.custom_progress_layout1);
            mDialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

    }

    public void hideProgress() {
        while (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();

        }
    }
}
