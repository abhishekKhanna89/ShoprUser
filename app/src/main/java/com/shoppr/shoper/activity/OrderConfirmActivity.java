package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.shoppr.shoper.R;

public class OrderConfirmActivity extends AppCompatActivity {
    TextView orderText;
    Button btn_continue_shoping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        getSupportActionBar().hide();
        orderText=findViewById(R.id.orderText);
        btn_continue_shoping=findViewById(R.id.btn_continue_shoping);

        String refid=getIntent().getStringExtra("refid");
        orderText.setText(refid);
        btn_continue_shoping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ChatActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}