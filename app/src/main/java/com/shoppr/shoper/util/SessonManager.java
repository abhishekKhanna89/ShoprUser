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
    public static final String mobileNo="mobile";
    public static final String chanelId="CHANEL_ID";
    public static final String NOTIFICATION_TOKEN="notification_token";
    
    public static final String driver_latitude="latitude";
    public  static final String driver_longitude="longitude";


    public static final String editaddress="editaddress";
    public static final String CHAT_ID="chat_id";

    public static final String strList="sList";



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

    public void setEditaddress(String editaddress1){
        editor.putString(editaddress,editaddress1);
        editor.apply();
    }
    public String getEditaddress(){
        return sharedPreference.getString(editaddress,"");
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

    public void setMobileNo(String mobile){
        editor.putString(mobileNo,mobile);
        editor.apply();
    }
    public String getMobileNo(){
        return sharedPreference.getString(mobileNo,"");
    }

    public void setToken(String token) {
     //   Log.d("sssss", token);
        editor.putString(Token, token);
        editor.commit();
    }

    public String getToken() {
        return sharedPreference.getString(Token, "");
    }
    public void setNotificationToken(String notificationToken){
        editor.putString(NOTIFICATION_TOKEN,notificationToken);
        editor.apply();
    }
    public String getNotificationToken(){
        return sharedPreference.getString(NOTIFICATION_TOKEN,"");
    }
    public void setChanelId(String chanelId1){
        editor.putString(chanelId,chanelId1);
        editor.apply();
    }
    public String getChanelId(){
        return sharedPreference.getString(chanelId,"");
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


    public void setDriver_latitude(String Latitude){
        editor.putString(driver_latitude,Latitude);
        editor.apply();
    }
    public void setDriver_longitude(String Longitude){
        editor.putString(driver_longitude,Longitude);
        editor.apply();
    }
    
    public  String getDriver_latitude(){
        return sharedPreference.getString(driver_latitude,"");
    }
    public String getDriver_longitude(){
        return sharedPreference.getString(driver_longitude,"");
    }

    public void setChatId(String chatId){
        editor.putString(CHAT_ID,chatId);
        editor.apply();
    }

    public String getChatId(){
        return sharedPreference.getString(CHAT_ID,"");
    }

    public void setStringList(String stringList){
        editor.putString(strList,stringList);
        editor.apply();
    }

    public String getStrList(){
        return sharedPreference.getString(strList,"");
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
