package com.shoppr.shoper.activity;

import static com.shoppr.shoper.Service.ApiExecutor.baseUrl;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.shoppr.shoper.Model.NotificationList.Datum;
import com.shoppr.shoper.Model.NotificationList.NotificationListModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.ApiService;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class NotificationListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerView.OnScrollChangeListener {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv_notification;
    SessonManager sessonManager;
    List<Datum> notificationList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    int currentPage = 1;
    int page;
    NotificationListAdapter notificationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);
        Log.d("token", sessonManager.getToken());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        rv_notification = (RecyclerView) findViewById(R.id.rv_notification);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_notification.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_notification.getContext(),
                linearLayoutManager.getOrientation());
        rv_notification.addItemDecoration(dividerItemDecoration);
        rv_notification.setNestedScrollingEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        rv_notification.setOnScrollChangeListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                notificationList();
            }
        });

        notificationListAdapter = new NotificationListAdapter(NotificationListActivity.this, notificationList);
        rv_notification.setAdapter(notificationListAdapter);
        notificationListAdapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void notificationList() {
        swipeRefreshLayout.setRefreshing(true);
        if (CommonUtils.isOnline(NotificationListActivity.this)) {
            sessonManager.showProgress(NotificationListActivity.this);
            Call<NotificationListModel> call = ApiExecutor.getApiService(this)
                    .apiNotificationList("Bearer " + sessonManager.getToken(), currentPage);
            call.enqueue(new Callback<NotificationListModel>() {
                @Override
                public void onResponse(Call<NotificationListModel> call, Response<NotificationListModel> response) {
// stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        NotificationListModel notificationListModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            notificationListRead();
                            if (notificationListModel.getData().getNotifications().getData() != null) {
                                page = notificationListModel.getData().getNotifications().getLastPage();
                                notificationList.addAll(notificationListModel.getData().getNotifications().getData());
                                notificationListAdapter.notifyDataSetChanged();
                                if (notificationList.size() > 0) {
                                    currentPage = currentPage + 1;
                                }
                            }
                        } else {
                            Toast.makeText(NotificationListActivity.this, "" + notificationListModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NotificationListModel> call, Throwable t) {
// stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                    sessonManager.hideProgress();
                }
            });

        } else {
            CommonUtils.showToastInCenter(NotificationListActivity.this, getString(R.string.please_check_network));
        }


    }

    private void notificationListRead() {
        if (CommonUtils.isOnline(NotificationListActivity.this)) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiReadNotification(headers)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            sessonManager.hideProgress();
                            JsonObject jsonObject = response.body();
                            String code = jsonObject.get("status").getAsString();
                            if (code.equals("success")) {
                                String msg = jsonObject.get("message").getAsString();
                                //Toast.makeText(NotificationListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = jsonObject.get("message").getAsString();
                                Toast.makeText(NotificationListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(NotificationListActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public void onRefresh() {
        notificationList();
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
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(rv_notification)) {
            //Calling the method getdata again
            if (currentPage < page) {
                notificationList();
            }

        }
    }

    public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.Holder> {
        List<Datum> notificationList;
        Context context;

        public NotificationListAdapter(Context context, List<Datum> notificationList) {
            this.context = context;
            this.notificationList = notificationList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_notification_list, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Datum datum = notificationList.get(position);
            if (datum.getTitle().isEmpty()) {

            } else {
                holder.textTitle.setText(datum.getTitle());
            }
            if (datum.getDescription().isEmpty()) {

            } else {
                holder.textDescription.setText(datum.getDescription());
            }
            if (datum.getCreatedAt().isEmpty()) {

            } else {
                holder.textDate.setText(datum.getCreatedAt());
            }
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView textTitle,
                    textDate, textDescription;

            public Holder(@NonNull View itemView) {
                super(itemView);
                textTitle = (TextView) itemView.findViewById(R.id.textTitle);
                textDescription = (TextView) itemView.findViewById(R.id.textDescription);
                textDate = (TextView) itemView.findViewById(R.id.textDate);
            }
        }
    }
}