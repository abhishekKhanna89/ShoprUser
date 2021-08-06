package com.shoppr.shoper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.util.SessonManager;


public class SplashActivity extends AppCompatActivity {
    SessonManager sessonManager;
    String newToken;
    String mEncodedAuthInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Sendbird", "start1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_new);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(SplashActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            sessonManager.setNotificationToken(newToken);
            // Log.d("notificationToken",newToken);
        });

        AuthenticationUtils.autoAuthenticate(this, userId -> {

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
    private boolean hasDeepLink() {
        boolean result = false;

        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                String scheme = data.getScheme();
                if (scheme != null && scheme.equals("sendbird")) {
                 /*   Log.i(BaseApplication.TAG, "[SplashActivity] deep link: " + data.toString());*/
                    mEncodedAuthInfo = data.getHost();
                    if (!TextUtils.isEmpty(mEncodedAuthInfo)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
//    private void autoAuthenticate() {
//        AuthenticationUtils.autoAuthenticate(SplashActivity.this, userId -> {
//
//        });
//    }

}