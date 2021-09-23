package com.shoppr.shoper.SendBird.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.SendBird.utils.PushUtils;
import com.shoppr.shoper.activity.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Intent intent;
    Uri notification;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e("TAG", "check_message_sendbird_call "+remoteMessage.getData().toString());
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
               // Log.e("TAG", "message_sendbird_call => " + remoteMessage.getData().get("sendbird_call").toString());
        }
        else
        {
            showNotiForOrderAssign(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);

        }
       // showNotiForOrderAssign(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);

    }


    public void showNotiForOrderAssign(String title, String message, RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
       /* try {
            chat_id = jsonObject.getString("chat_id");
            type = jsonObject.getString("type");


        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        intent = new Intent(MyFirebaseMessagingService.this, ChatActivity.class);
     //   intent.putExtra("findingchatid", chat_id);
        intent.putExtra("chat_status", "1");
        intent.setAction(Intent.ACTION_MAIN);

        String channel_id = "order_assign_notification";

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            //r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.splash)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setSound(notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(getCustomDesign(title, message, remoteMessage))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Order Assign", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(4, builder.build());
    }


    // Method to get the custom Design for the display of5
    // notification.
    private RemoteViews getCustomDesign(String title, String message, RemoteMessage remoteMessage) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_layout);
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            title = jsonObject.getString("title");
            message = jsonObject.getString("message");
            remoteViews.setTextViewText(R.id.title, title);
            remoteViews.setTextViewText(R.id.messages, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.splash);
        return remoteViews;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.e("TAG", "[MyFirebaseMessagingService] onNewToken(token: " + token + ")");

        if (SendBirdCall.getCurrentUser() != null) {
            PushUtils.registerPushToken(getApplicationContext(), token, e -> {
                if (e != null) {
                    Log.e("TAG", "[MyFirebaseMessagingService] registerPushTokenForCurrentUser() => e: " + e.getMessage());
                }
            });
        } else {
            PrefUtils.setPushToken(getApplicationContext(), token);
        }
    }

}
