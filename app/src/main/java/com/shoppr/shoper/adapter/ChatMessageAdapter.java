package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class ChatMessageAdapter  extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
    List<Chat>chatList;
    Context context;
    public ChatMessageAdapter(Context context,List<Chat>chatList){
        this.context=context;
        this.chatList=chatList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
        .inflate(R.layout.my_chat_image_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String filePath=chatList.get(position).getFilePath();
        if (filePath.isEmpty()){

        }else {
            Picasso.get().load(chatList.get(position).getFilePath()).into(holder.productImage);
        }

        holder.pqText.setText("₹"+chatList.get(position).getPrice()+"-"+chatList.get(position).getQuantity());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView pqText;
        public Holder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.productImage);
            pqText=itemView.findViewById(R.id.pqText);
        }
    }
}
