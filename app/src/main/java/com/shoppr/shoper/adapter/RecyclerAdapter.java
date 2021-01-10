package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppr.shoper.Model.WalletHistory.FridayJun262020;
import com.shoppr.shoper.Model.WalletHistory.History;
import com.shoppr.shoper.R;

import java.util.List;

import model.RecyclerModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    List<FridayJun262020>historyList;
    Context context;
    public RecyclerAdapter(Context context,List<FridayJun262020>historyList){
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
        FridayJun262020 history=historyList.get(position);
        String type=history.getType();
        holder.button.setText(history.getDate());
        /*Todo:- Green Text*/
        holder.t.setText(history.getDescription());
        holder.tr.setText(history.getRefid());
        holder.price.setText("(+) "+history.getAmount());
        holder.bal.setText("Bal : "+history.getAmount());
        /*Todo:- Red Text*/
        holder.t1.setText(history.getDescription()+"\n"+
                history.getRefid());
        holder.price1.setText("(-) "+history.getAmount());
        holder.bal1.setText("Bal : "+history.getAmount());
        if (type!=null&&type.equalsIgnoreCase("Credit")){

        }else if (type!=null&&type.equalsIgnoreCase("Debit")){

        }



    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        Button button;
        /*Todo:- Green Text*/
        TextView t,tr,price,bal;
        /*Todo:- Red Text*/
        TextView t1,price1,bal1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.button);
            /*Todo:- Green Text*/
            t=itemView.findViewById(R.id.t);
            tr=itemView.findViewById(R.id.tr);
            price=itemView.findViewById(R.id.price);
            bal=itemView.findViewById(R.id.bal);
            /*Todo:- Red Text*/
            t1=itemView.findViewById(R.id.t1);
            price1=itemView.findViewById(R.id.price1);
            bal1=itemView.findViewById(R.id.bal1);
        }
    }
}
