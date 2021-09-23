
package com.shoppr.shoper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.shoppr.shoper.Model.CheckLocation.CheckLocationModel;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.call.CallService;
import com.shoppr.shoper.SendBird.call.VideoCallActivity;
import com.shoppr.shoper.SendBird.call.VoiceCallActivity;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.BroadcastUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.ChatHistoryActivity;
import com.shoppr.shoper.activity.EditLocationActivity;
import com.shoppr.shoper.activity.FindingShopprActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.MyOrderActivity;
import com.shoppr.shoper.activity.NotificationListActivity;
import com.shoppr.shoper.activity.WalletActivity;
import com.shoppr.shoper.adapter.BannerAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.ConstantValue;
import com.shoppr.shoper.util.CustomPopUp;
import com.shoppr.shoper.util.MyPreferences;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.RuntimePermission;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    SessonManager sessonManager;
    //double lat,lon;
    TextView shoprListText, addressText, txtUserName, txtUserMobile, noti_badge, txtChangeLocation;
    CircleImageView userProfilePic;
    LinearLayout llMyAccount, llWallet, llChat, llMyOrders, llHelp, llShareApp, llLogout;
    FrameLayout frameLayoutNoti, HelpToAdmin;
    BottomNavigationView navView;
    ImageView navMenu, imgClose;
    public FusedLocationProviderClient fusedLocationClient;
    Button btnMerchantRegister;
    private GoogleApiClient googleApiClient;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<Integer> bannerList;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    BroadcastReceiver mMessageReceiver;

    String key, latitude, longitude;
    //ArrayList<String> arrListLocation = new ArrayList<>();

    /*Todo:- Layout Screen*/
    ConstraintLayout secondPage;
    LinearLayout mainPage;
    Button updateLocation;
    Progressbar progressbar;
    private ViewPager viewPager;
    TabLayout tabLayout;
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

    boolean flag_banner = false;

  public  static   String call_id="";



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //git---->abhishek.khanna89@gmail.com<------->shopr@123

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        // private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
        // lists for permissions
        ArrayList<String> permissionsToRequest = permissionsToRequest(permissions);
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        shoprListText = findViewById(R.id.shoprListText);
        addressText = findViewById(R.id.addressText);
        txtChangeLocation = findViewById(R.id.txtChangeLocation);
        navMenu = findViewById(R.id.navMenu);
        System.out.println("TokenResponse" + sessonManager.getToken());
        //countText = findViewById(R.id.countText);
        /*Todo:- ConstraintLayout Screen Layout*/
        mainPage = findViewById(R.id.mainPage);
        secondPage = findViewById(R.id.secondPage);
        noti_badge = findViewById(R.id.noti_badge);
        updateLocation = findViewById(R.id.updateLocation);
        navView = findViewById(R.id.navView);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RelativeLayout helpRelative = (RelativeLayout) findViewById(R.id.helpRelative);
        helpRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "+919315957968";
                String url = "https://api.whatsapp.com/send?phone=" + number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);

        //ActivityUtils.startApplicationInformationActivity(MapsActivity.this);
        if (sessonManager.getBannerPopUp().equals("yes")) {
        } else {
            sessonManager.setBannerPopUp("yes");
            CustomPopUp.showBanner(MapsActivity.this);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        txtUserName = navigationView.findViewById(R.id.tv_user_name);
        txtUserMobile = navigationView.findViewById(R.id.tv_mobile);
        userProfilePic = navigationView.findViewById(R.id.userProfilePic);

        llMyAccount = navigationView.findViewById(R.id.llMyAccount);
        llWallet = navigationView.findViewById(R.id.llWallet);
        llChat = navigationView.findViewById(R.id.llCHat);
        llMyOrders = navigationView.findViewById(R.id.llMyOrders);
        llHelp = navigationView.findViewById(R.id.llHelp);
        llShareApp = navigationView.findViewById(R.id.llShare);
        llLogout = navigationView.findViewById(R.id.llLogout);
        imgClose = navigationView.findViewById(R.id.imgClose);


        btnMerchantRegister = findViewById(R.id.btnMerchantRegister);
        frameLayoutNoti = findViewById(R.id.frameLayoutNoti);
        HelpToAdmin = findViewById(R.id.HelpToAdmin);
        HelpToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "+919315957968";
                String url = "https://api.whatsapp.com/send?phone=" + number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Log.d("notifiallowed=", String.valueOf(NotificationManagerCompat.from(MapsActivity.this).areNotificationsEnabled()));

        NotificationManager manager = (NotificationManager) MapsActivity.this.getSystemService(NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = manager.getImportance();
        }

        boolean soundAllowed = importance < 0 || importance >= NotificationManager.IMPORTANCE_DEFAULT;

        Log.d("soundAllowed=", String.valueOf(soundAllowed));

        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.banner1);
        bannerList.add(R.drawable.banner2);
        bannerList.add(R.drawable.banner3);
        bannerList.add(R.drawable.banner4);
        /*bannerList.add(R.drawable.interior_design3);
        /*bannerList.add(R.drawable.interior_design3);
        bannerList.add(R.drawable.interior_design4);*/
        if (RuntimePermission.checkRunTimePermission(this)) {
            proceedAfterPermission();
        }

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
                            }
                          else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        registerBroadcast();

        navView.getMenu().getItem(0).setCheckable(false);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_chat:
                        item.setCheckable(true);
                        int chatId = MyPreferences.getInt(MapsActivity.this, ConstantValue.KEY_CHAT_ID);
                        boolean isChatProgress = MyPreferences.getBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);
                        if (isChatProgress) {
                            startActivity(new Intent(MapsActivity.this, ChatActivity.class)
                                    .putExtra("chat_status", "2").putExtra("findingchatid", chatId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            Intent intent1 = new Intent(MapsActivity.this, ChatHistoryActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        }
                        break;

                    case R.id.navigation_pickdel:
                        item.setCheckable(true);
                        Toast.makeText(MapsActivity.this, "In Progress", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.navigation_local_shop:
                        item.setCheckable(true);
                       /* startActivity(new Intent(MapsActivity.this, StorelistingActivity.class).putExtra("address", sessonManager.getEditaddress())
                                .putExtra("city", sessonManager.getCityName())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
                        Toast.makeText(MapsActivity.this, "In Progress", Toast.LENGTH_SHORT).show();

                        break;
                }
                return true;
            }
        });

        setUpBanner();

        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermission.checkRunTimePermission(MapsActivity.this)) {
                    if (isLocationEnabled())
                        startActivity(new Intent(MapsActivity.this, EditLocationActivity.class));
                    else
                        showGPSDisabledAlertToUser();
                }
            }
        });
        Log.d("Token", sessonManager.getToken());

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

        txtChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermission.checkRunTimePermission(MapsActivity.this)) {
                    if (isLocationEnabled())
                        startActivity(new Intent(MapsActivity.this, EditLocationActivity.class));
                    else
                        showGPSDisabledAlertToUser();
                }
            }
        });

        navMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        myProfile();

        btnMerchantRegister.setOnClickListener(this);
        llMyAccount.setOnClickListener(this);
        llWallet.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llMyOrders.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llShareApp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        frameLayoutNoti.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        //Log.d("newToken", getActivity().getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
       // initSendBirdCall(BaseApplication.APP_ID);
/*
       try {
           Bundle extras = getIntent().getExtras();
           if (extras != null) {
               String chat_status = getIntent().getStringExtra("chat_status");
                call_id = getIntent().getStringExtra("call_id");
               if (chat_status != null && chat_status.equalsIgnoreCase("5")) {
                   initSendBirdCall(PrefUtils.getAppId(getApplicationContext()));
               }
               else {
                   String value = String.valueOf(getIntent().getExtras().get("chat_status"));
                   //Log.d(TAG, "Key: " + "abcd" + " Value: " + value);
               }
           }
         //  initSendBirdCall(call_id);
         //  ActivityUtils.startCallActivityAsCallee(MapsActivity.this, call_id.toString());


        *//* Intent  intent = new Intent(MapsActivity.this, VoiceCallActivity.class);


           intent.putExtra(ActivityUtils.EXTRA_CALLEE_ID, "Shoppr-27");
           intent.putExtra(ActivityUtils.EXTRA_CALLEE_NAME, "Subhash");
           intent.putExtra(ActivityUtils.EXTRA_CALLEE_PIC, "https://shoppr-bucket.s3.ap-south-1.amazonaws.com/shopper/27/284_Square_Pic1614872802852.png");
           intent.putExtra(ActivityUtils.EXTRA_IS_VIDEO_CALL, "ye");
           intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
           MapsActivity.this.startActivity(intent);*//*
       }
       catch (Exception e)
       {}*/
    }
   /* public boolean initSendBirdCall(String appId) {
        Log.i(BaseApplication.TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = BaseApplication.APP_ID;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    int ongoingCallCount = SendBirdCall.getOngoingCallCount();

                    Log.i(BaseApplication.TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                    if (ongoingCallCount >= 2) {
                        call.end();
                        return;
                    }

                    call.setListener(new DirectCallListener() {
                        @Override
                        public void onConnected(DirectCall call) {
                        }

                        @Override
                        public void onEnded(DirectCall call) {
                            int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                            Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                            BroadcastUtils.sendCallLogBroadcast(context, call.getCallLog());


                            if (ongoingCallCount == 0) {
                                CallService.stopService(context);
                            }
                        }
                        // ActivityUtils.startCallActivityAsCallee(context, call);

                        //prefUtils.start
                        // PrefUtils.startCallActivityAsCallee(context, call);
                    });


                    //PrefUtils.startCallActivityAsCallee(context, call);
                    // CallService.onRinging(context, call);
                    ActivityUtils.startCallActivityAsCallee(context, call);

                    // PrefUtils.startCallActivityAsCallee(context, call);
                }
            });

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);

            return true;
        }
        return false;
    }*/

    private void proceedAfterPermission() {
        if (!isLocationEnabled()) {
            showGPSDisabledAlertToUser();
        }
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            proceedAfterPermission();

        } else {
            Toast.makeText(getApplicationContext(), "Please given all Permission", Toast.LENGTH_LONG).show();
            RuntimePermission.checkRunTimePermission(this);
        }
    }

    private void setUpBanner() {

        BannerAdapter myCustomPagerAdapter = new BannerAdapter(MapsActivity.this, bannerList);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.requestFocus();

        tabLayout.setupWithViewPager(viewPager, true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bannerList.size() > 0) {
                            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % bannerList.size());
                        }
                    }
                });
            }
        };
        Timer timer;
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 3000, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMyAccount:
                Intent intent2 = new Intent(MapsActivity.this, MyAccount.class);
                startActivity(intent2);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.llWallet:
                Intent intent = new Intent(MapsActivity.this, WalletActivity.class);
                startActivity(intent);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.llCHat:
                /*int chatId = MyPreferences.getInt(MapsActivity.this, ConstantValue.KEY_CHAT_ID);
                boolean isChatProgress = MyPreferences.getBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);
                if (isChatProgress) {
                    startActivity(new Intent(MapsActivity.this, ChatActivity.class)
                            .putExtra("chat_status", "2").putExtra("findingchatid", chatId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {*/
                Intent intent1 = new Intent(MapsActivity.this, ChatHistoryActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                // }
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.llMyOrders:
                startActivity(new Intent(MapsActivity.this, MyOrderActivity.class).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                ));
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.llHelp:
                String number = "+919315957968";
                String url = "https://api.whatsapp.com/send?phone=" + number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                drawer_layout.closeDrawer(GravityCompat.START);
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
                    drawer_layout.closeDrawer(GravityCompat.START);
                } catch (Exception e) {
                    //e.toString();
                    drawer_layout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.llLogout:
                callLogout();
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.btnMerchantRegister:
                Toast.makeText(MapsActivity.this, "In Progress", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(MapsActivity.this, RegisterMerchantActivity.class));
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.frameLayoutNoti:
                startActivity(new Intent(MapsActivity.this, NotificationListActivity.class));
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.imgClose:
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

        }
    }

    private void callLogout() {

        new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<LogoutModel> call = ApiExecutor.getApiService(MapsActivity.this)
                                .apiLogoutStatus("Bearer " + sessonManager.getToken());
                        call.enqueue(new Callback<LogoutModel>() {
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

                                            }
                                        });
                                    } else {
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
                                                shoprListText.setText("Active Riders : " + shoprListModel.getData().getShopper().get(i).getShopprCount());

                                                if (shoprListModel.getData().getNotifications().equalsIgnoreCase("0")) {
                                                    noti_badge.setVisibility(View.GONE);
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
        int chatId = MyPreferences.getInt(MapsActivity.this, ConstantValue.KEY_CHAT_ID);
        boolean isChatProgress = MyPreferences.getBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);
        if (isChatProgress) {
            startActivity(new Intent(MapsActivity.this, ChatActivity.class)
                    .putExtra("chat_status", "2").putExtra("findingchatid", chatId).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            startActivity(new Intent(MapsActivity.this, FindingShopprActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("address", addressText.getText().toString())
                    .putExtra("city", cityName));
        }
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

        navView.getMenu().getItem(0).setCheckable(false);
        navView.getMenu().getItem(1).setCheckable(false);
        navView.getMenu().getItem(2).setCheckable(false);

        //MyPreferences.saveBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS, false);
        boolean isChatProgress = MyPreferences.getBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);

        if (isChatProgress) {
            BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_chat);
            badge.setVisible(true);
            badge.setNumber(1);
        } else {
            BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_chat);
            badge.setVisible(false);
        }
        viewListShopr();
    }

    private void registerBroadcast() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isChatProgress = MyPreferences.getBoolean(MapsActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);
                if (!isChatProgress) {
                    BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_chat);
                    badge.setVisible(false);
                }
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");

        LocalBroadcastManager.getInstance(MapsActivity.this).registerReceiver(mMessageReceiver, i);

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
        /*Todo:- Location Manager*/
        @SuppressLint("MissingPermission") Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        //Lajpat Nagar III location
        //location.setLatitude(28.566338);
        //location.setLongitude(77.238676);

        if (location != null) {
            serviceMap(location);
        }
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
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

            String addressLocationValue = getIntent().getStringExtra("addressLocationValue");
            if (addressLocationValue != null && addressLocationValue.equalsIgnoreCase("0")) {
                String addressLocation = getIntent().getStringExtra("location_address");
                String latitude = getIntent().getStringExtra("latitude");
                String longitude = getIntent().getStringExtra("longitude");
                cityName = getIntent().getStringExtra("localitys");
                Log.d("resLocation if", latitude + longitude);

                setAddress(latitude, longitude);

            } else {
                Address address = list.get(0);
                cityName = address.getLocality();
                String location_address = address.getAddressLine(0);

                Log.d("mycordinates", "" + address.getLatitude() + ", " + address.getLongitude());
                Log.d("resLocation else", location_address);

                key = location_address;
                latitude = String.valueOf(address.getLatitude());
                longitude = String.valueOf(address.getLongitude());
                setAddress(latitude, longitude);
            }
        }
    }

    private void setAddress(String latitude, String longitude) {
        sessonManager.setLat(latitude);
        sessonManager.setLon(longitude);

        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";

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
                    key = jsonArray1.getJSONObject(1).getString("long_name") + ", " + jsonArray1.getJSONObject(2).getString("long_name");
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

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
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
        //Lajpat Nagar III location
        //location.setLatitude(28.566338);
        //location.setLongitude(77.238676);

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
                cityName = getIntent().getStringExtra("localitys");
                String latitude = getIntent().getStringExtra("latitude");
                String longitude = getIntent().getStringExtra("longitude");
                setAddress(latitude, longitude);
            } else {
                if (address.getPremises() != null && address.getSubLocality() != null) {
                    key = address.getPremises() + ", " + address.getSubLocality();
                    addressText.setText(key);
                } else {
                    setAddress(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                }

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
                        System.out.println("androidversion" + androidversion);
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
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}
