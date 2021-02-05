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
import com.shoppr.shoper.activity.EditLocationActivity;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.NotificationListActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
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
    TextView shoprListText,addressText;
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
    String city,address;
    int value;
    String location_address;
    boolean myLocationEnable=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sessonManager = new SessonManager(this);
        shoprListText = findViewById(R.id.shoprListText);
        addressText=findViewById(R.id.addressText);
        cir_man_hair_cut = findViewById(R.id.cir_man_hair_cut);

        Log.d("sss",sessonManager.getToken());

        linearstorelist = findViewById(R.id.linearstorelist);

        value=getIntent().getIntExtra("value",0);
        location_address=getIntent().getStringExtra("location_address");

        linearstorelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address!=null){
                    startActivity(new Intent(MapsActivity.this, StorelistingActivity.class)
                            .putExtra("address",address));
                }else {
                    Toast.makeText(MapsActivity.this, "Please wait....", Toast.LENGTH_SHORT).show();
                }

            }
        });


        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(MapsActivity.this, EditLocationActivity.class)
                );
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

        /*Todo:- Get Address*/
        geocoder = new Geocoder(this, Locale.getDefault());

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

        if (myLocationEnable){
            myLocationEnable=false;
            Geocoder coder = new Geocoder(MapsActivity.this);
            List<Address> address;

            try {
                //Get latLng from String
                address = coder.getFromLocationName(location_address, 5);

                //check for null
                if (address != null) {

                    //Lets take first possibility from the all possibilities.
                    try {
                        Address address1 = address.get(0);
                        LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());
                        addressText.setText(address1.getAddressLine(0));
                        //latitude = String.valueOf(latLng.latitude);
                        //longitude = String.valueOf(latLng.longitude);


                        //sharedPreferences.edit().putString("lat", ""+latitude).apply();
                        //sharedPreferences.edit().putString("lng", ""+longitude).apply();

                        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_logo)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f));




//                    Log.d("asdaskjasd",latLng.latitude+"   "+latLng.longitude);
                        //getAddress(latLng.latitude,latLng.longitude);
                    } catch (IndexOutOfBoundsException er) {
                        Toast.makeText(MapsActivity.this, "Location isn't available", Toast.LENGTH_SHORT).show();
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            myLocationEnable=true;
            addressText.setText("");
            mGoogleMap.setMyLocationEnabled(myLocationEnable);
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }




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
            if (latLng!=null){
                markerOptions.position(latLng);
            }
            if (address!=null){
                markerOptions.title(address);
            }

           // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_logo));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }else {
            showGPSDisabledAlertToUser();
        }

        mLocationRequest = new LocationRequest();
       /* mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds*/
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


        if (value==1){
            Geocoder coder = new Geocoder(MapsActivity.this);
            List<Address> address;

            try {
                //Get latLng from String
                address = coder.getFromLocationName(location_address, 5);

                //check for null
                if (address != null) {

                    //Lets take first possibility from the all possibilities.
                    try {
                        Address address1 = address.get(0);
                        LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());
                        addressText.setText(address1.getAddressLine(0));
                        //latitude = String.valueOf(latLng.latitude);
                        //longitude = String.valueOf(latLng.longitude);


                        //sharedPreferences.edit().putString("lat", ""+latitude).apply();
                        //sharedPreferences.edit().putString("lng", ""+longitude).apply();

                        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_logo)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f));




//                    Log.d("asdaskjasd",latLng.latitude+"   "+latLng.longitude);
                        //getAddress(latLng.latitude,latLng.longitude);
                    } catch (IndexOutOfBoundsException er) {
                        Toast.makeText(MapsActivity.this, "Location isn't available", Toast.LENGTH_SHORT).show();
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            if (currLocationMarker != null) {
                currLocationMarker.remove();
            }
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            String latitude=String.valueOf(location.getLatitude());
            String longitude=String.valueOf(location.getLongitude());

            //Log.d("loaction: ",""+latitude+" : "+longitude);
            if (latLng!=null){
                sessonManager.setLat(latitude);
                sessonManager.setLon(longitude);
            }


            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if (address!=null){
                    addressText.setText(address);
                }

                city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            } catch (IOException e) {
                e.printStackTrace();
            }


            MarkerOptions markerOptions = new MarkerOptions();
            if (latLng!=null){
                markerOptions.position(latLng);
            }


            if (address!=null){
                markerOptions.title(address);
            }
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_logo));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);

            //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

            //zoom to current position:
            if (latLng!=null){
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        }

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

    public void NotificationList(View view) {
        startActivity(new Intent(MapsActivity.this, NotificationListActivity.class));
    }
}