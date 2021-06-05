package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.gson.Gson;
import com.shoppr.shoper.Model.OrderDetails.OrderHistory.Detail;
import com.shoppr.shoper.Model.OrderDetails.OrderHistory.OrderHistoryModel;
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
            totalPaidText,payment_text;
    ArrayList<Detail> arrCartItemList;
    double total_paid;
    TextView emptyDeatils;
    String refId;
    String invoice_link;
    TextView invoiceDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        int orderId=getIntent().getIntExtra("orderId",0);
        Log.d("ressssss",sessonManager.getToken());

        progressbar = new Progressbar();
        /*Todo:- RecyclerView*/
        rv_order_details=findViewById(R.id.rv_order_details);
        rv_order_details.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        /*Todo:- CardView*/
        cardOrderSummary=findViewById(R.id.cardOrderSummary);
        /*Todo:- TextView*/
        emptyDeatils=findViewById(R.id.emptyDeatils);

        invoiceDownload=findViewById(R.id.invoiceDownload);

        orderIdText=findViewById(R.id.orderIdText);
        totalAmountText=findViewById(R.id.totalAmountText);
        serviceChargeText=findViewById(R.id.serviceChargeText);
        groundTotalText=findViewById(R.id.groundTotalText);
        walletAmountText=findViewById(R.id.walletAmountText);
        totalPaidText=findViewById(R.id.totalPaidText);
        payment_text=findViewById(R.id.payment_text);



        viewOrderDetails(orderId);

        invoiceDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invoice_link.equalsIgnoreCase("0")){

                }else {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(ApiExecutor.baseUrl+"download-invoice/"+refId);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long ref = downloadManager.enqueue(request);
                }
            }
        });

    }

    private void viewOrderDetails(int orderId) {
        Log.d("orderId",""+orderId);
        if (CommonUtils.isOnline(OrderDetailsActivity.this)) {
            sessonManager.showProgress(OrderDetailsActivity.this);
            Call<OrderHistoryModel> call= ApiExecutor.getApiService(this)
                    .apiOrderDetails("Bearer "+sessonManager.getToken(),orderId);
            call.enqueue(new Callback<OrderHistoryModel>() {
                @Override
                public void onResponse(Call<OrderHistoryModel> call, Response<OrderHistoryModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        OrderHistoryModel ordersDetailsModel=response.body();
                        Log.d("resOrderDetails",new Gson().toJson(ordersDetailsModel));
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (ordersDetailsModel.getData()!=null){
                                refId=ordersDetailsModel.getData().getOrder().getRefid();
                                invoice_link=ordersDetailsModel.getData().getShow_invoice_link();
                                if (invoice_link.equalsIgnoreCase("0")){
                                    invoiceDownload.setVisibility(View.GONE);
                                }
                                payment_text.setText(ordersDetailsModel.getData().getPayment_text());
                                orderIdText.setText(ordersDetailsModel.getData().getOrder().getRefid());
                                totalAmountText.setText("₹ " +ordersDetailsModel.getData().getOrder().getTotal());
                                serviceChargeText.setText("₹ " +ordersDetailsModel.getData().getOrder().getServiceCharge());
                                double num1 = Double.parseDouble(ordersDetailsModel.getData().getOrder().getTotal());
                                double num2 = Double.parseDouble(ordersDetailsModel.getData().getOrder().getServiceCharge());
                                // add both number and store it to sum
                                double sum = num1 + num2;
                                groundTotalText.setText("₹ " +sum);
                                walletAmountText.setText("₹ "+ordersDetailsModel.getData().getOrder().getBalanceUsed());


                                double grandPrice=Double.parseDouble(String.valueOf(sum));
                                double walletPrice=Double.parseDouble(ordersDetailsModel.getData().getOrder().getBalanceUsed());
                                if (grandPrice>walletPrice){
                                    total_paid=grandPrice-walletPrice;
                                }else if (grandPrice<walletPrice){
                                    total_paid=0;
                                }
                                totalPaidText.setText("₹ " +total_paid);

                                arrCartItemList = (ArrayList<Detail>) ordersDetailsModel.getData().getOrder().getDetails();

                                /*if (arrCartItemList.isEmpty()){
                                    cardOrderSummary.setVisibility(View.GONE);
                                    emptyDeatils.setVisibility(View.VISIBLE);
                                }else {
                                    cardOrderSummary.setVisibility(View.VISIBLE);
                                    emptyDeatils.setVisibility(View.GONE);
                                }*/
                                OrderDetailsAdapter orderDetailsAdapter=new OrderDetailsAdapter(OrderDetailsActivity.this,
                                        arrCartItemList);
                                rv_order_details.setAdapter(orderDetailsAdapter);
                                orderDetailsAdapter.notifyDataSetChanged();
                            }
                        }else {
                            Toast.makeText(OrderDetailsActivity.this, ""+ordersDetailsModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OrderHistoryModel> call, Throwable t) {
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

            if (detailList.get(position).getFilePath() != null && !detailList.get(position).getFilePath().isEmpty() && !detailList.get(position).getFilePath().equals("null")) {

                Picasso.get().load(detailList.get(position).getFilePath()).into(holder.productImage);
            }


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