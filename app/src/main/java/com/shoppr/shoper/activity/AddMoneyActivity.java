package com.shoppr.shoper.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.CustomerBalancceModel;
import com.shoppr.shoper.Model.Recharge.RechargeModel;
import com.shoppr.shoper.Model.VerifyRechargeModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.RechargeRequest;
import com.shoppr.shoper.requestdata.VerifyRechargeRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class AddMoneyActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG ="" ;
    TextView customerBalance,selectedBalance;
    TextView TvOneThousnads, TvTwoThousnads, TvThreeThousnads;
    Button btnsubmit;
    SessonManager sessonManager;
    String order_id,email,mobile,amount;
    String chat_id,value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("lakshmi","lakshmi");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("Add Money");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        chat_id=getIntent().getStringExtra("chat_id");
        value=getIntent().getStringExtra("value");
        /*Todo:- TextView find Id*/
        customerBalance=findViewById(R.id.customerBalance);
        selectedBalance=findViewById(R.id.selectedBalance);
        /*Todo:- LinearLayout find id*/
        TvOneThousnads = (TextView) findViewById(R.id.tv_one_thousands);
        TvTwoThousnads = (TextView)findViewById(R.id.tv_two_thousands);
        TvThreeThousnads = (TextView) findViewById(R.id.tv_three_thousands);
        btnsubmit=findViewById(R.id.btnsubmit);

        TvOneThousnads.setText("+ " + "\u20B9 " + 300);
        TvTwoThousnads.setText("+ " + "\u20B9 " + 500);
        TvThreeThousnads.setText("+ " + "\u20B9 " + 1000);
        selectedBalance.setText(" 300");

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=selectedBalance.getText().toString();
                String aa=a.replace("₹ ","");
                rechargeService(aa);
            }
        });

        TvOneThousnads.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                TvTwoThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvThreeThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvOneThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_gradient_reverse_full));
                TvOneThousnads.setTextColor(getResources().getColor(R.color.white));
                TvTwoThousnads.setTextColor(getResources().getColor(R.color.black));
                TvThreeThousnads.setTextColor(getResources().getColor(R.color.black));
                selectedBalance.setText(" 300");
                amount = "300";
                // Log.d("aaaaAmountAaa1",amount);

            }
        });

        TvTwoThousnads.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                TvOneThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvThreeThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvTwoThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_gradient_reverse_full));
                TvTwoThousnads.setTextColor(getResources().getColor(R.color.white));
                TvOneThousnads.setTextColor(getResources().getColor(R.color.black));
                TvThreeThousnads.setTextColor(getResources().getColor(R.color.black));
                selectedBalance.setText(" 500");
                amount = "500";
                //   Log.d("aaaaAmountAaa2",amount);
            }
        });

        TvThreeThousnads.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                TvOneThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvTwoThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_border_yellow));
                TvThreeThousnads.setBackground(AddMoneyActivity.this.getDrawable(R.drawable.rounded_gradient_reverse_full));
                TvThreeThousnads.setTextColor(getResources().getColor(R.color.white));
                TvOneThousnads.setTextColor(getResources().getColor(R.color.black));
                TvTwoThousnads.setTextColor(getResources().getColor(R.color.black));
                selectedBalance.setText(" 1000");
                amount = "1000";
                //Log.d("aaaaAmountAaa3",amount);
            }
        });



        viewCustomerBalance();
    }

    private void rechargeService(String aa) {
        System.out.println("chat_idchat_idchat_id"+chat_id);
        if (CommonUtils.isOnline(AddMoneyActivity.this)) {
            sessonManager.showProgress(AddMoneyActivity.this);
            RechargeRequest rechargeRequest=new RechargeRequest();
            rechargeRequest.setAmount(aa);
            rechargeRequest.setChat_id(chat_id);
            Call<RechargeModel>call=ApiExecutor.getApiService(this)
                    .apiRecharge("Bearer "+sessonManager.getToken(),rechargeRequest);
            call.enqueue(new Callback<RechargeModel>() {
                @Override
                public void onResponse(Call<RechargeModel> call, Response<RechargeModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        RechargeModel rechargeModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            Toast.makeText(AddMoneyActivity.this, ""+rechargeModel.getMessage(), Toast.LENGTH_SHORT).show();

                            if (rechargeModel.getData()!=null){
                                order_id=rechargeModel.getData().getOrderId();
                                email=rechargeModel.getData().getEmail();
                                mobile=rechargeModel.getData().getMobile();
                                amount= String.valueOf(rechargeModel.getData().getAmount());
                                startPayment(amount);
                            }
                        }else {
                            Toast.makeText(AddMoneyActivity.this, ""+rechargeModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RechargeModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(AddMoneyActivity.this, getString(R.string.please_check_network));
        }
    }

    private void startPayment(String amount) {
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Shoppr");
            options.put("description", "App Payment");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("order_id", order_id);
            String payment = amount;
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            double total = Double.parseDouble(payment);
            total = total * 100;
            options.put("amount", total);
            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mobile);
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void viewCustomerBalance() {
        if (CommonUtils.isOnline(AddMoneyActivity.this)) {
            sessonManager.showProgress(AddMoneyActivity.this);
            Call<CustomerBalancceModel>call= ApiExecutor.getApiService(AddMoneyActivity.this)
                    .apiCustomerbalance("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<CustomerBalancceModel>() {
                @Override
                public void onResponse(Call<CustomerBalancceModel> call, Response<CustomerBalancceModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        CustomerBalancceModel customerBalancceModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (customerBalancceModel.getData()!=null){
                                customerBalance.setText(String.valueOf("₹ "+customerBalancceModel.getData()));
                            }else {
                                Toast.makeText(AddMoneyActivity.this, ""+customerBalancceModel.getMessage(), Toast.LENGTH_SHORT).show();
                                if (response.body().getStatus().equalsIgnoreCase("failed")){
                                    if (response.body().getMessage().equalsIgnoreCase("logout")){
                                        AuthenticationUtils.deauthenticate(AddMoneyActivity.this, isSuccess -> {
                                            if (getApplication() != null) {
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(AddMoneyActivity.this,"");
                                                Toast.makeText(AddMoneyActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddMoneyActivity.this, LoginActivity.class));
                                                finishAffinity();

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CustomerBalancceModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(AddMoneyActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstBalance:
                first=firstBalanceText.getText().toString();
                Toast.makeText(this,first, Toast.LENGTH_SHORT).show();
                selectedBalance.setText(first);
                break;

            case R.id.secondBalance:
                 second=secondBalanceText.getText().toString();
                Toast.makeText(this,second, Toast.LENGTH_SHORT).show();
                selectedBalance.setText(second);
                break;
            case R.id.thirdBalance:
                 third=thirdBalanceText.getText().toString();
                Toast.makeText(this,third, Toast.LENGTH_SHORT).show();
                selectedBalance.setText(third);
                break;
            }

    }*/

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        String orderId=paymentData.getOrderId();
        String paymentId=paymentData.getPaymentId();
        String signature=paymentData.getSignature();
        if (CommonUtils.isOnline(AddMoneyActivity.this)) {
            sessonManager.showProgress(AddMoneyActivity.this);
            VerifyRechargeRequest verifyRechargeRequest=new VerifyRechargeRequest();
            verifyRechargeRequest.setRazorpay_order_id(orderId);
            verifyRechargeRequest.setRazorpay_payment_id(paymentId);
            verifyRechargeRequest.setRazorpay_signature(signature);
            Call<VerifyRechargeModel>call=ApiExecutor.getApiService(this)
                    .apiVerifyRecharge("Bearer "+sessonManager.getToken(),verifyRechargeRequest);
            call.enqueue(new Callback<VerifyRechargeModel>() {
                @Override
                public void onResponse(Call<VerifyRechargeModel> call, Response<VerifyRechargeModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        VerifyRechargeModel verifyRechargeModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            if (value!=null&&value.equalsIgnoreCase("1")){
                                Intent intent=new Intent(AddMoneyActivity.this,ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else if (value!=null&&value.equalsIgnoreCase("2")){
                                Intent intent=new Intent(AddMoneyActivity.this,MyAccount.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            Toast.makeText(AddMoneyActivity.this,verifyRechargeModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddMoneyActivity.this,verifyRechargeModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerifyRechargeModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(AddMoneyActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        //Log.e(TAG,  "error code "+String.valueOf(i)+" -- Payment failed "+s.toString()  );
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}