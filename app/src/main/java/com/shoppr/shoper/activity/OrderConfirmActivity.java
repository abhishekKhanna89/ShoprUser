package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.OrderDetails.Order;
import com.shoppr.shoper.R;

public class OrderConfirmActivity extends AppCompatActivity {
    TextView orderText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        getSupportActionBar().hide();
        orderText=findViewById(R.id.orderText);
        String refid=getIntent().getStringExtra("refid");
        orderText.setText(refid);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MyAccount.class));
        finish();
    }
}