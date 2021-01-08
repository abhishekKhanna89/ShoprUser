package com.shoppr.shoper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.shoppr.shoper.Model.StoreList.Store;
import com.shoppr.shoper.Model.StoreList.StoreListModel;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.MyAccount;
import com.shoppr.shoper.activity.SotoreDetailsActivity;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import model.Storemodel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorelistingActivity extends AppCompatActivity {
    RecyclerView storerecyclerview;
    Storeadapter storeadapter;
    List<Store> storeList;
    SessonManager sessonManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelosting);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sessonManager=new SessonManager(this);
        storerecyclerview=findViewById(R.id.storerecyclerview);
        storerecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(StorelistingActivity.this,1);
        storerecyclerview.setLayoutManager(layoutManager);
        setmethod();
    }
    private void setmethod(){
        if (CommonUtils.isOnline(StorelistingActivity.this)) {
            sessonManager.showProgress(StorelistingActivity.this);
            Call<StoreListModel>call= ApiExecutor.getApiService(this)
                    .apiStoreList(sessonManager.getLat(),sessonManager.getLon());
            call.enqueue(new Callback<StoreListModel>() {
                @Override
                public void onResponse(Call<StoreListModel> call, Response<StoreListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            StoreListModel storeListModel=response.body();
                            if(storeListModel.getData().getStores()!=null) {
                                storeList=storeListModel.getData().getStores();
                                storeadapter=new Storeadapter(storeList,StorelistingActivity.this);
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
        }else {
            CommonUtils.showToastInCenter(StorelistingActivity.this, getString(R.string.please_check_network));
        }
    }

    public class Storeadapter extends RecyclerView.Adapter<Storeadapter.ViewHolder>{
        List<Store>storeList;
        Context mcontext;

        public Storeadapter(List<Store> storeList, Context mcontext) {
            this.storeList = storeList;
            this.mcontext = mcontext;
        }

        @NonNull
        @Override
        public Storeadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater minflater=LayoutInflater.from(mcontext);
            view=minflater.inflate(R.layout.itemstoringlist,parent,false);
            return new Storeadapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Storeadapter.ViewHolder holder, final int position) {
            Picasso.get().load(storeList.get(position).getImage()).into(holder.imageview);
            holder.textname.setText(storeList.get(position).getStoreName());
            holder.textdescription.setText(storeList.get(position).getStoreType());
            holder.distance.setText(String.valueOf(storeList.get(position).getDistance()));
            holder.cardviewstorelist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(StorelistingActivity.this,SotoreDetailsActivity.class)
                    .putExtra("storeId",storeList.get(position).getId()));
                   /* Intent intentone = new Intent(StorelistingActivity.this, SotoreDetailsActivity.class);
                    startActivity(intentone);*/
                }
            });
            String isSale=String.valueOf(storeList.get(position).getIsSale());
            if (isSale!=null&&isSale.equalsIgnoreCase("1")){
                holder.saleOnLayout.setVisibility(View.VISIBLE);
            }else {
                holder.saleOnLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView textname,distance,textdescription,textcancel,textsave;
            ImageView imageview;
            CardView cardviewstorelist;
            RelativeLayout saleOnLayout;
            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                imageview=itemView.findViewById(R.id.image_order);
                textname = itemView.findViewById(R.id.textname);
                textdescription = itemView.findViewById(R.id.textdescription);
                distance = itemView.findViewById(R.id.distance);
                cardviewstorelist=itemView.findViewById(R.id.cardviewstorelist);
                saleOnLayout=itemView.findViewById(R.id.saleOnLayout);
                cardviewstorelist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentone = new Intent(StorelistingActivity.this, SotoreDetailsActivity.class);
                        startActivity(intentone);
                    }
                });
            }}
    }
}
