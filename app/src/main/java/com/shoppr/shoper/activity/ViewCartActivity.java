package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.CartView.CartViewModel;
import com.shoppr.shoper.Model.CartView.Item;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCartActivity extends AppCompatActivity {
    SessonManager sessonManager;
    Progressbar progressbar;
    RecyclerView RvMyCart;
    ArrayList<Item> arrCartItemList=new ArrayList<>();
    Button BtnPlaceOrder,btn_continue;
    ImageView imgCart;
    LinearLayout linrBottomOrder;
    TextView txt_cart_totalprice,txtNoItems;
    String productId,sizeId,value;
    int chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        sessonManager=new SessonManager(this);
        progressbar = new Progressbar();
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "My Cart" + "</font>")));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RvMyCart = (RecyclerView) findViewById(R.id.rv_my_cart);
        BtnPlaceOrder = (Button) findViewById(R.id.btn_place_order);
        btn_continue = (Button) findViewById(R.id.btn_continue_shoping);
        linrBottomOrder =findViewById(R.id.liner_order);
        txt_cart_totalprice = (TextView) findViewById(R.id.txt_cart_totalprice);
        imgCart = (ImageView) findViewById(R.id.imge_cart_img);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewCartActivity.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        chatId=getIntent().getIntExtra("chatId",0);
        hitCartDetailsApi(2);
    }
    public void hitCartDetailsApi(int chatId){
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            progressbar.showProgress(ViewCartActivity.this);
            Call<CartViewModel>call= ApiExecutor.getApiService(this).apiCartView("Bearer "+sessonManager.getToken(),chatId);
            call.enqueue(new Callback<CartViewModel>() {
                @Override
                public void onResponse(Call<CartViewModel> call, Response<CartViewModel> response) {
                    progressbar.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            CartViewModel cartViewModel=response.body();
                            if (cartViewModel.getData()!=null){
                                txt_cart_totalprice.setText("₹ " +cartViewModel.getData().getTotal());
                                arrCartItemList = (ArrayList<Item>) cartViewModel.getData().getItems();
                                if (arrCartItemList.isEmpty()){
                                    btn_continue.setVisibility(View.VISIBLE);
                                    imgCart.setVisibility(View.VISIBLE);
                                }else {
                                    linrBottomOrder.setVisibility(View.VISIBLE);
                                    RvMyCart.setHasFixedSize(true);
                                    RvMyCart.setItemViewCacheSize(20);
                                    RvMyCart.setDrawingCacheEnabled(true);
                                    RvMyCart.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ViewCartActivity.this, 1);
                                    RvMyCart.setLayoutManager(layoutManager);
                                    MyCartAdapter myCartAdapter = new MyCartAdapter(ViewCartActivity.this, arrCartItemList);
                                    RvMyCart.setAdapter(myCartAdapter);

                                }


                                /*chatList=chatMessageModel.getData().getChats();
                                ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
                                chatRecyclerView.setAdapter(chatMessageAdapter);
                                chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);
                                chatMessageAdapter.notifyDataSetChanged();*/
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartViewModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
        /*Call<CartViewModel> call1 = apo.getCartDetails("Bearer " + sharedPreference.getString("auth_token", ""));
        call1.enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(Call<CartModel> call, retrofit2.Response<CartModel> response) {
                String res = new Gson().toJson(response.body());
                progressbar.hideProgress();
                Log.d("checklk",res);
                if (response.isSuccessful()) {
                    CartModel cartDetailsModel = response.body();
                    txt_cart_totalprice.setText("₹ " +cartDetailsModel.getTotal());

                    arrCartItemList = (ArrayList<CartModel.Cartitem>) cartDetailsModel.getCartitem();
                    arrSaveLetterList = (ArrayList<CartModel.Savelater>) cartDetailsModel.getSavelater();

                    if (arrCartItemList.isEmpty() && arrSaveLetterList.isEmpty() ){
                        btn_continue.setVisibility(View.VISIBLE);
                        imgCart.setVisibility(View.VISIBLE);
                        txtSaveForLetter.setVisibility(View.GONE);
                    }else {

                        linrBottomOrder.setVisibility(View.VISIBLE);
                        RvMyCart.setHasFixedSize(true);
                        RvMyCart.setItemViewCacheSize(20);
                        RvMyCart.setDrawingCacheEnabled(true);
                        RvMyCart.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MyCartActivity.this, 1);
                        RvMyCart.setLayoutManager(layoutManager);
                        MyCartAdapter myCartAdapter = new MyCartAdapter(MyCartActivity.this, arrCartItemList);
                        RvMyCart.setAdapter(myCartAdapter);

                        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MyCartActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recycleSaveforletter.setLayoutManager(horizontalLayoutManagaer);
                        SaveLaterAdapter saveLaterAdapter = new SaveLaterAdapter(MyCartActivity.this, arrSaveLetterList);
                        recycleSaveforletter.setAdapter(saveLaterAdapter);
                    }

                } else {
                    Toast.makeText(MyCartActivity.this, "" + response, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<CartModel> call, Throwable t) {
                Toast.makeText(MyCartActivity.this, "onFailure called ", Toast.LENGTH_SHORT).show();
                call.cancel();
                progressbar.hideProgress();
            }
        });*/
    }
    public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
        Context context;
        ArrayList<Item> arList;
        public MyCartAdapter(Context context, ArrayList<Item> arList) {
            this.context = context;
            this.arList = arList;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_cart, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.TvPrice.setText(arList.get(position).getQuantity()+"  "+ "\u20B9 "+arList.get(position).getPrice());
            holder.TvName.setText(arList.get(position).getType());
            Glide.with(context)
                    .load(arList.get(position).getFilePath())
                    .into(holder.iv_cart_detail);


        }
        @Override
        public int getItemCount() {
            return arList.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView TvName, TvPrice;
            LinearLayout linear_card;
            ImageView iv_cart_detail;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_cart_detail =  itemView.findViewById(R.id.iv_cart_detail);
                TvPrice = (TextView) itemView.findViewById(R.id.tv_price_my);
                TvName = (TextView) itemView.findViewById(R.id.tv_name_my);
                linear_card = itemView.findViewById(R.id.linear_card);

                /*txtCartRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtCartRemove.setEnabled(false);
                        value="2";
                        productId=arList.get(getAdapterPosition()).getProductId();
                        sizeId=arList.get(getAdapterPosition()).getSizeId();
                        removeAt(getAdapterPosition());
                        hitAddtoCartApi(0,"");

                    }
                });*/


                /*TvPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type="plus";
                        value="0";
                        productId=arList.get(getAdapterPosition()).getProductId();
                        sizeId=arList.get(getAdapterPosition()).getSizeId();
                        int quantity= Integer.parseInt(TvQuantity.getText().toString()) +1;
                        hitAddtoCartApi(quantity,type);

                    }
                });
                TvSubtract.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type="minus";
                        value="0";
                        productId=arList.get(getAdapterPosition()).getProductId();
                        sizeId=arList.get(getAdapterPosition()).getSizeId();
                        if (Integer.parseInt(String.valueOf(TvQuantity.getText()))==arList.get(getAdapterPosition()).minQty){
                            txtCartRemove.setEnabled(false);
                            txtSaveForLetter.setEnabled(false);
                            linear_plus_sub_card_detail.setVisibility(View.GONE);
                            removeAt(getAdapterPosition());
                            hitAddtoCartApi(0,type);
                        }else{
                            int quantity= Integer.parseInt(TvQuantity.getText().toString()) -1;
                            hitAddtoCartApi(quantity,type);
                        }

                    }
                });*/
            }
            public void removeAt(int position) {
                arList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arList.size());
            }
            /*public void hitAddtoCartApi(final int count, final String type) {
                RequestQueue requestQueue = Volley.newRequestQueue(MyCartActivity.this);
                progressbar.showProgress(MyCartActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_CART, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.hideProgress();
                        Log.d("cheweeeerees",response);
                        AddCartModel addCartModel = ParseManager.getInstance().fromJSON(response, AddCartModel.class);
                        if (addCartModel.getStatus().equals("success")) {
                            txt_cart_totalprice.setText("₹ "+addCartModel.priceTotal);
                            if (type.equals("plus")){
                                TvQuantity.setText(String.valueOf(Integer.parseInt(TvQuantity.getText().toString()) + 1));
                            }else if (type.equals("minus")){
                                TvQuantity.setText(String.valueOf(Integer.parseInt(TvQuantity.getText().toString()) - 1));
                            }
                            if (addCartModel.priceTotal.equals("0")){
                                hitCartDetailsApi();
                            }
                        }else {
                            Toast.makeText(MyCartActivity.this, ""+addCartModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressbar.hideProgress();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        if (value.equals("0")){
                            hashMap.put("product_id", productId);
                            hashMap.put("size_id", sizeId);
                            hashMap.put("quantity", String.valueOf(count));
                        }else {
                            hashMap.put("product_id", productId);
                            hashMap.put("size_id", sizeId);
                            hashMap.put("quantity", "0");
                        }


                        Log.d("checkparms", String.valueOf(hashMap));
                        return hashMap;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headerMap = new HashMap<String, String>();
                        headerMap.put("Authorization", "Bearer " + sharedPreference.getString("auth_token", ""));
                        Log.d("rfgdfdf", sharedPreference.getString("auth_token", ""));
                        return headerMap;
                    }
                };
                requestQueue.getCache().clear();
                requestQueue.add(stringRequest);
            }*/

        }
    }
}