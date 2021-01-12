package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.StartChat.StartChatModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    RecyclerView chatRecyclerView;
    SessonManager sessonManager;
    int id;
    List<Chat>chatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        chatRecyclerView=findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        viewStartChat();
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
                    .apiChatMessage("Bearer "+sessonManager.getToken(),id);
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
}