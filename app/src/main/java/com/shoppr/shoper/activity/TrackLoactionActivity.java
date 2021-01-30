package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shoppr.shoper.Model.TrackLoaction.TrackLoactionModel;
import com.shoppr.shoper.OtpActivity;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.tecorb.hrmovecarmarkeranimation.AnimationClass.HRMarkerAnimation;
import com.tecorb.hrmovecarmarkeranimation.CallBacks.UpdateLocationCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class TrackLoactionActivity extends AppCompatActivity implements OnMapReadyCallback,DirectionCallback
       /* LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/{
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker marker;
    SessonManager sessonManager;
    int messageId;
    public static  double lat,lang;
    LatLng sydney,india,point;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;
    private String serverKey = "AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_loaction);
        sessonManager=new SessonManager(this);

        messageId=getIntent().getIntExtra("messageId",0);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();
        viewTrackLoaction();
        requestDirection();
    }

    private void viewTrackLoaction() {
        if (CommonUtils.isOnline(TrackLoactionActivity.this)) {
            Call<TrackLoactionModel>call= ApiExecutor.getApiService(this)
                    .apiTrackLocation("Bearer "+sessonManager.getToken(),messageId);
            call.enqueue(new Callback<TrackLoactionModel>() {
                @Override
                public void onResponse(Call<TrackLoactionModel> call, Response<TrackLoactionModel> response) {
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            TrackLoactionModel trackLoactionModel=response.body();
                            if (trackLoactionModel.getData().getShoppr()!=null){
                                 lat=trackLoactionModel.getData().getShoppr().getLat();
                                 lang=trackLoactionModel.getData().getShoppr().getLang();
                                 india=new LatLng(lat,lang);
                                 mMap.addMarker(new MarkerOptions().position(india).title("Marker in Sydney"))
                                 .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 15));
                                 //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,12f));
                                //mMap.setPadding(2000, 4000, 2000, 4000);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrackLoactionModel> call, Throwable t) {

                }
            });

        }else {
            CommonUtils.showToastInCenter(TrackLoactionActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        sydney = new LatLng(28.7041, 77.1025);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12f));
       // mMap.setPadding(2000, 4000, 2000, 4000);



    }

    private void requestDirection() {
        GoogleDirection.withServerKey(serverKey)
                .from(india)
                .to(sydney)
                .transportMode(TransportMode.WALKING)
                .alternativeRoute(true)
                .execute(this);
    }


    @Override
    public void onDirectionSuccess(@Nullable Direction direction) {
        Toast.makeText(this, ""+direction, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < direction.getRouteList().size(); i++) {
            Route route = direction.getRouteList().get(i);
            String color = colors[i % colors.length];
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.parseColor(color)));
        }
    }

    @Override
    public void onDirectionFailure(@NonNull Throwable t) {

    }
}