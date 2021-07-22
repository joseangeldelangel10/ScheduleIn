package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class EventQueries {

    public static void queryWeekEvents(Context context, ParseUser user, FindCallback callback) {

        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);

        query.include(Events.KEY_USER);
        query.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        query.whereLessThan(Events.KEY_START_DATE, DateTime.weekEnding());
        query.whereEqualTo(Events.KEY_USER, user);
        query.addAscendingOrder(Events.KEY_START_DATE);

        query.findInBackground(callback);

    }
}
