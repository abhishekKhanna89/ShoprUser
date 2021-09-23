package com.shoppr.shoper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.R;
import com.shoppr.shoper.adapter.CustomPagerAdapter;
import com.shoppr.shoper.util.SessonManager;

public class GetStartedActivity extends AppCompatActivity {
    Button Started;
    TextView SkipButton;
    SessonManager sessonManager;
    String newToken;
    ImageView indicator1, indicator2, indicator3, indicator4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        Started = (Button) findViewById(R.id.Started);
        SkipButton = (TextView) findViewById(R.id.SkipButton);
        indicator1 = (ImageView) findViewById(R.id.indicator1);
        indicator2 = (ImageView) findViewById(R.id.indicator2);
        indicator3 = (ImageView) findViewById(R.id.indicator3);
       // indicator4 = (ImageView) findViewById(R.id.indicator4);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(GetStartedActivity.this,R.color.white));

        sessonManager = new SessonManager(GetStartedActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            sessonManager.setNotificationToken(newToken);
            Log.e("deviceId: ", newToken);
        });

        Started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessonManager.getToken().isEmpty()) {
                    Log.d("resSession", sessonManager.getToken());
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(GetStartedActivity.this, MapsActivity.class);
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        });

        SkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessonManager.getToken().isEmpty()) {
                    Log.d("resSession", sessonManager.getToken());
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    sessonManager.getNotificationToken();
                    Intent intent = new Intent(GetStartedActivity.this, MapsActivity.class);
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        });


        viewPager.setAdapter(new CustomPagerAdapter(this));
        SkipButton.setVisibility(View.VISIBLE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    indicator1.setImageResource(R.drawable.ic_baseline_brightness_1_24);
                    indicator2.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator3.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                  //  indicator4.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                } else if (position == 1) {
                    indicator1.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator2.setImageResource(R.drawable.ic_baseline_brightness_1_24);
                    indicator3.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                  //  indicator4.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                } else if (position == 2) {
                    indicator1.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator2.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator3.setImageResource(R.drawable.ic_baseline_brightness_1_24);
                    //indicator4.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                } /*else if (position == 3) {
                    indicator1.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator2.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator3.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
                    indicator4.setImageResource(R.drawable.ic_baseline_brightness_1_24);
                }*/
                if (position == 2) {
                    SkipButton.setVisibility(View.GONE);
                    Started.setVisibility(View.VISIBLE);
                } else {
                    SkipButton.setVisibility(View.VISIBLE);
                    Started.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}