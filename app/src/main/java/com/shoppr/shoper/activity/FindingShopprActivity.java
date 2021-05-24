package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.AutoAssign.AutoAssignModel;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindingShopprActivity extends AppCompatActivity {
    RippleBackground rippleBackground;
    ImageView centerImage;
    //ProgressBar mProgressBar;
    //private TextView textViewShowTime; // will show the time
    CountDownTimer countDownTimer; // built in android class
    // CountDownTimer
    private long totalTimeCountInMilliseconds; // total count down time in
    // milliseconds
    private long timeBlinkInMilliseconds; // start time of start blinking
    private boolean blink; // controls the blinking .. on and off
    SessonManager sessonManager;
    int chat_id;
    int shopId;
    String  address,city;
    private PopupWindow pwindo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_shoppr);
        sessonManager = new SessonManager(this);
        address = getIntent().getStringExtra("address");
        city=getIntent().getStringExtra("city");
        //Log.d("ressCity",city);

        rippleBackground=(RippleBackground)findViewById(R.id.content);
        centerImage=findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        //shopId = getIntent().getIntExtra("shopId", 0);
        //mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        //textViewShowTime = (TextView) findViewById(R.id.tvTimeCount);
        shopId=getIntent().getIntExtra("shopId",0);
        viewStartChat1(shopId);
        setTimer();



    }

    private void setTimer() {
        //int time = 1;
       // totalTimeCountInMilliseconds = 60  * 1000;

       // timeBlinkInMilliseconds = 120 * 1000;
        startTimer();
    }

    private void startTimer() {
        new CountDownTimer(12000,2000) {
            @Override
            public void onTick(long millisUntilFinished) {
               // counttime.setText(String.valueOf(counter));
               // counter++;
            }
            @Override
            public void onFinish() {

                autoAssign(chat_id);
              //  counttime.setText("Finished");
            }
        }.start();
    }

    private void viewStartChat1(int shop_id) {
       String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+sessonManager.getLat()+","+sessonManager.getLon()+"&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";
        //String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + address + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditLocationResponse", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);
                    JSONArray jsonArray1=jsonObject1.getJSONArray("address_components");
                    String location = jsonArray1.toString();
                        if (CommonUtils.isOnline(FindingShopprActivity.this)) {
                            //sessonManager.showProgress(FindingShopprActivity.this);
                            String a=sessonManager.getLat();
                            String b=sessonManager.getLon();
                            Log.d("ssss",a+b);
                            Call<StartChatModel> call = ApiExecutor.getApiService(FindingShopprActivity.this)
                                    .apiChatStart("Bearer " + sessonManager.getToken(),shop_id,sessonManager.getLat(),sessonManager.getLon(),location,city);
                            call.enqueue(new Callback<StartChatModel>() {
                                @Override
                                public void onResponse(Call<StartChatModel> call, Response<StartChatModel> response) {
                                    //sessonManager.hideProgress();
                                    if (response.body() != null) {
                                        StartChatModel startChatModel = response.body();
                                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                            if (startChatModel.getData() != null) {
                                                chat_id = startChatModel.getData().getId();
                                                Log.d("chatid",""+chat_id);
                                                String aa=String.valueOf(chat_id);
                                                // chahanges by lk
                                                //sessonManager.setChatId(aa);
                                                // autoAssign(chat_id);

                                            }
                                        }else {
                                            Toast.makeText(FindingShopprActivity.this, ""+startChatModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                                    Call<LogoutModel>call1=ApiExecutor.getApiService(FindingShopprActivity.this)
                                                            .apiLogoutStatus("Bearer "+sessonManager.getToken());
                                                    call1.enqueue(new Callback<LogoutModel>() {
                                                        @Override
                                                        public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                                            if (response.body()!=null) {
                                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                                    AuthenticationUtils.deauthenticate(FindingShopprActivity.this, isSuccess -> {
                                                                        if (getApplication() != null) {
                                                                            sessonManager.setToken("");
                                                                            PrefUtils.setAppId(FindingShopprActivity.this,"");
                                                                            Toast.makeText(FindingShopprActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(FindingShopprActivity.this, LoginActivity.class));
                                                                            finishAffinity();

                                                                        }else {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<LogoutModel> call, Throwable t) {

                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<StartChatModel> call, Throwable t) {
                                    //sessonManager.hideProgress();
                                }
                            });
                        } else {
                            CommonUtils.showToastInCenter(FindingShopprActivity.this, getString(R.string.please_check_network));
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);




    }
    private void autoAssign(int chat_id) {
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+sessonManager.getLat()+","+sessonManager.getLon()+"&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";
        //String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + address + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditLocationResponse", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.d("resJsonAll",""+jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    Log.d("resJsonAll",""+jsonArray);
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);
                    JSONArray jsonArray1=jsonObject1.getJSONArray("address_components");
                    String location = jsonArray1.toString();
                        if (CommonUtils.isOnline(FindingShopprActivity.this)) {
                            //sessonManager.showProgress(FindingShopprActivity.this);
                            Call<AutoAssignModel>call=ApiExecutor.getApiService(FindingShopprActivity.this)
                                    .apiAutoAssign("Bearer " + sessonManager.getToken(),chat_id,location,city);
                            call.enqueue(new Callback<AutoAssignModel>() {
                                @Override
                                public void onResponse(Call<AutoAssignModel> call, Response<AutoAssignModel> response) {
                                    //Toast.makeText(FindingShopprActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                    if (response.body() != null) {
                                        AutoAssignModel autoAssignModel=response.body();
                                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                            if (autoAssignModel!=null) {
                                                Toast.makeText(FindingShopprActivity.this, "" + autoAssignModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(FindingShopprActivity.this, ChatActivity.class)
                                                        .putExtra("chat_status", "2").putExtra("findingchatid", chat_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                            }else {
                                                Toast.makeText(FindingShopprActivity.this, "" + autoAssignModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(FindingShopprActivity.this, ""+autoAssignModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            Dialog d=new Dialog(FindingShopprActivity.this);
                                            d.setContentView(R.layout.your_layout_screen);
                                            d.setCanceledOnTouchOutside(false);
                                            d.setCancelable(false);
                                            Button btnHome=d.findViewById(R.id.btnHome);
                                            btnHome.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(FindingShopprActivity.this,MapsActivity.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    d.dismiss();
                                                }
                                            });
                                            TextView errorText=d.findViewById(R.id.errorText);
                                            errorText.setText(response.body().getMessage());
                                            d.show();
                                            Log.d("failedResponse",response.body().getMessage());
                                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                                    sessonManager.setToken("");
                                                    PrefUtils.setAppId(FindingShopprActivity.this, "");
                                                    startActivity(new Intent(FindingShopprActivity.this, LoginActivity.class));
                                                    finishAffinity();
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<AutoAssignModel> call, Throwable t) {

                                }
                            });
                        } else {
                            CommonUtils.showToastInCenter(FindingShopprActivity.this, getString(R.string.please_check_network));
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }


}