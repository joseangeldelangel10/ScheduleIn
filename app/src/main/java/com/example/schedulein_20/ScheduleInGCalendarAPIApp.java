package com.example.schedulein_20;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

public class ScheduleInGCalendarAPIApp extends Application {

    public static GoogleCalendarClient getRestClient(Context context) {
        return (GoogleCalendarClient) GoogleCalendarClient.getInstance(GoogleCalendarClient.class, context);
    }
}
