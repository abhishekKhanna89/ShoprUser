
package com.shoppr.shoper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.CheckLocation.CheckLocationModel;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.AddMoneyActivity;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.ChatHistoryActivity;
import com.shoppr.shoper.activity.EditLocationActivity;
import com.shoppr.shoper.activity.FindingShopprActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.MyOrderActivity;
import com.shoppr.shoper.activity.NotificationListActivity;
import com.shoppr.shoper.activity.RegisterMerchantActivity;
import com.shoppr.shoper.activity.WalletActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    SessonManager sessonManager;
    //double lat,lon;
    TextView shoprListText, addressText, txtUserName, txtUserMobile, noti_badge;
    CircleImageView cir_man_hair_cut, userProfilePic;
    LinearLayout llWallet, llChat, llMyOrders, llHelp, llShareApp, llLogout;
    BottomNavigationView navView;
    Button btnMerchantRegister;
    /*Todo:- Location Manager*/
    private Location location;
    private GoogleApiClient googleApiClient;
    // private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    // private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> bannerList;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    String key, latitude, longitude;
    //ArrayList<String> arrListLocation = new ArrayList<>();

    /*Todo:- Layout Screen*/
    ConstraintLayout secondPage;
    LinearLayout mainPage;
    Button updateLocation;
    Progressbar progressbar;
    private ViewPager viewPager;
    private boolean firstLocation = true;
    DrawerLayout drawer_layout;
    NavigationView navigationView;

    /*Todo:- Version Check*/
    String VERSION_URL = ApiExecutor.baseUrl + "app-version";
    String sCurrentVersion;
    int hoursmilllisecond = 86400000;
    int value = 0, savedMillistime;

    String cityName;
    String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //git---->abhishek.khanna89@gmail.com<------->shopr@123

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        shoprListText = findViewById(R.id.shoprListText);
        addressText = findViewById(R.id.addressText);
        cir_man_hair_cut = findViewById(R.id.cir_man_hair_cut);
        //countText = findViewById(R.id.countText);
        /*Todo:- ConstraintLayout Screen Layout*/
        mainPage = findViewById(R.id.mainPage);
        secondPage = findViewById(R.id.secondPage);
        noti_badge=findViewById(R.id.noti_badge);
        updateLocation = findViewById(R.id.updateLocation);
        navView = findViewById(R.id.navView);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        txtUserName = navigationView.findViewById(R.id.tv_user_name);
        txtUserMobile = navigationView.findViewById(R.id.tv_mobile);
        userProfilePic = navigationView.findViewById(R.id.userProfilePic);

        llWallet = navigationView.findViewById(R.id.llWallet);
        llChat = navigationView.findViewById(R.id.llCHat);
        llMyOrders = navigationView.findViewById(R.id.llMyOrders);
        llHelp = navigationView.findViewById(R.id.llHelp);
        llShareApp = navigationView.findViewById(R.id.llShare);
        llLogout = navigationView.findViewById(R.id.llLogout);

        btnMerchantRegister = findViewById(R.id.btnMerchantRegister);

        Log.d("notifiallowed=", String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()));

        NotificationManager manager = (NotificationManager) MapsActivity.this.getSystemService(MapsActivity.this.NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = manager.getImportance();
        }
        boolean soundAllowed = importance < 0 || importance >= NotificationManager.IMPORTANCE_DEFAULT;

        Log.d("soundAllowed=", String.valueOf(soundAllowed));

        bannerList = new ArrayList<>();
        bannerList.add("");

        //if()
        if (String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()).equals("false")) {

            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please update sound,notification  lockscreen,floating notification setting to be better use");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, MapsActivity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", MapsActivity.this.getPackageName());
                                intent.putExtra("app_uid", MapsActivity.this.getApplicationInfo().uid);
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + MapsActivity.this.getPackageName()));
                            }
                            MapsActivity.this.startActivity(intent);


                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_chat:
                        startActivity(new Intent(MapsActivity.this, ChatHistoryActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case R.id.navigation_noti:
                        startActivity(new Intent(MapsActivity.this, NotificationListActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case R.id.navigation_local_shop:
                        startActivity(new Intent(MapsActivity.this, StorelistingActivity.class).putExtra("address", sessonManager.getEditaddress())
                                .putExtra("city", sessonManager.getCityName())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case R.id.navigation_account:
                        startActivity(new Intent(MapsActivity.this, MyAccount.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }
                return true;
            }
        });





      /*  AlertDialog alertDialog = new AlertDialog.Builder(this)
                   //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                    //set title
                .setTitle("Please Update notification setting to better use")
                     //set message
               // .setMessage("Exiting will call finish() method")
                     //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                    }
                })
                          //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(), "Nothing Happened", Toast.LENGTH_LONG).show();
                    }
                })
                .show();*/


        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, EditLocationActivity.class));
            }
        });
        Log.d("Token", sessonManager.getToken());

        /*Todo:- Get the location manager*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT);
            }
        }
        /*Todo:- Version Check*/
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            //String version = pInfo.versionName;
            sCurrentVersion = pInfo.versionName;
            Log.d("versionName", sCurrentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(sessonManager.getCurrenttime()) > 0) {
            value = Integer.parseInt(sessonManager.getCurrenttime());
        } else {
            value = 0;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        if (sessonManager.getCurrenttime().length() > 0) {
            // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
            value = Integer.parseInt(sessonManager.getCurrenttime());
            Log.d("hellovalueshared===", String.valueOf(value));
            String currentDateandTime = sdf.format(new Date());
            int savedMillis = (int) System.currentTimeMillis();
            int valuemus = (savedMillis - value);
            Log.d("valueminus", String.valueOf(valuemus));
            //Log.d("savemilsaecttime===", String.valueOf(savedMillis) + "," + value + "," + hoursmilllisecond + "," + valuemus);
            if (valuemus >= hoursmilllisecond) {

                appCheckVersionApi();
            }
        }


        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, EditLocationActivity.class));
            }
        });

        cir_man_hair_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        myProfile();

        btnMerchantRegister.setOnClickListener(this);
        llWallet.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llMyOrders.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llShareApp.setOnClickListener(this);
        llLogout.setOnClickListener(this);

        //Log.d("newToken", getActivity().getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llWallet:
                Intent intent=new Intent(MapsActivity.this, WalletActivity.class);
                startActivity(intent);
            break;

            case R.id.llCHat:
                Intent intent1=new Intent(MapsActivity.this, ChatActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            break;

            case R.id.llMyOrders:
                startActivity(new Intent(MapsActivity.this, MyOrderActivity.class).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                ));
                break;

            case R.id.llHelp:
                String number = "+919315957968";
                String url = "https://api.whatsapp.com/send?phone=" + number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

            case R.id.llShare:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String shareMessage = "Let me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
                break;

            case R.id.llLogout:
                callLogout();
                break;

            case R.id.btnMerchantRegister:
                startActivity(new Intent(MapsActivity.this, RegisterMerchantActivity.class));
                break;

        }

    }

    private void callLogout() {

        new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<LogoutModel>call=ApiExecutor.getApiService(MapsActivity.this)
                                .apiLogoutStatus("Bearer "+sessonManager.getToken());
                        call.enqueue(new Callback<LogoutModel>() {
                            @Override
                            public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                if (response.body()!=null) {
                                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                        AuthenticationUtils.deauthenticate(MapsActivity.this, isSuccess -> {
                                            if (getApplication() != null) {
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(MapsActivity.this,"");
                                                Toast.makeText(MapsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                                                finishAffinity();

                                            }
                                        });
                                    }else {
                                        AuthenticationUtils.deauthenticate(MapsActivity.this, isSuccess -> {
                                            if (getApplication() != null) {
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(MapsActivity.this,"");
                                                Toast.makeText(MapsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
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


    private void myProfile() {
        if (CommonUtils.isOnline(MapsActivity.this)) {
            //sessonManager.showProgress(MapsActivity.this);
            Call<MyProfileModel> call = ApiExecutor.getApiService(this).apiMyProfile("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<MyProfileModel>() {
                @Override
                public void onResponse(Call<MyProfileModel> call, Response<MyProfileModel> response) {

                    //sessonManager.hideProgress();
                    if (response.body() != null) {
                        MyProfileModel myProfileModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (myProfileModel.getData() != null) {
                                //sessonManager.setWalletAmount(String.valueOf(myProfileModel.getData().getBalance()));
                                Picasso.get().load(myProfileModel.getData().getImage()).into(cir_man_hair_cut);
                                Picasso.get().load(myProfileModel.getData().getImage()).into(userProfilePic);
                                txtUserName.setText(myProfileModel.getData().getName());
                                txtUserMobile.setText(myProfileModel.getData().getMobile());
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "" + myProfileModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")) {
                                if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                    AuthenticationUtils.deauthenticate(MapsActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(MapsActivity.this, "");
                                            Toast.makeText(MapsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MapsActivity.this, LoginActivity.class));
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
                    //sessonManager.hideProgress();
                }
            });


        } else {
            CommonUtils.showToastInCenter(MapsActivity.this, getString(R.string.please_check_network));
        }
    }

    private void viewListShopr() {
        if (CommonUtils.isOnline(MapsActivity.this)) {
            //Log.d("resAddd",addressText.getText().toString());
            //urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
            //Log.d("addressEEEE",key);

            urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + sessonManager.getLat() + "," + sessonManager.getLon() + "&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("EditLocationResponse", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
                        String location = jsonArray1.toString();
                        Log.d("arrListLocation", "" + location + "cityName" + cityName);
                        Call<ShoprListModel> call = ApiExecutor.getApiService(MapsActivity.this).apiShoprList("Bearer " + sessonManager.getToken(), location, cityName);
                        call.enqueue(new Callback<ShoprListModel>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(Call<ShoprListModel> call, Response<ShoprListModel> response) {
                                //sessonManager.hideProgress();
                                if (response.body() != null) {
                                    ShoprListModel shoprListModel = response.body();
                                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {


                                        if (shoprListModel.getData() != null) {
                                            for (int i = 0; i < shoprListModel.getData().getShopper().size(); i++) {
                                                String res = new Gson().toJson(shoprListModel.getData().getShopper().get(i).getShopprCount());
                                                Log.d("resShopo", res);
                                                shoprListText.setText("Active Shoppers : " + shoprListModel.getData().getShopper().get(i).getShopprCount());

                                                if (shoprListModel.getData().getNotifications().equalsIgnoreCase("0")) {
                                                    noti_badge.setVisibility(View.VISIBLE);
                                                } else {
                                                    noti_badge.setVisibility(View.VISIBLE);
                                                    noti_badge.setText(shoprListModel.getData().getNotifications());
                                                }

                                            }
                                        }
                                    } else {
                                        Toast.makeText(MapsActivity.this, "" + shoprListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        if (response.body().getStatus().equalsIgnoreCase("failed")) {
                                            if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                                Call<LogoutModel> call1 = ApiExecutor.getApiService(MapsActivity.this)
                                                        .apiLogoutStatus("Bearer " + sessonManager.getToken());
                                                call1.enqueue(new Callback<LogoutModel>() {
                                                    @Override
                                                    public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                                        if (response.body() != null) {
                                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                                AuthenticationUtils.deauthenticate(MapsActivity.this, isSuccess -> {
                                                                    if (getApplication() != null) {
                                                                        sessonManager.setToken("");
                                                                        PrefUtils.setAppId(MapsActivity.this, "");
                                                                        Toast.makeText(MapsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                                                                        finishAffinity();

                                                                    } else {

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
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ShoprListModel> call, Throwable t) {
                                // sessonManager.hideProgress();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } else {
            CommonUtils.showToastInCenter(MapsActivity.this, getString(R.string.please_check_network));
        }
    }


    public void chats(View view) {
        startActivity(new Intent(MapsActivity.this, FindingShopprActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("address", addressText.getText().toString())
                .putExtra("city", cityName));
    }

    public void menu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        /*  The below code in try catch is responsible to display icons*/
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_main, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        //implement click events
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_registerMerchant:
                        startActivity(new Intent(MapsActivity.this, RegisterMerchantActivity.class));
                        break;
                    case R.id.action_help:
                        String number = "+919315957968";
                        String url = "https://api.whatsapp.com/send?phone=" + number;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        /*startActivity(new Intent(MapsActivity.this, HelpActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
                        break;
                    /*case R.id.action_feedback:

                        break;*/
                    case R.id.action_shareApp:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                            String shareMessage = "Let me recommend you this application\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            //e.toString();
                        }
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("lakshmi===", "lakshmi");

        Log.d("lakshmi", String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()));

        if (String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()).equals("false")) {
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please update sound,notification  lockscreen,floating notification setting to be better use");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()).equals("false")) {

                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, MapsActivity.this.getPackageName());
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("app_package", MapsActivity.this.getPackageName());
                                    intent.putExtra("app_uid", MapsActivity.this.getApplicationInfo().uid);
                                } else {
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + MapsActivity.this.getPackageName()));
                                }
                                MapsActivity.this.startActivity(intent);
                            }

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


        }


        myProfile();
        viewListShopr();
    }

    /*Todo:- Location Change*/
    private void showGPSDisabledAlertToUser() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            //locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 0);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    /*Todo:- GPS*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            serviceMap(location);
        } else {
            showGPSDisabledAlertToUser();
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    private void serviceMap(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //lk changes here for this comment
          /*  Address address = list.get(0);
            cityName = address.getLocality();
            String location_address = address.getAddressLine(0);




            Log.d("mycordinates", ""+address.getLatitude()+", "+address.getLongitude());
            Log.d("resLocation",location_address);*/
            String addressLocationValue = getIntent().getStringExtra("addressLocationValue");
            if (addressLocationValue != null && addressLocationValue.equalsIgnoreCase("0")) {
                String addressLocation = getIntent().getStringExtra("location_address");
                String latitude = getIntent().getStringExtra("latitude");
                String longitude = getIntent().getStringExtra("longitude");
                String city_name = getIntent().getStringExtra("localitys");
                Log.d("resLocation", latitude + longitude);
                sessonManager.setLat(latitude);
                sessonManager.setLon(longitude);
                cityName = city_name;

                //Log.d("resCity",city_name);
                key = addressLocation;
                //String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";

                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";


                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("resJSon", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String complete = jsonObject.toString();
                            //Log.d("resJsonAll",""+jsonObject);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            Log.d("resJsonAll", "" + jsonArray);
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
                            String location = jsonArray1.toString();
                            Log.d("loactionTTTTT", location + "hhh  " + city_name);
                            Call<CheckLocationModel> call = ApiExecutor.getApiService(MapsActivity.this)
                                    .apiCheckLocation("Bearer " + sessonManager.getToken(), location, cityName);
                            call.enqueue(new Callback<CheckLocationModel>() {
                                @Override
                                public void onResponse(Call<CheckLocationModel> call, Response<CheckLocationModel> response) {
                                    //CheckLocationModel checkLocationModel=response.body();
                                    if (response.body() != null) {
                                        if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            // progressbar.hideProgress();
                                            addressText.setText(key);
                                            mainPage.setVisibility(View.VISIBLE);
                                            secondPage.setVisibility(View.GONE);
                                            viewListShopr();
                                            //Toast.makeText(MapsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            //Toast.makeText(MapsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            //progressbar.hideProgress();
                                            secondPage.setVisibility(View.VISIBLE);
                                        }


                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckLocationModel> call, Throwable t) {
                                    // progressbar.hideProgress();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } else {


                Address address = list.get(0);
                cityName = address.getLocality();
                String location_address = address.getAddressLine(0);


                Log.d("mycordinates", "" + address.getLatitude() + ", " + address.getLongitude());
                Log.d("resLocation", location_address);


                key = location_address;
                latitude = String.valueOf(address.getLatitude());
                longitude = String.valueOf(address.getLongitude());
                sessonManager.setLat(latitude);
                sessonManager.setLon(longitude);
                //Log.d("sss",latitude+longitude);
//                String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + address.getLatitude() + "," + address.getLongitude() + "&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String complete = jsonObject.toString();
                            Log.d("resJsonAll", "" + jsonObject);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
                            String location = jsonArray1.toString();
                            Log.d("jnxdjhxj", location);
                            Call<CheckLocationModel> call = ApiExecutor.getApiService(MapsActivity.this)
                                    .apiCheckLocation("Bearer " + sessonManager.getToken(), location, cityName);
                            call.enqueue(new Callback<CheckLocationModel>() {
                                @Override
                                public void onResponse(Call<CheckLocationModel> call, Response<CheckLocationModel> response) {
                                    Log.d("apiexecution", "Started");
                                    //CheckLocationModel checkLocationModel=response.body();
                                    if (response.body() != null) {
                                        Log.d("apiexecution", "body received");
                                        if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            // progressbar.hideProgress();
                                            addressText.setText(key);
                                            mainPage.setVisibility(View.VISIBLE);
                                            secondPage.setVisibility(View.GONE);
                                            viewListShopr();
                                            //Toast.makeText(MapsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            //Toast.makeText(MapsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            //progressbar.hideProgress();
                                            mainPage.setVisibility(View.GONE);
                                            secondPage.setVisibility(View.VISIBLE);
                                        }


                                    }
                                    //Log.d("apiexecution",response.toString());
                                }

                                @Override
                                public void onFailure(Call<CheckLocationModel> call, Throwable t) {
                                    // progressbar.hideProgress();
                                    Log.d("apiexecution", "body failed");

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstLocation) {
            Bundle bundle = new Bundle();
            onConnected(bundle);
            firstLocation = false;
        }
        Log.d("ressssssssLoa", "" + location);
        if (location != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = list.get(0);
            cityName = address.getLocality();
            String location_address = address.getAddressLine(0);
            String addressLocationValue = getIntent().getStringExtra("addressLocationValue");
            if (addressLocationValue != null && addressLocationValue.equalsIgnoreCase("0")) {
                String addressLocation = getIntent().getStringExtra("location_address");
                String city_name = getIntent().getStringExtra("localitys");
                cityName = city_name;
                key = addressLocation;
                addressText.setText(key);
            } else {
                key = location_address;
                addressText.setText(key);
            }
        }
    }

    /*Todo:- update version*/
    private void appCheckVersionApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, VERSION_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response====", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("responce===", jsonObject + "");
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String androidversion = jsonObject1.getString("customer_version");
                        if (androidversion.equalsIgnoreCase(sCurrentVersion)) {

                        } else {
                            showDialouge();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", "Bearer " + sessonManager.getToken());
                return headerMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void showDialouge() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Upgrade App")
                .setMessage(getResources().getString(R.string.force_update_app_message))
                .setPositiveButton("Update App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPlayStore();
                        dialog.dismiss();
                        savedMillistime = (int) System.currentTimeMillis();
                        sessonManager.setCurrenttime(String.valueOf(savedMillistime));
                        Log.d("helloTimemills", String.valueOf(savedMillistime));
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override


                    public void onClick(DialogInterface dialog, int which) {
                        savedMillistime = (int) System.currentTimeMillis();
                        sessonManager.setCurrenttime(String.valueOf(savedMillistime));
                        Log.d("helloTimemills", String.valueOf(savedMillistime));
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void openPlayStore() {
        if (getApplication() == null) {
            return;
        }
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy");
    }

}
