package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.EditLocationActivity;
import com.shoppr.shoper.activity.FindingShopprActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.NotificationListActivity;
import com.shoppr.shoper.activity.RegisterMerchantActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{
    SessonManager sessonManager;
    //double lat,lon;
    TextView shoprListText, addressText,countText;
    CircleImageView cir_man_hair_cut;

    /*Todo:- Location Manager*/
    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);
        sessonManager = new SessonManager(this);
        shoprListText = findViewById(R.id.shoprListText);
        addressText = findViewById(R.id.addressText);
        cir_man_hair_cut = findViewById(R.id.cir_man_hair_cut);
        countText=findViewById(R.id.countText);

        Log.d("Token", sessonManager.getToken());

        /*Todo:- Get the location manager*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT);
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


        viewListShopr();
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
            sessonManager.showProgress(MapsActivity.this);
            Call<ShoprListModel> call = ApiExecutor.getApiService(this).apiShoprList("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ShoprListModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ShoprListModel> call, Response<ShoprListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ShoprListModel shoprListModel = response.body();
                            if (shoprListModel.getData() != null) {
                                for (int i = 0; i < shoprListModel.getData().getShopper().size(); i++) {
                                    shoprListText.setText(shoprListModel.getData().getShopper().get(i).getShopprCount() + "\t" + shoprListModel.getData().getShopper().get(i).getLocation());
                                    if (shoprListModel.getData().getNotifications().equalsIgnoreCase("0")){
                                        countText.setVisibility(View.GONE);
                                    }else {
                                        countText.setVisibility(View.VISIBLE);
                                        countText.setText(shoprListModel.getData().getNotifications());
                                    }

                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ShoprListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        } else {
            CommonUtils.showToastInCenter(MapsActivity.this, getString(R.string.please_check_network));
        }
    }


    public void chats(View view) {
        startActivity(new Intent(MapsActivity.this, FindingShopprActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

                        break;
                    case R.id.action_feedback:

                        break;
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
                .putExtra("address", addressText.getText().toString()));
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
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            //addressText.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }else {
            showGPSDisabledAlertToUser();
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = list.get(0);
            String localitys = address.getLocality();
            String location_address = address.getAddressLine(0);
            String jj=getIntent().getStringExtra("value");
            if (jj!=null&&jj.equalsIgnoreCase("0")){
                String editLocation=getIntent().getStringExtra("location_address");
                addressText.setText(editLocation);
            }else {
               addressText.setText(location_address);
            }

           // addressText.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }




}