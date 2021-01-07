package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppr.shoper.R;

import model.RecyclerModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    RecyclerModel[]recyclerModels;
    Context context;
    public RecyclerAdapter(Context context, RecyclerModel[]recyclerModels){
        this.recyclerModels=recyclerModels;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_recycler,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
        RecyclerModel recyclerModel=recyclerModels[position];
        holder.button.setText(recyclerModel.getDate());
        /*Todo:- Green Text*/
        holder.t.setText(recyclerModel.getAddMoney());
        holder.tr.setText(recyclerModel.getTransactionId());
        holder.price.setText("(+) "+recyclerModel.getGreenPrice());
        holder.bal.setText("Bal : "+recyclerModel.getBalance());
        /*Todo:- Red Text*/
        holder.t1.setText(recyclerModel.getReceived());
        holder.price1.setText("(-) "+recyclerModel.getRedPrice());
        holder.bal1.setText("Bal : "+recyclerModel.getBalance());
    }

    @Override
    public int getItemCount() {
        return recyclerModels.length;
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
