package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.appevents.ml.Utils;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    RecyclerView chatRecyclerView;
    SessonManager sessonManager;
    int id;
    List<Chat> chatList;
    EditText editText;
    ImageView sendMsgBtn;
    /*Todo:- Recording*/
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean audioRecordingPermissionGranted = false;

    int count=0;

    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        editText = findViewById(R.id.editText);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        mediaRecorder = new MediaRecorder();

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                count++;
                if(count%2==0)
                {
                    stopRecording();
                }
                else
                {
                    startRecording();
                }
            }
        });


        /*sendMsgBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //if Button is Pressed.! or user Id Holding Button
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    startRecording();


                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    //Do Nothing
                    stopRecording();


                }


                return false;
            }
        });*/


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    sendMsgBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));

                    // is only executed if the EditText was directly changed by the user
                } else {
                    sendMsgBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
                    sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(ChatActivity.this, "Send", Toast.LENGTH_SHORT).show();
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
        viewStartChat();
    }

    private void stopRecording() {
        if (null != mediaRecorder) {
            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //mediaRecorder.stop();
            //mediaRecorder.release();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRecording() {
        try {

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
           // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            Log.d("path====", String.valueOf(path));


            File file = new File(path, "/YouTubeAudio.mp3");
            mediaRecorder.setOutputFile(file);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        }


    private void viewStartChat() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            sessonManager.showProgress(ChatActivity.this);
            Call<StartChatModel>call= ApiExecutor.getApiService(this)
                    .apiChatStart("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<StartChatModel>() {
                @Override
                public void onResponse(Call<StartChatModel> call, Response<StartChatModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            StartChatModel startChatModel=response.body();
                            if (startChatModel.getData()!=null){
                                id=startChatModel.getData().getId();
                                chatMessageList(id);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<StartChatModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    private void chatMessageList(int id) {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            sessonManager.showProgress(ChatActivity.this);
            Call<ChatMessageModel>call=ApiExecutor.getApiService(this)
                    .apiChatMessage("Bearer "+sessonManager.getToken(),5);
            call.enqueue(new Callback<ChatMessageModel>() {
                @Override
                public void onResponse(Call<ChatMessageModel> call, Response<ChatMessageModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ChatMessageModel chatMessageModel=response.body();
                            if (chatMessageModel.getData()!=null){
                                chatList=chatMessageModel.getData().getChats();
                                ChatMessageAdapter chatMessageAdapter=new ChatMessageAdapter(ChatActivity.this,chatList);
                                chatRecyclerView.setAdapter(chatMessageAdapter);
                                chatMessageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatMessageModel> call, Throwable t) {

                }
            });

        }else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                audioRecordingPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!audioRecordingPermissionGranted) {
            finish();
        }
    }
}