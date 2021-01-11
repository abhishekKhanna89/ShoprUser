package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppr.shoper.Model.WalletHistory.Transaction;
import com.shoppr.shoper.R;

import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.Holder> {
    List<Transaction>transactionList;
    Context context;
    public CreditAdapter(Context context,List<Transaction>transactionList){
        this.context=context;
        this.transactionList=transactionList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
        .inflate(R.layout.credit_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.t.setText(transactionList.get(position).getDescription());
        holder.tr.setText(transactionList.get(position).getRefid());
        holder.price.setText("(+) "+transactionList.get(position).getAmount());
        holder.bal.setText(transactionList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView t,tr,price,bal;
        public Holder(@NonNull View itemView) {
            super(itemView);
            t=itemView.findViewById(R.id.t);
            tr=itemView.findViewById(R.id.tr);
            price=itemView.findViewById(R.id.price);
            bal=itemView.findViewById(R.id.bal);
        }
    }
}
