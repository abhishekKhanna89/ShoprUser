package com.shoppr.shoper.notifications;

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

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.R;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.util.ConstantValue;
import com.shoppr.shoper.util.MyPreferences;
import com.shoppr.shoper.util.SessonManager;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    SessonManager sessonManager;
    Uri notification;
    public static String chat_id;
    Intent intent;
    String type;
    //String a;

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        // Logic to turn on the screen
        /* getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
        sessonManager = new SessonManager(this);
        Log.e("MyFirebaseMessaging notification: ", remoteMessage.getData().toString());
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.d("MyFirebaseMessaging ", remoteMessage.getData().toString());
        } else {
            /*  Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());*/
            //
            String type = remoteMessage.getData().get("type");
            if (type.equalsIgnoreCase("chat-assigned"))
                showNotiForOrderAssign(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            else if (type.equalsIgnoreCase("chat"))
                showNotiForChat(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            else {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            }

            if (remoteMessage.getNotification().getBody().contains("with the shopper has been terminated")||
                    remoteMessage.getNotification().getTitle().contains("Order Delivered")) {
                MyPreferences.saveBoolean(getApplicationContext(), ConstantValue.KEY_IS_CHAT_PROGRESS, false);
            }
            Intent intent = new Intent("message_subject_intent");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
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

    // Method to display the notifications
    public void showNotification(String title, String message, RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            chat_id = jsonObject.getString("chat_id");
            type = jsonObject.getString("type");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
        intent.putExtra("findingchatid", chat_id);
        intent.putExtra("chat_status", "1");
        intent.setAction(Intent.ACTION_MAIN);

        String channel_id = "simple_notification";

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
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(getCustomDesign(title, message, remoteMessage))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Simple Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());

    }

    public void showNotiForChat(String title, String message, RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            chat_id = jsonObject.getString("chat_id");
            type = jsonObject.getString("type");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
        intent.putExtra("findingchatid", chat_id);
        intent.putExtra("chat_status", "1");
        intent.setAction(Intent.ACTION_MAIN);

        String channel_id = "chat_notification";

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
                .setContentIntent(pendingIntent)
                .setContent(getCustomDesign(title, message, remoteMessage))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Chat Alert", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(2, builder.build());

    }

    public void showNotiForOrderAssign(String title, String message, RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            chat_id = jsonObject.getString("chat_id");
            type = jsonObject.getString("type");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
        intent.putExtra("findingchatid", chat_id);
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

}
