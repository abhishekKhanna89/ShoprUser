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
    CallbackManager callbackManager;
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    EditText editusername;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(LoginActivity.this);
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);


        textregister=findViewById(R.id.textregister);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


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

/*
    private void hitLoginApi() {
        if (CommonUtils.isOnline(LoginActivity.this)) {
            sessonManager.showProgress(LoginActivity.this);

        }
    }
*/





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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }*/
    }
  /*  private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            gotoProfile();
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }
    private void gotoProfile(){
        Intent intent=new Intent(LoginActivity.this,ProfileActivity.class);
        startActivity(intent);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
