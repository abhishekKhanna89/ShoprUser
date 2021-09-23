package com.shoppr.shoper.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.call.CallActivity;
import com.shoppr.shoper.SendBird.call.VideoCallActivity;
import com.shoppr.shoper.SendBird.call.VoiceCallActivity;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
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
        sessonManager = new SessonManager(this);
        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.e("screen on.................................", ""+isScreenOn);
        if(isScreenOn==false)
        {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.d("MyFirebaseMessaging ", remoteMessage.getData().toString());
            showNotificationCall("", "", remoteMessage);
        } else {
            Log.e("Firebase_notification: ", remoteMessage.getData().toString());
            String type = remoteMessage.getData().get("type");
            if (type.equalsIgnoreCase("chat-assigned"))
                showNotiForOrderAssign(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            else if (type.equalsIgnoreCase("chat"))
                showNotiForChat(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            else {
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage);
            }
            if (remoteMessage.getNotification().getBody().contains("with the shopper has been terminated") ||
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

    private RemoteViews getCustomDesignCall(String title, String message, RemoteMessage remoteMessage) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.call_dial_notification_layout);
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
      /*  try {
           // title = jsonObject.getString("title");
            message = jsonObject.getString("message");
            remoteViews.setTextViewText(R.id.title, "Calling");
            remoteViews.setTextViewText(R.id.messages, "ShopR Calling You");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.messages, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.splash);
        return remoteViews;
    }


    // Method to display the notifications
    public void showNotification(String title, String message, RemoteMessage remoteMessage) {
        System.out.println("ddddddddddddddxxxxxxxxxxxxxx");

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
            //  notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Simple Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }

    public void showNotificationCall(String title, String message, RemoteMessage remoteMessage) {
        try {
            if (remoteMessage.getData().containsKey("sendbird_call")) {
               try {
                   JSONObject sendbird = new JSONObject(remoteMessage.getData().get("sendbird_call"));
                   JSONObject channel = (JSONObject) sendbird.get("command");
                   JSONObject channel1 = (JSONObject) channel.get("payload");
                   boolean is_video_call = (boolean) channel1.get("is_video_call");
                   String call_id = (String) channel1.get("call_id");
                   String push_alert = (String) sendbird.get("push_alert");
                   String user_id = (String) sendbird.get("user_id");
                   String type = (String) channel.get("type");
                   if (type.equals("dial")) {
                       if (is_video_call) {
                           intent = new Intent(FirebaseMessageReceiver.this, VideoCallActivity.class);
                       } else {
                           intent = new Intent(FirebaseMessageReceiver.this, VoiceCallActivity.class);
                       }
                       // Intent intent=new Intent(FirebaseMessageReceiver.this, VoiceCallActivity.class);
                       intent.putExtra(ActivityUtils.EXTRA_INCOMING_CALL_ID, call_id);
                       intent.setAction(Intent.ACTION_MAIN);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                       // startActivity(intent);
                       System.out.println("sendbirdsendbirdsendbirdsendbird," + call_id);

                       String channel_id = "simple_notification";

                       PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                       try {
                           //  notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
                               .setContent(getCustomDesignCall(user_id, push_alert, remoteMessage))
                               .setPriority(NotificationCompat.PRIORITY_HIGH);

                       NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                           NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Simple Notification", NotificationManager.IMPORTANCE_HIGH);
                           notificationManager.createNotificationChannel(notificationChannel);
                       }
                       notificationManager.notify(0, builder.build());

                   }
               }
               catch (Exception e)
               {

               }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showNotificationCall1(String title, String message, RemoteMessage remoteMessage) {
        System.out.println("ddddddddddddddxxxxxxxxxxxxxx");

        try {
            if (remoteMessage.getData().containsKey("sendbird_call")) {
                JSONObject sendbird = new JSONObject(remoteMessage.getData().get("sendbird_call"));
                JSONObject channel = (JSONObject) sendbird.get("command");
                JSONObject channel1 = (JSONObject) channel.get("payload");
                String call_id = (String) channel1.get("call_id");
                Intent intent = new Intent(FirebaseMessageReceiver.this, CallActivity.class);
                intent.putExtra(ActivityUtils.EXTRA_INCOMING_CALL_ID, call_id);
                startActivity(intent);
                // JSONObject sendbird = new JSONObject(remoteMessage.getData().get("command"));
                //JSONObject channel = (JSONObject) sendbird.get("channel");
                //  int channelUrl = (int) sendbird.get("sequence_number");
                //  messageTitle = (String) sendbird.get("push_title");
                //  messageBody = (String) sendbird.get("message");
                System.out.println("sendbirdsendbirdsendbirdsendbird," + call_id);
                // If you want to customize a notification with the received FCM message,
                // write your method like the sendNotification() below.

            }
        } catch (Exception e) {
        }

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
