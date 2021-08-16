package com.shoppr.shoper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;
import com.rygelouv.audiosensei.player.OnPlayerViewClickListener;
import com.shoppr.shoper.Model.AcceptModel;
import com.shoppr.shoper.Model.CancelModel;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.RatingsModel;
import com.shoppr.shoper.Model.RejectedModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.activity.AddMoneyActivity;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.OrderDetailsActivity;
import com.shoppr.shoper.activity.ShareLocationActivity;
import com.shoppr.shoper.activity.TrackLoactionActivity;
import com.shoppr.shoper.activity.ViewCartActivity;
import com.shoppr.shoper.requestdata.RatingsRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.ConstantValue;
import com.shoppr.shoper.util.MyPreferences;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.himanshusoni.chatmessageview.ChatMessageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

public class
ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.Holder>{
    private static final int IO_BUFFER_SIZE = 1;
    List<Chat> chatList;
    Context context;
    // private int SELF = 1;
    View itemView;
    SessonManager sessonManager;
    BroadcastReceiver mMessageReceiver;
    String title, body;
    String ratingValue;

    private int SELF_DIRECTION = 1;
    private int SELF_TEXT_IN = 1;
    private int SELF_IMAGE_IN = 3;
    private int SELF_TEXT_OUT = 2;
    private int SELF_IMAGE_OUT = 4;
    private int SELF_PRODUCT_IN = 5;
    private int SELF_PRODUCT_OUT = 6;
    private int SELF_RATING_IN = 7;
    private int SELF_RATING_OUT = 8;
    private int SELF_AUDIO_IN = 9;
    private int SELF_AUDIO_OUT = 10;
    private int SELF_ADDMONEY_IN = 11;
    private int SELF_ADDMONEY_OUT = 12;
    private int SELF_RECHARGE_IN = 13;
    private int SELF_RECHARGE_OUT = 14;
    private int SELF_PAID_IN = 15;
    private int SELF_PAID_OUT = 16;
    private int SELF_ADDRESS_IN = 17;
    private int SELF_ADDRESS_OUT = 18;
    private int SELF_STORE_IN = 19;
    private int SELF_STORE_OUT = 20;
    private int SELF_ORDERCONFIRMED_IN = 21;
    private int SELF_ORDERCONFIRMED_OUT = 22;
    private int SELF_ADDRESSTYPE_IN = 23;
    private int SELF_ADDRESSTYPE_OUT = 24;
    private int SELF_TRACK_IN = 26;
    private int SELF_TRACK_OUT = 27;
    // private int  SELF_ADDMONEY_IN=24;
    private int SELF_DISCOUNT_IN = 30;
    private int SELF_DISCOUNT_OUT = 31;
    boolean isPLAYING = false;
    private int countforplay = 0;


    // addmoney

    public ChatMessageAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     /*   //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_layout, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_layout, parent, false);
        }*/

        if (viewType == SELF_TEXT_IN) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_text_layout, parent, false);
        } else if (viewType == SELF_TEXT_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_text_layout, parent, false);
        } else if (viewType == SELF_DISCOUNT_IN) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_discount_layout, parent, false);
        } else if (viewType == SELF_DISCOUNT_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_discount_layout, parent, false);
        }

        // lk changes here
        else if (viewType == SELF_PRODUCT_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_product_layout, parent, false);
        } else if (viewType == SELF_PRODUCT_OUT) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_product_layout, parent, false);
        } else if (viewType == SELF_RATING_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_rating_layout, parent, false);
        } else if (viewType == SELF_RATING_OUT) {
            //Log.d("outrating==", String.valueOf(SELF_RATING_OUT));

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_rating_layout, parent, false);
        } else if (viewType == SELF_AUDIO_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_audio_layout_two, parent, false);
        } else if (viewType == SELF_AUDIO_OUT) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_audio_layout_two, parent, false);
        } else if (viewType == SELF_ADDMONEY_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_addwallet_layout, parent, false);
        } else if (viewType == SELF_ADDMONEY_OUT) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_addwallet_layout, parent, false);
        } else if (viewType == SELF_RECHARGE_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_recharge_layout, parent, false);
        } else if (viewType == SELF_RECHARGE_OUT) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_recharge_layout, parent, false);
        } else if (viewType == SELF_PAID_IN) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_payment_layout, parent, false);
        } else if (viewType == SELF_PAID_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_payment_layout, parent, false);
        } else if (viewType == SELF_ADDRESS_IN) {

            // Log.d("outratingaddin==", String.valueOf(SELF_RATING_IN));
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_location_layout, parent, false);
        } else if (viewType == SELF_ADDRESS_OUT) {

            Log.d("outratingaddout==", String.valueOf(SELF_RATING_IN));
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_location_layout, parent, false);
        } else if (viewType == SELF_STORE_IN) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_store_layout, parent, false);
        } else if (viewType == SELF_STORE_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_store_layout, parent, false);
        } else if (viewType == SELF_ORDERCONFIRMED_IN) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_orderconfirmed_layout, parent, false);
        } else if (viewType == SELF_ORDERCONFIRMED_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_oredercofirm_layout, parent, false);
        } else if (viewType == SELF_IMAGE_IN) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_image_layout, parent, false);
        } else if (viewType == SELF_IMAGE_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_image_layout, parent, false);
        } else if (viewType == SELF_ADDRESSTYPE_IN) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_locationtype_layout, parent, false);
        } else if (viewType == SELF_ADDRESSTYPE_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_locationtype_layout, parent, false);
        } else if (viewType == SELF_TRACK_IN) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_track_layout, parent, false);
        } else if (viewType == SELF_TRACK_OUT) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.out_msg_track_layout, parent, false);
        } else {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.in_msg_blank_layout, parent, false);
        }
//
//


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
                    //  holder.textLayout.setVisibility(View.GONE);
                }
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, i);


        if (chat.getType().equalsIgnoreCase("image")) {
            if (!chat.getFilePath().isEmpty())
            Picasso.get().load(chat.getFilePath()).into(holder.image);
            //Glide.with(context).load(chat.getFilePath()).into(holder.image);
            holder.imageText.setText(chat.getMessage());
            holder.dateImage.setText(chat.getCreatedAt());
            holder.imageLayout.setVisibility(View.VISIBLE);
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

        }/* else {
            holder.imageLayout.setVisibility(View.GONE);

        }*/ else if (chat.getType().equalsIgnoreCase("text")) {
            holder.message_body.setText(chat.getMessage());
            holder.dateText.setText(chat.getCreatedAt());
            holder.textLayout.setVisibility(View.VISIBLE);
            if (chat.getMessage().contains("with the shopper has been terminated")||chat.getMessage().contains("Order has been delivered")){
                MyPreferences.saveBoolean(context, ConstantValue.KEY_IS_CHAT_PROGRESS, false);
            }
        } else if (chat.getType().equalsIgnoreCase("discount")) {
            holder.message_body.setText(chat.getMessage());
            holder.dateText.setText(chat.getCreatedAt());
            //holder.textLayout.setVisibility(View.VISIBLE);
        }

        else if (chat.getType().equalsIgnoreCase("product")||chat.getType().equalsIgnoreCase("products")) {
            holder.productLayout.setVisibility(View.VISIBLE);
            if (chat.getFilePath().length() == 0) {

            } else {
                Picasso.get().load(chat.getFilePath()).into(holder.productImage);
            }
            //Glide.with(context).load(chat.getFilePath()).into(holder.productImage);
            holder.productMessage.setText(chat.getMessage());
            holder.dateProduct.setText(chat.getCreatedAt());
            holder.pqText.setText(chat.getQuantity() + " for " + "₹" + chat.getPrice());

            if (chat.getStatus().equalsIgnoreCase("accepted")) {
                holder.greenLayout.setVisibility(View.VISIBLE);
                holder.closeRedLayout.setVisibility(View.GONE);
                holder.rejectText.setVisibility(View.GONE);
                holder.cancelText.setVisibility(View.GONE);
                holder.acceptText.setVisibility(View.GONE);
                //   holder.ratingLayout.setEnabled(false);
                //  holder.ratingBar.setIsIndicator(true);

            } else if (chat.getStatus().equalsIgnoreCase("rejected")) {
                holder.closeRedLayout.setVisibility(View.VISIBLE);
                holder.greenLayout.setVisibility(View.GONE);
                holder.acceptText.setVisibility(View.GONE);
                holder.rejectText.setVisibility(View.GONE);
                holder.cancelText.setVisibility(View.GONE);
            } else if (chat.getStatus().equalsIgnoreCase("cancelled")) {
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
                                            //make canceltext disable if product is accepted
                                            holder.cancelText.setVisibility(View.GONE);
                                            holder.acceptText.setVisibility(View.GONE);
                                            if (context instanceof ChatActivity) {
                                                ((ChatActivity) context).yourDesiredMethod();
                                            }
                              /*              context.startActivity(new Intent(context, ChatActivity.class)
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
                                                ((ChatActivity) context).yourDesiredMethod();
                                            }
                                        }
                                        Toast.makeText(context, "" + rejectedModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "" + rejectedModel.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                ((ChatActivity) context).yourDesiredMethod();
                                            }
                                        }
                                        Toast.makeText(context, "" + cancelModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "" + cancelModel.getMessage(), Toast.LENGTH_SHORT).show();
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


            //
        }/* else {
            holder.productLayout.setVisibility(View.GONE);
        }*/ else if (chat.getType().equalsIgnoreCase("rating")) {
            holder.ratingLayout.setEnabled(true);
            holder.ratingBar.setIsIndicator(true);
            holder.ratingsMessage.setText(chat.getMessage());
            holder.dateRating.setText(chat.getCreatedAt());
            holder.ratingBar.setRating(Float.parseFloat(chat.getQuantity()));
            holder.ratingLayout.setVisibility(View.VISIBLE);


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
                    AppCompatButton submitRatingBtn = dialog.findViewById(R.id.submitRatingBtn);
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
                                            RatingsModel ratingsModel = response.body();
                                            if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                if (context instanceof ChatActivity) {
                                                    ((ChatActivity) context).yourDesiredMethod();
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


        } /*else {
            holder.ratingLayout.setVisibility(View.GONE);
        }*/ else if (chat.getType().equalsIgnoreCase("audio")) {

            holder.dateText11.setText(chat.getCreatedAt());
            holder.audio_player.setAudioTarget(chat.getFilePath());

            holder.dateText11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("msg_time", "msg_time");
                }
            });
        } else if (chat.getType().equalsIgnoreCase("address-request")) {
            holder.addressText.setText(chat.getMessage());
            holder.addressDate.setText(chat.getCreatedAt());
            holder.addressLayout.setVisibility(View.VISIBLE);

            holder.addressLinkText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int chatId = chat.getChatId();
                    //Log.d("chat_id",""+chatId);
                    context.startActivity(new Intent(context, ShareLocationActivity.class)
                            .putExtra("chatId", chatId)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        } /*else {
            holder.addressLayout.setVisibility(View.GONE);
        }*/ else if (chat.getType().equalsIgnoreCase("address")) {
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
            try {
                String a = parts[0];
                String b = parts[1];

                //Log.d("value1==", (String) holder.location2Text.getText());
                //  Log.d("value1===",(String) holder.locationText.getText());
                holder.locationText.setText(a);
                holder.location2Text.setVisibility(View.GONE);
                holder.locationText.setVisibility(View.VISIBLE);
                //holder.location2Text.setText(b);
                /// holder.locationText.setText(a);
            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.locationDate.setText(chat.getCreatedAt());
            holder.mapLayout.setVisibility(View.VISIBLE);
            holder.locationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + chat.getLat() + "," + chat.getLang());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            });


        } /*else {
            holder.mapLayout.setVisibility(View.GONE);
        }*/ else if (chat.getType().equalsIgnoreCase("track")) {
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
        } else if (chat.getType().equalsIgnoreCase("add-money")) {
            //holder.addWalletLayout.setVisibility(View.VISIBLE);
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
        } else if (chat.getType().equalsIgnoreCase("recharge")) {
            holder.rechargeLayout.setVisibility(View.VISIBLE);
            holder.rechargeMsgText.setText(chat.getMessage());
            holder.rechargeDateText.setText(chat.getCreatedAt());
        } else if (chat.getType().equalsIgnoreCase("payment")) {
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
        } else if (chat.getType().equalsIgnoreCase("order_confirmed")) {
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
     /*   if (message.getDirection() == 1) {
            //Returning self
            return SELF;
        }*/
        //else returning position
        if (message.getDirection() == 1) {
            //Returning self

            if (message.getType().equalsIgnoreCase("text")) {
                return SELF_TEXT_IN;
            } else if (message.getType().equalsIgnoreCase("discount")) {
                return SELF_DISCOUNT_IN;
            } else if (message.getType().equalsIgnoreCase("image")) {
                return SELF_IMAGE_IN;
            } else if (message.getType().equalsIgnoreCase("product")||message.getType().equalsIgnoreCase("products")) {
                return SELF_PRODUCT_IN;
            } else if (message.getType().equalsIgnoreCase("rating")) {
                Log.d("messagetype===", message.getType());

                return SELF_RATING_IN;
            } else if (message.getType().equalsIgnoreCase("audio")) {
                return SELF_AUDIO_IN;
            } else if (message.getType().equalsIgnoreCase("add-money")) {
                return SELF_ADDMONEY_IN;
            } else if (message.getType().equalsIgnoreCase("recharge")) {
                return SELF_RECHARGE_IN;
            } else if (message.getType().equalsIgnoreCase("payment")) {
                return SELF_PAID_IN;
            } else if (message.getType().equalsIgnoreCase("address-request")) {
                return SELF_ADDRESS_IN;
            } else if (message.getType().equalsIgnoreCase("store")) {
                return SELF_STORE_IN;
            } else if (message.getType().equalsIgnoreCase("order_confirmed")) {
                return SELF_ORDERCONFIRMED_IN;
            } else if (message.getType().equalsIgnoreCase("address")) {
                return SELF_ADDRESSTYPE_IN;
            } else if (message.getType().equalsIgnoreCase("track")) {
                return SELF_TRACK_IN;
            }


            //return SELF_TEXT;
        } else {
            if (message.getType().equalsIgnoreCase("text")) {
                return SELF_TEXT_OUT;
            } else if (message.getType().equalsIgnoreCase("discount")) {
                return SELF_DISCOUNT_OUT;
            } else if (message.getType().equalsIgnoreCase("image")) {
                return SELF_IMAGE_OUT;
            } else if (message.getType().equalsIgnoreCase("product")) {
                return SELF_PRODUCT_OUT;
            } else if (message.getType().equalsIgnoreCase("rating")) {
                Log.d("messagetype===", message.getType());
                return SELF_RATING_OUT;
            } else if (message.getType().equalsIgnoreCase("audio")) {
                return SELF_AUDIO_OUT;
            } else if (message.getType().equalsIgnoreCase("add-money")) {
                return SELF_ADDMONEY_OUT;
            } else if (message.getType().equalsIgnoreCase("recharge")) {
                return SELF_RECHARGE_OUT;
            } else if (message.getType().equalsIgnoreCase("payment")) {
                return SELF_PAID_OUT;
            } else if (message.getType().equalsIgnoreCase("address-request")) {

                return SELF_ADDRESS_OUT;
            } else if (message.getType().equalsIgnoreCase("store")) {
                return SELF_STORE_OUT;
            } else if (message.getType().equalsIgnoreCase("order_confirmed")) {
                return SELF_ORDERCONFIRMED_OUT;
            } else if (message.getType().equalsIgnoreCase("address")) {
                return SELF_ADDRESSTYPE_OUT;
            } else if (message.getType().equalsIgnoreCase("track")) {
                return SELF_TRACK_OUT;
            }

        }


        return 0;
    }


    public class Holder extends RecyclerView.ViewHolder {
        /*Todo:- Location*/
        ImageView locationImage;
        TextView locationText, locationDate, location2Text;
        ChatMessageView mapLayout;
        /*Todo:- Text*/
        TextView message_body, dateText;
        ChatMessageView textLayout;
        /*Todo:- Product*/
        ImageView productImage;
        TextView pqText, dateProduct, productMessage;
        AppCompatButton acceptText, rejectText, cancelText;
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
        AudioSenseiPlayerView audio_player;

        CircleImageView img_play;
        TextView dateText11;

        /*Todo:- Address*/
        ChatMessageView addressLayout;
        TextView addressText, addressLinkText, addressDate;

        /*Todo:- Track Location*/
        ChatMessageView trackLocationLayout;
        AppCompatButton trackLocationText;

        /*Todo:- Add Wallet*/
        ChatMessageView addWalletLayout;
        TextView addWalletMsgText, addwalletDate;
        AppCompatButton addWalletBtn;

        /*Todo:- Recharge*/
        ChatMessageView rechargeLayout;
        TextView rechargeMsgText, rechargeDateText;

        /*Todo:- Payment*/
        ChatMessageView paymentLayout;
        AppCompatButton paymentBtn;
        TextView paymentDate;

        /*Todo:- Confirm Details*/
        ChatMessageView orderConfirmLayout;
        TextView orderConfirmMessage, orderConfirmDate;
        AppCompatButton detailsBtn;

        public Holder(@NonNull View itemView) {
            super(itemView);
            /*Todo:- Location*/
            locationImage = itemView.findViewById(R.id.locationImage);
            locationText = itemView.findViewById(R.id.locationText);
            locationDate = itemView.findViewById(R.id.locationDate);
            mapLayout = itemView.findViewById(R.id.mapLayout);
            location2Text = itemView.findViewById(R.id.location2Text);
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
          /*  ratingBar.setFocusableInTouchMode(true);
            ratingBar.setFocusable(true);
            ratingBar.setIsIndicator(true);*/
            /*Todo:- Audio*/
            audio_player = itemView.findViewById(R.id.audio_player);
            dateText11 = itemView.findViewById(R.id.dateText11);
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

    public String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        String value = "";
        if (seconds >= 216000000)
            value = String.format("%02d:%02d:%02d", h, m, s);
        else
            value = String.format("%02d:%02d", m, s);

        return value;
    }

    public void recycle() {
        unregisterNotifications();
    }

    private void unregisterNotifications() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

}
