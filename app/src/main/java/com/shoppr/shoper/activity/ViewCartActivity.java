package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.CartCancel.CartCancelModel;
import com.shoppr.shoper.Model.CartView.CartViewModel;
import com.shoppr.shoper.Model.CartView.Item;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCartActivity extends AppCompatActivity {
    SessonManager sessonManager;
    Progressbar progressbar;
    RecyclerView RvMyCart;
    ArrayList<Item> arrCartItemList=new ArrayList<>();
    Button btn_payNow,btn_continue,btn_cod;
    ImageView imgCart;
    LinearLayout linrBottomOrder;
    TextView totalAmountText,serviceChargeText,groundTotalText;
    Integer productId;
    int chatId;
    CartViewModel cartViewModel;
    CardView cardOrderSummary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        sessonManager=new SessonManager(this);
        progressbar = new Progressbar();
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "My Cart" + "</font>")));
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RvMyCart = (RecyclerView) findViewById(R.id.rv_my_cart);
        btn_payNow = (Button) findViewById(R.id.btn_payNow);
        btn_continue = (Button) findViewById(R.id.btn_continue_shoping);
        linrBottomOrder =findViewById(R.id.liner_order);
        btn_cod =findViewById(R.id.btn_cod);
        totalAmountText = findViewById(R.id.totalAmountText);
        serviceChargeText = findViewById(R.id.serviceChargeText);
        groundTotalText = findViewById(R.id.groundTotalText);
        imgCart = (ImageView) findViewById(R.id.imge_cart_img);
        cardOrderSummary=findViewById(R.id.cardOrderSummary);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewCartActivity.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        chatId=getIntent().getIntExtra("chatId",0);
        hitCartDetailsApi(chatId);
    }
    public void hitCartDetailsApi(int chatId){
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            progressbar.showProgress(ViewCartActivity.this);
            Call<CartViewModel>call= ApiExecutor.getApiService(this).apiCartView("Bearer "+sessonManager.getToken(),chatId);
            call.enqueue(new Callback<CartViewModel>() {
                @Override
                public void onResponse(Call<CartViewModel> call, Response<CartViewModel> response) {
                    progressbar.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            cartViewModel=response.body();
                            if (cartViewModel.getData()!=null){
                                totalAmountText.setText("₹ " +cartViewModel.getData().getTotal());
                                serviceChargeText.setText("₹ " +cartViewModel.getData().getServiceCharge());
                                groundTotalText.setText("₹ " +cartViewModel.getData().getGrandTotal());
                                arrCartItemList = (ArrayList<Item>) cartViewModel.getData().getItems();
                                if (arrCartItemList.isEmpty()){
                                    btn_continue.setVisibility(View.VISIBLE);
                                    imgCart.setVisibility(View.VISIBLE);
                                    cardOrderSummary.setVisibility(View.GONE);
                                }else {
                                    cardOrderSummary.setVisibility(View.VISIBLE);
                                    linrBottomOrder.setVisibility(View.VISIBLE);
                                    RvMyCart.setHasFixedSize(true);
                                    RvMyCart.setItemViewCacheSize(20);
                                    RvMyCart.setDrawingCacheEnabled(true);
                                    RvMyCart.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ViewCartActivity.this, 1);
                                    RvMyCart.setLayoutManager(layoutManager);
                                    MyCartAdapter myCartAdapter = new MyCartAdapter(ViewCartActivity.this, arrCartItemList);
                                    RvMyCart.setAdapter(myCartAdapter);

                                }

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartViewModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
    }
    public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
        Context context;
        ArrayList<Item> arList;
        public MyCartAdapter(Context context, ArrayList<Item> arList) {
            this.context = context;
            this.arList = arList;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_cart, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            Glide.with(context)
                    .load(arList.get(position).getFilePath())
                    .into(holder.productImage);
            holder.nameProductText.setText(arList.get(position).getType());
            holder.priceProductText.setText("\u20B9 "+arList.get(position).getPrice());
            holder.quantityProductText.setText(arList.get(position).getQuantity());


        }
        @Override
        public int getItemCount() {
            return arList.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView productImage,deleteImage;
            TextView nameProductText,priceProductText,quantityProductText;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                productImage =  itemView.findViewById(R.id.productImage);
                deleteImage = itemView.findViewById(R.id.deleteImage);
                nameProductText = (TextView) itemView.findViewById(R.id.nameProductText);
                priceProductText = itemView.findViewById(R.id.priceProductText);
                quantityProductText = itemView.findViewById(R.id.quantityProductText);


                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteImage.setEnabled(false);
                        productId=arList.get(getAdapterPosition()).getId();
                        //sizeId=arList.get(getAdapterPosition()).getSizeId();
                        removeAt(getAdapterPosition());
                        hitAddtoCartApi(productId);

                    }
                });


            }
            public void removeAt(int position) {
                arList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arList.size());
            }

        }
    }

    private void hitAddtoCartApi(Integer productId) {
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            progressbar.showProgress(ViewCartActivity.this);
            Call<CartCancelModel> call = ApiExecutor.getApiService(this).apiCartCancel("Bearer " + sessonManager.getToken(), productId);
            call.enqueue(new Callback<CartCancelModel>() {
                @Override
                public void onResponse(Call<CartCancelModel> call, Response<CartCancelModel> response) {
                    progressbar.hideProgress();
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        hitCartDetailsApi(2);
                    }else {
                        btn_continue.setVisibility(View.VISIBLE);
                        imgCart.setVisibility(View.VISIBLE);
                        cardOrderSummary.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<CartCancelModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}