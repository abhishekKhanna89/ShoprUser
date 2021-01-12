package com.shoppr.shoper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shoppr.shoper.Model.WalletHistory.Transaction;
import com.shoppr.shoper.Model.WalletHistory.WalletTransaction;
import com.shoppr.shoper.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    List<WalletTransaction>historyList;
    Context context;
    public RecyclerAdapter(Context context,List<WalletTransaction>historyList){
        this.historyList=historyList;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_recycler,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
        WalletTransaction history=historyList.get(position);
        holder.button.setText(history.getDate());
        List<Transaction>transactionList=history.getTransactions();
        holder.transactionListRecycler.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        CreditAdapter creditAdapter=new CreditAdapter(context,transactionList);
        holder.transactionListRecycler.setAdapter(creditAdapter);

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        Button button;
        RecyclerView transactionListRecycler;
        public Holder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.button);
            transactionListRecycler=itemView.findViewById(R.id.transactionListRecycler);
        }
    }
}
