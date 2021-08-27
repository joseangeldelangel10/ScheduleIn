package com.example.schedulein_20.RESTclientOpetations;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.schedulein_20.GoogleCalendarClient;
import com.example.schedulein_20.ScheduleInGCalendarAPIApp;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.RelationRequestsAdapter;
import com.example.schedulein_20.models.TimeSuggestions;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

public class MergingDiffCalendarsEvents {
    public static final String TAG = "checkGoogleEvents";
    private static int mergesCompleted = 0;


    public interface OnMergeEvents{
        void OnMergeEventsCompleted();
    }

    public static void checkGoogleEvents(Context context, ArrayList<Events> googleRetrievedEvents, Fragment fragment) {
        mergesCompleted = 0;

        /* ------------------------------------------------------------------------------------------------
                                    WE CREATE A BACKGROUND TASK WHICH WAITS UNTIL
                                   ALL GOOGLE EVENTS HAVE BEEN UPDATED IN PARSE DB
                                         (MERGES COMPLETED == GOOGLE EVS SIZE)
         ------------------------------------------------------------------------------------------------ */
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mergesCompleted == googleRetrievedEvents.size()){
                    mergesCompleted = 0;
                    OnMergeEvents listener = (OnMergeEvents) fragment;
                    listener.OnMergeEventsCompleted();
                    Log.e(TAG, "calling merges completed");
                }else {
                    Log.e(TAG, "MERGING HASN'T COMPLETED YET");
                    handler.postDelayed(this, 300);
                }
            }
        };
        handler.postDelayed(runnable, 300);

        /* ------------------------------------------------------------------------------------------------
                                    WE MAKE A QUERY TO FIND THE GOOGLE EV IN PARSE,
                                    IF THE EVENT ALREADY EXIST (objects.size != 0),
                                    WE UPDATE THE EXISTING EVENT, ELSE WE CREATE A NEW
                                    EVENT IN DB
         ------------------------------------------------------------------------------------------------ */

        for(int i = 0; i<googleRetrievedEvents.size(); i++){
            int finalI = i;
            EventQueries.queryGoogleEvent(context, googleRetrievedEvents.get(i), new FindCallback<Events>() {
                @Override
                public void done(List<Events> objects, ParseException e) {
                    Log.e(TAG, "google equivalent events found object");
                    if (objects.size() != 0){
                        Log.e(TAG, "objects found: " + objects.toString());
                        compareGoogleAndParseEvents(context, objects.get(0), googleRetrievedEvents.get(finalI));
                    }else {
                        Events ithEvent = googleRetrievedEvents.get(finalI);
                        ithEvent.saveInBackground();
                        mergesCompleted += 1;
                    }
                    return;
                }
            });
        }

    }

    private static void compareGoogleAndParseEvents(Context context, Events ParseGevent, Events GoogleGEvent) {
        if (ParseGevent.getGooglesLastUpdate().compareTo(GoogleGEvent.getGooglesLastUpdate()) == -1) {
            EventQueries.updateEventInDB(context, ParseGevent, GoogleGEvent.getTitle(), GoogleGEvent.getStartDate(), GoogleGEvent.getEndDate(), ParseGevent.hasPublicAccess(), ParseGevent.getRepeat(), ParseGevent.getRepeatUntil(), ParseGevent.getColor(), ParseGevent.getInvitees(), GoogleGEvent.getGooglesLastUpdate(),
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.e("compareGoogleAndParseEvents", "ParseEventUpdated");
                            } else {
                                Log.e("compareGoogleAndParseEvents", "failed updating parse event");
                            }
                            mergesCompleted += 1;
                        }
                    });
        }else {
            mergesCompleted += 1;
        }

    }
}
