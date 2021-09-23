package com.example.schedulein_20;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.TodoItems;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ParseObject.registerSubclass(Events .class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(TodoItems.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId( getString(R.string.application_id))
                .clientKey( getString(R.string.client_key))
                .server(getString(R.string.server))
                .build()
        );

        // IF THE SDK IS OREO OR ABOVE WE CREATE A NOTIFICATION CHANNEL
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configure the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = null;
            channel = new NotificationChannel("myChannelId", "My Channel", importance);
            channel.setDescription("Reminders");
            // Register the channel with the notifications manager
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }

    }
}
