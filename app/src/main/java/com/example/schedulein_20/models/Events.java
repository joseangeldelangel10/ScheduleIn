package com.example.schedulein_20.models;

import androidx.annotation.Nullable;

import com.example.schedulein_20.R;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@Parcel
@ParseClassName("Events")
public class Events extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "creator";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static HashMap<Integer, Integer> dayInt2Str = new HashMap<>();

    public Events(){
        dayInt2Str.put(0, R.id.week_view_sun);
        dayInt2Str.put(1, R.id.week_view_mon);
        dayInt2Str.put(2, R.id.week_view_tue);
        dayInt2Str.put(3, R.id.week_view_wed);
        dayInt2Str.put(4, R.id.week_view_thu);
        dayInt2Str.put(5, R.id.week_view_fri);
        dayInt2Str.put(6, R.id.week_view_sat);
    }

    public String getTitle() { return getString(KEY_TITLE); }

    public void  setTitle(String title) { put(KEY_TITLE, title); }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void  setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    public ParseUser getUser(){ return getParseUser(KEY_USER); }

    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public Date getStartDate() {return getDate(KEY_START_DATE);}

    public void setStartDate(Date date) {put(KEY_START_DATE, date);}

    public Date getEndDate() {return getDate(KEY_END_DATE);}

    public void setEndDate(Date date) {put(KEY_END_DATE, date);}


    public int getWeekDay(){
        return getStartDate().getDay();
    }

    public int getDurationInMins(){
        Date start = getStartDate();
        Date end = getEndDate();
        int minutesInHour = 60;

        int startMinute = start.getHours() * minutesInHour + start.getMinutes();
        int endMinute = end.getHours() * minutesInHour + end.getMinutes();

        return endMinute - startMinute;
    }

    public int getStartInMins(){
        Date start = getStartDate();
        int minutesInHour = 60;

        int startMinute = start.getHours() * minutesInHour + start.getMinutes();

        return startMinute;
    }


}