package com.shoppr.shoper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.Gson;
import com.shoppr.shoper.Model.OtpVerifyModel;
import com.shoppr.shoper.Model.ResendOtp.ResendOtpModel;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.OtpVerifyRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    Button btnsubmit;
    EditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;
    SessonManager sessonManager;
    String type, mobile, otp="";
    CountDownTimer countDownTimer;
    TextView txtTimerCount, txtResendOtp, txtOtpPhone;

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
        setContentView(R.layout.activity_otp1);
        sessonManager = new SessonManager(OtpActivity.this);
        progressbar = new Progressbar();
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);
        btnsubmit = findViewById(R.id.btnsubmit);
        txtTimerCount = findViewById(R.id.txtTimerCount);
        txtResendOtp = findViewById(R.id.txtResendOtp);
        txtOtpPhone = findViewById(R.id.txtOtpPhone);
        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        etCode5 = findViewById(R.id.etCode5);
        etCode6 = findViewById(R.id.etCode6);

        type = getIntent().getStringExtra("type");
        mobile = getIntent().getStringExtra("mobile");
        txtOtpPhone.setText("Please check "+mobile+" SMS! Provide us 6-digit verification code");
        //sendVerificationCode(mobile);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            //Log.e("newToken", newToken);
            //getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });

        etCode1.addTextChangedListener(new GenericTextWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new GenericTextWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new GenericTextWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new GenericTextWatcher(etCode4, etCode5));
        etCode5.addTextChangedListener(new GenericTextWatcher(etCode5, etCode6));
        etCode6.addTextChangedListener(new GenericTextWatcher(etCode6, null));

        etCode1.setOnKeyListener(new GenericKeyEvent(etCode1, null));
        etCode2.setOnKeyListener(new GenericKeyEvent(etCode2, etCode1));
        etCode3.setOnKeyListener(new GenericKeyEvent(etCode3, etCode2));
        etCode4.setOnKeyListener(new GenericKeyEvent(etCode4, etCode3));
        etCode5.setOnKeyListener(new GenericKeyEvent(etCode5, etCode4));
        etCode6.setOnKeyListener(new GenericKeyEvent(etCode6, etCode5));

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    OtpVerifyAPI();
                }
            }
        });

        startTimer();
    }

    private boolean isValid() {
        EditText[] editTextArray = {etCode1, etCode2, etCode3, etCode4, etCode5, etCode6};
        for (EditText etcode : editTextArray) {
            if (etcode.getText().equals("")) {
                etcode.setError("OTP Field Can't be blank");
                return false;
            } else {
                otp += etcode.getText().toString();
                Log.e("OTP", "OTP IS : " + otp);
            }
        }
        return true;
    }

    private void OtpVerifyAPI() {
        progressbar.showProgress(OtpActivity.this);
        if (CommonUtils.isOnline(OtpActivity.this)) {
            OtpVerifyRequest otpVerifyRequest = new OtpVerifyRequest();
            otpVerifyRequest.setOtp(otp);
            otpVerifyRequest.setMobile(mobile);
            otpVerifyRequest.setType(type);
            if (sessonManager.getNotificationToken() != null) {
                otpVerifyRequest.setNotification_token(sessonManager.getNotificationToken());
                Log.d("notification", sessonManager.getNotificationToken());
            } else if (newToken != null) {
                otpVerifyRequest.setNotification_token(newToken);
            }

            Call<OtpVerifyModel> call = ApiExecutor.getApiService(OtpActivity.this)
                    .otpService(otpVerifyRequest);
            call.enqueue(new Callback<OtpVerifyModel>() {
                @Override
                public void onResponse(Call<OtpVerifyModel> call, Response<OtpVerifyModel> response) {
                    progressbar.hideProgress();

                    Log.d("lsdkjfksdgf", new Gson().toJson(response.body()));

                    if (response.body() != null) {
                        OtpVerifyModel otpVerifyModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            Log.d("ressOtp", response.body().getStatus());
                            String userId = otpVerifyModel.getUser_id();
                            String sendbird_token = otpVerifyModel.getSendbird_token();
                            String savedAppId = BaseApplication.APP_ID;
                            //String savedAppId = PrefUtils.getAppId(OtpActivity.this);
                            if ((!otp.isEmpty())) {
                                Log.d("dszhfjdsvv", otp);
                                sessonManager.setToken(response.body().getToken());
                                if (((BaseApplication) getApplication()).initSendBirdCall(savedAppId)) {
                                    AuthenticationUtils.authenticate(OtpActivity.this, userId, sendbird_token, isSuccess -> {
                                        if (isSuccess) {
                                            Log.d("djjjgkdfbnjb", "goToNextActivity");
                                            setResult(RESULT_OK, null);
                                            Toast.makeText(OtpActivity.this, otpVerifyModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OtpActivity.this, MapsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                } else {
                                    Toast.makeText(OtpActivity.this, otpVerifyModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(OtpActivity.this, "" + otpVerifyModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpVerifyModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        } else {
            CommonUtils.showToastInCenter(OtpActivity.this, getString(R.string.please_check_network));
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+91" + mobile)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String sms = phoneAuthCredential.getSmsCode();
                        if (sms != null) {
                            verifyOtpCode(sms);
                            etCode1.setText(sms);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        mVerificationId = s;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtpCode(String sms) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, sms);
        singInProccess(credential);
    }

    private void singInProccess(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(OtpActivity.this, "Successfull!!!" + "\n" + "user Id: " + user.getUid(), Toast.LENGTH_SHORT).show();


            }
        });
    }


    public void resend(View view) {
        progressbar.showProgress(OtpActivity.this);
        if (CommonUtils.isOnline(OtpActivity.this)) {
            Call<ResendOtpModel> call = ApiExecutor.getApiService(this).apiResendOtp(type, mobile);
            call.enqueue(new Callback<ResendOtpModel>() {
                @Override
                public void onResponse(Call<ResendOtpModel> call, Response<ResendOtpModel> response) {
                    progressbar.hideProgress();
                    ResendOtpModel resendOtpModel = response.body();
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(OtpActivity.this, resendOtpModel.getMessage(), Toast.LENGTH_SHORT).show();
                        txtTimerCount.setVisibility(View.VISIBLE);
                        txtResendOtp.setVisibility(View.GONE);
                        startTimer();
                    } else {
                        Toast.makeText(OtpActivity.this, resendOtpModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResendOtpModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });
        } else {
            CommonUtils.showToastInCenter(OtpActivity.this, getString(R.string.please_check_network));
        }
    }


    class GenericKeyEvent implements View.OnKeyListener {
        EditText currentView, previousView;

        public GenericKeyEvent(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL &&
                    currentView.getId()!=R.id.etCode1&&currentView.getText().toString().isEmpty()){
                previousView.setText(null);
                previousView.requestFocus();
                return true;
            }
                return false;
        }
    }

    class GenericTextWatcher implements TextWatcher {
        EditText currentView, nextView;

        public GenericTextWatcher(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.nextView = previousView;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void afterTextChanged(Editable editable) {
            String text=editable.toString();
            switch (currentView.getId()){
                case R.id.etCode1 : if (text.length() == 1) nextView.requestFocus();
                break;
                case R.id.etCode2 : if (text.length() == 1) nextView.requestFocus();
                    break;
                case R.id.etCode3 : if (text.length() == 1) nextView.requestFocus();
                    break;
                case R.id.etCode4 : if (text.length() == 1) nextView.requestFocus();
                    break;
                case R.id.etCode5 : if (text.length() == 1) nextView.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

    private void startTimer() {
        countDownTimer= new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                //long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000);
                txtTimerCount.setText("Resend OTP in "+ f.format(sec)+" seconds.");
            }

            @Override
            public void onFinish() {
                txtTimerCount.setVisibility(View.GONE);
                txtResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}