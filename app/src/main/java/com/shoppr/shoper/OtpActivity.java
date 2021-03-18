package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    Button btnsubmit;
    EditText editusername;
    SessonManager sessonManager;
    String type,mobile;

    /*Todo:- Firebase Authentication*/
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    //firebase auth object
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sessonManager = new SessonManager(OtpActivity.this);
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);
        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);
        type=getIntent().getStringExtra("type");
        mobile=getIntent().getStringExtra("mobile");
        sendVerificationCode(mobile);

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
                            OtpVerifyModel otpVerifyModel=response.body();
                            String userId=otpVerifyModel.getUser_id();
                            String sendbird_token=otpVerifyModel.getSendbird_token();
                            String savedAppId = PrefUtils.getAppId(OtpActivity.this);
                            if((!editusername.getText().toString().isEmpty())){
                                sessonManager.setToken(response.body().getToken());
                                if (((BaseApplication)getApplication()).initSendBirdCall(savedAppId)) {
                                    AuthenticationUtils.authenticate(OtpActivity.this, userId, sendbird_token, isSuccess -> {
                                        if (isSuccess) {
                                            setResult(RESULT_OK, null);
                                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(OtpActivity.this, MapsActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            finish();
                                        }
                                    });
                                }
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
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+mobile,
                60,
                TimeUnit.SECONDS,
                OtpActivity.this,
                mCallBack);

    }


    //the callback to detect the verification status
    PhoneAuthProvider.OnVerificationStateChangedCallbacks  mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editusername.setText(code);
                //verifying the code
                //verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };
}