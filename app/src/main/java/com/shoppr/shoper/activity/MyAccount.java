package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccount extends AppCompatActivity {
    LinearLayout linarwallet;
    TextView texxname,textEmail,walletAmountText,textaddmoney;
    CircleImageView image_order;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        linarwallet=findViewById(R.id.linarwallet);
        textaddmoney=findViewById(R.id.textaddmoney);
        /*Todo:- Text Find Id*/
        texxname=findViewById(R.id.nameText);
        textEmail=findViewById(R.id.textEmail);
        walletAmountText=findViewById(R.id.walletAmountText);
        /*Todo:- CircleImageView find id*/
        image_order=findViewById(R.id.image_order);


        linarwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyAccount.this,WalletActivity.class);
                startActivity(intent);
            }
        });
        textaddmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyAccount.this,AddMoneyActivity.class);
                startActivity(intent);
            }
        });
        myProfile();

    }

    private void myProfile() {
        if (CommonUtils.isOnline(MyAccount.this)){
            sessonManager.showProgress(MyAccount.this);
            Call<MyProfileModel> call = ApiExecutor.getApiService(this).apiMyProfile("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<MyProfileModel>() {
                @Override
                public void onResponse(Call<MyProfileModel> call, Response<MyProfileModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            MyProfileModel myProfileModel=response.body();
                            if(myProfileModel.getData()!=null) {
                                Picasso.get().load(myProfileModel.getData().getImage()).into(image_order);
                                texxname.setText(String.valueOf(myProfileModel.getData().getName()));
                                textEmail.setText(myProfileModel.getData().getEmail());
                                walletAmountText.setText(String.valueOf(myProfileModel.getData().getBalance()));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyProfileModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });


        }else {
            CommonUtils.showToastInCenter(MyAccount.this, getString(R.string.please_check_network));
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
}