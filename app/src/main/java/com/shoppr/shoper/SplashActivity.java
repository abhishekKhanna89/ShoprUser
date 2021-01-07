package com.shoppr.shoper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                // This method w
                /*finish();
                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(i);*/
            }
        }, 5000);
    }
}