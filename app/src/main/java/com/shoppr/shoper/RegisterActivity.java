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
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.RegisterRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Button btnsubmit;
    EditText editmobile,editusername;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sessonManager = new SessonManager(RegisterActivity.this);
        editmobile=findViewById(R.id.editmobile);
        editusername=findViewById(R.id.editusername);
        btnsubmit=findViewById(R.id.btnsubmit);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if(editmobile.getText().toString().isEmpty()){
                            editmobile.setError("Mobile Field Can't be blank");
                            editmobile.requestFocus();
                        }
                        else if(editmobile.getText().toString().length()!=10){
                            editmobile.setError("Mobile No. should be 10 digit");
                            editmobile.requestFocus();
                        }else if (editusername.getText().toString().isEmpty()){
                            editusername.setError("Name Field Can't be blank");
                            editusername.requestFocus();
                        }
                        else {
                            MobileEmailAPI();
                        }
            }
        });
    }

    private void MobileEmailAPI() {
        if (CommonUtils.isOnline(RegisterActivity.this)) {
            sessonManager.showProgress(RegisterActivity.this);
            RegisterRequest registerRequest=new RegisterRequest();
            registerRequest.setMobile(editmobile.getText().toString());
            registerRequest.setName(editusername.getText().toString());
            Call<LoginModel> call= ApiExecutor.getApiService(this)
                    .registerUser(registerRequest);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    sessonManager.hideProgress();
                    //Log.d("response",response.body().getStatus());
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Toast.makeText(RegisterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editmobile.getText().toString().isEmpty())){
                                startActivity(new Intent(RegisterActivity.this,OtpActivity.class)
                                        .putExtra("type","register")
                                        .putExtra("mobile",editmobile.getText().toString())
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }else {
                                sessonManager.getToken();
                                //sessonManager.setToken(response.body().getToken());
                                startActivity(new Intent(RegisterActivity.this,MapsActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(RegisterActivity.this, getString(R.string.please_check_network));
        }
    }

    public void login(View view) {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
}