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
import android.widget.TextView;

import java.util.ArrayList;

import com.shoppr.shoper.activity.SotoreDetailsActivity;
import model.Storemodel;

public class StorelistingActivity extends AppCompatActivity {
    RecyclerView storerecyclerview;
    Storeadapter storeadapter;
    ArrayList<Storemodel> storemodel;
    String[] names = {"Manglam Store","Manglam Store","Manglam Store","Manglam Store","Manglam Store"};
    String[] description = {"Grocery","Grocery","Grocery","Grocery","Grocery"};
    String[] distance = {"0.45 KM","0.45 KM","0.45 KM","0.45 KM","0.45 KM"};
    Integer[] image={R.drawable.images,R.drawable.images,R.drawable.images,R.drawable.images,R.drawable.images};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelosting);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
      //  getSupportActionBar().setTitle("Store Listing");
      //  getSupportActionBar().hide();
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
// Remove default title text
       //getSupportActionBar().setDisplayShowTitleEnabled(true);
// Get access to the custom title view
      //  TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setLogo(R.mipmap.ic_launcher);
       // getSupportActionBar().setDisplayUseLogoEnabled(true);
        storerecyclerview=findViewById(R.id.storerecyclerview);
        storemodel=new ArrayList<Storemodel>();
        setmethod();
    }
    private void setmethod(){
        storerecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(StorelistingActivity.this,1);
        storerecyclerview.setLayoutManager(layoutManager);
        storemodel.clear();
        for (int i=0;i<image.length;i++){
            Storemodel featuremodel=new Storemodel();
            featuremodel.setImage(image[i]);
            featuremodel.setName(names[i]);
            featuremodel.setDescription(description[i]);
            featuremodel.setDistance(distance[i]);
            storemodel.add(featuremodel);
        }
        storeadapter=new Storeadapter(storemodel,StorelistingActivity.this);
        storerecyclerview.setAdapter(storeadapter);
        storerecyclerview.setFocusable(false);

    }

    public class Storeadapter extends RecyclerView.Adapter<Storeadapter.ViewHolder>{
        ArrayList<Storemodel>mData;
        Context mcontext;

        public Storeadapter(ArrayList<Storemodel> mData, Context mcontext) {
            this.mData = mData;
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
             holder.imageview.setImageResource(mData.get( position).getImage());
           // Picasso.get().load(mData.get(position).getImage()).into(holder.imgarrival);
            holder.textname.setText(mData.get(position).getName());
            holder.textdescription.setText(mData.get(position).getDescription());
            holder.distance.setText(mData.get(position).getDistance());
            holder.cardviewstorelist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentone = new Intent(StorelistingActivity.this, SotoreDetailsActivity.class);
                    startActivity(intentone);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder{
          //  CircleImageView imgarrival;
            TextView textname,distance,textdescription,textcancel,textsave;
            ImageView imageview;
            CardView cardviewstorelist;

            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                imageview=itemView.findViewById(R.id.image_order);
                textname = itemView.findViewById(R.id.textname);
                textdescription = itemView.findViewById(R.id.textdescription);
                distance = itemView.findViewById(R.id.distance);
                cardviewstorelist=itemView.findViewById(R.id.cardviewstorelist);
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
