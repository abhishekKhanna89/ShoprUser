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
import android.widget.Toast;

import com.google.gson.Gson;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.Model.StoreListDetails.StoreListDetailsModel;
import com.shoppr.shoper.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
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
    int shopId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sotore_details);
        getSupportActionBar().setTitle("Store Listing");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessonManager=new SessonManager(this);

        storeId=getIntent().getIntExtra("storeId",0);
        Log.d("ShoprId",""+sessonManager.getCityName());

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
                 startActivity(new Intent(SotoreDetailsActivity.this,FindingShopprActivity.class)
                .putExtra("shopId",shopId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                 .putExtra("address",sessonManager.getEditaddress())
                 .putExtra("city",sessonManager.getCityName()));  /*startActivity(new Intent(SotoreDetailsActivity.this,ChatActivity.class)
                .putExtra("shopId",shopId).putExtra("chat_status","3").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));  startActivity(new Intent(SotoreDetailsActivity.this,ChatActivity.class)
                .putExtra("shopId",shopId).putExtra("chat_status","3").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
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
                            shopId=storeListDetailsModel.getData().getStoresDetails().getId();
                            Log.d("ShopId",""+shopId);
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
                        }else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(SotoreDetailsActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(SotoreDetailsActivity.this,"");
                                            Toast.makeText(SotoreDetailsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SotoreDetailsActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        }else {

                                        }
                                    });
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

    public void galleryImage(View view) {
        startActivity(new Intent(this,ImageZoomActivity.class)
                .putExtra("shopId",shopId)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void notification(View view) {
        startActivity(new Intent(SotoreDetailsActivity.this,NotificationListActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void home(View view) {
        startActivity(new Intent(SotoreDetailsActivity.this, MapsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void store_list(View view) {
        startActivity(new Intent(SotoreDetailsActivity.this, StorelistingActivity.class).putExtra("address",sessonManager.getEditaddress())
                .putExtra("city",sessonManager.getCityName())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}