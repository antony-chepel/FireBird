package com.app.thefirebirdoffreedom;

import com.google.firebase.messaging.RemoteMessage;

public interface PNL {

    void onPushNotification(String message, RemoteMessage remoteMessage);
}
