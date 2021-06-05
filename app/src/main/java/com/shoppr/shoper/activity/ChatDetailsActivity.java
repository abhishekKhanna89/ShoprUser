package com.shoppr.shoper.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.ChatModel;
import com.shoppr.shoper.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.ApiService;
import com.shoppr.shoper.adapter.ChatAppMsgAdapter;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.requestdata.TextTypeRequest;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Helper;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class ChatDetailsActivity extends AppCompatActivity {
    boolean flag = false;
    RecyclerView chatRecyclerView;
    SessonManager sessonManager;
    List<Chat> chatList;
    EditText editText;
    ImageButton chooseImage, sendMsgBtn;
    /*Todo:- BroadCast Receiver*/
    BroadcastReceiver mMessageReceiver;
    String body;
    List<ChatModel> msgDtoList;
    ChatAppMsgAdapter chatAppMsgAdapter;
    /*Todo:- Image Choose*/
    int PICK_IMAGE_MULTIPLE = 1;
    File photoFile;
    Uri photoUri;
    String mCurrentMPath;
    ArrayList<String> imagePathList = new ArrayList<>();
    Bitmap bitmap = null;
    private String photoPath;
    String imageEncoded;
    private static String baseUrl = ApiExecutor.baseUrl;

    /*Todo:- Voice Recorder*/
    //private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    String pathforaudio;
    String calleeId;
    int chat_id;

    /*Todo:- UserDP*/
    CircleImageView userDp;
    TextView userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);





       // Log.d("checkvalue===",str);


        chat_id = getIntent().getIntExtra("id", 0);

       /*String  checkfornavigation=getIntent().getStringExtra("checkfornavigation");

       if(checkfornavigation!=null&&checkfornavigation.equalsIgnoreCase("1")) {
           chat_id = getIntent().getIntExtra("id", 0);
           //Log.d("Chat_historyId",""+chat_id);
       }
       else
       {
           chat_id = Integer.parseInt(getIntent().getStringExtra("whattodo"));
           //Log.d("Chat_notificationId",""+chat_id);
       }*/





        //Log.d("ChatId+",""+chat_id);

        if (sessonManager.getChatId().isEmpty()){
            sessonManager.setChatId("");
            chatMessageList(chat_id);
        }else {
            String cId=sessonManager.getChatId();
            int a=Integer.parseInt(cId);
            sessonManager.setChatId("");
            chatMessageList(a);
           // Log.d("ChatId+",""+a);
        }
        //chatMessageList(chat_id);

        /*Todo:- UserDP*/
        userDp=findViewById(R.id.userDp);
        userName=findViewById(R.id.userName);

        editText = findViewById(R.id.editText);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        chooseImage = findViewById(R.id.chooseImage);

        /*Todo:- Voice Recorder*/
        timer = findViewById(R.id.record_timer);


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
        msgDtoList = new ArrayList<>();


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("chat_id") != null) {
                    chat_id = intent.getIntExtra("chat_id", 0);
                    if (sessonManager.getChatId() != null) {
                        String chatid = String.valueOf(sessonManager.getChatId());
                        chatMessageList(Integer.parseInt(chatid));
                    }

                }
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(ChatDetailsActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(i));

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (!flag) {
                    if (checkPermissions()) {
                        //Start Recording
                        startRecording();
                        // Change button image and set Recording state to false
                        sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        flag = true;
                    }
                } else {
                    sendMsgBtn.setBackgroundResource(R.drawable.record_btn_stopped);
                    stopRecording();
                    flag = false;
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_stopped));
                    sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.Q)
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View v) {
                            if (!flag) {
                                if (checkPermissions()) {
                                    //Start Recording
                                    startRecording();
                                    // Change button image and set Recording state to false
                                    sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_recording, null));
                                    flag = true;
                                }
                            } else {
                                sendMsgBtn.setBackgroundResource(R.drawable.record_btn_stopped);
                                stopRecording();
                                flag = false;
                            }
                        }
                    });


                    //sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                    // is only executed if the EditText was directly changed by the user
                } else {
                    if (sessonManager.getChatId().isEmpty()){
                        sessonManager.setChatId("");
                        chat_id = getIntent().getIntExtra("id", 0);
                        //chatMessageList(chat_id);
                    }else {
                        String cId=sessonManager.getChatId();
                        int a= Integer.parseInt(cId);
                        chat_id=a;
                        sessonManager.setChatId("");
                    }
                    /*String  checkfornavigation=getIntent().getStringExtra("checkfornavigation");

                    if(checkfornavigation!=null&&checkfornavigation.equalsIgnoreCase("1")) {
                        chat_id = getIntent().getIntExtra("id", 0);
                        //Log.d("Chat_historyId",""+chat_id);
                    }
                    else
                    {
                        chat_id = Integer.parseInt(getIntent().getStringExtra("whattodo"));
                        //Log.d("Chat_notificationId",""+chat_id);
                    }*/

                    sendMsgBtn.setBackground(getResources().getDrawable(R.drawable.send));
                    sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String msgContent = editText.getText().toString();
                            if (!TextUtils.isEmpty(msgContent)) {

                                if (CommonUtils.isOnline(ChatDetailsActivity.this)) {
                                    //sessonManager.showProgress(ChatActivity.this);
                                    TextTypeRequest textTypeRequest = new TextTypeRequest();
                                    textTypeRequest.setType("text");
                                    textTypeRequest.setMessage(msgContent);
                                    Call<SendModel> call = ApiExecutor.getApiService(ChatDetailsActivity.this)
                                            .apiSend("Bearer " + sessonManager.getToken(), chat_id, textTypeRequest);
                                    call.enqueue(new Callback<SendModel>() {
                                        @Override
                                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                            //sessonManager.hideProgress();
                                            if (response.body() != null) {
                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                    editText.getText().clear();
                                                    chatMessageList(chat_id);
                                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SendModel> call, Throwable t) {
                                            //sessonManager.hideProgress();
                                        }
                                    });
                                } else {
                                    CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
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

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setItemViewCacheSize(20);
        chatRecyclerView.setDrawingCacheEnabled(true);
        chatRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        chatRecyclerView.setNestedScrollingEnabled(false);
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 786);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }


    private void chatMessageList(int chat_id) {
        if (CommonUtils.isOnline(ChatDetailsActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            Call<ChatMessageModel> call = ApiExecutor.getApiService(this).apiChatMessage("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<ChatMessageModel>() {
                @Override
                public void onResponse(Call<ChatMessageModel> call, Response<ChatMessageModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ChatMessageModel chatMessageModel = response.body();
                            Gson gson=new Gson();
                            String json=gson.toJson(chatMessageModel);
                            Log.d("hshdh",json);
                            if (chatMessageModel.getData() != null) {
                                chatList = chatMessageModel.getData().getChats();
                                Picasso.get().load(chatMessageModel.getData().getShoppr().getImage()).into(userDp);
                                userName.setText(chatMessageModel.getData().getShoppr().getName());
                                ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(ChatDetailsActivity.this, chatList);
                                chatRecyclerView.setAdapter(chatMessageAdapter);
                                chatRecyclerView.scrollToPosition(chatList.size() - 1);
                                chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                                //chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);
                                chatMessageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatMessageModel> call, Throwable t) {
                    //sessonManager.showProgress(ChatActivity.this);
                }
            });

        } else {
            CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
        }
    }


   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_cart) {
            Intent intent = new Intent(ChatDetailsActivity.this, ViewCartActivity.class);
            intent.putExtra("chatId", chat_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.action_audio) {
            initializationVoice(chat_id);
        } else if (id == R.id.action_video) {
            initializationVideo(chat_id);
           *//* startActivity(new Intent(ChatDetailsActivity.this,VideoChatViewActivity.class)
                    .putExtra("chatId",chat_id)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*//*
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void initializationVideo(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel> call = ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            InitiateVideoCallModel initiateVideoCallModel = response.body();
                            if (initiateVideoCallModel.getData() != null) {
                                String savedUserId = initiateVideoCallModel.getData().getUser_id();
                                PrefUtils.setCalleeId(ChatDetailsActivity.this, savedUserId);
                                String savedCalleeId = PrefUtils.getCalleeId(ChatDetailsActivity.this);

                                //CallService.dial(ChatDetailsActivity.this, savedCalleeId, true);
                                ActivityUtils.startCallActivityAsCaller(ChatDetailsActivity.this, savedUserId, true);



                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        } else {
            CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
        }
    }

    private void initializationVoice(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel> call = ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            InitiateVideoCallModel initiateVideoCallModel = response.body();
                            if (initiateVideoCallModel.getData() != null) {
                                String savedUserId = initiateVideoCallModel.getData().getUser_id();
                                PrefUtils.setCalleeId(ChatDetailsActivity.this, savedUserId);
                                String savedCalleeId = PrefUtils.getCalleeId(ChatDetailsActivity.this);
                               // CallService.dial(ChatDetailsActivity.this, savedCalleeId, false);
                                ActivityUtils.startCallActivityAsCaller(ChatDetailsActivity.this, savedUserId, false);

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        } else {
            CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == 1)) {

            try {
                rotateImage();
            } catch (Exception e) {
                e.printStackTrace();

            }


        } else if ((requestCode == 786)) {
            selectFromGallery(data);
        }

    }

    private void takeCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createFile();
                //Log.d("checkexcesdp", String.valueOf(photoFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                //Log.d("checkexcep", ex.getMessage());
            }
            photoFile = createFile();
            photoUri = FileProvider.getUriForFile(ChatDetailsActivity.this, getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, 1);
        }

    }

    private File createFile() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMPath = image.getAbsolutePath();
        return image;
    }

    public void rotateImage() throws IOException {

        try {
            String photoPath = photoFile.getAbsolutePath();
            imagePathList.add(photoPath);

            // Log.d("ress",""+imagePathList);
            bitmap = MediaStore.Images.Media.getBitmap(ChatDetailsActivity.this.getContentResolver(), photoUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

            if (Build.VERSION.SDK_INT > 23) {
                bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), photoUri);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

            }
            //ProfileUpdateAPI();
            //circleImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /*private void ProfileUpdateAPI() {
        if (CommonUtils.isOnline(ChatDetailsActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("image"));
            MultipartBody.Part[] imageArray1 = new MultipartBody.Part[imagePathList.size()];
            //Log.d("arrayLis",""+imageArray1);

            for (int i = 0; i < imageArray1.length; i++) {
                File file = new File(imagePathList.get(i));
                try {
                    File compressedfile = new Compressor(ChatDetailsActivity.this).compressToFile(file);
                    RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                    imageArray1[i] = MultipartBody.Part.createFormData("file", compressedfile.getName(), requestBodyArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiImageSend(headers, chat_id, imageArray1, partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            //sessonManager.hideProgress();
                            // Log.d("res",response.message());
                            if (response.body() != null) {
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    chatMessageList(chat_id);
                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
        }
    }*/


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectFromGallery(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPath = Helper.pathFromUri(ChatDetailsActivity.this, mImageUri);
                        imagePathList.add(photoPath);


                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor1.getString(columnIndex1);

                        if (Build.VERSION.SDK_INT > 23) {
                            // Log.d("inelswe", "inelse");
                            bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                        } else {
                            // Log.d("inelse", "inelse");
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

                        }


                        //   deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        //ProfileUpdateAPI();
                        //circleImage.setImageBitmap(bitmap);


                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPath = Helper.pathFromUri(ChatDetailsActivity.this, mImageUri);
                    imagePathList.add(photoPath);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor1.getString(columnIndex1);


                    if (Build.VERSION.SDK_INT > 23) {
                        //Log.d("inelswe", "inelse");
                        bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                    } else {
                        //Log.d("inelse", "inelse");
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

                    }

                    //  deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);

                    cursor1.close();
                    //ProfileUpdateAPI();
                    //circleImage.setImageBitmap(bitmap);


                }


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }


    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = null;
        if (Build.VERSION.SDK_INT > 23) {
            ei = new ExifInterface(input);
        }


        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }


    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void askForPermissioncamera(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatDetailsActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ChatDetailsActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ChatDetailsActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }


    }

    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();
        //Change text on page to file saved
        //filenameText.setText("Recording Stopped, File Saved : " + recordFile);
        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        uploadFile();


    }

    private void uploadFile() {
        if (CommonUtils.isOnline(ChatDetailsActivity.this)) {
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
                                if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    timer.setVisibility(View.GONE);
                                    timer.stop();
                                    chatMessageList(chat_id);
                                    Toast.makeText(ChatDetailsActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatDetailsActivity.this, "" + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(ChatDetailsActivity.this, getString(R.string.please_check_network));
        }
    }


    private void startRecording() {
        //Start timer from 0
        timer.setVisibility(View.VISIBLE);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

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


        pathforaudio = recordPath + "/" + recordFile;

        Log.d("recordpath====", recordPath + "/" + recordFile);
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
        if (flag) {
            stopRecording();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        chatMessageList(chat_id);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void initializationVoice(View view) {
        initializationVoice(chat_id);
    }

    public void initializationVideo(View view) {
        initializationVideo(chat_id);
    }

    public void help(View view) {
        startActivity(new Intent(ChatDetailsActivity.this,HelpActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}