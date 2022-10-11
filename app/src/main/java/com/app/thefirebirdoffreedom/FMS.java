package com.app.thefirebirdoffreedom;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FMS extends FirebaseMessagingService {
    private static final String  TAG = "SCGAMES_Messaging_Service";

    private PNL pushNotificationsListener;

    private class SCGAMES_Messaging_ServiceBinder extends Binder {
        FMS getService() {
            return FMS.this;
        }

    }

    public void PNL(PNL pushNotificationsListener) {
        this.pushNotificationsListener = pushNotificationsListener;
    }

    public static FMS from(IBinder binder) {
        return ((SCGAMES_Messaging_ServiceBinder) binder).getService();
    }

    @Override
    public void onNewToken(@NonNull String token) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                    }
                });

        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    @Override
    public void onMessageReceived (@NonNull RemoteMessage message){
        super.onMessageReceived(message);
        if (message.getData().get("message") != null){

            String messageStr = message.getData().get("message");
            if (pushNotificationsListener != null){
                pushNotificationsListener.onPushNotification(messageStr, message);
            }
            messageStr = messageStr == null ? "" : messageStr;
            Log.i(TAG, messageStr);
        } else {
            Log.i(TAG, "Push notification appears to be empty");
        }
    }

}
