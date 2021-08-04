package com.example.schedulein_20.models;

import android.util.Log;

import com.example.schedulein_20.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//@Parcel
@ParseClassName("Events")
public class Events extends ParseObject {
    private static final String TAG = "EventsModel";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "creator";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_INVITEES = "invitees";
    public static final String KEY_ACCESS = "public";
    public static final String KEY_COLOR = "color";
    public static HashMap<Integer, Integer> dayInt2Layout = new HashMap<>();

    public Events(){
        dayInt2Layout.put(0, R.id.week_view_sun);
        dayInt2Layout.put(1, R.id.week_view_mon);
        dayInt2Layout.put(2, R.id.week_view_tue);
        dayInt2Layout.put(3, R.id.week_view_wed);
        dayInt2Layout.put(4, R.id.week_view_thu);
        dayInt2Layout.put(5, R.id.week_view_fri);
        dayInt2Layout.put(6, R.id.week_view_sat);
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

    public ArrayList<String> getInvitees() {return (ArrayList<String>) get(KEY_INVITEES);}

    public void setInvitees(ArrayList<String> invitees) {put(KEY_INVITEES, invitees);}

    public boolean hasPublicAccess() { return getBoolean(KEY_ACCESS); }

    public void setPublicAccess(boolean access) { put(KEY_ACCESS, access); }

    public int getColor() {return getInt(KEY_COLOR); };

    public void setColor(int color) { put(KEY_COLOR, color ); };

    public int getWeekDay(){
        return getStartDate().getDay();
    }

    public static Events fromJsonObject(ParseUser user, JSONObject jsonObject){
        Events event = new Events();
        try {
            Date startDate = RFC3339Date.parseRFC3339Date(jsonObject.getJSONObject("start").getString("dateTime"));
            Date endDate = RFC3339Date.parseRFC3339Date(jsonObject.getJSONObject("end").getString("dateTime"));

            event.setTitle(jsonObject.getString("summary"));
            event.setUser(user);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            event.setPublicAccess(false);
            event.setInvitees(new ArrayList<>());
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return event;
    }

    public static ArrayList<Events> fromJsonArray(ParseUser user, JSONArray jsonArray){
        ArrayList<Events> result = new ArrayList<>();
        for(int i = 0; i< jsonArray.length(); i++){
            try {
                result.add(Events.fromJsonObject( user, (JSONObject) jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "failed creating google events list");
                return null;
            }
        }
        Log.e(TAG, "suucesa creating google events list");
        return result;
    }

    public int getDurationInMins(){
        Date start = getStartDate();
        Date end = getEndDate();
        int minutesInHour = 60;
        int minutesInDay = 24*60;

        int startMinute = start.getDate()*minutesInDay + start.getHours() * minutesInHour + start.getMinutes();
        int endMinute = end.getDate()*minutesInDay + end.getHours() * minutesInHour + end.getMinutes();

        return endMinute - startMinute;
    }

    public int getStartInMins(){
        Date start = getStartDate();
        int minutesInHour = 60;

        int startMinute = start.getHours() * minutesInHour + start.getMinutes();

        return startMinute;
    }

    public int getEndInMins(){
        Date end = getEndDate();
        int minutesInHour = 60;

        int endMinute = end.getHours() * minutesInHour + end.getMinutes();

        return endMinute;
    }


}
