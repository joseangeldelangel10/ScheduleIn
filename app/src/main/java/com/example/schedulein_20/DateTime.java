package com.example.schedulein_20;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTime {
    private static String TAG = "DateTime";
    private static long currentMillis = System.currentTimeMillis();
    private static Calendar calendar = Calendar.getInstance();

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Log.e(TAG, now.toString());
        return dtf.format(now);
    }

    public static String getFormalCurrentDate(){
        java.util.Date date = calendar.getTime();
        String res = date.toString();
        res = onlyDate(res);
        return res;
    }

    private static String onlyDate(String res) {
        return(res.substring(0,11));
    }

    public static String timeBasedGreeting(){
        java.util.Date date = calendar.getTime();
        int hour = date.getHours();
        String greet = "Good ";
        if ( 4 <= hour && hour < 12){
            return greet + "morning";
        }
        if ( 12 <= hour && hour < 20){
            return greet + "afternoon";
        }
        if ( 8 <= hour && hour < 4){
            return greet + "night";
        }
        return "Hey ";
    }

}
