package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shoppr.shoper.Model.OrderDetails.Order;
import com.shoppr.shoper.Model.OrderDetails.OrderDetailsModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyOrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView myOrderRecycler;
    SessonManager sessonManager;
    MyOrderAdapter myOrderAdapter;
    LinearLayoutManager linearLayoutManager;
    public static List<Order>orderList=new ArrayList<>();
    TextView orderEmptyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessonManager=new SessonManager(this);
        Log.d("token",sessonManager.getToken());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        myOrderRecycler = (RecyclerView) findViewById(R.id.myOrderRecycler);
        orderEmptyText=findViewById(R.id.orderEmptyText);
        linearLayoutManager = new LinearLayoutManager(this);
        myOrderRecycler.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myOrderRecycler.getContext(),
                linearLayoutManager.getOrientation());
        myOrderRecycler.addItemDecoration(dividerItemDecoration);
        myOrderRecycler.setNestedScrollingEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                viewMyOrder();
            }
        });

    }
    private void viewMyOrder() {
        swipeRefreshLayout.setRefreshing(true);
        if (CommonUtils.isOnline(MyOrderActivity.this)) {
            sessonManager.showProgress(MyOrderActivity.this);
            Call<OrderDetailsModel>call=ApiExecutor.getApiService(this)
                    .apiMyOrder("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<OrderDetailsModel>() {
                @Override
                public void onResponse(Call<OrderDetailsModel> call, Response<OrderDetailsModel> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            OrderDetailsModel orderDetailsModel=response.body();
                            if (orderDetailsModel.getData().getOrders()!=null){
                                if (orderDetailsModel.getData().getOrders().size()==0){
                                    orderEmptyText.setVisibility(View.VISIBLE);
                                    myOrderRecycler.setVisibility(View.GONE);
                                }else {
                                    orderEmptyText.setVisibility(View.GONE);
                                    myOrderRecycler.setVisibility(View.VISIBLE);
                                }
                                orderList=orderDetailsModel.getData().getOrders();
                                myOrderAdapter=new MyOrderAdapter(MyOrderActivity.this,orderList);
                                myOrderRecycler.setAdapter(myOrderAdapter);
                                myOrderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrderDetailsModel> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(MyOrderActivity.this, getString(R.string.please_check_network));
        }

    }

    public class  MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder>{
        List<Order>datumList;
        Context context;
        public MyOrderAdapter(Context context,List<Order>datumList){
            this.context=context;
            this.datumList=datumList;
        }
        @NonNull
        @Override
        public MyOrderAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_my_order,null));
        }

        @Override
        public void onBindViewHolder(@NonNull MyOrderAdapter.Holder holder, int position) {
            Order order=datumList.get(position);

            if (order.getDetails().size()==0){
                Picasso.get().load(R.drawable.pin_logo).into(holder.itemImage);
            }else {
                Picasso.get().load(order.getDetails().get(position).getFilePath()).into(holder.itemImage);
            }

            holder.rfIdText.setText("Order Id :"+order.getRefid());
            holder.itemDate.setText(order.getCreatedAt());
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
                            .putExtra("orderId",order.getId())
                            .putExtra("position",position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return datumList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            CircleImageView itemImage;
            TextView rfIdText,itemDate,
                    totalText,serviceChargeText,
                    statusText;
            public Holder(@NonNull View itemView) {
                super(itemView);
                itemImage=itemView.findViewById(R.id.itemImage);
                rfIdText=itemView.findViewById(R.id.rfIdText);
                itemDate=itemView.findViewById(R.id.itemDate);
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


    @Override
    public void onRefresh() {
        viewMyOrder();
    }
}