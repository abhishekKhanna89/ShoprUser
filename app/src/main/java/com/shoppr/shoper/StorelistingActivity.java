package com.shoppr.shoper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.StoreList.Category;
import com.shoppr.shoper.Model.ShoprList.Data;
import com.shoppr.shoper.Model.StoreList.Store;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.SotoreDetailsActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelosting);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        TextView textAddress = findViewById(R.id.textAddress);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String address = getIntent().getStringExtra("address");
        //Log.d("ress",address);
        textAddress.setText(address);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sessonManager = new SessonManager(this);
        storerecyclerview = findViewById(R.id.storerecyclerview);
        storerecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(StorelistingActivity.this, 1);
        storerecyclerview.setLayoutManager(layoutManager);
        checkedFriends=new ArrayList<String>();
        setmethod();


    }

    public void setmethod() {
        Toast.makeText(this, ""+checkedFriends, Toast.LENGTH_SHORT).show();
        if (CommonUtils.isOnline(StorelistingActivity.this)) {
            sessonManager.showProgress(StorelistingActivity.this);
            Call<StoreListModel> call = ApiExecutor.getApiService(this)
                    .apiStoreList(sessonManager.getLat(), sessonManager.getLon(),checkedFriends,"");
            //Log.d("location",sessonManager.getLat()+":"+sessonManager.getLon());
            call.enqueue(new Callback<StoreListModel>() {
                @Override
                public void onResponse(Call<StoreListModel> call, Response<StoreListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StoreListModel storeListModel = response.body();
                            if (storeListModel.getData().getStores() != null) {
                                storeList = storeListModel.getData().getStores();
                                storeadapter = new Storeadapter(storeList, StorelistingActivity.this);
                                storerecyclerview.setAdapter(storeadapter);
                                storeadapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<StoreListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        } else {
            CommonUtils.showToastInCenter(StorelistingActivity.this, getString(R.string.please_check_network));
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void sort(View view) {

    }

    public void filter(View view) {

      if(checkedFriends.size()>0)
      {

          checkedFriends.clear();
      }
        showCallingDailouge();
    }

    /*Todo:- FullScreen*/
    private void showCallingDailouge() {
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
            Call<StoreListModel> call = ApiExecutor.getApiService(this)
                    .apiStoreCategoryList(sessonManager.getLat(), sessonManager.getLon());
            //Log.d("location",sessonManager.getLat()+":"+sessonManager.getLon());
            call.enqueue(new Callback<StoreListModel>() {
                @Override
                public void onResponse(Call<StoreListModel> call, Response<StoreListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StoreListModel storeListModel = response.body();
                            Gson gson=new Gson();
                            String jshsh=gson.toJson(storeListModel);
                            Log.d("ress",jshsh);
                            if (storeListModel.getData().getCategories() != null) {
                                categoryList=storeListModel.getData().getCategories();
                                FullScreenAdapter fullScreenAdapter=new FullScreenAdapter(StorelistingActivity.this,categoryList);
                                filterRecycler.setAdapter(fullScreenAdapter);
                                fullScreenAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<StoreListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
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
                        Log.d("hello", "abcd");
                        checkedFriends.add(String.valueOf(categoryList.get(position).getId()));

                        //String check = String.valueOf(categoryList.get(position).getId());
                    } else {



                       // checkedFriends.clear();

                       // Log.d("hello", checkedFriends.remove(position));
                        if(checkedFriends.size()>0) {
                            checkedFriends.remove(position);
                            Log.d("hello", String.valueOf(checkedFriends));
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
    public class Storeadapter extends RecyclerView.Adapter<Storeadapter.ViewHolder> {
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
                            .putExtra("storeId", storeList.get(position).getId()));
                   /* Intent intentone = new Intent(StorelistingActivity.this, SotoreDetailsActivity.class);
                    startActivity(intentone);*/
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
