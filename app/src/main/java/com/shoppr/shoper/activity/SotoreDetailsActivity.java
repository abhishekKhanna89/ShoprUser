package com.shoppr.shoper.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.Model.StoreListDetails.StoreListDetailsModel;
import com.shoppr.shoper.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.StorelistingActivity;
import com.shoppr.shoper.adapter.SliderAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import model.Slidermode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SotoreDetailsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager mViewPager;
    Button btnsubmit;
    TextView aboutText,storeNameText,
            categoryNameText,openingTimeText,
            emailText,mobileText,addressText;
    SessonManager sessonManager;
    int storeId;
    /*Todo:- View Pager*/
    List<Image>imageList;
    Timer timer;
    int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sotore_details);
        getSupportActionBar().setTitle("Store Listing");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessonManager=new SessonManager(this);

        storeId=getIntent().getIntExtra("storeId",0);


        /*Todo:- ViewPager With TabLayout find id*/
        mViewPager = (ViewPager)findViewById(R.id.viewPage);
        tabLayout = findViewById(R.id.tab_dots);
        /*Todo:- Button find id*/
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        /*Todo:- TextView find id*/
        aboutText=findViewById(R.id.aboutText);
        storeNameText=findViewById(R.id.storeNameText);
        categoryNameText=findViewById(R.id.categoryNameText);
        openingTimeText=findViewById(R.id.openingTimeText);
        emailText=findViewById(R.id.emailText);
        mobileText=findViewById(R.id.mobileText);
        addressText=findViewById(R.id.addressText);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SotoreDetailsActivity.this,MyAccount.class));
            }
        });
        viewDetails();


    }

    private void viewDetails() {
        if (CommonUtils.isOnline(SotoreDetailsActivity.this)) {
            sessonManager.showProgress(SotoreDetailsActivity.this);
            Call<StoreListDetailsModel> call= ApiExecutor.getApiService(this)
                    .apiStoreListDetails("Bearer "+sessonManager.getToken(),storeId);
            call.enqueue(new Callback<StoreListDetailsModel>() {
                @Override
                public void onResponse(Call<StoreListDetailsModel> call, Response<StoreListDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StoreListDetailsModel storeListDetailsModel=response.body();
                            imageList=storeListDetailsModel.getData().getStoresDetails().getImages();
                            SliderAdapter sliderAdapter=new SliderAdapter(SotoreDetailsActivity.this,imageList);
                            mViewPager.setAdapter(sliderAdapter);
                            tabLayout.setupWithViewPager(mViewPager,true);
                            final Handler handler = new Handler();
                            final Runnable Update = new Runnable() {
                                public void run() {
                                    if (currentPage==imageList.size()){
                                        currentPage=0;
                                        //viewPagerOurChanelPartner.setCurrentItem(position);
                                    }else {
                                        currentPage++;
                                    }
                                    mViewPager.setCurrentItem(currentPage);
                                }
                            };
                            timer = new Timer(); // This will create a new Thread
                            timer.schedule(new TimerTask() { // task to be scheduled
                                @Override
                                public void run() {
                                    handler.post(Update);
                                }
                            }, 2500, 3000);
                            if (storeListDetailsModel.getData().getStoresDetails()!=null){
                                String about=storeListDetailsModel.getData().getStoresDetails().getAboutStore();
                                if (about!=null){
                                    aboutText.setText(about);
                                }
                                String storeName=storeListDetailsModel.getData().getStoresDetails().getStoreName();

                                if (storeName!=null){
                                    storeNameText.setText(storeName);
                                }
                                String categoryName=storeListDetailsModel.getData().getStoresDetails().getStoreType();

                                if (categoryName!=null){
                                    categoryNameText.setText(categoryName);
                                }
                                String openingTime=storeListDetailsModel.getData().getStoresDetails().getOpeningTime();
                                if (openingTime!=null){
                                    openingTimeText.setText(openingTime);
                                }
                                String email=storeListDetailsModel.getData().getStoresDetails().getEmail();
                                if (email!=null){
                                    emailText.setText(email);
                                }
                                String mobile=storeListDetailsModel.getData().getStoresDetails().getMobile();
                                if (mobile!=null){
                                    mobileText.setText(mobile);
                                }
                                String address=storeListDetailsModel.getData().getStoresDetails().getAddress();
                                if (address!=null){
                                    addressText.setText(address);
                                }

                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<StoreListDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(SotoreDetailsActivity.this, getString(R.string.please_check_network));
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