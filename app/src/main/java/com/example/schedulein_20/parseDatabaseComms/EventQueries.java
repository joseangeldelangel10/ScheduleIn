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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventQueries {

    public static void createEventInDB(Context context, String eventTitle, Date startDate, Date endDate, boolean eventIsPublic, String repeatFlag, Date repeatUntil, int eventColor, ArrayList<String> inviteesIds, SaveCallback callback) {
        Events event = new Events();
        event.setUser(ParseUser.getCurrentUser());
        event.setTitle(eventTitle);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setInvitees(inviteesIds);
        event.setPublicAccess(eventIsPublic);
        event.setColor(eventColor);
        if(repeatFlag != null) {
            event.setRepeat(repeatFlag);
        }
        if(repeatUntil != null) {
            event.setRepeatUntil(repeatUntil);
        }

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

    public static void updateEventInDB(Context context, Events event2update, String eventTitle, Date startDate, Date endDate, boolean eventIsPublic, String repeatFlag, Date repeatUntil, int eventColor, ArrayList<String> inviteesIds, Date googlesLastUpdate, SaveCallback callback) {
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);

        // Retrieve the object by id
        query.getInBackground(event2update.getObjectId(), (object, e) -> {
            if (e == null) {
                // Update the fields we want to
                object.put(Events.KEY_START_DATE, startDate);
                object.put(Events.KEY_END_DATE, endDate);
                object.put(Events.KEY_TITLE, eventTitle);
                object.put(Events.KEY_INVITEES, inviteesIds);
                object.put(Events.KEY_ACCESS, eventIsPublic);
                object.put(Events.KEY_COLOR, eventColor);

                if(repeatFlag != null) {
                    object.put(Events.KEY_REPEAT, repeatFlag);
                }
                if(repeatUntil != null) {
                    object.put(Events.KEY_REPEAT_UNTIL, repeatUntil);
                }

                if(googlesLastUpdate != null){
                    object.put(Events.KEY_GOOGLE_LAST_UPDATE, googlesLastUpdate);
                }

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

        /* ------------------------------------------------------------------------------
                                    RECURRING EVENTS QUERIES
        ------------------------------------------------------------------------------ */
        Calendar c1 = Calendar.getInstance();
        c1.setTime(startOfDay);

        ParseQuery<Events> dailyEventsQuery = ParseQuery.getQuery(Events.class);

        dailyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "daily");
        dailyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        dailyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        dailyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        ParseQuery<Events> weeklyEventsQuery = ParseQuery.getQuery(Events.class);

        weeklyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "weekly");
        weeklyEventsQuery.whereEqualTo(Events.KEY_DAY_OF_WEEK, c1.get(Calendar.DAY_OF_WEEK));
        weeklyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        weeklyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        weeklyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        ParseQuery<Events> monthlyEventsQuery = ParseQuery.getQuery(Events.class);

        monthlyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "monthly");
        monthlyEventsQuery.whereEqualTo(Events.KEY_DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH));
        monthlyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        monthlyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        monthlyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        List<ParseQuery<Events>> recurringEventsQueries = new ArrayList<ParseQuery<Events>>();
        recurringEventsQueries.add(dailyEventsQuery);
        recurringEventsQueries.add(weeklyEventsQuery);
        recurringEventsQueries.add(monthlyEventsQuery);

        ParseQuery<Events> userRecurringEventsQuery = ParseQuery.or(recurringEventsQueries);

        /* ------------------------------------------------------------------------------
                                    RECURRING EVENTS INVITED TO QUERIES
        ------------------------------------------------------------------------------ */
        Calendar c2 = Calendar.getInstance();
        c2.setTime(startOfDay);

        ParseQuery<Events> dailyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        dailyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "daily");
        dailyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        dailyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        dailyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        ParseQuery<Events> weeklyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "weekly");
        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_DAY_OF_WEEK, c2.get(Calendar.DAY_OF_WEEK));
        weeklyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        weeklyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        ParseQuery<Events> monthlyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "monthly");
        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH));
        monthlyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        monthlyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        List<ParseQuery<Events>> recurringEventsInvitedToQueries = new ArrayList<ParseQuery<Events>>();
        recurringEventsInvitedToQueries.add(dailyEventsInvitedToQuery);
        recurringEventsInvitedToQueries.add(weeklyEventsInvitedToQuery);
        recurringEventsInvitedToQueries.add(monthlyEventsInvitedToQuery);

        ParseQuery<Events> userRecurringEventsInvitedToQuery = ParseQuery.or(recurringEventsInvitedToQueries);

        /* ------------------------------------------------------------------------------ */


        List<ParseQuery<Events>> queries = new ArrayList<ParseQuery<Events>>();
        queries.add(userEventsQuery);
        queries.add(eventsInvitedToQuery);
        queries.add(userRecurringEventsQuery);
        queries.add(userRecurringEventsInvitedToQuery);

        ParseQuery<Events> mainQuery = ParseQuery.or(queries);
        mainQuery.addAscendingOrder(Events.KEY_START_DATE);
        mainQuery.findInBackground(callback);

    }

    public static void queryParseDayEvents(Context context, ParseUser user, Date dayOfWeek, FindCallback callback) {
        Date endOfDay = new Date(dayOfWeek.getTime());
        Date startOfDay = new Date(dayOfWeek.getTime());

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


        eventsInvitedToQuery.whereGreaterThan(Events.KEY_START_DATE, startOfDay);
        eventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, endOfDay);
        eventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user.getObjectId());

        /* ------------------------------------------------------------------------------
                                    RECURRING EVENTS QUERIES
        ------------------------------------------------------------------------------ */
        Calendar c1 = Calendar.getInstance();
        c1.setTime(startOfDay);

        ParseQuery<Events> dailyEventsQuery = ParseQuery.getQuery(Events.class);

        dailyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "daily");
        dailyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        dailyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        dailyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        ParseQuery<Events> weeklyEventsQuery = ParseQuery.getQuery(Events.class);

        weeklyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "weekly");
        weeklyEventsQuery.whereEqualTo(Events.KEY_DAY_OF_WEEK, c1.get(Calendar.DAY_OF_WEEK));
        weeklyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        weeklyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        weeklyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        ParseQuery<Events> monthlyEventsQuery = ParseQuery.getQuery(Events.class);

        monthlyEventsQuery.whereEqualTo(Events.KEY_REPEAT, "monthly");
        monthlyEventsQuery.whereEqualTo(Events.KEY_DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH));
        monthlyEventsQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        monthlyEventsQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        monthlyEventsQuery.whereEqualTo(Events.KEY_USER, user);

        List<ParseQuery<Events>> recurringEventsQueries = new ArrayList<ParseQuery<Events>>();
        recurringEventsQueries.add(dailyEventsQuery);
        recurringEventsQueries.add(weeklyEventsQuery);
        recurringEventsQueries.add(monthlyEventsQuery);

        ParseQuery<Events> userRecurringEventsQuery = ParseQuery.or(recurringEventsQueries);

        /* ------------------------------------------------------------------------------
                                    RECURRING EVENTS INVITED TO QUERIES
        ------------------------------------------------------------------------------ */
        Calendar c2 = Calendar.getInstance();
        c2.setTime(startOfDay);

        ParseQuery<Events> dailyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        dailyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "daily");
        dailyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        dailyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        dailyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        ParseQuery<Events> weeklyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "weekly");
        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_DAY_OF_WEEK, c2.get(Calendar.DAY_OF_WEEK));
        weeklyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        weeklyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        weeklyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        ParseQuery<Events> monthlyEventsInvitedToQuery = ParseQuery.getQuery(Events.class);

        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_REPEAT, "monthly");
        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH));
        monthlyEventsInvitedToQuery.whereGreaterThanOrEqualTo(Events.KEY_REPEAT_UNTIL, startOfDay);
        monthlyEventsInvitedToQuery.whereLessThan(Events.KEY_END_DATE, startOfDay);
        monthlyEventsInvitedToQuery.whereEqualTo(Events.KEY_INVITEES, user);

        List<ParseQuery<Events>> recurringEventsInvitedToQueries = new ArrayList<ParseQuery<Events>>();
        recurringEventsInvitedToQueries.add(dailyEventsInvitedToQuery);
        recurringEventsInvitedToQueries.add(weeklyEventsInvitedToQuery);
        recurringEventsInvitedToQueries.add(monthlyEventsInvitedToQuery);

        ParseQuery<Events> userRecurringEventsInvitedToQuery = ParseQuery.or(recurringEventsInvitedToQueries);

        /* ------------------------------------------------------------------------------ */


        List<ParseQuery<Events>> queries = new ArrayList<ParseQuery<Events>>();
        queries.add(userEventsQuery);
        queries.add(eventsInvitedToQuery);
        queries.add(userRecurringEventsQuery);
        queries.add(userRecurringEventsInvitedToQuery);

        ParseQuery<Events> mainQuery = ParseQuery.or(queries);
        mainQuery.addAscendingOrder(Events.KEY_START_DATE);
        mainQuery.whereEqualTo(Events.KEY_GOOGLE_EVENT_ID, "0");
        mainQuery.findInBackground(callback);
    }

    public static void queryGoogleWeekEvents(Context context, ParseUser user, FindCallback<Events> callback) {

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
        mainQuery.whereNotEqualTo(Events.KEY_GOOGLE_EVENT_ID, "0");
        mainQuery.findInBackground(callback);

    }

    public static void queryGoogleEvent(Context context, Events googleRetrievedEvent ,FindCallback<Events> callback) {
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        query.whereEqualTo(Events.KEY_GOOGLE_EVENT_ID, googleRetrievedEvent.getGoogleEventId() );

        query.findInBackground(callback);

    }


}
