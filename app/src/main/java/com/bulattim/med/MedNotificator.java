package com.bulattim.med;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bulattim.med.helpers.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

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
public class MedNotificator extends Service {
    static boolean f = false;
    DocumentSnapshot doc;

    public MedNotificator() {}

    public static boolean getState() {
        return f;
    }

    public static void stopService() {
        f = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "ACTION_START_FOREGROUND_SERVICE":
                case "ACTION_BOOT_COMPLETED":
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case "ACTION_STOP_FOREGROUND_SERVICE":
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        f = true;
        Calendar cal = Calendar.getInstance();
        Log.e("Service", "Цикл сервиса");
        try {
            doc = DBHelper.getDB(getBaseContext());
            if (doc != null) if (doc.exists()) {
                Map<String, Object> map = doc.getData();
                JSONArray j = new JSONArray(map.get("med").toString());
                for (int i = 0; i < j.length(); i++) {
                    String time = new JSONObject(j.get(i).toString()).get("time").toString();
                    String name = new JSONObject(j.get(i).toString()).get("name").toString();
                    int hours = Integer.parseInt(time.split(":")[0]);
                    int min = Integer.parseInt(time.split(":")[1]);
                    if (cal.get(Calendar.HOUR_OF_DAY) == hours && min - 5 <= cal.get(Calendar.MINUTE) && cal.get(Calendar.MINUTE) <= min + 5) {
                        NotificationCompat.Builder build = new NotificationCompat.Builder(getBaseContext(), "Таблэтки").setContentTitle("MedNotificator").setContentText("Время принимать " + name).setPriority(NotificationCompat.PRIORITY_MAX);
                        startForeground(1, build.build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Service", e.getLocalizedMessage());
        }
    }

    private void stopForegroundService()
    {
        Log.d("Service", "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }
}
