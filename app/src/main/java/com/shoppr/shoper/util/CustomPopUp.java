package com.shoppr.shoper.util;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.shoppr.shoper.R;

public class CustomPopUp {
    public static void showBanner(final Activity context) {
       // final Dialog dialog = new Dialog(context);
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.popup_banner);
        Button close = (Button) d.findViewById(R.id.close);
        ImageView imgBanner = (ImageView) d.findViewById(R.id.imgBanner);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.show();
        d.getWindow().setAttributes(lp);

/*

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.width = DensityUtil.dp2px(context, 200);
        lp.height = DensityUtil.dp2px(context, 200);
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setContentView(R.layout.popup_banner);
        // if button is clicked, close the custom dialog
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Dismissed..!!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
*/


       /* Rect displayRectangle = new Rect();
        Window window = context.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
        dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
        dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Button buttonOk=dialogView.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/
    }
}
