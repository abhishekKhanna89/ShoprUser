package com.shoppr.shoper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
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
    TextView texxname, textEmail, walletAmountText;
    CircleImageView image_order;
    ImageView imgZoomed;
    SessonManager sessonManager;
    LinearLayout logoutLayout;
    Button btnedit, btnAddMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        linarwallet = findViewById(R.id.linarwallet);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        logoutLayout = findViewById(R.id.logoutLayout);
        imgZoomed = findViewById(R.id.imgZoomed);
        /*Todo:- Text Find Id*/
        texxname = findViewById(R.id.nameText);
        textEmail = findViewById(R.id.textEmail);
        walletAmountText = findViewById(R.id.walletAmountText);
        /*Todo:- CircleImageView find id*/
        image_order = findViewById(R.id.image_order);
        /*Todo:- Button Edit*/
        btnedit = findViewById(R.id.btnedit);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccount.this, UpdateProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        linarwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this, WalletActivity.class);
                startActivity(intent);
            }
        });
        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccount.this, AddMoneyActivity.class);
                intent.putExtra("value", "2");
                startActivity(intent);
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MyAccount.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<LogoutModel> call = ApiExecutor.getApiService(MyAccount.this)
                                        .apiLogoutStatus("Bearer " + sessonManager.getToken());
                                call.enqueue(new Callback<LogoutModel>() {
                                    @Override
                                    public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                        if (response.body() != null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                AuthenticationUtils.deauthenticate(MyAccount.this, isSuccess -> {
                                                    if (getApplication() != null) {
                                                        sessonManager.setToken("");
                                                        PrefUtils.setAppId(MyAccount.this, "");
                                                        Toast.makeText(MyAccount.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(MyAccount.this, LoginActivity.class));
                                                        finishAffinity();

                                                    }
                                                });
                                            } else {
                                                AuthenticationUtils.deauthenticate(MyAccount.this, isSuccess -> {
                                                    if (getApplication() != null) {
                                                        sessonManager.setToken("");
                                                        PrefUtils.setAppId(MyAccount.this, "");
                                                        Toast.makeText(MyAccount.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(MyAccount.this, LoginActivity.class));
                                                        finishAffinity();

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<LogoutModel> call, Throwable t) {

                                    }
                                });
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        myProfile();

    }

    private void myProfile() {
        if (CommonUtils.isOnline(MyAccount.this)) {
            sessonManager.showProgress(MyAccount.this);
            Call<MyProfileModel> call = ApiExecutor.getApiService(this).
                    apiMyProfile("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<MyProfileModel>() {
                @Override
                public void onResponse(Call<MyProfileModel> call, Response<MyProfileModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        MyProfileModel myProfileModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (myProfileModel.getData() != null) {
                                sessonManager.setWalletAmount(String.valueOf(myProfileModel.getData().getBalance()));
                                Picasso.get().load(myProfileModel.getData().getImage()).into(image_order);
                                Picasso.get().load(myProfileModel.getData().getImage()).into(imgZoomed);
                                texxname.setText(String.valueOf(myProfileModel.getData().getName()));
                                textEmail.setText(myProfileModel.getData().getMobile());
                                walletAmountText.setText("₹ " + String.valueOf(myProfileModel.getData().getBalance()));
                                sessonManager.setMobileNo(myProfileModel.getData().getMobile());
                                sessonManager.setProfilePic(myProfileModel.getData().getImage());
                            }
                        } else {
                            Toast.makeText(MyAccount.this, "" + myProfileModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")) {
                                if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                    AuthenticationUtils.deauthenticate(MyAccount.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(MyAccount.this, "");
                                            Toast.makeText(MyAccount.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MyAccount.this, LoginActivity.class));
                                            finishAffinity();

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyProfileModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });


        } else {
            CommonUtils.showToastInCenter(MyAccount.this, getString(R.string.please_check_network));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void chatHistory(View view) {
        startActivity(new Intent(MyAccount.this, ChatHistoryActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void MyOrder(View view) {
        startActivity(new Intent(MyAccount.this, MyOrderActivity.class).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        ));
    }
}