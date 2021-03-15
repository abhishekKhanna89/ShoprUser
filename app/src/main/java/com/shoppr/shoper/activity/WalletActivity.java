package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.WalletHistory.WalletHistoryModel;
import com.shoppr.shoper.Model.WalletHistory.WalletTransaction;
import com.shoppr.shoper.R;

import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.adapter.RecyclerAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessonManager sessonManager;
    TextView balanceText;
    List<WalletTransaction>historyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        sessonManager=new SessonManager(this);
        recyclerView=findViewById(R.id.recyclerView);
        balanceText=findViewById(R.id.balanceText);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        getSupportActionBar().setTitle("Wallet Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewData();
    }

    private void viewData() {
        if (CommonUtils.isOnline(WalletActivity.this)){
            sessonManager.showProgress(WalletActivity.this);
            Call<WalletHistoryModel> call= ApiExecutor.getApiService(WalletActivity.this)
                    .apiWalletHistory("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<WalletHistoryModel>() {
                @Override
                public void onResponse(Call<WalletHistoryModel> call, Response<WalletHistoryModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            WalletHistoryModel walletHistoryModel=response.body();
                            if (walletHistoryModel.getData()!=null){
                                balanceText.setText("\u20B9 "+walletHistoryModel.getData().getBalance());
                                historyList= walletHistoryModel.getData().getWalletTransactions();
                                RecyclerAdapter recyclerAdapter=new RecyclerAdapter(WalletActivity.this,historyList);
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<WalletHistoryModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(WalletActivity.this, getString(R.string.please_check_network));
        }

    }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle action bar item clicks here. The action bar will
       // automatically handle clicks on the Home/Up button, so long
       // as you specify a parent com.example.shoper.activity in AndroidManifest.xml.

       //noinspection SimplifiableIfStatement
       int id = item.getItemId();
       if (id==android.R.id.home){
           onBackPressed();
       }

       return super.onOptionsItemSelected(item);
   }
}