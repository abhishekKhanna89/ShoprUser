package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
        String type=transactionList.get(position).getType();
        if (type.equalsIgnoreCase("Credit")){
            holder.t.setText(transactionList.get(position).getDescription());
            holder.tr.setText(transactionList.get(position).getRefid());
            holder.price.setText("(+) "+transactionList.get(position).getAmount());
            holder.bal.setText(transactionList.get(position).getDate());
            holder.redLayout.setVisibility(View.GONE);
        }else {
            /*Todo:- Red Text*/
            holder.t1.setText(transactionList.get(position).getDescription());
            holder.tr1.setText( transactionList.get(position).getRefid());
            holder.price1.setText("(-) "+transactionList.get(position).getAmount());
            holder.bal1.setText(transactionList.get(position).getDate());
            holder.greenLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView t,tr,price,bal;
        /*Todo:- Red Text*/
        TextView t1,tr1,price1,bal1;

        /*Todo:- RelativeLayout*/
        RelativeLayout greenLayout,redLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            t=itemView.findViewById(R.id.t);
            tr=itemView.findViewById(R.id.tr);
            price=itemView.findViewById(R.id.price);
            bal=itemView.findViewById(R.id.bal);
            /*Todo:- Red Text*/
            t1=itemView.findViewById(R.id.t1);
            tr1=itemView.findViewById(R.id.tr1);
            price1=itemView.findViewById(R.id.price1);
            bal1=itemView.findViewById(R.id.bal1);
            /*Todo:- RelativeLayout*/
            greenLayout=itemView.findViewById(R.id.greenLayout);
            redLayout=itemView.findViewById(R.id.redLayout);

        }
    }
}
