package com.shoppr.shoper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    Button btnsubmit;
    EditText editusername;
    SessonManager sessonManager;
    String type,mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sessonManager = new SessonManager(OtpActivity.this);
        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);
        type=getIntent().getStringExtra("type");
        mobile=getIntent().getStringExtra("mobile");
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editusername.getText().toString().isEmpty()){
                    editusername.setError("OTP Field Can't be blank");
                    editusername.requestFocus();
                }
                else if(editusername.getText().toString().length()!=6){
                    editusername.setError("OTP should be 6 digit");
                    editusername.requestFocus();
                }
                else {
                    OtpVerifyAPI();
                }
               /* Intent i = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(i);*/
            }
        });
    }

    private void OtpVerifyAPI() {
        if (CommonUtils.isOnline(OtpActivity.this)) {
            sessonManager.showProgress(OtpActivity.this);
            OtpVerifyRequest otpVerifyRequest=new OtpVerifyRequest();
            otpVerifyRequest.setOtp(editusername.getText().toString());
            otpVerifyRequest.setMobile(mobile);
            otpVerifyRequest.setType(type);
            otpVerifyRequest.setNotification_token(sessonManager.getNotificationToken());
            Call<OtpVerifyModel> call= ApiExecutor.getApiService(OtpActivity.this)
                    .otpService(otpVerifyRequest);
            call.enqueue(new Callback<OtpVerifyModel>() {
                @Override
                public void onResponse(Call<OtpVerifyModel> call, Response<OtpVerifyModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editusername.getText().toString().isEmpty())){
                                sessonManager.setToken(response.body().getToken());
                                //Log.d("token",response.body().getToken());
                                startActivity(new Intent(OtpActivity.this,MapsActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        }else {
                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpVerifyModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(OtpActivity.this, getString(R.string.please_check_network));
        }
    }
}