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

public class DebitAdapter extends RecyclerView.Adapter<DebitAdapter.Holder> {
    List<Transaction>transactionList;
    Context context;
    public DebitAdapter(Context context,List<Transaction>transactionList){
        this.context=context;
        this.transactionList=transactionList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
        .inflate(R.layout.debit_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.t1.setText(transactionList.get(position).getDescription());
        holder.tr1.setText( transactionList.get(position).getRefid());
        holder.price1.setText("(-) "+transactionList.get(position).getAmount());
        holder.bal1.setText(transactionList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView t1,tr1,price1,bal1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            t1=itemView.findViewById(R.id.t1);
            tr1=itemView.findViewById(R.id.tr1);
            price1=itemView.findViewById(R.id.price1);
            bal1=itemView.findViewById(R.id.bal1);
        }
    }
}
