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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shoppr.shoper.R;
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
    String a;

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        sessonManager = new SessonManager(this);

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage);

            Intent intent = new Intent("message_subject_intent");
            intent.putExtra("chat_id", chat_id);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.mipmap.ic_launcher);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title, String message, RemoteMessage remoteMessage) {


        //Log.d("title",title);
        // Pass the intent to switch to the MainActivity
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        Log.d("ChatId+",""+jsonObject);
        try {
            chat_id = jsonObject.getString("chat_id");
            //Log.d("ChatId+",chat_id);
            type = jsonObject.getString("type");
            if (type.equalsIgnoreCase("chat-assigned")){
                startActivity(new Intent(this, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                sessonManager.setChatId("");
                sessonManager.setChatId(chat_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent = new Intent(this, ChatDetailsActivity.class);

        intent.putExtra("whattodo", chat_id);
        intent.putExtra("checkfornavigation","0");
// add this:
        intent.setAction("showmessage");
        sessonManager.setChatId(chat_id);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setSound(notification)
                .setContentIntent(pendingIntent);





        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
                getCustomDesign(title, message));
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

        notificationManager.notify(0, builder.build());


    }

}
