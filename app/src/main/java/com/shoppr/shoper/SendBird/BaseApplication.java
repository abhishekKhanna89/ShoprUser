package com.shoppr.shoper.SendBird;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.call.CallActivity;
import com.shoppr.shoper.SendBird.call.CallService;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
import com.shoppr.shoper.SendBird.utils.BroadcastUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;


import java.util.UUID;

public class BaseApplication extends Application {

    public static final String VERSION = "1.0.2";

    private static final String TAG = "BaseApplication";

    // Refer to "https://github.com/sendbird/quickstart-calls-android".
    public static final String APP_ID = "A7EF97E9-E7B5-4E23-A6C9-B85E4B84C549";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        initSendBirdCall(PrefUtils.getAppId(getApplicationContext()));
    }
    public boolean initSendBirdCall(String appId) {
        Log.i(BaseApplication.TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_ID;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    int ongoingCallCount = SendBirdCall.getOngoingCallCount();

                    Log.i(BaseApplication.TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                    if (ongoingCallCount >= 2) {
                        call.end();
                        return;
                    }

                    call.setListener(new DirectCallListener() {
                        @Override
                        public void onConnected(DirectCall call) {
                        }

                        @Override
                        public void onEnded(DirectCall call) {
                            int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                            Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                            BroadcastUtils.sendCallLogBroadcast(context, call.getCallLog());

                            if (ongoingCallCount == 0) {
                                CallService.stopService(context);
                            }
                        }
                        // ActivityUtils.startCallActivityAsCallee(context, call);

                        //prefUtils.start
                        // PrefUtils.startCallActivityAsCallee(context, call);
                    });


                    //PrefUtils.startCallActivityAsCallee(context, call);
                   // CallService.onRinging(context, call);
                    ActivityUtils.startCallActivityAsCallee(context, call);

                    // PrefUtils.startCallActivityAsCallee(context, call);
                }
            });

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);

            return true;
        }
        return false;
    }



}



   /* public boolean initSendBirdCall(String appId) {
        Log.d(TAG, "initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_ID;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    Log.d(TAG, "onRinging() => callId: " + call.getCallId());
                    if (CallActivity.sIsRunning) {
                        call.end();
                        return;
                    }
                    ActivityUtils.startCallActivityAsCallee(context, call);
                }
            });

           *//* SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);*//*
            return true;
        }
        return false;
    }
}*/


/*
public class BaseApplication extends MultiDexApplication { // multidex

    public static final String VERSION = "1.4.0";

    public static final String TAG = "SendBirdCalls";
    // Refer to "https://github.com/sendbird/quickstart-calls-android".
    public static final String APP_ID = "A7EF97E9-E7B5-4E23-A6C9-B85E4B84C549";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(BaseApplication.TAG, "[BaseApplication] onCreate()");
        initSendBirdCall(PrefUtils.getAppId(getApplicationContext()));


    }

    public boolean initSendBirdCall(String appId) {
        Log.i(BaseApplication.TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_ID;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    int ongoingCallCount = SendBirdCall.getOngoingCallCount();

                    Log.i(BaseApplication.TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                    if (ongoingCallCount >= 2) {
                        call.end();
                        return;
                    }

                    call.setListener(new DirectCallListener() {
                        @Override
                        public void onConnected(DirectCall call) {
                        }

                        @Override
                        public void onEnded(DirectCall call) {
                            int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                            Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                            BroadcastUtils.sendCallLogBroadcast(context, call.getCallLog());

                            if (ongoingCallCount == 0) {
                                CallService.stopService(context);
                            }
                        }
                    });

                    //PrefUtils.startCallActivityAsCallee(context, call);
                    CallService.onRinging(context, call);

                }
            });

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);

            return true;
        }
        return false;
    }



}
*/
