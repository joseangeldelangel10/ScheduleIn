package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventQueries {

    public static void createEventInDB(Context context, String eventTitle, Date startDate, Date endDate, ArrayList<String> inviteesIds, SaveCallback callback) {
        Events event = new Events();
        event.setUser(ParseUser.getCurrentUser());
        event.setTitle(eventTitle);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setInvitees(inviteesIds);

        if (startDate.compareTo(endDate) == 1) {
            Toast.makeText(context, "starting date cannot be after end", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Please change the dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (callback != null){
            event.saveInBackground(callback);
        }else{
            event.saveInBackground();
        }
    }

    public static void updateEventInDB(Context context, Events event2update, String eventTitle, Date startDate, Date endDate, ArrayList<String> inviteesIds, SaveCallback callback) {
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);

        // Retrieve the object by id
        query.getInBackground(event2update.getObjectId(), (object, e) -> {
            if (e == null) {
                // Update the fields we want to
                object.put(Events.KEY_START_DATE, startDate);
                object.put(Events.KEY_END_DATE, endDate);
                object.put(Events.KEY_TITLE, eventTitle);
                object.put(Events.KEY_INVITEES, inviteesIds);

                // All other fields will remain the same
                if(callback != null){
                    object.saveInBackground(callback);
                }else {
                    object.saveInBackground();
                }

            } else {
                Toast.makeText(context, "Failed when updating event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteEventInDB(Context context, Events event2delete, DeleteCallback callback) {
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        query.getInBackground(event2delete.getObjectId(), (object, e) -> {
            if (e == null) {
                // Deletes the fetched ParseObject from the database
                if (callback != null){
                    object.deleteInBackground(callback);
                }else{
                    object.deleteInBackground();
                }

            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void queryWeekEvents(Context context, ParseUser user, FindCallback callback) {

        ParseQuery<Events> userEventsQuery = ParseQuery.getQuery(Events.class);
        ParseQuery<Events> eventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        userEventsQuery.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        userEventsQuery.whereLessThan(Events.KEY_END_DATE, DateTime.weekEnding());
        userEventsQuery.whereEqualTo(Events.KEY_USER, user);
        //query.whereEqualTo(Events.KEY_INVITEES, user.getObjectId());


        eventsInvitedToQuery.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        eventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, DateTime.weekEnding());
        eventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user.getObjectId());


        List<ParseQuery<Events>> queries = new ArrayList<ParseQuery<Events>>();
        queries.add(userEventsQuery);
        queries.add(eventsInvitedToQuery);

        ParseQuery<Events> mainQuery = ParseQuery.or(queries);
        mainQuery.addAscendingOrder(Events.KEY_START_DATE);
        mainQuery.findInBackground(callback);

    }

    public static void queryDayEvents(Context context,ParseUser user, Date day ,FindCallback callback) {
        Date endOfDay = new Date(day.getTime());
        Date startOfDay = new Date(day.getTime());

        startOfDay.setHours(0);
        startOfDay.setMinutes(00);
        startOfDay.setSeconds(00);

        endOfDay.setHours(23);
        endOfDay.setMinutes(59);
        endOfDay.setSeconds(59);

        ParseQuery<Events> userEventsQuery = ParseQuery.getQuery(Events.class);
        ParseQuery<Events> eventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        userEventsQuery.whereGreaterThan(Events.KEY_START_DATE, startOfDay);
        userEventsQuery.whereLessThan(Events.KEY_END_DATE, endOfDay);
        userEventsQuery.whereEqualTo(Events.KEY_USER, user);
        //query.whereEqualTo(Events.KEY_INVITEES, user.getObjectId());


        eventsInvitedToQuery.whereGreaterThan(Events.KEY_START_DATE, startOfDay);
        eventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, endOfDay);
        eventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user.getObjectId());


        List<ParseQuery<Events>> queries = new ArrayList<ParseQuery<Events>>();
        queries.add(userEventsQuery);
        queries.add(eventsInvitedToQuery);

        ParseQuery<Events> mainQuery = ParseQuery.or(queries);
        mainQuery.addAscendingOrder(Events.KEY_START_DATE);
        mainQuery.findInBackground(callback);

    }
}
