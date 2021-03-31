package com.shoppr.shoper;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.LoginModel;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.LoginRequest;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    Button btnsubmit;
    TextView textregister;
    EditText editusername;
    SessonManager sessonManager;

    /*Todo:- Current Location*/
    boolean gpsCheck = false;
    Location mLastLocation;
    String location_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(LoginActivity.this);


        btnsubmit=findViewById(R.id.btnsubmit);
        editusername=findViewById(R.id.editusername);


        textregister=findViewById(R.id.textregister);



        textregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editusername.getText().toString().isEmpty()){
                    editusername.setError("Mobile Field Can't be blank");
                    editusername.requestFocus();
                }
                else if(editusername.getText().toString().length()!=10){
                    editusername.setError("Mobile No. should be 10 digit");
                    editusername.requestFocus();
                }
                else {
                    MobileEmailAPI();
                }

                //startActivity(new Intent(LoginActivity.this,MapsActivity.class));
            }
        });
        /*Todo:- Current Location*/
        if (savedInstanceState != null) {
            gpsCheck=savedInstanceState.getBoolean("GPS");
        }

        if (!gpsCheck) {
            EnableGPSAutoMatically();

        }
        final LocationManager locman = (LocationManager) getSystemService(LOCATION_SERVICE);
        //gives us the location services

        //to use these services we need a listner as given below


        final LocationListener loclis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!= null) {
                    mLastLocation = location;
                    Geocoder geocoder = new Geocoder(LoginActivity.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = list.get(0);
                    //String localitys = address.getLocality();
                    location_address = address.getAddressLine(0);
                }

                //do something when location is changed

                Log.d("MA", "Latitude: " + location.getLatitude());
                Log.d("MA", "Longitude: " + location.getLongitude());
                Log.d("MA", "Altitude: " + location.getAltitude());

                   /* latd.setText("Latitude:" + location.getLatitude());
                    longtd.setText("Longitude:" + location.getLongitude());
                    altd.setText("Altitude:" + location.getAltitude());*/


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                //do something when status is changed
                //status means from where the provider is getting its data
                //previous locations,current locations etc.

            }

            @Override
            public void onProviderEnabled(String provider) {

                //does something when provider is enabled

            }

            @Override
            public void onProviderDisabled(String provider) {
                //does something when provider is disabled

//                if(provider.equals(LocationManager.GPS_PROVIDER))
//                {
//
//                }
//

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //we set the listner to the location manager


        locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, loclis);
        /*
        1st para=Provider i.e. from where are we getting our data; our phones network or gps?
        2nd para=time in milliseconds after which locations must be updated
        3rd para=distance in meters after which we want updating to occur
        4th para=locations listner
         */

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(LoginActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, loclis);
            }
        }.start();

    }

    private void MobileEmailAPI() {
        if (CommonUtils.isOnline(LoginActivity.this)) {
            sessonManager.showProgress(LoginActivity.this);
            LoginRequest loginRequest=new LoginRequest();
            loginRequest.setMobile(editusername.getText().toString());
            Call<LoginModel>call=ApiExecutor.getApiService(LoginActivity.this)
                    .loginUser(loginRequest);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if((!editusername.getText().toString().isEmpty())){
                                startActivity(new Intent(LoginActivity.this,OtpActivity.class)
                                        .putExtra("type","login")
                                        .putExtra("mobile",editusername.getText().toString())
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }else {
                                sessonManager.getToken();
                                PrefUtils.getAppId(LoginActivity.this);
                                //sessonManager.setToken(response.body().getToken());
                                startActivity(new Intent(LoginActivity.this,MapsActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finishAffinity();
                                finish();
                            }
                        }else {
                            Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(LoginActivity.this, getString(R.string.please_check_network));
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    /*Todo:- Current Location*/
    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(LoginActivity.this)
                    .addOnConnectionFailedListener(LoginActivity.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); // this is the key ingredient


            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                gpsCheck = true;
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                gpsCheck = false;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.d("MA", "Window has been closed");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("GPS", gpsCheck);
        super.onSaveInstanceState(savedInstanceState);
    }

}
