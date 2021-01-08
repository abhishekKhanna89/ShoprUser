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
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    public static final String NAME = "MY_PREFERENCES";
    public static final String Token = "token";
    public Dialog mDialog;
    public static  String filterList = "FilterList";
    public static final  String lat="lat";
    public static final String lon="lon";


    public SessonManager(Context ctx) {
        sharedPreference = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }


    public static SessonManager getInstance(Context ctx) {
        if (pref == null) {
            pref = new SessonManager(ctx);
        }
        return pref;
    }

    public void setLat(String lat) {
        editor.putString(lat, lat);
        editor.commit();
    }

    public void setLon(String lon) {
        editor.putString(lon, lon);
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
