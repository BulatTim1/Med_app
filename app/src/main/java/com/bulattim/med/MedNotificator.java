package com.bulattim.med;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bulattim.med.helpers.DBHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;


public class MedNotificator extends Worker {

    static boolean f;
    final Context context;

    public MedNotificator(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    public static void stopWork() {
        f = false;
    }

    public static OneTimeWorkRequest getWork() {
        return new OneTimeWorkRequest.Builder(MedNotificator.class).build();
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        if (f) return Result.failure();
        f = true;
        while (f) {
            try {
                Log.e("Service", "Цикл сервиса");
                Map<String, Object> map = DBHelper.getDB(getApplicationContext());
                JSONArray j = new JSONArray(map.get("med").toString());
                for (int i = 0; i < j.length(); i++) {
                    String time = new JSONObject(j.get(i).toString()).get("time").toString();
                    String name = new JSONObject(j.get(i).toString()).get("name").toString();
                    int hours = Integer.parseInt(time.split(":")[0]);
                    int min = Integer.parseInt(time.split(":")[1]);
                    Calendar cal = Calendar.getInstance();
                    if (cal.get(Calendar.HOUR_OF_DAY) == hours && min - 1 <= cal.get(Calendar.MINUTE) && cal.get(Calendar.MINUTE) <= min + 1) {
                        NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext(), "Таблэтки").setContentTitle("MedNotificator").setContentText("Время принимать " + name).setPriority(NotificationCompat.PRIORITY_MAX).setSmallIcon(R.drawable.common_google_signin_btn_icon_light).setVibrate(new long[]{NotificationCompat.DEFAULT_VIBRATE});
                        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String channelId = "Таблэтки";
                            NotificationChannel channel = new NotificationChannel(
                                    channelId,
                                    "MedNotificator",
                                    NotificationManager.IMPORTANCE_HIGH);
                            nm.createNotificationChannel(channel);
                        }
                        nm.notify(1, build.build());
                        Log.e("Notif", "Notification created");
                    }
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                Log.e("Service", e.getLocalizedMessage());
            }
        }
        return Result.success();
    }
}
