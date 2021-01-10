package com.shoppr.shoper.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.shoppr.shoper.R;


public class SessonManager {

    private static SessonManager pref;
    private final SharedPreferences sharedPreference;
    private final SharedPreferences.Editor editor;
    public static final String NAME = "MY_PREFERENCES";
    public static final String Token = "token";

    public static final String filterList = "FilterList";
    public static final  String lat="lat";
    public static final String lon="lon";
    public static final String walletAmount="walletAmount";
    public Dialog mDialog;
    public SessonManager(Context ctx) {
        sharedPreference = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        editor.apply();
    }


    public static SessonManager getInstance(Context ctx) {
        if (pref == null) {
            pref = new SessonManager(ctx);
        }
        return pref;
    }

    public void setLat(String lat1) {
        editor.putString(lat, lat1);
        editor.commit();
    }

    public void setLon(String lon1) {
        editor.putString(lon, lon1);
        editor.commit();
    }

    public  String getLat() {
        return sharedPreference.getString(lat,"");
    }

    public  String getLon() {
        return sharedPreference.getString(lon,"");
    }

    public void setToken(String token) {
     //   Log.d("sssss", token);
        editor.putString(Token, token);
        editor.commit();
    }

    public String getToken() {
        return sharedPreference.getString(Token, "");
    }

    public void setWalletAmount(String walletAmount1){
        editor.putString(walletAmount,walletAmount1);
        editor.apply();
    }
    public String getWalletAmount(){
        return sharedPreference.getString(walletAmount,"");
    }
    public void setFilterList(String filterList1) {

     //   Log.d("sssss", token);
        editor.putString(filterList, filterList1);
        editor.commit();
    }

    public String getFilterList() {
        return sharedPreference.getString(filterList, "");
    }


    public void hideProgress() {
        while (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();

        }

    }


    public void showProgress(Context mContext) {
        if(mContext!=null){
            mDialog= new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.custom_progress_layout);
            mDialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

    }
}
