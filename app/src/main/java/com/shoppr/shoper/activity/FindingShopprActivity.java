package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppr.shoper.Model.AutoAssign.AutoAssignModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.skyfishjy.library.RippleBackground;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_shoppr);
        sessonManager = new SessonManager(this);
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
        new CountDownTimer(6000,2000) {
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

    private void viewStartChat1(int shopId) {
        if (CommonUtils.isOnline(FindingShopprActivity.this)) {
            //sessonManager.showProgress(FindingShopprActivity.this);
            Call<StartChatModel> call = ApiExecutor.getApiService(this)
                    .apiChatStart("Bearer " + sessonManager.getToken(),shopId);
            call.enqueue(new Callback<StartChatModel>() {
                @Override
                public void onResponse(Call<StartChatModel> call, Response<StartChatModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StartChatModel startChatModel = response.body();
                            if (startChatModel.getData() != null) {
                                chat_id = startChatModel.getData().getId();


                                Log.d("chatid",""+chat_id);
                                String aa=String.valueOf(chat_id);
                                // chahanges by lk
                                //sessonManager.setChatId(aa);
                               // autoAssign(chat_id);


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
    }
    private void autoAssign(int chat_id) {
        if (CommonUtils.isOnline(FindingShopprActivity.this)) {
            //sessonManager.showProgress(FindingShopprActivity.this);
            Call<AutoAssignModel>call=ApiExecutor.getApiService(this)
                    .apiAutoAssign("Bearer " + sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<AutoAssignModel>() {
                @Override
                public void onResponse(Call<AutoAssignModel> call, Response<AutoAssignModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            AutoAssignModel autoAssignModel=response.body();
                            if (autoAssignModel!=null){
                                Toast.makeText(FindingShopprActivity.this, ""+autoAssignModel.getMessage(), Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(FindingShopprActivity.this, ChatActivity.class)
                                       .putExtra("chat_status","2").putExtra("findingchatid", chat_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                               finish();

                            }else {
                                Toast.makeText(FindingShopprActivity.this, ""+autoAssignModel.getMessage(), Toast.LENGTH_SHORT).show();
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
    }


}