package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;


import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.shoppr.shoper.Model.CheckLocation.CheckLocationModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.EditLocationActivity;
import com.shoppr.shoper.activity.FindingShopprActivity;
import com.shoppr.shoper.activity.HelpActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.NotificationListActivity;
import com.shoppr.shoper.activity.RegisterMerchantActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
import com.shoppr.shoper.activity.ViewCartActivity;
import com.shoppr.shoper.activity.WalletActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    SessonManager sessonManager;
    //double lat,lon;
    TextView shoprListText, addressText, countText;
    CircleImageView cir_man_hair_cut;

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
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    String key;
    //ArrayList<String> arrListLocation = new ArrayList<>();


    /*Todo:- Layout Screen*/
    ConstraintLayout secondPage;
    LinearLayout mainPage;
    Button updateLocation;
    Progressbar progressbar;
    private boolean firstLocation=true;

    /*Todo:- Version Check*/
    String VERSION_URL = "http://shoppr.avaskmcompany.xyz/api/app-version";
    String sCurrentVersion;
    int hoursmilllisecond = 86400000;
    int value = 0, savedMillistime;

    String cityName;
    String urlString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        shoprListText = findViewById(R.id.shoprListText);
        addressText = findViewById(R.id.addressText);
        cir_man_hair_cut = findViewById(R.id.cir_man_hair_cut);
        countText = findViewById(R.id.countText);
        /*Todo:- ConstraintLayout Screen Layout*/
        mainPage = findViewById(R.id.mainPage);
        secondPage = findViewById(R.id.secondPage);
        updateLocation = findViewById(R.id.updateLocation);




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
                startActivity(new Intent(MapsActivity.this, MyAccount.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        myProfile();


        //Log.d("newToken", getActivity().getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
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
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            MyProfileModel myProfileModel = response.body();
                            if (myProfileModel.getData() != null) {
                                //sessonManager.setWalletAmount(String.valueOf(myProfileModel.getData().getBalance()));
                                Picasso.get().load(myProfileModel.getData().getImage()).into(cir_man_hair_cut);
                            }
                        } else {
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    sessonManager.setToken("");
                                    PrefUtils.setAppId(MapsActivity.this, "");
                                    Toast.makeText(MapsActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                                    finishAffinity();
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
            urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
            //Log.d("addressEEEE",key);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("EditLocationResponse", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        JSONArray jsonArray1=jsonObject1.getJSONArray("terms");
                        String location = jsonArray1.toString();
                        Log.d("arrListLocation",""+location+"cityName"+cityName);
                        Call<ShoprListModel> call = ApiExecutor.getApiService(MapsActivity.this).apiShoprList("Bearer " + sessonManager.getToken(),location,cityName);
                        call.enqueue(new Callback<ShoprListModel>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(Call<ShoprListModel> call, Response<ShoprListModel> response) {
                                //sessonManager.hideProgress();
                                if (response.body() != null) {
                                    if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                        ShoprListModel shoprListModel = response.body();

                                        if (shoprListModel.getData() != null) {
                                            for (int i = 0; i < shoprListModel.getData().getShopper().size(); i++) {
                                                String res=new Gson().toJson(shoprListModel.getData().getShopper().get(i).getShopprCount());
                                                Log.d("resShopo",res);
                                                shoprListText.setText("Active Shoppers : " + shoprListModel.getData().getShopper().get(i).getShopprCount());
                                                if (shoprListModel.getData().getNotifications().equalsIgnoreCase("0")) {
                                                    countText.setVisibility(View.GONE);
                                                } else {
                                                    countText.setVisibility(View.VISIBLE);
                                                    countText.setText(shoprListModel.getData().getNotifications());
                                                }

                                            }
                                        }
                                    } else {
                                        if (response.body().getStatus().equalsIgnoreCase("failed")){
                                            if (response.body().getMessage().equalsIgnoreCase("logout")){
                                                sessonManager.setToken("");
                                                PrefUtils.setAppId(MapsActivity.this, "");
                                                Toast.makeText(MapsActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                                                finishAffinity();
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
        .putExtra("city",cityName));
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
                        startActivity(new Intent(MapsActivity.this, HelpActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        myProfile();
        viewListShopr();
    }

    public void notification(View view) {
        startActivity(new Intent(MapsActivity.this, NotificationListActivity.class));
    }

    public void store_list(View view) {
        startActivity(new Intent(MapsActivity.this, StorelistingActivity.class)
                .putExtra("address", addressText.getText().toString())
                .putExtra("city",cityName));
    }

    public void my_account(View view) {
        startActivity(new Intent(MapsActivity.this, MyAccount.class));
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
            sessonManager.setLat(String.valueOf(location.getLatitude()));
            sessonManager.setLon(String.valueOf(location.getLongitude()));
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
            Log.d("resLocation",location_address);
            String addressLocationValue=getIntent().getStringExtra("addressLocationValue");
            if (addressLocationValue!=null&&addressLocationValue.equalsIgnoreCase("0")){
               String addressLocation=getIntent().getStringExtra("location_address");
               String city_name=getIntent().getStringExtra("localitys");
               cityName=city_name;
               //Log.d("resCity",city_name);
                key = addressLocation;
                String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
                StringRequest stringRequest=new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("resJSon",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);
                            JSONArray jsonArray1=jsonObject1.getJSONArray("terms");
                            String location = jsonArray1.toString();
                            Log.d("loactionTTTTT",location+"hhh  "+city_name);
                            Call<CheckLocationModel> call = ApiExecutor.getApiService(MapsActivity.this)
                                    .apiCheckLocation("Bearer " + sessonManager.getToken(), location,cityName);
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

                    }
                });
                RequestQueue requestQueue=Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }else
                {
                key = location_address;
                String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
                StringRequest stringRequest=new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);
                            JSONArray jsonArray1=jsonObject1.getJSONArray("terms");
                            String location = jsonArray1.toString();
                            Log.d("jnxdjhxj",location);
                            Call<CheckLocationModel> call = ApiExecutor.getApiService(MapsActivity.this)
                                    .apiCheckLocation("Bearer " + sessonManager.getToken(), location,cityName);
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
                                            mainPage.setVisibility(View.GONE);
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

                    }
                });
                RequestQueue requestQueue=Volley.newRequestQueue(this);
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
            Bundle bundle=new Bundle();
            onConnected(bundle);
            firstLocation = false;
        }
        Log.d("ressssssssLoa",""+location);
        if (location != null) {
            sessonManager.setLat(String.valueOf(location.getLatitude()));
            sessonManager.setLon(String.valueOf(location.getLongitude()));
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = list.get(0);
            cityName=address.getLocality();
            String location_address = address.getAddressLine(0);
            String addressLocationValue=getIntent().getStringExtra("addressLocationValue");
            if (addressLocationValue!=null&&addressLocationValue.equalsIgnoreCase("0")){
                String addressLocation=getIntent().getStringExtra("location_address");
                String city_name=getIntent().getStringExtra("localitys");
                cityName=city_name;
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
                .setPositiveButton("Update App", new DialogInterface.OnClickListener()
                {
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
}