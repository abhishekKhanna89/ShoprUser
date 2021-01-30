package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppr.shoper.Model.OrdersList.Order;
import com.shoppr.shoper.Model.OrdersList.OrdersListModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderActivity extends AppCompatActivity {
    RecyclerView myOrderRecycler;
    TextView emptyOrderText;
    LinearLayoutManager linearLayoutManager;
    SessonManager sessonManager;
    public  static List<Order>orderList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        myOrderRecycler=findViewById(R.id.myOrderRecycler);
        emptyOrderText=findViewById(R.id.emptyOrderText);
        linearLayoutManager = new LinearLayoutManager(this);
        myOrderRecycler.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myOrderRecycler.getContext(),
                linearLayoutManager.getOrientation());
        myOrderRecycler.addItemDecoration(dividerItemDecoration);
        myOrderRecycler.setNestedScrollingEnabled(true);

        viewMyOrder();
    }

    private void viewMyOrder() {
        if (CommonUtils.isOnline(MyOrderActivity.this)) {
            sessonManager.showProgress(MyOrderActivity.this);
            Call<OrdersListModel>call= ApiExecutor.getApiService(this)
                    .apiMyOrder("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<OrdersListModel>() {
                @Override
                public void onResponse(Call<OrdersListModel> call, Response<OrdersListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            OrdersListModel ordersListModel=response.body();
                            if (ordersListModel!=null){
                                orderList=ordersListModel.getData().getOrders();
                                MyOrderAdapter myOrderAdapter=new MyOrderAdapter(MyOrderActivity.this,orderList);
                                myOrderRecycler.setAdapter(myOrderAdapter);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrdersListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(MyOrderActivity.this, getString(R.string.please_check_network));
        }
    }
    public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder>{
        List<Order>orderList;
        Context context;
        public MyOrderAdapter(Context context,List<Order>orderList){
            this.context=context;
            this.orderList=orderList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_my_order,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Order order=orderList.get(position);
            holder.rfIdText.setText("Order Id :"+order.getRefid());
            holder.dateText.setText("Date :"+order.getCreatedAt());
            holder.totalText.setText("Total :"+order.getTotal());
            holder.serviceChargeText.setText("Service Charge :"+order.getServiceCharge());
            if (order.getStatus().equalsIgnoreCase("Confirmed")){
                holder.statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                holder.statusText.setText("Status :"+order.getStatus());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context,OrderDetailsActivity.class)
                    .putExtra("orderId",order.getId()));
                }
            });

        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView rfIdText,dateText,
                    totalText,serviceChargeText,
                    statusText;
            public Holder(@NonNull View itemView) {
                super(itemView);
                rfIdText=itemView.findViewById(R.id.rfIdText);
                dateText=itemView.findViewById(R.id.dateText);
                totalText=itemView.findViewById(R.id.totalText);
                serviceChargeText=itemView.findViewById(R.id.serviceChargeText);
                statusText=itemView.findViewById(R.id.statusText);
            }
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
}