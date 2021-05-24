package com.shoppr.shoper.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.activity.ChatActivity;
import com.shoppr.shoper.activity.ChatDetailsActivity;
import com.shoppr.shoper.activity.FindingShopprActivity;
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
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
           /* Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());*/
        }else {
          /*  Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());*/
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage);

            Intent intent = new Intent("message_subject_intent");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title, String message, RemoteMessage remoteMessage) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_layout);
        JSONObject jsonObject=new JSONObject(remoteMessage.getData());
        try {
            title=jsonObject.getString("title");
            message=jsonObject.getString("message");
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
        intent.putExtra("findingchatid",chat_id);
        intent.putExtra("chat_status","1");
        intent.setAction(Intent.ACTION_MAIN);

        String channel_id = "notification_channel";

        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags

        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            //r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }



        @SuppressLint("WrongConstant") NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.splash)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setSound(notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);




        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
                getCustomDesign(title, message,remoteMessage));
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }
        builder.setOngoing(true);
        notificationManager.notify(0, builder.build());


    }

}
