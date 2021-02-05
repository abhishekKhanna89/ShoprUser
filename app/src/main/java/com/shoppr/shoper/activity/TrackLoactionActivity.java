package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shoppr.shoper.Model.TrackLoaction.TrackLoactionModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.DirectionsParser;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TrackLoactionActivity extends AppCompatActivity implements OnMapReadyCallback
       /* LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/{
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker marker;
    SessonManager sessonManager;
    int messageId;
    public static  double lat,lang;
    LatLng sydney,india,aaa;
    private String serverKey = "AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};

    String str_dest,str_org,url;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_loaction);
        sessonManager=new SessonManager(this);

        messageId=getIntent().getIntExtra("messageId",0);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewTrackLoaction();
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
                                 mMap.addMarker(new MarkerOptions().position(india))
                                 .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.scooter));
                                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 15));
                                 //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,12f));
                                 mMap.setPadding(2000, 4000, 2000, 4000);
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

        viewTrackLoaction();

        countDownTimer =  new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                //viewTrackLoaction();

            }

            public void onFinish() {
                viewTrackLoaction();
                countDownTimer.start();

            }
        };
        countDownTimer.start();
        // Add a marker in Sydney and move the camera
        sydney = new LatLng(28.7041, 77.1025);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        mMap.setPadding(2000, 4000, 2000, 4000);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12f));
       // mMap.setPadding(2000, 4000, 2000, 4000);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                aaa=new LatLng(lat,lang);
                if (aaa!=null){
                    getRequestUrl(sydney,aaa);
                    getDeviceLocation(sydney,aaa);
                }
            }
        },2000);

    }
    /*AIzaSyBq0kgTo_fwzmQpo-z901CFaXfKVqZXma8*/
    private String getRequestUrl(LatLng sydney, LatLng aaa) {
        if (sydney !=null||aaa!=null){
            Log.d("LocationService", sydney +":"+aaa);
            str_org = "origin="+ sydney.latitude+","+ sydney.longitude;
            str_dest = "destination="+aaa.latitude+","+aaa.longitude;

            String sensor =  "sensor=true";
            String mode = "mode=driving";
            String output = "json";
            String key = "key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
            String param = str_org+"&"+str_dest+"&"+sensor+"&"+mode;
            url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&"+key;

        }
        return url;
    }
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.connect();

            ////////////Get Response
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(30);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void getDeviceLocation(LatLng sydney, LatLng aaa) {
        final ArrayList<LatLng> listPoints = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "onComplete: found location!");

                            listPoints.add(sydney);
                            listPoints.add(aaa);

                            if (listPoints.size() == 2) {
                                //Create the URL to get request from first marker to second marker
                                String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                taskRequestDirections.execute(url);
                            }


                        } else {
                            Log.d("TAG", "onComplete: current location is null");
                            Toast.makeText(TrackLoactionActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        } catch (SecurityException e) {
            Log.e("TAG", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }



    @Override
    protected void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        countDownTimer.cancel();
        super.onDestroy();
    }

}