package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.shoppr.shoper.R;

public class OrderConfirmActivity extends AppCompatActivity {
    TextView orderText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        orderText=findViewById(R.id.orderText);
    }
}