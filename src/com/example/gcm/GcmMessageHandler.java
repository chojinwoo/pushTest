package com.example.gcm;

import android.app.*;
import android.net.Uri;
import android.view.*;
import android.widget.TextView;
import com.example.pushTest.MainActivity;
import com.example.pushTest.R;
import com.example.wakeLock.WakeUpScreen;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
import android.app.IntentService;
import android.os.Handler;
import android.widget.Toast;


public class GcmMessageHandler extends IntentService {


    String mes;
    String title;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    private static void generateNotification(Context context, String message) {

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        mes = extras.getString("message");

        showNotification();

//        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showNotification() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                WakeUpScreen.acquire(getApplicationContext());
                generateNotification(getApplicationContext(), title + " : " + mes);
                Toast toast = Toast.makeText(getApplicationContext(), title + " : " + mes, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 220);
                toast.show();
            }
        });
    }


}