package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shoppr.shoper.Model.CartView.Item;
import com.shoppr.shoper.Model.OrderDetails.Detail;
import com.shoppr.shoper.Model.OrderDetails.Order;
import com.shoppr.shoper.Model.OrderDetails.OrdersDetailsModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {
    SessonManager sessonManager;
    Progressbar progressbar;
    RecyclerView rv_order_details;
    CardView cardOrderSummary;
    TextView orderIdText,totalAmountText,
            serviceChargeText,
            groundTotalText,
            walletAmountText,
            totalPaidText;
    ArrayList<Detail> arrCartItemList=new ArrayList<>();
    double total_paid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int orderId=getIntent().getIntExtra("orderId",0);
        sessonManager=new SessonManager(this);
        progressbar = new Progressbar();
        /*Todo:- RecyclerView*/
        rv_order_details=findViewById(R.id.rv_order_details);
        rv_order_details.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        /*Todo:- CardView*/
        cardOrderSummary=findViewById(R.id.cardOrderSummary);
        /*Todo:- TextView*/
        orderIdText=findViewById(R.id.orderIdText);
        totalAmountText=findViewById(R.id.totalAmountText);
        serviceChargeText=findViewById(R.id.serviceChargeText);
        groundTotalText=findViewById(R.id.groundTotalText);
        walletAmountText=findViewById(R.id.walletAmountText);
        totalPaidText=findViewById(R.id.totalPaidText);
        viewOrderDetails(orderId);
    }

    private void viewOrderDetails(int orderId) {
        if (CommonUtils.isOnline(OrderDetailsActivity.this)) {
            sessonManager.showProgress(OrderDetailsActivity.this);
            Call<OrdersDetailsModel>call= ApiExecutor.getApiService(this)
                    .apiOrderDetails("Bearer "+sessonManager.getToken(),orderId);
            call.enqueue(new Callback<OrdersDetailsModel>() {
                @Override
                public void onResponse(Call<OrdersDetailsModel> call, Response<OrdersDetailsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            OrdersDetailsModel ordersDetailsModel=response.body();
                            if (ordersDetailsModel.getData()!=null){
                                orderIdText.setText(ordersDetailsModel.getData().getOrder().getRefid());
                                totalAmountText.setText("₹ " +ordersDetailsModel.getData().getOrder().getTotal());
                                serviceChargeText.setText("₹ " +ordersDetailsModel.getData().getOrder().getServiceCharge());
                                double num1 = Double.parseDouble(ordersDetailsModel.getData().getOrder().getTotal());
                                double num2 = Double.parseDouble(ordersDetailsModel.getData().getOrder().getServiceCharge());
                                // add both number and store it to sum
                                double sum = num1 + num2;
                                groundTotalText.setText("₹ " +sum);
                                walletAmountText.setText(ordersDetailsModel.getData().getOrder().getBalanceUsed());


                                double grandPrice=Double.parseDouble(String.valueOf(sum));
                                double walletPrice=Double.parseDouble(ordersDetailsModel.getData().getOrder().getBalanceUsed());
                                if (grandPrice>walletPrice){
                                    total_paid=grandPrice-walletPrice;
                                }else if (grandPrice<walletPrice){
                                    total_paid=walletPrice-grandPrice;
                                }
                                totalPaidText.setText("₹ " +total_paid);

                                arrCartItemList = (ArrayList<Detail>) ordersDetailsModel.getData().getOrder().getDetails();

                                if (arrCartItemList.isEmpty()){
                                    cardOrderSummary.setVisibility(View.GONE);
                                }else {
                                    cardOrderSummary.setVisibility(View.VISIBLE);
                                }
                                OrderDetailsAdapter orderDetailsAdapter=new OrderDetailsAdapter(OrderDetailsActivity.this,
                                        arrCartItemList);
                                rv_order_details.setAdapter(orderDetailsAdapter);
                                orderDetailsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrdersDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(OrderDetailsActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.Holder>{
        List<Detail>detailList;
        Context context;
        public OrderDetailsAdapter(Context context,List<Detail>detailList){
            this.context=context;
            this.detailList=detailList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return  new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_order_details,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Picasso.get().load(detailList.get(position).getFilePath()).into(holder.productImage);
            holder.nameProductText.setText(detailList.get(position).getMessage());
            holder.priceProductText.setText("\u20B9 "+detailList.get(position).getPrice());
            holder.quantityProductText.setText(detailList.get(position).getQuantity());
        }

        @Override
        public int getItemCount() {
            return detailList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView nameProductText,priceProductText,quantityProductText;
            public Holder(@NonNull View itemView) {
                super(itemView);
                productImage =  itemView.findViewById(R.id.productImage);
                nameProductText = (TextView) itemView.findViewById(R.id.nameProductText);
                priceProductText = itemView.findViewById(R.id.priceProductText);
                quantityProductText = itemView.findViewById(R.id.quantityProductText);
            }
        }
    }
}