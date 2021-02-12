package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.ShareLocationRequest;
import com.shoppr.shoper.util.CheckNetwork;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class EditLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "EditLocationActivity";
    AutoCompleteTextView autoCompleteTextViewLoaction;
    Button addBTN;
    LatLng latLng;
    Marker marker;
    Location currentLocation;
    public static String latitude, longitude, location_address;
    double lateee;
    double lngeee;
    Circle circle;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    ArrayAdapter<String> arrayAdapter_stateLocation;
    ArrayList<String> arrListLocation = new ArrayList<>();
    ImageView imgClose;
    String locality, subLocality;
    SessonManager sessonManager;
    int chat_id;
    String merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        sessonManager = new SessonManager(this);
        addBTN = findViewById(R.id.btn_map_address);
        imgClose = findViewById(R.id.imgClose);
        chat_id = getIntent().getIntExtra("chatId", 0);

        merchant = getIntent().getStringExtra("merchant");
        autoCompleteTextViewLoaction = findViewById(R.id.AutoComplte_tv_home);

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location_address = autoCompleteTextViewLoaction.getText().toString();
                /*location_address= autoCompleteTextViewLoaction.getText().toString();

                Geocoder coder = new Geocoder(EditLocationActivity.this);
                List<Address> address;

                try {
                    //Get latLng from String
                    address = coder.getFromLocationName(location_address, 5);

                    //check for null
                    if (address != null) {

                        //Lets take first possibility from the all possibilities.
                        try {
                            Address location = address.get(0);
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            latitude = String.valueOf(latLng.latitude);
                            longitude = String.valueOf(latLng.longitude);


                            //sharedPreferences.edit().putString("lat", ""+latitude).apply();
                            //sharedPreferences.edit().putString("lng", ""+longitude).apply();

                            //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            mMap.addMarker(new MarkerOptions().position(latLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f));




//                    Log.d("asdaskjasd",latLng.latitude+"   "+latLng.longitude);
                            getAddress(latLng.latitude,latLng.longitude);
                        } catch (IndexOutOfBoundsException er) {
                            Toast.makeText(EditLocationActivity.this, "Location isn't available", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                if (merchant != null) {
                   /* startActivity(new Intent(EditLocationActivity.this, RegisterMerchantActivity.class)
                            .putExtra("location_address", location_address)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/

                    /*Intent intent = new Intent();
                    intent.putExtra("location_address", location_address);
                    setResult(1, intent);*/
                    Intent intent = new Intent();
                    intent.putExtra("location_address", location_address);
                    setResult(RESULT_OK, intent);
                    finish();


                } else {
                    startActivity(new Intent(EditLocationActivity.this, MapsActivity.class)
                            .putExtra("location_address", location_address)
                            .putExtra("value", 1)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }


                //onBackPressed();


            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!autoCompleteTextViewLoaction.getText().toString().isEmpty()) {
                    autoCompleteTextViewLoaction.getText().clear();
                }

            }
        });

        autoCompleteTextViewLoaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToLocationFromAddress(autoCompleteTextViewLoaction.getText().toString());
            }
        });


        autoCompleteTextViewLoaction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CheckNetwork.isInternetAvailable(getApplicationContext())) //returns true if internet available
                {
                    if (s.toString().length() >= 2) {
                        arrListLocation.clear();
                        hitUrlForSearchLocation(String.valueOf(s));
                    } else if (s.toString().length() < 3) {
                        arrListLocation.clear();
                        if (arrayAdapter_stateLocation != null) {
                            arrayAdapter_stateLocation.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationPermissionsGranted = true;
            initMap();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocationPermission();
        }
    }




    public void goToLocationFromAddress(String strAddress) {
        mMap.clear();
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress, 5);

            //check for null
            if (address != null) {

                //Lets take first possibility from the all possibilities.
                try {
                    Address location = address.get(0);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    latitude = String.valueOf(latLng.latitude);
                    longitude = String.valueOf(latLng.longitude);

                    //Log.d("asdaskjasd",latitude+"   "+longitude);
                    //sharedPreferences.edit().putString("lat", ""+latitude).apply();
                    //sharedPreferences.edit().putString("lng", ""+longitude).apply();

                    //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f));

//                    Log.d("asdaskjasd",latLng.latitude+"   "+latLng.longitude);
                    getAddress(latLng.latitude, latLng.longitude);
                } catch (IndexOutOfBoundsException er) {
                    Toast.makeText(this, "Location isn't available", Toast.LENGTH_SHORT).show();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAddress(double lat, double log) {
        Geocoder geocoder = new Geocoder(EditLocationActivity.this);

        //  String addresses = String.valueOf(geocoder.getFromLocation(lat, log, 1));

        List<Address> addressess = null;
        try {
            addressess = geocoder.getFromLocation(lat, log, 1);
            locality = addressess.get(0).getLocality();
            subLocality = addressess.get(0).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // tv_location.setText(addresses.get(0).getAddressLine(0));
        //Log.d("sasajksadsad",locality+"\n"+subLocality);


        //  Log.d("sasajkdsxad",city);
        //   Toast.makeText(getApplicationContext(), ""+addressess.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void hitUrlForSearchLocation(final String key) {
        String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + key + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("ResponseSearch", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("predictions");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject searchObj = jsonArray.getJSONObject(i);
                        arrListLocation.add(searchObj.getString("description"));

                    }

                    //location_address = arrListLocation.get(0);

                    //   Log.d("sladsjjdaldasd",location_address);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    arrayAdapter_stateLocation = new ArrayAdapter<String>(getApplicationContext(), R.layout.search_item, R.id.txt_search_place, arrListLocation);
                    autoCompleteTextViewLoaction.setAdapter(arrayAdapter_stateLocation);//setting the adapter data into the AutoCompleteTextView
                    arrayAdapter_stateLocation.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                arrListLocation.clear();
            }
        }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditLocationActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    public void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                lateee = currentLocation.getLatitude();
                                lngeee = currentLocation.getLongitude();
                                latitude = String.valueOf(currentLocation.getLatitude());
                                longitude = String.valueOf(currentLocation.getLongitude());
                                if (marker != null) {
                                    marker.remove();
                                    mMap.clear();
                                }

                                //Log.d("sdyuvftr", latitude);
                                //Log.d("mvbfryubvj", longitude);
                                //sharedPreferences.edit().putString("lat", ""+latitude).apply();
                                //sharedPreferences.edit().putString("lng", ""+longitude).apply();

                                Geocoder geocoder = new Geocoder(EditLocationActivity.this);
                                List<Address> list = null;
                                try {
                                    list = geocoder.getFromLocation(lateee, lngeee, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Address address = list.get(0);
                                String localitys = address.getLocality();
                                location_address = address.getAddressLine(0);
                                locality = address.getLocality();
                                subLocality = address.getSubLocality();
                                autoCompleteTextViewLoaction.setText(address.getAddressLine(0));

                                LatLng latLng = new LatLng(Double.parseDouble(latitude), (Double.parseDouble(longitude)));
                                //Log.d("cheklatlong", String.valueOf(latLng));
                                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(localitys));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f));
                                circle = DrawCircle(latLng);


                            } else {
                            }

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(EditLocationActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private Circle DrawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(5000)
                .fillColor(R.color.colorPrimary)
                .strokeColor(R.color.colorPrimary);
        // .strokeWidth()
        return mMap.addCircle(circleOptions);

    }

}