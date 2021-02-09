 package com.shoppr.shoper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.appevents.ml.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.AcceptModel;
import com.shoppr.shoper.Model.CancelModel;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.RatingsModel;
import com.shoppr.shoper.Model.RejectedModel;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
import com.shoppr.shoper.activity.TrackLoactionActivity;
import com.shoppr.shoper.requestdata.RatingsRequest;
import com.shoppr.shoper.requestdata.ShareLocationRequest;
import com.shoppr.shoper.requestdata.TextTypeRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.os.FileUtils.copy;

 public class ChatMessageAdapter  extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
     private static final int IO_BUFFER_SIZE = 1;
     List<Chat>chatList;
    Context context;
    private int SELF = 1;
    View itemView;
    SessonManager sessonManager;
     BroadcastReceiver mMessageReceiver;
     String title,body;
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


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
       Chat chat=chatList.get(position);
       sessonManager=new SessonManager(context);
        recycle();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("title")!=null||intent.getStringExtra("body")!=null){
                    holder.textLayout.setVisibility(View.VISIBLE);
                    title=intent.getStringExtra("title");
                     body=intent.getStringExtra("body");
                     holder.message_body.setText(title+"\t"+body);
                   // Toast.makeText(context, "Title:- "+title+" Body:- "+body, Toast.LENGTH_SHORT).show();
                }else {
                    holder.textLayout.setVisibility(View.GONE);
                }
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,new IntentFilter(i));


       if (chat.getType().equalsIgnoreCase("image")){
           Picasso.get().load(chat.getFilePath()).into(holder.image);
           //Glide.with(context).load(chat.getFilePath()).into(holder.image);
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
           Picasso.get().load(chat.getFilePath()).into(holder.productImage);
           //Glide.with(context).load(chat.getFilePath()).into(holder.productImage);
           holder.productMessage.setText(chat.getMessage());
           holder.dateProduct.setText(chat.getCreatedAt());
           holder.pqText.setText(chat.getQuantity()+" / "+"₹"+chat.getPrice());
       }else {
           holder.productLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("ratings")){
           holder.ratingsMessage.setText(chat.getMessage());
           holder.dateRating.setText(chat.getCreatedAt());
           holder.ratingBar.setRating(Float.parseFloat(chat.getQuantity()));

       }else {
           holder.ratingLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("audio")){
           holder.voicePlayerView.setAudio(chat.getFilePath());
       }else {
           holder.voicePlayerView.setVisibility(View.GONE);
       }

       if (chat.getType().equalsIgnoreCase("address-request")){
           holder.addressText.setText(chat.getMessage());
           holder.addressDate.setText(chat.getCreatedAt());
       }else {
           holder.addressLayout.setVisibility(View.GONE);
       }
       if (chat.getType().equalsIgnoreCase("address")){
           String lat = chat.getLat();
           String lon = chat.getLang();
           String url ="https://maps.googleapis.com/maps/api/staticmap?";
           url+="&zoom=14";
           url+="&size=200x200";
           url+="&maptype=roadmap";
           url+="&markers=color:red%7Clabel:%7C"+lat+", "+lon;
           url+="&key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
           Picasso.get().load(url).into(holder.locationImage);
           holder.locationText.setText(chat.getMessage());
           holder.locationDate.setText(chat.getCreatedAt());
       }else {
           holder.mapLayout.setVisibility(View.GONE);
       }

       if (chat.getType().equalsIgnoreCase("track")){
           holder.trackLocationText.setText(chat.getMessage());
       }else {
           holder.trackLocationLayout.setVisibility(View.GONE);
       }


       /*Todo:- Visibility Concept*/
       if (chat.getStatus().equalsIgnoreCase("accepted")){
           holder.greenLayout.setVisibility(View.VISIBLE);
           holder.closeRedLayout.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.VISIBLE);
           holder.acceptText.setVisibility(View.GONE);
           holder.ratingBar.setIsIndicator(true);
       }
       if (chat.getStatus().equalsIgnoreCase("rejected")){
           holder.closeRedLayout.setVisibility(View.VISIBLE);
           holder.greenLayout.setVisibility(View.GONE);
           holder.acceptText.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.GONE);
       }
       if (chat.getStatus().equalsIgnoreCase("cancelled")){
           holder.closeRedLayout.setVisibility(View.VISIBLE);
           holder.greenLayout.setVisibility(View.GONE);
           holder.acceptText.setVisibility(View.GONE);
           holder.rejectText.setVisibility(View.GONE);
           holder.cancelText.setVisibility(View.GONE);
       }


       holder.acceptText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   //sessonManager.showProgress(context);
                   Call<AcceptModel>call= ApiExecutor.getApiService(context)
                           .apiAccept("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<AcceptModel>() {
                       @Override
                       public void onResponse(Call<AcceptModel> call, Response<AcceptModel> response) {
                           //sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   AcceptModel acceptModel=response.body();
                                   if (acceptModel.getStatus().equalsIgnoreCase("success")){
                                       holder.greenLayout.setVisibility(View.VISIBLE);
                                       holder.closeRedLayout.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.VISIBLE);
                                       holder.acceptText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<AcceptModel> call, Throwable t) {
                           //sessonManager.hideProgress();
                       }
                   });
               }else
               {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }

           }
       });
       holder.rejectText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   //sessonManager.showProgress(context);
                   Call<RejectedModel>call=ApiExecutor.getApiService(context)
                           .apiRejected("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<RejectedModel>() {
                       @Override
                       public void onResponse(Call<RejectedModel> call, Response<RejectedModel> response) {
                           //sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   RejectedModel rejectedModel=response.body();
                                   if (rejectedModel.getStatus().equalsIgnoreCase("success")){
                                       holder.closeRedLayout.setVisibility(View.VISIBLE);
                                       holder.greenLayout.setVisibility(View.GONE);
                                       holder.acceptText.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<RejectedModel> call, Throwable t) {
                           //sessonManager.hideProgress();
                       }
                   });
               }else {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }

           }
       });
       holder.cancelText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (CommonUtils.isOnline(context)) {
                   //sessonManager.showProgress(context);
                   Call<CancelModel>call=ApiExecutor.getApiService(context)
                           .apiCancel("Bearer "+sessonManager.getToken(),chat.getId());
                   call.enqueue(new Callback<CancelModel>() {
                       @Override
                       public void onResponse(Call<CancelModel> call, Response<CancelModel> response) {
                           //sessonManager.hideProgress();
                           if (response.body()!=null) {
                               if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                   CancelModel cancelModel=response.body();
                                   if (cancelModel.getStatus().equalsIgnoreCase("success")){
                                       holder.closeRedLayout.setVisibility(View.VISIBLE);
                                       holder.greenLayout.setVisibility(View.GONE);
                                       holder.acceptText.setVisibility(View.GONE);
                                       holder.rejectText.setVisibility(View.GONE);
                                       holder.cancelText.setVisibility(View.GONE);
                                   }
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<CancelModel> call, Throwable t) {
                           //sessonManager.hideProgress();
                       }
                   });
               }else {
                   CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
               }
           }
       });

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float a=rating;
                int b;
                b=(int)a;
                String ratingValue=String.valueOf(b);
                if (CommonUtils.isOnline(context)) {
                    //sessonManager.showProgress(context);
                    RatingsRequest ratingsRequest=new RatingsRequest();
                    ratingsRequest.setRatings(ratingValue);
                    Call<RatingsModel>call=ApiExecutor.getApiService(context)
                            .apiRatings("Bearer "+sessonManager.getToken(),chat.getId(),ratingsRequest);
                    call.enqueue(new Callback<RatingsModel>() {
                        @Override
                        public void onResponse(Call<RatingsModel> call, Response<RatingsModel> response) {
                            //sessonManager.hideProgress();
                            //Log.d("response",response.body().getStatus());
                            if (response.body()!=null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    //ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(context,chatList);
                                    //chatMessageAdapter.refreshEvents(chatList);
                                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RatingsModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
                }else {
                    CommonUtils.showToastInCenter((Activity) context,context.getString(R.string.please_check_network));
                }
            }
        });
        holder.addressLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ShareLocationActivity.class)
                .putExtra("chatId",chat.getChatId()));
            }
        });

        holder.trackLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, TrackLoactionActivity.class)
                        .putExtra("messageId",chat.getChatId()));
            }
        });

        /*Todo:-   Zoom Image*/
        holder.locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst= (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst= (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.image_layout);
                ImageView imageFirst= (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });

    }



     @Override
    public int getItemCount() {
         if (chatList != null) {
             return chatList.size();
         } else return 0;
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
        TextView locationText,locationDate;
         ChatMessageView mapLayout;
        /*Todo:- Text*/
        TextView message_body,dateText;
         ChatMessageView textLayout;
        /*Todo:- Product*/
        ImageView productImage;
        TextView pqText,dateProduct,productMessage,acceptText,rejectText,cancelText;
         LinearLayout greenLayout,closeRedLayout;
         ChatMessageView productLayout;
        /*Todo:- Image*/
        ImageView image;
        TextView imageText,dateImage;
         ChatMessageView imageLayout;
        /*Todo:- Rating*/
        ChatMessageView ratingLayout;
        TextView ratingsMessage,dateRating;
        AppCompatRatingBar ratingBar;
        /*Todo:- Audio*/
        VoicePlayerView voicePlayerView;

        /*Todo:- Address*/
        ChatMessageView addressLayout;
         TextView addressText,addressLinkText
                 ,addressDate;

         /*Todo:- Track Location*/
         ChatMessageView trackLocationLayout;
         TextView trackLocationText;

        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
            locationImage=itemView.findViewById(R.id.locationImage);
            locationText=itemView.findViewById(R.id.locationText);
            locationDate=itemView.findViewById(R.id.locationDate);
            mapLayout=itemView.findViewById(R.id.mapLayout);
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
            cancelText=itemView.findViewById(R.id.cancelText);
            /*Todo:- Image*/
            image=itemView.findViewById(R.id.Image);
            imageText=itemView.findViewById(R.id.Text);
            dateImage=itemView.findViewById(R.id.dateImage);
            imageLayout=itemView.findViewById(R.id.imageLayout);
            /*Todo:- Rating*/
            ratingLayout=itemView.findViewById(R.id.ratingLayout);
            ratingsMessage=itemView.findViewById(R.id.ratingsMessage);
            dateRating=itemView.findViewById(R.id.dateRating);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            /*Todo:- Audio*/
            voicePlayerView=itemView.findViewById(R.id.voicePlayerView);
            /*Todo:- Address*/
            addressLayout=itemView.findViewById(R.id.addressLayout);
            addressText=itemView.findViewById(R.id.addressText);
            addressLinkText=itemView.findViewById(R.id.addressLinkText);
            addressDate=itemView.findViewById(R.id.addressDate);
            /*Todo:- Track Location*/
            trackLocationLayout=itemView.findViewById(R.id.trackLocationLayout);
            trackLocationText=itemView.findViewById(R.id.trackLocationText);


        }


     }
     public void recycle() {
         unregisterNotifications();
     }
     private void unregisterNotifications() {
         LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
     }

}
