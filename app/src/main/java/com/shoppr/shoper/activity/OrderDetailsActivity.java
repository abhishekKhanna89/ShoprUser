package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shoppr.shoper.R;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        int position=getIntent().getIntExtra("position",0);

    }
}