 package com.shoppr.shoper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.R;


import java.util.List;

public class ChatMessageAdapter  extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
    List<Chat>chatList;
    Context context;
    private int SELF = 1;
    View itemView;
    public ChatMessageAdapter(Context context,List<Chat>chatList){
        this.context=context;
        this.chatList=chatList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_layout, parent, false);
        } else{
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_layout, parent, false);
        }



        //returing the view
        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
       Chat chat=chatList.get(position);
       if (chat.getType().equalsIgnoreCase("image")){
           Glide.with(context).load(chat.getFilePath()).into(holder.image);
           holder.imageText.setText(chat.getMessage());
           holder.dateImage.setText(chat.getCreatedAt());

       }else {
           holder.imageLayout.setVisibility(View.GONE);

       }
       if (chat.getType().equalsIgnoreCase("text")){
           holder.message_body.setText(chat.getMessage());
           holder.dateText.setText(chat.getCreatedAt());
       }else {
           holder.textLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("product")){
           Glide.with(context).load(chat.getFilePath()).into(holder.productImage);
           holder.productMessage.setText(chat.getMessage());
           holder.dateProduct.setText(chat.getCreatedAt());
           holder.pqText.setText("₹"+chat.getPrice()+"-"+chat.getQuantity());
       }else {
           holder.productLayout.setVisibility(View.GONE);
       }

       holder.acceptText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               holder.greenLayout.setVisibility(View.VISIBLE);
               holder.closeRedLayout.setVisibility(View.GONE);
               holder.rejectText.setVisibility(View.GONE);
               holder.acceptText.setText("Cancel");
               holder.acceptText.setGravity(Gravity.CENTER);
               holder.acceptText.setTextColor(Color.parseColor("#C9A1A1A1"));
           }
       });
       holder.rejectText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               holder.closeRedLayout.setVisibility(View.VISIBLE);
               holder.greenLayout.setVisibility(View.GONE);
               holder.acceptText.setVisibility(View.GONE);
           }
       });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Chat message = chatList.get(position);

        //If its owner  id is  equals to the logged in user id
        if (message.getDirection()==1) {
            //Returning self
            return SELF;
        }
        //else returning position
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder {
        /*Todo:- Location*/
        ImageView locationImage;
        TextView locationText,dateLocation;
        /*Todo:- Text*/
        TextView message_body,dateText;
        LinearLayout textLayout;
        /*Todo:- Product*/
        ImageView productImage;
        TextView pqText,dateProduct,productMessage,acceptText,rejectText;
        LinearLayout productLayout,greenLayout,closeRedLayout;
        /*Todo:- Image*/
        ImageView image;
        TextView imageText,dateImage;
        LinearLayout imageLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
           /* locationImage=itemView.findViewById(R.id.locationImage);
            locationText=itemView.findViewById(R.id.locationText);
            dateLocation=itemView.findViewById(R.id.dateLocation);*/
            /*Todo:- Text*/
            message_body=itemView.findViewById(R.id.message_body);
            dateText=itemView.findViewById(R.id.dateText);
            textLayout=itemView.findViewById(R.id.textLayout);
            /*Todo:- Product*/
            productImage=itemView.findViewById(R.id.productImage);
            pqText=itemView.findViewById(R.id.pqText);
            dateProduct=itemView.findViewById(R.id.dateProduct);
            productMessage=itemView.findViewById(R.id.productMessage);
            productLayout=itemView.findViewById(R.id.productLayout);
            greenLayout=itemView.findViewById(R.id.greenLayout);
            closeRedLayout=itemView.findViewById(R.id.closeRedLayout);
            acceptText=itemView.findViewById(R.id.acceptText);
            rejectText=itemView.findViewById(R.id.rejectText);
            /*Todo:- Image*/
            image=itemView.findViewById(R.id.Image);
            imageText=itemView.findViewById(R.id.Text);
            dateImage=itemView.findViewById(R.id.dateImage);
            imageLayout=itemView.findViewById(R.id.imageLayout);

        }
    }
}
