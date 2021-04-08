package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.call.CallService;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;

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
    String mVerificationId;
    //firebase auth object
    FirebaseAuth mAuth;
    Progressbar progressbar;
    String newToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sessonManager = new SessonManager(OtpActivity.this);
        progressbar=new Progressbar();
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);
        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);
        type=getIntent().getStringExtra("type");
        mobile=getIntent().getStringExtra("mobile");
        //sendVerificationCode(mobile);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            //Log.e("newToken", newToken);
            //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });

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
                    //verifyOtpCode(editusername.getText().toString());
                    OtpVerifyAPI();
                }

            }
        });
        
    }

    private void OtpVerifyAPI() {
        progressbar.showProgress(OtpActivity.this);
        if (CommonUtils.isOnline(OtpActivity.this)) {
            OtpVerifyRequest otpVerifyRequest=new OtpVerifyRequest();
            otpVerifyRequest.setOtp(editusername.getText().toString());
            otpVerifyRequest.setMobile(mobile);
            otpVerifyRequest.setType(type);
            if (sessonManager.getNotificationToken()!=null){
                otpVerifyRequest.setNotification_token(sessonManager.getNotificationToken());
                Log.d("notification",sessonManager.getNotificationToken());
            }else if (newToken!=null){
                otpVerifyRequest.setNotification_token(newToken);
            }

            Call<OtpVerifyModel> call= ApiExecutor.getApiService(OtpActivity.this)
                    .otpService(otpVerifyRequest);
            call.enqueue(new Callback<OtpVerifyModel>() {
                @Override
                public void onResponse(Call<OtpVerifyModel> call, Response<OtpVerifyModel> response) {
                    progressbar.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Log.d("ressOtp",response.body().getStatus());
                            OtpVerifyModel otpVerifyModel=response.body();
                            String userId=otpVerifyModel.getUser_id();
                            String sendbird_token=otpVerifyModel.getSendbird_token();
                            String  savedAppId=BaseApplication.APP_ID;
                            //String savedAppId = PrefUtils.getAppId(OtpActivity.this);
                            if((!editusername.getText().toString().isEmpty())){
                                sessonManager.setToken(response.body().getToken());

                                Log.d("savedid+++",savedAppId);
                                //Log.d("savedid+++", String.valueOf(((BaseApplication)getApplication()).initSendBirdCall(savedAppId)));
                                if (!TextUtils.isEmpty(savedAppId) && !TextUtils.isEmpty(userId) &&((BaseApplication)getApplication()).initSendBirdCall(savedAppId)) {
                                    AuthenticationUtils.authenticate(OtpActivity.this, userId, sendbird_token, isSuccess -> {
                                        if (isSuccess) {
                                            setResult(RESULT_OK, null);
                                            sessonManager.getNotificationToken();
                                            Toast.makeText(OtpActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                            CallService.dial(OtpActivity.this, "lakshmikant", false);

                                            //CallService.stopService(getApplicationContext());

                                           /* Intent intent = new Intent(OtpActivity.this, MapsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();*/

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
                    progressbar.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(OtpActivity.this, getString(R.string.please_check_network));
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+91"+mobile)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String sms=phoneAuthCredential.getSmsCode();
                        if (sms!=null){
                            verifyOtpCode(sms);
                            editusername.setText(sms);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        mVerificationId=s;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtpCode(String sms) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,sms);
        singInProccess(credential);
    }

    private void singInProccess(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user=mAuth.getCurrentUser();
                Toast.makeText(OtpActivity.this, "Successfull!!!"+"\n"+"user Id: "+user.getUid(), Toast.LENGTH_SHORT).show();


            }
        });
    }


}