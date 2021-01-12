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

        for ( int i=0;i<transactionList.size();i++){
            String type=transactionList.get(i).getType();

            if(type.equalsIgnoreCase("Credit")){

            }else {

            }

            /*Todo:- Green Text*/
            holder.t.setText(transactionList.get(i).getDescription());
            holder.tr.setText(transactionList.get(i).getRefid());
            holder.price.setText("(+) "+transactionList.get(i).getAmount());
            holder.bal.setText(transactionList.get(i).getDate());
            /*Todo:- Red Text*/
            holder.t1.setText(transactionList.get(i).getDescription());
            holder.tr1.setText( transactionList.get(i).getRefid());
            holder.price1.setText("(-) "+transactionList.get(i).getAmount());
            holder.bal1.setText(transactionList.get(i).getDate());



        }

        holder.greenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                ViewGroup viewGroup = v.findViewById(android.R.id.content);

                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(context).inflate(R.layout.green_cradit_layout, viewGroup, false);
                RecyclerView recyclerView=dialogView.findViewById(R.id.creditRecycler);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
                CreditAdapter creditAdapter=new CreditAdapter(context,historyList.get(position).getTransactions());
                recyclerView.setAdapter(creditAdapter);
                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        holder.redLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                ViewGroup viewGroup = v.findViewById(android.R.id.content);

                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(context).inflate(R.layout.red_debit_layout, viewGroup, false);
                RecyclerView recyclerView=dialogView.findViewById(R.id.debitRecycler);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
                DebitAdapter debitAdapter=new DebitAdapter(context,historyList.get(position).getTransactions());
                recyclerView.setAdapter(debitAdapter);
                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        Button button;
        RelativeLayout greenLayout,redLayout;
        /*Todo:- Green Text*/
        TextView t,tr,price,bal;
        /*Todo:- Red Text*/
        TextView t1,tr1,price1,bal1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.button);
            /*Todo:- Green Text*/
            t=itemView.findViewById(R.id.t);
            tr=itemView.findViewById(R.id.tr);
            price=itemView.findViewById(R.id.price);
            bal=itemView.findViewById(R.id.bal);
            greenLayout=itemView.findViewById(R.id.greenLayout);
            /*Todo:- Red Text*/
            t1=itemView.findViewById(R.id.t1);
            tr1=itemView.findViewById(R.id.tr1);
            price1=itemView.findViewById(R.id.price1);
            bal1=itemView.findViewById(R.id.bal1);
            redLayout=itemView.findViewById(R.id.redLayout);
        }
    }
}
