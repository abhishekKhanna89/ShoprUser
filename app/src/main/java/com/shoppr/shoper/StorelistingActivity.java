package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.Model.StoreList.Category;
import com.shoppr.shoper.Model.StoreList.Store;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.SotoreDetailsActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorelistingActivity extends AppCompatActivity {
    RecyclerView storerecyclerview;
    Storeadapter storeadapter;
    List<Store> storeList;
    SessonManager sessonManager;
    List<Category>categoryList;
    ArrayList<String> checkedFriends ;
    String radioName;

    private SearchView searchView;
    String search;
    String address,cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelosting);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sessonManager = new SessonManager(this);
        searchView=findViewById(R.id.searchView);

        TextView textAddress = findViewById(R.id.textAddress);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        address = getIntent().getStringExtra("address");
        cityName=getIntent().getStringExtra("city");
        Log.d("cityName",cityName);
        textAddress.setText(address);
        sessonManager.setEditaddress(address);
        sessonManager.setCityName(cityName);




        storerecyclerview = findViewById(R.id.storerecyclerview);
        storerecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(StorelistingActivity.this, 1);
        storerecyclerview.setLayoutManager(layoutManager);
        checkedFriends=new ArrayList<String>();
        setmethod();

        /*Todo:Search Method*/
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search=query;
                setmethod();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                search=query;
                setmethod();
                return false;
            }
        });

    }
    public void setmethod() {
        if (CommonUtils.isOnline(StorelistingActivity.this)) {
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+sessonManager.getLat()+","+sessonManager.getLon()+"&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";
           // String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + address + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("EditLocationResponse", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //Log.d("resJsonAll",""+jsonObject);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        Log.d("resJsonAll",""+jsonArray);
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        JSONArray jsonArray1=jsonObject1.getJSONArray("address_components");
                        String location = jsonArray1.toString();
//sessonManager.showProgress(StorelistingActivity.this);
                            Call<StoreListModel> call = ApiExecutor.getApiService(StorelistingActivity.this)
                                    .apiStoreList("Bearer " + sessonManager.getToken(),sessonManager.getLat(), sessonManager.getLon(),checkedFriends,search,radioName,
                                            location,cityName);
                            //Log.d("location",sessonManager.getLat()+":"+sessonManager.getLon());
                            call.enqueue(new Callback<StoreListModel>() {
                                @Override
                                public void onResponse(Call<StoreListModel> call, Response<StoreListModel> response) {
                                    //sessonManager.hideProgress();
                                    if (response.body() != null) {
                                        StoreListModel storeListModel = response.body();
                                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                            if (storeListModel.getData().getStores() != null) {
                                                String hlw=new Gson().toJson(storeListModel);
                                                Log.d("Hlw",hlw);
                                                storeList = storeListModel.getData().getStores();
                                                String aaa=new Gson().toJson(storeList);
                                                //Log.d("jsjsjs",aaa);
                                                storeadapter = new Storeadapter(storeList, StorelistingActivity.this);
                                                storerecyclerview.setAdapter(storeadapter);
                                                storeadapter.notifyDataSetChanged();
                                            }
                                        }else {
                                            Toast.makeText(StorelistingActivity.this, ""+storeListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                                    Call<LogoutModel>call1=ApiExecutor.getApiService(StorelistingActivity.this)
                                                            .apiLogoutStatus("Bearer "+sessonManager.getToken());
                                                    call1.enqueue(new Callback<LogoutModel>() {
                                                        @Override
                                                        public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                                            if (response.body()!=null) {
                                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                                    AuthenticationUtils.deauthenticate(StorelistingActivity.this, isSuccess -> {
                                                                        if (getApplication() != null) {
                                                                            sessonManager.setToken("");
                                                                            PrefUtils.setAppId(StorelistingActivity.this,"");
                                                                            Toast.makeText(StorelistingActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(StorelistingActivity.this, LoginActivity.class));
                                                                            finishAffinity();

                                                                        }else {

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
                                public void onFailure(Call<StoreListModel> call, Throwable t) {
                                    //sessonManager.hideProgress();
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
            CommonUtils.showToastInCenter(StorelistingActivity.this, getString(R.string.please_check_network));
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void sort(View view) {
        bottomSheetDailog();
    }

    private void bottomSheetDailog() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.bottom_sheet_layout,null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        RadioGroup radioGroup=bottomSheetDialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radio=group.findViewById(checkedId);
                if (null!=radio&&checkedId>-1){
                    String name=radio.getText().toString();
                    if (name.equalsIgnoreCase("By Name")){
                         radioName="name";
                         setmethod();
                         bottomSheetDialog.dismiss();
                    }else if(name.equalsIgnoreCase("By Distance")){
                        radioName="distance";
                        setmethod();
                        bottomSheetDialog.dismiss();
                    }
                }


            }
        });

        bottomSheetDialog.show();
    }

    public void filter(View view) {

      if(checkedFriends.size()>0)
      {
          checkedFriends.clear();
      }
        fullScreenDailouge();
    }

    /*Todo:- FullScreen*/
    private void fullScreenDailouge() {
        final Dialog dialog = new Dialog(StorelistingActivity.this, R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getLayoutInflater().inflate(R.layout.fullscreen_filter_layout
                , null));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        ImageView finishBack=dialog.findViewById(R.id.finishBack);
        finishBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });
        Button applyBtn=dialog.findViewById(R.id.applyBtn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setmethod();
                dialog.dismiss();
            }
        });
        RecyclerView filterRecycler=dialog.findViewById(R.id.filterRecycler);
        filterRecycler.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        if (CommonUtils.isOnline(StorelistingActivity.this)) {
            sessonManager.showProgress(StorelistingActivity.this);
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+sessonManager.getLat()+","+sessonManager.getLon()+"&key=AIzaSyA9weSsdSDj-mOYVOc1swqsew5J2QOYCGk";
            //String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + address + "&" + "key=AIzaSyA38xR5NkHe1OsEAcC1aELO47qNOE3BL-k";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("EditLocationResponse", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //Log.d("resJsonAll",""+jsonObject);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        Log.d("resJsonAll",""+jsonArray);
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        JSONArray jsonArray1=jsonObject1.getJSONArray("address_components");
                        String location = jsonArray1.toString();
                            Call<StoreListModel> call = ApiExecutor.getApiService(StorelistingActivity.this)
                                    .apiStoreCategoryList("Bearer " + sessonManager.getToken(),sessonManager.getLat(), sessonManager.getLon(),
                                            location,cityName);
                            //Log.d("location",sessonManager.getLat()+":"+sessonManager.getLon());
                            call.enqueue(new Callback<StoreListModel>() {
                                @Override
                                public void onResponse(Call<StoreListModel> call, Response<StoreListModel> response) {
                                    sessonManager.hideProgress();
                                    if (response.body() != null) {
                                        StoreListModel storeListModel = response.body();
                                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                            Gson gson=new Gson();
                                            String jshsh=gson.toJson(storeListModel);
                                            Log.d("ress",jshsh);
                                            if (storeListModel.getData().getCategories() != null) {
                                                categoryList=storeListModel.getData().getCategories();
                                                FullScreenAdapter fullScreenAdapter=new FullScreenAdapter(StorelistingActivity.this,categoryList);
                                                filterRecycler.setAdapter(fullScreenAdapter);
                                                fullScreenAdapter.notifyDataSetChanged();
                                            }
                                        }else {
                                            Toast.makeText(StorelistingActivity.this, ""+storeListModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                                    Call<LogoutModel>call1=ApiExecutor.getApiService(StorelistingActivity.this)
                                                            .apiLogoutStatus("Bearer "+sessonManager.getToken());
                                                    call1.enqueue(new Callback<LogoutModel>() {
                                                        @Override
                                                        public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                                            if (response.body()!=null) {
                                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                                    AuthenticationUtils.deauthenticate(StorelistingActivity.this, isSuccess -> {
                                                                        if (getApplication() != null) {
                                                                            sessonManager.setToken("");
                                                                            PrefUtils.setAppId(StorelistingActivity.this,"");
                                                                            Toast.makeText(StorelistingActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(StorelistingActivity.this, LoginActivity.class));
                                                                            finishAffinity();

                                                                        }else {

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
                                public void onFailure(Call<StoreListModel> call, Throwable t) {
                                    sessonManager.hideProgress();
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
            CommonUtils.showToastInCenter(StorelistingActivity.this, getString(R.string.please_check_network));
        }



        dialog.show();


    }
    /*Todo:- FullScreen Adapter*/
    public class FullScreenAdapter extends RecyclerView.Adapter<FullScreenAdapter.Holder>{
        List<Category>categoryList;
        Context context;
        public FullScreenAdapter(Context context,List<Category>categoryList){
            this.context=context;
            this.categoryList=categoryList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_filter,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.checkbox.setText(categoryList.get(position).getName());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedFriends.add(String.valueOf(categoryList.get(position).getId()));
                        //String check = String.valueOf(categoryList.get(position).getId());
                    } else {
                        for (int i=0;i<checkedFriends.size();i++){
                            checkedFriends.remove(i);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            CheckBox checkbox;
            public Holder(@NonNull View itemView) {
                super(itemView);
                checkbox=itemView.findViewById(R.id.checkbox);
            }
        }
    }


    /*Todo:-Main Response*/
    public class Storeadapter extends RecyclerView.Adapter<Storeadapter.ViewHolder>  {
        List<Store> storeList;
        Context mcontext;
        public Storeadapter(List<Store> storeList, Context mcontext) {
            this.storeList = storeList;
            this.mcontext = mcontext;

        }

        @NonNull
        @Override
        public Storeadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater minflater = LayoutInflater.from(mcontext);
            view = minflater.inflate(R.layout.itemstoringlist, parent, false);
            return new Storeadapter.ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Storeadapter.ViewHolder holder, final int position) {
            Picasso.get().load(storeList.get(position).getImage()).into(holder.imageview);
            holder.textname.setText(storeList.get(position).getStoreName());
            holder.textdescription.setText(storeList.get(position).getStoreType());
            holder.distance.setText(storeList.get(position).getDistance() + " Km");
            holder.cardviewstorelist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(StorelistingActivity.this, SotoreDetailsActivity.class)
                            .putExtra("storeId", storeList.get(position).getId()).putExtra("address",sessonManager.getEditaddress()));
                }
            });
            String isSale = String.valueOf(storeList.get(position).getIsSale());
            if (isSale != null && isSale.equalsIgnoreCase("1")) {
                holder.saleOnLayout.setVisibility(View.VISIBLE);
            } else {
                holder.saleOnLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textname, distance, textdescription, textcancel, textsave;
            ImageView imageview;
            CardView cardviewstorelist;
            RelativeLayout saleOnLayout;

            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                imageview = itemView.findViewById(R.id.image_order);
                textname = itemView.findViewById(R.id.textname);
                textdescription = itemView.findViewById(R.id.textdescription);
                distance = itemView.findViewById(R.id.distance);
                cardviewstorelist = itemView.findViewById(R.id.cardviewstorelist);
                saleOnLayout = itemView.findViewById(R.id.saleOnLayout);
                cardviewstorelist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentone = new Intent(StorelistingActivity.this, SotoreDetailsActivity.class);
                        startActivity(intentone);
                    }
                });
            }
        }
    }

}
