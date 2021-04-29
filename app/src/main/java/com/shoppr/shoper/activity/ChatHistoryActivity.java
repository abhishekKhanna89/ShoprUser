package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.MapsActivity;
import com.shoppr.shoper.Model.ChatList.ChatListModel;
import com.shoppr.shoper.Model.ChatList.Userchat;
import com.shoppr.shoper.Model.Logout.LogoutModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHistoryActivity extends AppCompatActivity {
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerChatHistory;
    TextView historyEmptyText;
    SwipeRefreshLayout swipeRefreshLayout;
    SessonManager sessonManager;
    List<Userchat> chatsListModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        recyclerChatHistory=findViewById(R.id.recyclerChatHistory);
        swipeRefreshLayout=findViewById(R.id.SwipeRefresh);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerChatHistory.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerChatHistory.getContext(),
                linearLayoutManager.getOrientation());
        recyclerChatHistory.addItemDecoration(dividerItemDecoration);
        recyclerChatHistory.setNestedScrollingEnabled(true);
        historyEmptyText=findViewById(R.id.historyEmptyText);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewUserChatList();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        viewUserChatList();

    }

    private void viewUserChatList() {
        if (CommonUtils.isOnline(this)) {
            sessonManager.showProgress(this);
            Call<ChatListModel> call = ApiExecutor.getApiService(this)
                    .apiUserChatList("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ChatListModel>() {
                @Override
                public void onResponse(Call<ChatListModel> call, Response<ChatListModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        ChatListModel chatsListModel=response.body();
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){

                            if(chatsListModel.getData().getUserchats()!=null) {
                                if (chatsListModel.getData().getUserchats().size()==0){
                                    historyEmptyText.setVisibility(View.VISIBLE);
                                    recyclerChatHistory.setVisibility(View.GONE);
                                }else {
                                    historyEmptyText.setVisibility(View.GONE);
                                    recyclerChatHistory.setVisibility(View.VISIBLE);
                                }
                                chatsListModelList = chatsListModel.getData().getUserchats();
                                ChatHistoryAdapter chatHistoryAdapter=new ChatHistoryAdapter(ChatHistoryActivity.this,chatsListModelList);
                                recyclerChatHistory.setAdapter(chatHistoryAdapter);
                            }
                        }else {
                            Toast.makeText(ChatHistoryActivity.this, ""+chatsListModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ChatHistoryActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatHistoryActivity.this,"");
                                            Toast.makeText(ChatHistoryActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatHistoryActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatListModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(ChatHistoryActivity.this, getString(R.string.please_check_network));
        }
    }
    public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.Holder>{
        List<Userchat> chatsListModelList;
        Context context;
        public ChatHistoryAdapter(Context context,List<Userchat> chatsListModelList){
            this.context=context;
            this.chatsListModelList=chatsListModelList;
        }
        @NonNull
        @Override
        public ChatHistoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context)
            .inflate(R.layout.layout_chats_list,null));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHistoryAdapter.Holder holder, int position) {
            final Userchat userchat=chatsListModelList.get(position);
            Picasso.get().load(userchat.getImage()).into(holder.image_view_customer_head_shot);
            holder.text_view_customer_name.setText(userchat.getName());
            holder.text_view_customer_chat.setText(userchat.getChat());
            holder.text_view_date.setText(userchat.getDate());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra("findingchatid", userchat.getId())
                            .putExtra("chat_status","0")
                            .putExtra("image",userchat.getImage())
                            .putExtra("name",userchat.getName())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatsListModelList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            CircleImageView image_view_customer_head_shot;
            TextView text_view_customer_name,text_view_customer_chat,
                    text_view_date;
            public Holder(@NonNull View itemView) {
                super(itemView);
                image_view_customer_head_shot=itemView.findViewById(R.id.image_view_customer_head_shot);
                text_view_customer_name=itemView.findViewById(R.id.text_view_customer_name);
                text_view_customer_chat=itemView.findViewById(R.id.text_view_customer_chat);
                text_view_date=itemView.findViewById(R.id.text_view_date);
            }
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
}