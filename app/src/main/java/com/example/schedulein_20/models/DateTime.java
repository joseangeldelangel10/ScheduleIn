package com.example.schedulein_20.models;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.schedulein_20.R;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
    /* ------------------------------------------------------------------------
    * This is a helper class with several static methods related to time matters
    * ------------------------------------------------------------------------ */
    private static String TAG = "DateTime";
    private static long currentMillis = System.currentTimeMillis();
    private static Calendar calendar = Calendar.getInstance();
    private static Resources res;


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

    public static String onlyDate(String res) {
        return(res.substring(0,11));
    }

    public static String onlyDate(Date date){
        return date.toString().substring(0,11);
    }

    public static String onlyTime(Date date){
        DecimalFormat formatter = new DecimalFormat("00");
        String hour = formatter.format( date.getHours() );
        String minutes = formatter.format( date.getMinutes() );

        return hour + ":" + minutes;
    }

    public static String timeBasedGreeting(Context context){
        res = context.getResources();
        Date date = calendar.getTime();
        int hour = date.getHours();
        String greet = res.getString(R.string.good) + " ";
        if ( 4 <= hour && hour < 12){
            return greet + res.getString(R.string.morning);
        }
        if ( 12 <= hour && hour < 20){
            return greet + res.getString(R.string.afternoon);
        }
        if ( 20 <= hour || hour < 4){
            return greet + res.getString(R.string.night);
        }
        return res.getString(R.string.generic_greeting);
    }

    public static Date weekStart(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        Date result = c.getTime();
        Log.e("UserProfile", "start: " + result.toString());
        return result;
    }

    public static Date weekEnding(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.add(Calendar.DATE,7);
        Date result = c.getTime();
        Log.e("UserProfile", "end: " + result.toString());
        return result;
    }


    public static Date currentDate(){
        return Calendar.getInstance().getTime();
    }

}

