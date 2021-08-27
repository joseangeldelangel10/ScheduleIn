package com.example.schedulein_20.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.github.mmin18.widget.RealtimeBlurView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayViewFragment extends Fragment {
    private final String TAG = "DayViewFragment";
    Context context;
    ParseUser currentUser;
    ArrayList<Events> dayEvents;
    ImageButton backArrow;
    TextView tvDay;
    ImageButton forwardArrow;
    TextView dayHeaderTitle;
    public Date displayedDate;
    //RealtimeBlurView bluredMargin;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DayViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @return A new instance of fragment DayViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayViewFragment newInstance(Date param1) {
        DayViewFragment fragment = new DayViewFragment();
        Bundle args = new Bundle();
        fragment.displayedDate = param1;
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
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
        return inflater.inflate(R.layout.fragment_day_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        currentUser = ParseUser.getCurrentUser();
        //displayedDate = DateTime.currentDate();
        dayEvents = new ArrayList<>();

        backArrow = view.findViewById(R.id.day_view_rangeBackArrow);
        tvDay = view.findViewById(R.id.day_view_rangeTv);
        forwardArrow = view.findViewById(R.id.day_view_rangeForwardArrow);
        dayHeaderTitle = view.findViewById(R.id.day_view_column_header_title);
        tvDay.setText(DateTime.onlyDate(displayedDate));
        dayHeaderTitle.setText(DateTime.onlyDate(displayedDate));

        FindCallback onDayEventsFound = dayEventsCallback(view);
        EventQueries.queryDayEvents(context, currentUser, displayedDate, onDayEventsFound);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date newRange = computeLastDayRange();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, DayViewFragment.newInstance(newRange))
                        .commit();
            }
        });

        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date newRange = computeNextDayRange();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, DayViewFragment.newInstance(newRange))
                        .commit();
            }
        });

    }


    private Date computeLastDayRange(){
        Calendar c = Calendar.getInstance();
        c.setTime(displayedDate);
        c.add(Calendar.DATE, -1);
        Date result = c.getTime();

        return result;
    }

    private Date computeNextDayRange(){
        Calendar c = Calendar.getInstance();
        c.setTime(displayedDate);
        c.add(Calendar.DATE, 1);
        Date result = c.getTime();

        return result;
    }

    private FindCallback<Events> dayEventsCallback(View view){
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
                dayEvents.clear();
                dayEvents.addAll(objects);
                Toast.makeText(context, getString(R.string.events_loaded_succesfully), Toast.LENGTH_SHORT).show();
                CalendarViewsGenerator.generateDayView(view, context, dayEvents, DayViewFragment.this, null );
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "request code: " +  String.valueOf(requestCode) + "result code: " + String.valueOf(resultCode));
        if (requestCode == CalendarViewsGenerator.UPDATE_EVENT_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                // WE CREATE A NEW FRAGMENT TO SHOW THE USER THE NEW EVENT HE HAS CREATED, DELETED OR UPDATED
                // GIVING USER INSTANT RESPONSE
                Fragment fragment = new DayViewFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, fragment)
                        .commit();
            }
        }
    }


}

