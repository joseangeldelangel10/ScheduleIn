package com.example.schedulein_20.notificationCreators;

import static android.content.Context.ALARM_SERVICE;
import static com.parse.Parse.getApplicationContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent intent1 = new Intent(context, MyNewIntentService.class);
        //context.startService(intent1);
        String title = intent.getStringExtra("notificationTitle");
        String content = intent.getStringExtra("notificationContent");
        int notificationId = intent.getIntExtra("notificationId", 0);
        /*String repeatFlag = intent.getStringExtra("repeatFlag");
        if(repeatFlag.equals("")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,  firingTime, pendingIntent);
        }*/
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification(title, content, notificationId);
    }
}
