package com.shoppr.shoper.activity;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.CartCancel.CartCancelModel;
import com.shoppr.shoper.Model.CartView.CartViewModel;
import com.shoppr.shoper.Model.CartView.Item;
import com.shoppr.shoper.Model.Initiat.InitiatOrderModel;
import com.shoppr.shoper.Model.InitiatPayment.InitiatPaymentModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.requestdata.InitiatePaymentRequest;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Progressbar;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewCartActivity extends AppCompatActivity {
    SessonManager sessonManager;
    Progressbar progressbar;
    RecyclerView RvMyCart;
    ArrayList<Item> arrCartItemList = new ArrayList<>();
    Button btn_payNow, btn_continue, btn_cod;
    ImageView imgCart;
    LinearLayout linrBottomOrder;
    TextView totalAmountText, serviceChargeText, groundTotalText, tv_discount_charge;
    Integer productId;
    int chatId;
    CartViewModel cartViewModel;
    CardView cardOrderSummary, walletCardView;
    CheckBox checkbox;
    int value;
    String total;
    String order_id;
    String valueId;
    String chat_id;
    String hashkey;
    String phonenumber;
    String email;
    String name;
    String refid;
    String product;
    String total1;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private AppPreference mAppPreference;
    private boolean isDisableExitConfirmation = false;
    //BaseApplicationpay BaseApplicationpay;
    int total_pay_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        // BaseApplicationpay=new BaseApplicationpay();
        sessonManager = new SessonManager(this);
        progressbar = new Progressbar();
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAppPreference = new AppPreference();

        btn_payNow = (Button) findViewById(R.id.btn_payNow);
        btn_continue = (Button) findViewById(R.id.btn_continue_shoping);
        linrBottomOrder = findViewById(R.id.liner_order);
        btn_cod = findViewById(R.id.btn_cod);
        totalAmountText = findViewById(R.id.totalAmountText);
        serviceChargeText = findViewById(R.id.serviceChargeText);
        tv_discount_charge = findViewById(R.id.tv_discount_charge);
        groundTotalText = findViewById(R.id.groundTotalText);
        imgCart = (ImageView) findViewById(R.id.imge_cart_img);
        cardOrderSummary = findViewById(R.id.cardOrderSummary);
        walletCardView = findViewById(R.id.walletCardView);

        valueId = getIntent().getStringExtra("valueId");
        if (valueId != null && valueId.equalsIgnoreCase("1")) {
            chatId = getIntent().getIntExtra("chat_id", 0);
            Log.d("resChatId", "" + chatId);
            hitCartDetailsApi(chatId);
        } else if (valueId != null && valueId.equalsIgnoreCase("2")) {
            chat_id = getIntent().getStringExtra("chat_id");
            chatId = Integer.parseInt(chat_id);
            Log.d("resChatId", "" + chatId);
            hitCartDetailsApi(chatId);
        }
        RvMyCart = (RecyclerView) findViewById(R.id.rv_my_cart);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ViewCartActivity.this, 1);
        RvMyCart.setLayoutManager(layoutManager);


        /*Todo:- CheckBox Button*/
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    value = 1;

                } else {
                    value = 0;

                }
            }
        });

        btn_payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(ViewCartActivity.this)) {
                    progressbar.showProgress(ViewCartActivity.this);
                    Call<InitiatOrderModel> call = ApiExecutor.getApiService(ViewCartActivity.this)
                            .apiInitiateOrder("Bearer " + sessonManager.getToken(), chatId);
                    call.enqueue(new Callback<InitiatOrderModel>() {
                        @Override
                        public void onResponse(Call<InitiatOrderModel> call, Response<InitiatOrderModel> response) {
                            progressbar.hideProgress();
                            if (response.body() != null) {
                                InitiatOrderModel initiatOrderModel = response.body();
                                System.out.println("apiInitiateOrder_payment" + new Gson().toJson(response.body()));
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                                    int orderId = initiatOrderModel.getData().getOrderId();
                                    String online = "online";
                                    initiatPayment(orderId, online);
                                } else {
                                    Toast.makeText(ViewCartActivity.this, "" + initiatOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<InitiatOrderModel> call, Throwable t) {
                            progressbar.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
                }
            }
        });
        btn_cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isOnline(ViewCartActivity.this)) {
                    progressbar.showProgress(ViewCartActivity.this);
                    Call<InitiatOrderModel> call = ApiExecutor.getApiService(ViewCartActivity.this)
                            .apiInitiateOrder("Bearer " + sessonManager.getToken(), chatId);
                    call.enqueue(new Callback<InitiatOrderModel>() {
                        @Override
                        public void onResponse(Call<InitiatOrderModel> call, Response<InitiatOrderModel> response) {
                            progressbar.hideProgress();
                            if (response.body() != null) {
                                InitiatOrderModel initiatOrderModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    int orderId = initiatOrderModel.getData().getOrderId();
                                    String cod = "cod";
                                    initiatPayment(orderId, cod);
                                } else {
                                    Toast.makeText(ViewCartActivity.this, "" + initiatOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<InitiatOrderModel> call, Throwable t) {
                            progressbar.hideProgress();
                        }
                    });
                } else {
                    CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
                }
            }
        });


        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void initiatPayment(int orderId, String type) {
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            progressbar.showProgress(ViewCartActivity.this);
            InitiatePaymentRequest initiatePaymentRequest = new InitiatePaymentRequest();
            initiatePaymentRequest.setType(type);
            initiatePaymentRequest.setUse_balance(total_pay_amount);
         /*   Call<JsonObject> call = ApiExecutor.getApiService(this).apiInitiatePayment("Bearer "+sessonManager.getToken(),orderId,type);;
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("res", response.body().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("error",t.getMessage());
                }
            });*/


            // Log.d("lakshmi====","")


            Call<InitiatPaymentModel> call = ApiExecutor.getApiService(this).apiInitiatePayment("Bearer " + sessonManager.getToken(), orderId, initiatePaymentRequest);
            call.enqueue(new Callback<InitiatPaymentModel>() {
                @Override
                public void onResponse(Call<InitiatPaymentModel> call, Response<InitiatPaymentModel> response) {

                    Log.d("responsebody===", response.toString());
                    progressbar.hideProgress();
                    if (response.body() != null) {
                        InitiatPaymentModel initiatPaymentModel = response.body();
                        Log.d("intiateresponse===", new Gson().toJson(response.body()));

                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (initiatPaymentModel.getData().getPaymentDone().equalsIgnoreCase("No")) {
                                order_id = initiatPaymentModel.getData().getRazorpayOrderId();
                                total = initiatPaymentModel.getData().getTotal();

                                hashkey = initiatPaymentModel.getData().getHash();
                                phonenumber = initiatPaymentModel.getData().getMobile();
                                name = initiatPaymentModel.getData().getName();

                                refid = initiatPaymentModel.getData().getRefid();
                                product = initiatPaymentModel.getData().getProduct();
                                email = initiatPaymentModel.getData().getEmail();

                                total1 = String.valueOf(initiatPaymentModel.getData().getTotal());
                                // lk changes here
                                launchPayUMoneyFlow();
                            } else {

                                startActivity(new Intent(ViewCartActivity.this, OrderConfirmActivity.class)
                                        .putExtra("refid", initiatPaymentModel.getData().getRefid())
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        } else {
                            Toast.makeText(ViewCartActivity.this, "" + initiatPaymentModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiatPaymentModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        } else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
    }

    public void hitCartDetailsApi(int chatId) {

        Log.d("cartactivity=", String.valueOf(chatId));
        Log.d("bearerToken= ", sessonManager.getToken());
        if (CommonUtils.isOnline(ViewCartActivity.this)) {

            Log.d("hello", "hello");
            progressbar.showProgress(ViewCartActivity.this);
            Call<CartViewModel> call = ApiExecutor.getApiService(this).apiCartView("Bearer " + sessonManager.getToken(), chatId);
            call.enqueue(new Callback<CartViewModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<CartViewModel> call, Response<CartViewModel> response) {

                    Log.d("cartresponse=", String.valueOf(response));
                    System.out.println("cartresponse" + chatId+","+sessonManager.getToken()+",,,,,,,,,,,,,,,,,,,,"+new Gson().toJson(response.body()));

                    progressbar.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            cartViewModel = response.body();
                            if (cartViewModel.getData() != null) {
                                totalAmountText.setText("₹ " + cartViewModel.getData().getTotal());
                                serviceChargeText.setText("₹ " + cartViewModel.getData().getServiceCharge());
                                groundTotalText.setText("₹ " + cartViewModel.getData().getGrandTotal());
                                total_pay_amount=cartViewModel.getData().getGrandTotal();
                                tv_discount_charge.setText("₹ " + cartViewModel.getData().getDiscount());
                                if (cartViewModel.getData().getWallet_balance() > 0) {
                                    walletCardView.setVisibility(VISIBLE);
                                    checkbox.setText("₹ " + cartViewModel.getData().getWallet_balance());
                                } else if (cartViewModel.getData().getWallet_balance() == 0) {
                                    walletCardView.setVisibility(GONE);
                                }

                                if (cartViewModel.getData().getGrandTotal() > 700) {
                                    btn_cod.setVisibility(GONE);

                                } else {
                                    btn_cod.setVisibility(VISIBLE);
                                }


                                arrCartItemList = (ArrayList<Item>) cartViewModel.getData().getItems();
                                if (arrCartItemList.isEmpty()) {
                                    btn_continue.setVisibility(VISIBLE);
                                    imgCart.setVisibility(VISIBLE);
                                    cardOrderSummary.setVisibility(GONE);
                                    linrBottomOrder.setVisibility(GONE);
                                    walletCardView.setVisibility(GONE);
                                } else {
                                    cardOrderSummary.setVisibility(VISIBLE);
                                    linrBottomOrder.setVisibility(VISIBLE);
                                    walletCardView.setVisibility(VISIBLE);
                                }
                                MyCartAdapter myCartAdapter = new MyCartAdapter(ViewCartActivity.this, arrCartItemList);
                                RvMyCart.setAdapter(myCartAdapter);
                                myCartAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(ViewCartActivity.this, "" + cartViewModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")) {
                                if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                    AuthenticationUtils.deauthenticate(ViewCartActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ViewCartActivity.this, "");
                                            Toast.makeText(ViewCartActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ViewCartActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        } else {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartViewModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        } else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
    }

    /*Todo:- PayU  integration */
    //  ------------------------------------------

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        // AppEnvironment appEnvironment = ((BaseApplicationpay) getApplication()).getAppEnvironment();
        AppEnvironment appEnvironment = AppEnvironment.SANDBOX;

        // stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(hashkey);
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }


    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        //   payUmoneyConfig.setDoneButtonText(((EditText) findViewById(R.id.status_page_et)).getText().toString());

        //Use this to set your custom title for the activity
        //  payUmoneyConfig.setPayUmoneyActivityTitle(((EditText) findViewById(R.id.activity_title_et)).getText().toString());

        // payUmoneyConfig.setDoneButtonText("Done");


        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        String amount = "";
        //try {
        amount = total;

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String txnId = refid;
        //String txnId = "TXNID720431525261327973";
        String phone = phonenumber;
        String productName = product;
        String firstName = name;
        String email = this.email;
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";


        // BaseApplicationpay BaseApplicationpay=new BaseApplicationpay();
        AppEnvironment appEnvironment = AppEnvironment.SANDBOX;

        Log.d("checkout_data", txnId + "-->" + phone + "-->" + productName + "-->" + firstName + "-->" + email + "-->" + appEnvironment.surl() + "-->" + appEnvironment.furl() + "-->" + amount);


        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
                .setKey("Cf8wcQiQ")
                .setMerchantId("7406162");

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */

            //
            mPaymentParams.setMerchantHash(hashkey);

            Log.d("mPaymentParams", String.valueOf(hashkey));

            // mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, ViewCartActivity.this, AppPreference.selectedTheme, true);
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, ViewCartActivity.this, R.style.AppTheme_default, true);
            }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //  payNowButton.setEnabled(true);
        }
    }


    /**
     * This method generates hash from server.
     * <p>
     * //  * @param paymentParam payments params used for hash generation
     */
/*
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }
*/
    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewCartActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();
            //  payNowButton.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(ViewCartActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

            }
        }
    }


    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }






    /*Todo:- RazorPay*/
//    private void startPayment(int amount) {
//        final Activity activity = this;
//        final Checkout co = new Checkout();
//        try {
//            JSONObject options = new JSONObject();
//            options.put("name", "Shoppr");
//            options.put("description", "App Payment");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
//            options.put("currency", "INR");
//            options.put("order_id", order_id);
//            String payment = String.valueOf(amount);
//            // amount is in paise so please multiple it by 100
//            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
//            double total = Double.parseDouble(payment);
//            total = total * 100;
//            options.put("amount", total);
//            JSONObject preFill = new JSONObject();
//            preFill.put("email", "");
//            preFill.put("contact", "");
//            options.put("prefill", preFill);
//            co.open(activity, options);
//        } catch (Exception e) {
//            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }


  /*  @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        String orderId=paymentData.getOrderId();
        String paymentId=paymentData.getPaymentId();
        String signature=paymentData.getSignature();
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            sessonManager.showProgress(ViewCartActivity.this);
            PaymentSuccessRequest paymentSuccessRequest=new PaymentSuccessRequest();
            paymentSuccessRequest.setRazorpay_order_id(orderId);
            paymentSuccessRequest.setRazorpay_payment_id(paymentId);
            paymentSuccessRequest.setRazorpay_signature(signature);
            Call<PaymentSuccessModel>call=ApiExecutor.getApiService(this)
                    .apiPaymentSuccess("Bearer "+sessonManager.getToken(),paymentSuccessRequest);
            call.enqueue(new Callback<PaymentSuccessModel>() {
                @Override
                public void onResponse(Call<PaymentSuccessModel> call, Response<PaymentSuccessModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        PaymentSuccessModel paymentSuccessModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            startActivity(new Intent(ViewCartActivity.this,OrderConfirmActivity.class)
                                    .putExtra("refid",paymentSuccessModel.getData().getRefid())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }else {
                            Toast.makeText(ViewCartActivity.this,paymentSuccessModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaymentSuccessModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }*/

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
            if (arList.get(position).getFilePath().length() == 0) {

            } else {
                Picasso.get().load(arList.get(position).getFilePath()).into(holder.productImage);
            }

            holder.nameProductText.setText(arList.get(position).getMessage());
            //holder.priceProductText.setText("Item total :- "+"\u20B9 "+arList.get(position).getPrice());
            //holder.quantityProductText.setText("Quantity :- "+arList.get(position).getQuantity());
            holder.priceProductText.setText(Html.fromHtml("<b>" + "Item total - " + "</b>" + "<medium>" + "\u20B9 " + arList.get(position).getPrice() + "</medium>"));
            holder.quantityProductText.setText(Html.fromHtml("<b>" + "Quantity - " + "</b>" + "<medium>" + arList.get(position).getQuantity() + "</medium>"));
        }

        @Override
        public int getItemCount() {
            return arList.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView productImage, deleteImage;
            TextView nameProductText, priceProductText, quantityProductText;

            public ViewHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                deleteImage = itemView.findViewById(R.id.deleteImage);
                nameProductText = (TextView) itemView.findViewById(R.id.nameProductText);
                priceProductText = itemView.findViewById(R.id.priceProductText);
                quantityProductText = itemView.findViewById(R.id.quantityProductText);


                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteImage.setEnabled(false);
                        productId = arList.get(getAdapterPosition()).getId();
                        //sizeId=arList.get(getAdapterPosition()).getSizeId();
                        removeAt(getAdapterPosition());
                        hitAddtoCartApi(productId);

                    }
                });


            }

            public void removeAt(int position) {
                arList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arList.size());
            }

        }
    }

    private void hitAddtoCartApi(Integer productId) {
        if (CommonUtils.isOnline(ViewCartActivity.this)) {
            progressbar.showProgress(ViewCartActivity.this);
            Call<CartCancelModel> call = ApiExecutor.getApiService(this).apiCartCancel("Bearer " + sessonManager.getToken(), productId);
            call.enqueue(new Callback<CartCancelModel>() {
                @Override
                public void onResponse(Call<CartCancelModel> call, Response<CartCancelModel> response) {
                    progressbar.hideProgress();
                    if (response.body() != null) {
                        CartCancelModel cartCancelModel = response.body();
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            hitCartDetailsApi(chatId);
                        } else {
                            Toast.makeText(ViewCartActivity.this, "" + cartCancelModel.getMessage(), Toast.LENGTH_SHORT).show();
                            btn_continue.setVisibility(VISIBLE);
                            imgCart.setVisibility(VISIBLE);
                            cardOrderSummary.setVisibility(GONE);
                            walletCardView.setVisibility(GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartCancelModel> call, Throwable t) {
                    progressbar.hideProgress();
                }
            });

        } else {
            CommonUtils.showToastInCenter(ViewCartActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("Payment Sucessfully Done!!!")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    dialog.dismiss();

                                    Intent intent = new Intent(ViewCartActivity.this, ChatActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }
                            }).show();


                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();


            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d("shopr", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("shopr", "Both objects are null!");
            }
        }
    }


    /*@Override
    protected void onDestroy() {
        super.onDestroy();

    }*/
}