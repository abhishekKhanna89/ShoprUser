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
import android.widget.Button;
import android.widget.EditText;
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
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.StorelistingActivity;
import com.shoppr.shoper.activity.AddMoneyActivity;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.OrderDetailsActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
import com.shoppr.shoper.activity.TrackLoactionActivity;
import com.shoppr.shoper.activity.ViewCartActivity;
import com.shoppr.shoper.activity.WalletActivity;
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
import java.util.ArrayList;
import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.os.FileUtils.copy;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
    private static final int IO_BUFFER_SIZE = 1;
    List<Chat> chatList;
    Context context;
    private int SELF = 1;
    View itemView;
    SessonManager sessonManager;
    BroadcastReceiver mMessageReceiver;
    String title, body;
    String ratingValue;

    public ChatMessageAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_layout, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_layout, parent, false);
        }
        //returing the view
        return new Holder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Chat chat = chatList.get(position);
        sessonManager = new SessonManager(context);
        recycle();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("title") != null || intent.getStringExtra("body") != null) {
                    holder.textLayout.setVisibility(View.VISIBLE);
                    title = intent.getStringExtra("title");
                    body = intent.getStringExtra("body");
                    //holder.message_body.setText(title+"\t"+body);
                    // Toast.makeText(context, "Title:- "+title+" Body:- "+body, Toast.LENGTH_SHORT).show();
                } else {
                    holder.textLayout.setVisibility(View.GONE);
                }
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter(i));


        if (chat.getType().equalsIgnoreCase("image")) {
            Picasso.get().load(chat.getFilePath()).into(holder.image);
            //Glide.with(context).load(chat.getFilePath()).into(holder.image);
            holder.imageText.setText(chat.getMessage());
            holder.dateImage.setText(chat.getCreatedAt());

        } else {
            holder.imageLayout.setVisibility(View.GONE);

        }
        if (chat.getType().equalsIgnoreCase("text")) {
            holder.message_body.setText(chat.getMessage());
            holder.dateText.setText(chat.getCreatedAt());
        } else {
            holder.textLayout.setVisibility(View.GONE);
        }
        if (chat.getType().equalsIgnoreCase("product")) {
            if (chat.getFilePath().length() == 0) {

            } else {
                Picasso.get().load(chat.getFilePath()).into(holder.productImage);
            }
            //Glide.with(context).load(chat.getFilePath()).into(holder.productImage);
            holder.productMessage.setText(chat.getMessage());
            holder.dateProduct.setText(chat.getCreatedAt());
            holder.pqText.setText(chat.getQuantity() + " / " + "₹" + chat.getPrice());
        } else {
            holder.productLayout.setVisibility(View.GONE);
        }
        if (chat.getType().equalsIgnoreCase("rating")) {
            holder.ratingsMessage.setText(chat.getMessage());
            holder.dateRating.setText(chat.getCreatedAt());
            holder.ratingBar.setRating(Float.parseFloat(chat.getQuantity()));

        } else {
            holder.ratingLayout.setVisibility(View.GONE);
        }
        if (chat.getType().equalsIgnoreCase("audio")) {
            holder.voicePlayerView.setAudio(chat.getFilePath());
        } else {
            holder.voicePlayerView.setVisibility(View.GONE);
        }

        if (chat.getType().equalsIgnoreCase("address-request")) {
            holder.addressText.setText(chat.getMessage());
            holder.addressDate.setText(chat.getCreatedAt());
        } else {
            holder.addressLayout.setVisibility(View.GONE);
        }
        if (chat.getType().equalsIgnoreCase("address")) {
            String lat = chat.getLat();
            String lon = chat.getLang();
            String url = "https://maps.googleapis.com/maps/api/staticmap?";
            url += "&zoom=14";
            url += "&size=200x200";
            url += "&maptype=roadmap";
            url += "&markers=color:red%7Clabel:%7C" + lat + ", " + lon;
            url += "&key=AIzaSyCHl8Ff_ghqPjWqlT2BXJH5BOYH1q-sw0E";
            Picasso.get().load(url).into(holder.locationImage);
            String currentString = chat.getMessage();
            String[] parts = currentString.split("#");
            String a=parts[0];
            String b=parts[1];
            holder.location2Text.setText(b);
            holder.locationText.setText(a);
            holder.locationDate.setText(chat.getCreatedAt());
        } else {
            holder.mapLayout.setVisibility(View.GONE);
        }

        if (chat.getType().equalsIgnoreCase("track")) {
            holder.trackLocationLayout.setVisibility(View.VISIBLE);
            holder.trackLocationText.setText(chat.getMessage());
            holder.trackLocationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chat_id = String.valueOf(chat.getChatId());
                    Log.d("resChatId", chat_id);
                    context.startActivity(new Intent(context, TrackLoactionActivity.class)
                            .putExtra("chatId", chat_id));
                }
            });
        }

        if (chat.getType().equalsIgnoreCase("add-money")) {
            holder.addWalletLayout.setVisibility(View.VISIBLE);
            holder.addWalletMsgText.setText(chat.getMessage());
            holder.addwalletDate.setText(chat.getCreatedAt());
            /*Todo:-Add Wallet Listener*/
            holder.addWalletBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chat_id = String.valueOf(chat.getChatId());
                    //Log.d("chatIDDD",""+chat_id);
                    context.startActivity(new Intent(context, AddMoneyActivity.class)
                            .putExtra("chat_id", chat_id)
                            .putExtra("value", "1"));
                }
            });
        }
        if (chat.getType().equalsIgnoreCase("recharge")) {
            holder.rechargeLayout.setVisibility(View.VISIBLE);
            holder.rechargeMsgText.setText(chat.getMessage());
            holder.rechargeDateText.setText(chat.getCreatedAt());
        }

        if (chat.getType().equalsIgnoreCase("payment")) {
            holder.paymentLayout.setVisibility(View.VISIBLE);
            holder.paymentDate.setText(chat.getCreatedAt());
            holder.paymentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chat_id = String.valueOf(chat.getChatId());
                    Log.d("ChatIDDD", chat_id);
                    context.startActivity(new Intent(context, ViewCartActivity.class)
                            .putExtra("valueId", "2")
                            .putExtra("chat_id", chat_id));
                }
            });
        }

        if (chat.getType().equalsIgnoreCase("order_confirmed")) {
            holder.orderConfirmLayout.setVisibility(View.VISIBLE);
            holder.orderConfirmMessage.setText(chat.getMessage());
            holder.orderConfirmDate.setText(chat.getCreatedAt());
            holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chat_id = String.valueOf(chat.getChatId());
                    context.startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("orderId", chat.getOrder_id())
                            .putExtra("position", position));
                }
            });
        }

        /*Todo:- Visibility Concept*/
        if (chat.getStatus().equalsIgnoreCase("accepted")) {
            holder.greenLayout.setVisibility(View.VISIBLE);
            holder.closeRedLayout.setVisibility(View.GONE);
            holder.rejectText.setVisibility(View.GONE);
            holder.cancelText.setVisibility(View.VISIBLE);
            holder.acceptText.setVisibility(View.GONE);
            holder.ratingLayout.setEnabled(false);
            holder.ratingBar.setIsIndicator(true);

        }
        if (chat.getStatus().equalsIgnoreCase("rejected")) {
            holder.closeRedLayout.setVisibility(View.VISIBLE);
            holder.greenLayout.setVisibility(View.GONE);
            holder.acceptText.setVisibility(View.GONE);
            holder.rejectText.setVisibility(View.GONE);
            holder.cancelText.setVisibility(View.GONE);
        }
        if (chat.getStatus().equalsIgnoreCase("cancelled")) {
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
                    Call<AcceptModel> call = ApiExecutor.getApiService(context)
                            .apiAccept("Bearer " + sessonManager.getToken(), chat.getId());
                    call.enqueue(new Callback<AcceptModel>() {
                        @Override
                        public void onResponse(Call<AcceptModel> call, Response<AcceptModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                AcceptModel acceptModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                    if (acceptModel.getStatus().equalsIgnoreCase("success")) {
                                        holder.greenLayout.setVisibility(View.VISIBLE);
                                        holder.closeRedLayout.setVisibility(View.GONE);
                                        holder.rejectText.setVisibility(View.GONE);
                                        holder.cancelText.setVisibility(View.VISIBLE);
                                        holder.acceptText.setVisibility(View.GONE);
                                        if (context instanceof ChatActivity) {
                                            ((ChatActivity)context).yourDesiredMethod();
                                        }
                                       /* context.startActivity(new Intent(context, ChatActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
                                    }
                                    Toast.makeText(context, "" + acceptModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AcceptModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                }

            }
        });

        holder.rejectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(context)) {
                    //sessonManager.showProgress(context);
                    Call<RejectedModel> call = ApiExecutor.getApiService(context)
                            .apiRejected("Bearer " + sessonManager.getToken(), chat.getId());
                    call.enqueue(new Callback<RejectedModel>() {
                        @Override
                        public void onResponse(Call<RejectedModel> call, Response<RejectedModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                RejectedModel rejectedModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    if (rejectedModel.getStatus().equalsIgnoreCase("success")) {
                                        holder.closeRedLayout.setVisibility(View.VISIBLE);
                                        holder.greenLayout.setVisibility(View.GONE);
                                        holder.acceptText.setVisibility(View.GONE);
                                        holder.rejectText.setVisibility(View.GONE);
                                        holder.cancelText.setVisibility(View.GONE);
                                        if (context instanceof ChatActivity) {
                                            ((ChatActivity)context).yourDesiredMethod();
                                        }
                                    }
                                    Toast.makeText(context, "" + rejectedModel.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "" +rejectedModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RejectedModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                }

            }
        });
        holder.cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(context)) {
                    //sessonManager.showProgress(context);
                    Call<CancelModel> call = ApiExecutor.getApiService(context)
                            .apiCancel("Bearer " + sessonManager.getToken(), chat.getId());
                    call.enqueue(new Callback<CancelModel>() {
                        @Override
                        public void onResponse(Call<CancelModel> call, Response<CancelModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                CancelModel cancelModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                    if (cancelModel.getStatus().equalsIgnoreCase("success")) {
                                        holder.closeRedLayout.setVisibility(View.VISIBLE);
                                        holder.greenLayout.setVisibility(View.GONE);
                                        holder.acceptText.setVisibility(View.GONE);
                                        holder.rejectText.setVisibility(View.GONE);
                                        holder.cancelText.setVisibility(View.GONE);
                                        if (context instanceof ChatActivity) {
                                            ((ChatActivity)context).yourDesiredMethod();
                                        }
                                    }
                                    Toast.makeText(context, "" + cancelModel.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "" +cancelModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CancelModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                }
            }
        });


        holder.addressLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chatId=chat.getChatId();
                //Log.d("chat_id",""+chatId);
                context.startActivity(new Intent(context, ShareLocationActivity.class)
                        .putExtra("chatId", chatId)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



        /*Todo:-   Zoom Image*/
        holder.locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + chat.getLat() + "," + chat.getLang());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.image_layout
                        , null));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);
                ImageView imageFirst = (ImageView) dialog.findViewById(R.id.imageView);
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
                final Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.image_layout
                        , null));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);
                ImageView imageFirst = (ImageView) dialog.findViewById(R.id.imageView);
                Picasso.get().load(chat.getFilePath()).into(imageFirst);
                PhotoViewAttacher pAttacher;
                pAttacher = new PhotoViewAttacher(imageFirst);
                pAttacher.update();
                dialog.show();
            }
        });

        holder.ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.layout_rating_dialog
                        , null));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);

                ImageView backPress = dialog.findViewById(R.id.backPress);
                backPress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                AppCompatRatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        float a = rating;
                        int b;
                        b = (int) a;
                        ratingValue = String.valueOf(b);

                    }
                });
                EditText messageEt = dialog.findViewById(R.id.messageEt);
                Button submitRatingBtn = dialog.findViewById(R.id.submitRatingBtn);
                submitRatingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isOnline(context)) {
                            //sessonManager.showProgress(context);
                            RatingsRequest ratingsRequest = new RatingsRequest();
                            ratingsRequest.setRatings(ratingValue);
                            ratingsRequest.setComment(messageEt.getText().toString());

                            Call<RatingsModel> call = ApiExecutor.getApiService(context)
                                    .apiRatings("Bearer " + sessonManager.getToken(), chat.getId(), ratingsRequest);
                            call.enqueue(new Callback<RatingsModel>() {
                                @Override
                                public void onResponse(Call<RatingsModel> call, Response<RatingsModel> response) {
                                    //sessonManager.hideProgress();
                                    //Log.d("response",response.body().getStatus());
                                    if (response.body() != null) {
                                        RatingsModel ratingsModel=response.body();
                                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                            Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            if (context instanceof ChatActivity) {
                                                ((ChatActivity)context).yourDesiredMethod();
                                            }
                                        } else {
                                            Toast.makeText(context, "" + ratingsModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<RatingsModel> call, Throwable t) {
                                    //sessonManager.hideProgress();
                                }
                            });
                        } else {
                            CommonUtils.showToastInCenter((Activity) context, context.getString(R.string.please_check_network));
                        }
                    }
                });

                dialog.show();
            }
        });

        holder.setIsRecyclable(false);


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
        if (message.getDirection() == 1) {
            //Returning self
            return SELF;
        }
        //else returning position
        return position;
    }


    public class Holder extends RecyclerView.ViewHolder {
        /*Todo:- Location*/
        ImageView locationImage;
        TextView locationText, locationDate,location2Text;
        ChatMessageView mapLayout;
        /*Todo:- Text*/
        TextView message_body, dateText;
        ChatMessageView textLayout;
        /*Todo:- Product*/
        ImageView productImage;
        TextView pqText, dateProduct, productMessage;
        Button acceptText, rejectText, cancelText;
        LinearLayout greenLayout, closeRedLayout;
        ChatMessageView productLayout;
        /*Todo:- Image*/
        ImageView image;
        TextView imageText, dateImage;
        ChatMessageView imageLayout;
        /*Todo:- Rating*/
        ChatMessageView ratingLayout;
        TextView ratingsMessage, dateRating;
        AppCompatRatingBar ratingBar;
        /*Todo:- Audio*/
        VoicePlayerView voicePlayerView;

        /*Todo:- Address*/
        ChatMessageView addressLayout;
        TextView addressText, addressLinkText, addressDate;

        /*Todo:- Track Location*/
        ChatMessageView trackLocationLayout;
        Button trackLocationText;

        /*Todo:- Add Wallet*/
        ChatMessageView addWalletLayout;
        TextView addWalletMsgText, addwalletDate;
        Button addWalletBtn;

        /*Todo:- Recharge*/
        ChatMessageView rechargeLayout;
        TextView rechargeMsgText, rechargeDateText;

        /*Todo:- Payment*/
        ChatMessageView paymentLayout;
        Button paymentBtn;
        TextView paymentDate;

        /*Todo:- Confirm Details*/
        ChatMessageView orderConfirmLayout;
        TextView orderConfirmMessage, orderConfirmDate;
        Button detailsBtn;

        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
            locationImage = itemView.findViewById(R.id.locationImage);
            locationText = itemView.findViewById(R.id.locationText);
            locationDate = itemView.findViewById(R.id.locationDate);
            mapLayout = itemView.findViewById(R.id.mapLayout);
            location2Text=itemView.findViewById(R.id.location2Text);
            /*Todo:- Text*/
            message_body = itemView.findViewById(R.id.message_body);
            dateText = itemView.findViewById(R.id.dateText);
            textLayout = itemView.findViewById(R.id.textLayout);
            /*Todo:- Product*/
            productImage = itemView.findViewById(R.id.productImage);
            pqText = itemView.findViewById(R.id.pqText);
            dateProduct = itemView.findViewById(R.id.dateProduct);
            productMessage = itemView.findViewById(R.id.productMessage);
            productLayout = itemView.findViewById(R.id.productLayout);
            greenLayout = itemView.findViewById(R.id.greenLayout);
            closeRedLayout = itemView.findViewById(R.id.closeRedLayout);
            acceptText = itemView.findViewById(R.id.acceptText);
            rejectText = itemView.findViewById(R.id.rejectText);
            cancelText = itemView.findViewById(R.id.cancelText);
            /*Todo:- Image*/
            image = itemView.findViewById(R.id.Image);
            imageText = itemView.findViewById(R.id.Text);
            dateImage = itemView.findViewById(R.id.dateImage);
            imageLayout = itemView.findViewById(R.id.imageLayout);
            /*Todo:- Rating*/
            ratingLayout = itemView.findViewById(R.id.ratingLayout);
            ratingsMessage = itemView.findViewById(R.id.ratingsMessage);
            dateRating = itemView.findViewById(R.id.dateRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingBar.setFocusableInTouchMode(true);
            ratingBar.setFocusable(true);
            ratingBar.setIsIndicator(true);
            /*Todo:- Audio*/
            voicePlayerView = itemView.findViewById(R.id.voicePlayerView);
            /*Todo:- Address*/
            addressLayout = itemView.findViewById(R.id.addressLayout);
            addressText = itemView.findViewById(R.id.addressText);
            addressLinkText = itemView.findViewById(R.id.addressLinkText);
            addressDate = itemView.findViewById(R.id.addressDate);

            /*Todo:- Track Location*/
            trackLocationLayout = itemView.findViewById(R.id.trackLocationLayout);
            trackLocationText = itemView.findViewById(R.id.trackLocationText);
            /*Todo:- Add Wallet*/
            addWalletLayout = itemView.findViewById(R.id.addWalletLayout);
            addWalletMsgText = itemView.findViewById(R.id.addWalletMsgText);
            addwalletDate = itemView.findViewById(R.id.addwalletDate);
            addWalletBtn = itemView.findViewById(R.id.addWalletBtn);
            /*Todo:- Recharge*/
            rechargeLayout = itemView.findViewById(R.id.rechargeLayout);
            rechargeMsgText = itemView.findViewById(R.id.rechargeMsgText);
            rechargeDateText = itemView.findViewById(R.id.rechargeDateText);
            /*Todo:- Payment*/
            paymentLayout = itemView.findViewById(R.id.paymentLayout);
            paymentBtn = itemView.findViewById(R.id.paymentBtn);
            paymentDate = itemView.findViewById(R.id.paymentDate);

            /*Todo:- Confirm Details*/
            orderConfirmLayout = itemView.findViewById(R.id.orderConfirmLayout);
            orderConfirmMessage = itemView.findViewById(R.id.orderConfirmMessage);
            orderConfirmDate = itemView.findViewById(R.id.orderConfirmDate);
            detailsBtn = itemView.findViewById(R.id.detailsBtn);
        }
    }

    public void recycle() {
        unregisterNotifications();
    }

    private void unregisterNotifications() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

}
