package com.shoppr.shoper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.login.Login;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.util.SessonManager;


public class SplashActivity extends AppCompatActivity {
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessonManager.getToken().isEmpty()){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                    finish();

                }

                // This method w
                /*finish();
                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(i);*/
            }
        }, 5000);
    }
}