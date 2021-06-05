package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.ChatModel;
import com.shoppr.shoper.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.SendBird.utils.ToastUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.ApiService;
import com.shoppr.shoper.adapter.ChatAppMsgAdapter;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.requestdata.TextTypeRequest;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Integer.parseInt;

public class ChatActivity extends AppCompatActivity {

    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    boolean flag=false;
    public static int chat_id, chatid;
    RecyclerView chatRecyclerView;
    SessonManager sessonManager;
    List<Chat> chatList;
    EditText editText;
    ImageButton chooseImage,sendMsgBtn;
    /*Todo:- BroadCast Receiver*/
    BroadcastReceiver mMessageReceiver;
    String body;
    List<ChatModel> msgDtoList;
    ChatAppMsgAdapter chatAppMsgAdapter;

    private static String baseUrl=ApiExecutor.baseUrl;

    /*Todo:- Voice Recorder*/
    //private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    String pathforaudio;
    int shopId;
    /*Todo:- UserDP*/
    CircleImageView userDp;
    TextView userName;

     //String TAG="lakshmi";

    BottomSheetDialog bottomSheetDialog;
    TextView cart_badge;
    /*Todo:- Recording Library*/
    RecordView recordView;
    RecordButton recordButton;
    /*Todo:- image upload*/
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    File destination;
    String userChoosenTask;
    byte[] byteArray;
    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);

       //askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);
        checkPermissions1();
        permissionToDrawOverlays();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        //chatRecyclerView.setHasFixedSize(false);
        //chatRecyclerView.setItemViewCacheSize(1000);
        //chatRecyclerView.setDrawingCacheEnabled(true);
        //chatRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //chatRecyclerView.setNestedScrollingEnabled(false);

        /*Todo:- Recording Library*/
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);
        checkPermissions();
        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);


        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);


        recordView.setSlideToCancelText("Slide To Cancel");


        recordView.setCustomSounds(0, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                recordView.setVisibility(View.VISIBLE);
                Log.d("RecordView", "onStart");
                startRecording();

            }

            @Override
            public void onCancel() {
                //Toast.makeText(ChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onCancel");

            }



            @Override
            public void onFinish(long recordTime) {
                recordView.setVisibility(View.GONE);
                String time = getHumanTimeText(recordTime);
                stopRecording();
                //Toast.makeText(ChatActivity.this, "onFinishRecord - Recorded Time is: " + time, Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onFinish");

                Log.d("RecordTime", time);
            }
            @Override
            public void onLessThanSecond() {
               // Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                recordView.setVisibility(View.GONE);
                Log.d("RecordView", "Basket Animation Finished");
            }
        });




        Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
           String chat_status = getIntent().getStringExtra("chat_status");
           if (chat_status!=null&&chat_status.equalsIgnoreCase("1")){
               Log.d("hello",chat_status);
               chat_id = Integer.parseInt(extras.getString("findingchatid"));
               //chatMessageList(chat_id);
           }

           else if(chat_status!=null&&chat_status.equalsIgnoreCase("0")) {

               // Log.d("Chsgss", String.valueOf(chat_id));
               chat_id = getIntent().getIntExtra("findingchatid", 0);
               //chatMessageList(chat_id);
           }

           else if(chat_status!=null&&chat_status.equalsIgnoreCase("2")) {

               // Log.d("Chsgss", String.valueOf(chat_id));
               chat_id = getIntent().getIntExtra("findingchatid", 0);
               //chatMessageList(chat_id);
           }
           else{

               String  value = String.valueOf(getIntent().getExtras().get("chat_id"));
               chat_id= Integer.parseInt(value);
               //chatMessageList(chat_id);
               //Log.d(TAG, "Key: " + "abcd" + " Value: " + value);
           }


        }
        if (chatList==null){
            chatMessageList(chat_id);
        }else if(chatList.size()>0){
            ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
            chatRecyclerView.setAdapter(chatMessageAdapter);
            //chatMessageAdapter.setHasStableIds(true);
            chatRecyclerView.scrollToPosition(chatList.size()-1);
            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
            //chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);
            chatMessageAdapter.notifyDataSetChanged();

        }
        /*if (chatList.size()==0){

        }else {
            chatList.size();

        }*/



        Log.d("ChatIdForTesting","" +chat_id);
        /*Todo:- UserDP*/
        userDp=findViewById(R.id.userDp);
        userName=findViewById(R.id.userName);

        editText = findViewById(R.id.editText);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        chooseImage=findViewById(R.id.chooseImage);



        /*Todo:- Cart Count View*/
        cart_badge=findViewById(R.id.cart_badge);


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                //startDialog();
            }
        });
        msgDtoList=new ArrayList<>();


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessageList(Integer.parseInt(String.valueOf(chat_id)));
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(mMessageReceiver,new IntentFilter(i));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    sendMsgBtn.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);

                } else {
                    sendMsgBtn.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                    recordView.setVisibility(View.GONE);
                    sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.send));
                    sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String msgContent = editText.getText().toString();
                            if(!TextUtils.isEmpty(msgContent))
                            {


                                Log.d("verfy===", String.valueOf(chat_id));
                                if (CommonUtils.isOnline(ChatActivity.this)) {
                                    //sessonManager.showProgress(ChatActivity.this);
                                    TextTypeRequest textTypeRequest=new TextTypeRequest();
                                    textTypeRequest.setType("text");
                                    textTypeRequest.setMessage(msgContent);
                                    Call<SendModel>call=ApiExecutor.getApiService(ChatActivity.this)
                                            .apiSend("Bearer "+sessonManager.getToken(),chat_id,textTypeRequest);
                                    call.enqueue(new Callback<SendModel>() {
                                        @Override
                                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {

                                            //sessonManager.hideProgress();
                                            if (response.body()!=null) {
                                                SendModel sendModel=response.body();
                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                    editText.getText().clear();
                                                    chatMessageList(chat_id);
                                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(ChatActivity.this, ""+sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SendModel> call, Throwable t) {
                                            //sessonManager.hideProgress();
                                        }
                                    });
                                }else {
                                    CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
                                }

                            }
                        }
                    });

                }
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });





    }





    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void checkPermissions1() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }
    private void chatMessageList(int chat_id) {
        Log.d("chatiddssss", String.valueOf(chat_id));
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            Call<ChatMessageModel>call=ApiExecutor.getApiService(this).apiChatMessage("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<ChatMessageModel>() {
                @Override
                public void onResponse(Call<ChatMessageModel> call, Response<ChatMessageModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body()!=null) {
                        ChatMessageModel chatMessageModel=response.body();
                        Log.d("chatResponse",new Gson().toJson(chatMessageModel));
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (chatMessageModel.getData()!=null){
                                chatList=chatMessageModel.getData().getChats();
                                String cartCount=chatMessageModel.getData().getItems_count();
                                if (cartCount.equalsIgnoreCase("0")){
                                    cart_badge.setVisibility(View.GONE);
                                }else {
                                    cart_badge.setVisibility(View.VISIBLE);
                                    cart_badge.setText(cartCount);
                                }
                                Picasso.get().load(chatMessageModel.getData().getShoppr().getImage()).into(userDp);
                                userName.setText(chatMessageModel.getData().getShoppr().getName());
                                if (chatList.size()==0) {
                                }else {
                                    ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatList);
                                    chatRecyclerView.setAdapter(chatMessageAdapter);
                                    chatRecyclerView.scrollToPosition(chatList.size() - 1);
                                    chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                                    chatMessageAdapter.notifyDataSetChanged();
                                }
                                //chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);

                            }
                        }else {
                            Toast.makeText(ChatActivity.this, ""+chatMessageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ChatActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatActivity.this,"");
                                            Toast.makeText(ChatActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        }else {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatMessageModel> call, Throwable t) {
                    //sessonManager.showProgress(ChatActivity.this);
                }
            });

        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
/*Todo:-Image Upload*/
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(ChatActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                } else {
                    Log.d("lakshmi", "granted");

                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byteArray=stream.toByteArray();
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(stream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProfileUpdateAPI();
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArray=bytes.toByteArray();
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProfileUpdateAPI();

    }

    private void ProfileUpdateAPI() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("image"));
            RequestBody backBike = RequestBody
                    .create(MediaType.parse("image/*"),byteArray);
            MultipartBody.Part imageArray1 = MultipartBody.Part.createFormData("file", destination.getName(), backBike);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer "+sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiImageSend(headers,chat_id,imageArray1,partMap)
            .enqueue(new Callback<SendModel>() {
                @Override
                public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                    //sessonManager.hideProgress();
                   // Log.d("res",response.message());
                    if (response.body()!=null) {
                        SendModel sendModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            chatMessageList(chat_id);
                            Toast.makeText(ChatActivity.this, ""+sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ChatActivity.this, sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SendModel> call, Throwable t) {
                    //sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }
    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context)
        {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    /*Todo:- Audio Upload*/
    private void stopRecording() {
        //Stop Timer, very obvious
        //Change text on page to file saved
        //filenameText.setText("Recording Stopped, File Saved : " + recordFile);
        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        uploadFile();


    }
    private void uploadFile() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            File file = new File(pathforaudio);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            //RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);

            //Call call = getResponse.uploadFile(fileToUpload, filename);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("audio"));
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());

            iApiServices.apiAudioSend(headers, chat_id, fileToUpload, partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                SendModel sendModel=response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    chatMessageList(chat_id);
                                    Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }



    private void startRecording() {
        //Start timer from 0
        //.setVisibility(View.VISIBLE);
        //timer.setBase(SystemClock.elapsedRealtime());
        //timer.start();
        //Get app external directory path
        String recordPath = getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        //filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);



        pathforaudio=recordPath + "/" + recordFile;

        Log.d("recordpath====",recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }
    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(flag){
            stopRecording();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        chatMessageList(chat_id);
        //Toast.makeText(this, "Restart", Toast.LENGTH_SHORT).show();
        //ChatActivity.this.finish();
    }


    public void back(View view) {
        onBackPressed();
    }

    public void help(View view) {
        String number="+919315957968";
        String url = "https://api.whatsapp.com/send?phone="+number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
      /*  startActivity(new Intent(ChatActivity.this,HelpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
    }

    public void MyCart(View view) {
        startActivity(new Intent(ChatActivity.this,ViewCartActivity.class)
                .putExtra("valueId","1")
                .putExtra("chat_id",chat_id)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void calling(View view) {
        bottomSheetDialog=new BottomSheetDialog(this,R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.calling_bottom_dialog,null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

    }

    public void initializationVoice(View view) {
        initializationVoice(chat_id);
    }

    public void initializationVideo(View view) {
        initializationVideo(chat_id);
    }
    private void initializationVideo(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel>call= ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body()!=null) {
                        InitiateVideoCallModel initiateVideoCallModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (initiateVideoCallModel.getData()!=null){
                                String savedUserId=initiateVideoCallModel.getData().getUser_id();
                                //PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                //CallService.dial(ChatActivity.this, savedUserId, true);
                                ActivityUtils.startCallActivityAsCaller(ChatActivity.this, savedUserId, true);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);

                                bottomSheetDialog.dismiss();
                            }
                        }else {
                            Toast.makeText(ChatActivity.this, ""+initiateVideoCallModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    private void initializationVoice(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel>call= ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer "+sessonManager.getToken(),chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body()!=null) {
                        InitiateVideoCallModel initiateVideoCallModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (initiateVideoCallModel.getData()!=null){
                                String savedUserId=initiateVideoCallModel.getData().getUser_id();
                               // PrefUtils.setCalleeId(ChatActivity.this, savedUserId);

                                ActivityUtils.startCallActivityAsCaller(ChatActivity.this, savedUserId, false);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                               // CallService.dial(ChatActivity.this, savedUserId, false);
                               // SendBirdCall.Options.

                                bottomSheetDialog.dismiss();
                            }
                        }else {
                            Toast.makeText(ChatActivity.this, ""+initiateVideoCallModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatMessageList(chat_id);
        //Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
    }

    public void yourDesiredMethod() {
        chatMessageList(chat_id);
    }

    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }

}
