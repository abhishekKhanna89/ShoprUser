package com.shoppr.shoper.SendBird.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shoppr.shoper.SendBird.BaseApplication;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.SendBird.utils.PushUtils;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onNewToken(token: " + token + ")");

        if (SendBirdCall.getCurrentUser() != null)  {
            PushUtils.registerPushToken(getApplicationContext(), token, e -> {
                if (e != null) {
                    Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] registerPushTokenForCurrentUser() => e: " + e.getMessage());
                }
            });
        } else {
            PrefUtils.setPushToken(getApplicationContext(), token);
        }
    }
}
