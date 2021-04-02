package com.shoppr.shoper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.util.SessonManager;


public class SplashActivity extends AppCompatActivity {
    SessonManager sessonManager;
    String newToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(SplashActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            sessonManager.setNotificationToken(newToken);
            // Log.d("notificationToken",newToken);
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessonManager.getToken().isEmpty()){
                    Log.d("resSession",sessonManager.getToken());
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                  //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }

            }
        }, 2000);
    }
}