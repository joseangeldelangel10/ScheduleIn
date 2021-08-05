package com.example.schedulein_20.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.schedulein_20.GoogleCalendarClient;
import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.R;
import com.example.schedulein_20.RESTclientOpetations.MergingDiffCalendarsEvents;
import com.example.schedulein_20.ScheduleInGCalendarAPIApp;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarViewFragment extends Fragment {
    private final String TAG = "CalendarViewFragment";
    Context context;
    ParseUser currentUser;
    List<Events> weekEvents;
    ArrayList<Events> googleWeekEvents;
    GoogleCalendarClient gCalendarClient;
    String primaryGCalendarId;
    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarView.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarViewFragment newInstance(String param1, String param2) {
        CalendarViewFragment fragment = new CalendarViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        context = getContext();
        currentUser = ParseUser.getCurrentUser();
        weekEvents = new ArrayList<>();
        googleWeekEvents = new ArrayList<>();
        gCalendarClient = ScheduleInGCalendarAPIApp.getRestClient(context);
        primaryGCalendarId = null;

        FindCallback onWeekEventsFound = weekEventsCallback(view);
        EventQueries.queryWeekEvents(context, currentUser, onWeekEventsFound);
    }

    private FindCallback<Events> weekEventsCallback(View view){
        return new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if(e!=null){
                    Toast.makeText(context, R.string.loading_events_problem, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(objects.size() == 0){
                    Toast.makeText(context, getString(R.string.no_events_loaded_this_week), Toast.LENGTH_SHORT).show();
                    return;
                }
                weekEvents.addAll(objects);
                Toast.makeText(context, getString(R.string.events_loaded_succesfully), Toast.LENGTH_SHORT).show();
                CalendarViewsGenerator.generateWeekView(view, context, (ArrayList<Events>) weekEvents, CalendarViewFragment.this);
            }
        };
    }


}
