package com.shoppr.shoper.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.shoppr.shoper.util.CustomMapInfoWindow;
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
        GoogleApiClient.OnConnectionFailedListener*/ {
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker marker;
    SessonManager sessonManager;
    String messageId;
    public static double lat, lang, lat_driver, lang_driver;
    LatLng customer, driver;
    ArrayList<LatLng> dvrLoc = new ArrayList();
    String str_dest, str_org, url;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    CountDownTimer countDownTimer;
    String location_address;
    Marker drivermarker;

    public int checkforroutes;
    private Polyline lastPolyline;
    public int checkforzoommarker;
    int i = 0;
    private float start_rotation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_loaction);

        sessonManager = new SessonManager(this);

        messageId = getIntent().getStringExtra("chatId");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkforroutes = 2;
        checkforzoommarker = 2;

        dvrLoc.add(new LatLng(28.565532, 77.244447));
        dvrLoc.add(new LatLng(28.566052, 77.244417));
        dvrLoc.add(new LatLng(28.566918, 77.244311));
        dvrLoc.add(new LatLng(28.567478, 77.243068));
        dvrLoc.add(new LatLng(28.568139, 77.241460));
        dvrLoc.add(new LatLng(28.568677, 77.240057));
        dvrLoc.add(new LatLng(28.569002, 77.239112));
        dvrLoc.add(new LatLng(28.568094, 77.239342));
        dvrLoc.add(new LatLng(28.567310, 77.239687));
        dvrLoc.add(new LatLng(28.566627, 77.239980));
        dvrLoc.add(new LatLng(28.565988, 77.240184));
        dvrLoc.add(new LatLng(28.565797, 77.239763));
        dvrLoc.add(new LatLng(28.565898, 77.238908));
        dvrLoc.add(new LatLng(28.566268, 77.238653));

    }

    private void viewTrackLoaction() {
        if (CommonUtils.isOnline(TrackLoactionActivity.this)) {
            Call<TrackLoactionModel> call = ApiExecutor.getApiService(this)
                    .apiTrackLocation("Bearer " + sessonManager.getToken(), Integer.parseInt(messageId));
            call.enqueue(new Callback<TrackLoactionModel>() {
                @Override
                public void onResponse(Call<TrackLoactionModel> call, Response<TrackLoactionModel> response) {
                    if (response.body() != null) {
                        TrackLoactionModel trackLoactionModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            //Toast.makeText(TrackLoactionActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();

                            if (trackLoactionModel.getData().getShoppr() != null) {
                                /*Todo:- Customer Lat Lang*/
                                lat = trackLoactionModel.getData().getCustomer().getLat();
                                lang = trackLoactionModel.getData().getCustomer().getLang();
                                Log.d("CustomerLaaLANG", lat + "::" + lang);

                                customer = new LatLng(lat, lang);

                                lat_driver = trackLoactionModel.getData().getShoppr().getLat();
                                lang_driver = trackLoactionModel.getData().getShoppr().getLang();

                                //lat_driver=28.565954;
                                //lang_driver=77.246269;

                                /*if (i<14) {
                                    lat_driver = dvrLoc.get(i).latitude;
                                    lang_driver = dvrLoc.get(i).longitude;
                                    i++;
                                }else
                                    i=0;*/

                                Log.d("DriverLaaLANG", lat_driver + "::" + lang_driver);
                                driver = new LatLng(lat_driver, lang_driver);

                                if (checkforzoommarker == 2) {
                                    checkforzoommarker = 3;
                                    Geocoder geocoder = new Geocoder(TrackLoactionActivity.this);
                                    List<Address> list = null;
                                    try {
                                        list = geocoder.getFromLocation(lat, lang, 1);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < list.size(); i++) {
                                        Address address = list.get(i);
                                        String localitys = address.getLocality();
                                        location_address = address.getAddressLine(0);
                                    }
                                    int heightC = 120;
                                    int widthC = 80;
                                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.pin_logo);
                                    Bitmap b = bitmapdraw.getBitmap();
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, widthC, heightC, false);
                                    CustomMapInfoWindow customMapInfoWindow = new CustomMapInfoWindow(TrackLoactionActivity.this);
                                    mMap.setInfoWindowAdapter(customMapInfoWindow);
                                    mMap.addMarker(new MarkerOptions().position(customer).title(location_address))
                                            .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customer, 15));

                                    Log.d("DriverLaaLANG", lat_driver + "::" + lang_driver);
                                    //  driver = new LatLng(lat_driver, lang_driver);
                                    Geocoder geocoderD = new Geocoder(TrackLoactionActivity.this);
                                    List<Address> listD = null;
                                    try {
                                        listD = geocoderD.getFromLocation(lat_driver, lang_driver, 1);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Address addressD = listD.get(0);
                                    String location_addressD = addressD.getAddressLine(0);
                                    int heightD = 120;
                                    int widthD = 80;
                                    BitmapDrawable bitmapdrawD = (BitmapDrawable) getResources().getDrawable(R.drawable.bike_icon);
                                    Bitmap bD = bitmapdrawD.getBitmap();
                                    Bitmap smallMarkerD = Bitmap.createScaledBitmap(bD, widthD, heightD, false);

                                    drivermarker = mMap.addMarker(new MarkerOptions().position(driver).title(location_addressD));
                                    drivermarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarkerD));

                                    Location dvrLocation = new Location("");
                                    dvrLocation.setLatitude(driver.latitude);
                                    dvrLocation.setLongitude(driver.longitude);

                                    moveVehicle(drivermarker, dvrLocation);
                                    rotateMarker(drivermarker, dvrLocation.getBearing(), start_rotation);
                                    // drivermarker.setPosition(driver);
                                    drivermarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarkerD));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driver, 15));
                                }

                                if (customer != null || driver != null) {
                                    Log.e("TAG", "getRequestUrl: inside ");
                                    getRequestUrl(customer, driver);
                                    getDeviceLocation(customer, driver);
                                    //viewTrackLoaction();
                                }
                            }
                        } else {
                            Toast.makeText(TrackLoactionActivity.this, trackLoactionModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrackLoactionModel> call, Throwable t) {

                }
            });

        } else {
            CommonUtils.showToastInCenter(TrackLoactionActivity.this, getString(R.string.please_check_network));
        }
    }

    private void moveVehicle(Marker myMarker, Location driver) {
        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (driver.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (driver.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });
    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        viewTrackLoaction();
        callTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callTimer();
    }

    private void callTimer() {
        countDownTimer = new CountDownTimer(10000, 100) {
            public void onTick(long millisUntilFinished) {
                //viewTrackLoaction();
            }

            public void onFinish() {
                viewTrackLoaction();
                countDownTimer.start();
                Log.e("TAG", "onFinish: time called");
            }
        };
        countDownTimer.start();
    }

    /*AIzaSyBq0kgTo_fwzmQpo-z901CFaXfKVqZXma8*/
    private String getRequestUrl(LatLng customer, LatLng driver) {
        if (customer != null || driver != null) {
            //Log.d("LocationService", sydney +":"+aaa);
            str_org = "origin=" + driver.latitude + "," + driver.longitude;
            str_dest = "destination=" + customer.latitude + "," + customer.longitude;

            String sensor = "sensor=true";
            String mode = "mode=driving";
            String output = "json";
            String key = "key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
            String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&" + key;

        }
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            ////////////Get Response
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
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

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

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

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLACK);
                polylineOptions.geodesic(true);
            }

            if (lastPolyline != null) {
                lastPolyline.remove();

                Log.d("removed", "polyline");
            }
            lastPolyline = mMap.addPolyline(polylineOptions);

        }
    }

    public void getDeviceLocation(LatLng customer, LatLng driver) {
        final ArrayList<LatLng> listPoints = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Log.d(TAG, "onComplete: found location!");

                        Log.d("pointsize==", String.valueOf(listPoints.size()));

                        listPoints.add(customer);
                        listPoints.add(driver);

                        Log.d("pointsize==", String.valueOf(listPoints.size()));
                        {
                            if (listPoints.size() == 2) {
                                // checkforroutes=3;

                                //Create the URL to get request from first marker to second marker
                                String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                taskRequestDirections.execute(url);
                                drivermarker.setPosition(driver);

                            }
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