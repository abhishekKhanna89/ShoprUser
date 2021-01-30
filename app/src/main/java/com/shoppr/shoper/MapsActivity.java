package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.Model.ShoprList.ShoprListModel;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    LinearLayout linearstorelist;
    SessonManager sessonManager;
    //double lat,lon;
    TextView shoprListText;
    CircleImageView cir_man_hair_cut;

    /*Todo:- Google Map And Current Location*/
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    /*Todo:- Address*/
    Geocoder geocoder;
    List<Address> addresses;
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sessonManager = new SessonManager(this);
        shoprListText = findViewById(R.id.shoprListText);
        cir_man_hair_cut = findViewById(R.id.cir_man_hair_cut);


        linearstorelist = findViewById(R.id.linearstorelist);

        linearstorelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, StorelistingActivity.class));
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

        /*Todo:- Google Map and Location*/
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);


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
            Call<ShoprListModel> call = ApiExecutor.getApiService(this).apiShoprList();
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

    public void back(View view) {
        onBackPressed();
    }

    public void chats(View view) {
        startActivity(new Intent(MapsActivity.this, ChatActivity.class));
    }

    /*Todo:- Google Map and Loaction*/
    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }else {

        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }else {
            showGPSDisabledAlertToUser();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String latitude=String.valueOf(location.getLatitude());
        String longitude=String.valueOf(location.getLongitude());
        Log.d("loaction: ",""+latitude+" : "+longitude);
        sessonManager.setLat(latitude);
        sessonManager.setLon(longitude);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(city);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }
    private void showGPSDisabledAlertToUser(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}