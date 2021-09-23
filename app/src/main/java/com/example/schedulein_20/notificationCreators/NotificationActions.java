package com.example.schedulein_20.notificationCreators;

import static android.content.Context.ALARM_SERVICE;
import static com.parse.Parse.getApplicationContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

public class NotificationActions {
    public static void createOneTimeNotification(Context context, ParseUser currentUser, String title, String content, Long firingTime /*use calendar.getTimeInMillis()*/){
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        int notificationId = currentUser.getInt("notificationCount");
        intent.putExtra("notificationTitle", title);
        intent.putExtra("notificationContent", content);
        intent.putExtra("notificationId", notificationId);
        currentUser.put("notificationCount", notificationId+1);
        currentUser.saveInBackground();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  firingTime, pendingIntent);
    }

    public static void createRepeatingNotification(Context context, ParseUser currentUser, String title, String content, Long firingTime /*use calendar.getTimeInMillis()*/, String repeatFlag){
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        int notificationId = currentUser.getInt("notificationCount");
        intent.putExtra("notificationTitle", title);
        intent.putExtra("notificationContent", content);
        intent.putExtra("notificationId", notificationId);
        currentUser.put("notificationCount", notificationId+1);
        currentUser.saveInBackground();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP,  firingTime, pendingIntent);
        if(repeatFlag.equals("daily")){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firingTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }
}
