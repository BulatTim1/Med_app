package com.bulattim.med;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.TimeUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

//public class MedNotificator extends FirebaseMessagingService {
//    public MedNotificator() {
//    }
//
//    @Override
//    public void onNewToken(@NonNull @NotNull String s) {
//        super.onNewToken(s);
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // ...
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
////        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        remoteMessage.getData().size();//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//// For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
////                scheduleJob();
//
//        // Check if message contains a notification payload.
//        remoteMessage.getNotification();//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//}
public class MedNotificator extends IntentService {

    public MedNotificator(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startForegroundService(intent);
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        while(true) {
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(t -> {
                for (Object o : t.get("med", Collection.class)) {
                    try {
                        JSONObject j = new JSONObject(o.toString());
                        int hours = Integer.parseInt(j.get("time").toString().split(":")[0]);
                        int min = Integer.parseInt(j.get("time").toString().split(":")[1]);
                        Calendar cal = Calendar.getInstance();
                        if (cal.get(Calendar.HOUR_OF_DAY) == hours && min - 5 <= cal.get(Calendar.MINUTE) && cal.get(Calendar.MINUTE) <= min + 5) {
                            NotificationCompat.Builder build = new NotificationCompat.Builder(getBaseContext(), "Таблэтки").setContentTitle("MedNotificator").setContentText("Время принимать " + j.get("name")).setPriority(NotificationCompat.PRIORITY_MAX);
                            NotificationManagerCompat man = NotificationManagerCompat.from(getBaseContext());
                            man.notify(-1, build.build());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
}
