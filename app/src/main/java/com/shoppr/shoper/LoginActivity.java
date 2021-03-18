package com.shoppr.shoper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.LoginRequest;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;


import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btnsubmit;
    TextView textregister;
    EditText editusername;
    SessonManager sessonManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(LoginActivity.this);


        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);


        textregister=findViewById(R.id.textregister);



        textregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editusername.getText().toString().isEmpty()){
                    editusername.setError("Mobile Field Can't be blank");
                    editusername.requestFocus();
                }
                else if(editusername.getText().toString().length()!=10){
                    editusername.setError("Mobile No. should be 10 digit");
                    editusername.requestFocus();
                }
                else {
                    MobileEmailAPI();
                }

                //startActivity(new Intent(LoginActivity.this,MapsActivity.class));
            }
        });
    }







    private void MobileEmailAPI() {
        if (CommonUtils.isOnline(LoginActivity.this)) {
            sessonManager.showProgress(LoginActivity.this);
            LoginRequest loginRequest=new LoginRequest();
            loginRequest.setMobile(editusername.getText().toString());
            Call<LoginModel>call=ApiExecutor.getApiService(LoginActivity.this)
                    .loginUser(loginRequest);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editusername.getText().toString().isEmpty())){
                                startActivity(new Intent(LoginActivity.this,OtpActivity.class)
                                        .putExtra("type","login")
                                        .putExtra("mobile",editusername.getText().toString())
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }else {
                                sessonManager.getToken();
                                PrefUtils.getAppId(LoginActivity.this);
                                //sessonManager.setToken(response.body().getToken());
                                startActivity(new Intent(LoginActivity.this,MapsActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }
                        }else {
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(LoginActivity.this, getString(R.string.please_check_network));
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
