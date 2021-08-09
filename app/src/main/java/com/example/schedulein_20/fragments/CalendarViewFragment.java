package com.example.schedulein_20.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    View view;
    ImageButton backArrow;
    TextView weekRangeTv;
    ImageButton forwardArrow;
    public Date displayedStartDate;
    public Date displayedEndDate;

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

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarView.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarViewFragment newInstance(Date weekStart, Date weekEnding) {
        CalendarViewFragment fragment = new CalendarViewFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.displayedStartDate = weekStart;
        fragment.displayedEndDate = weekEnding;
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
        //displayedStartDate = DateTime.weekStart();
        //displayedEndDate = DateTime.weekEnding();

        backArrow = view.findViewById(R.id.weekViewFragmentLastWeekBt);
        weekRangeTv = view.findViewById(R.id.weekViewFragmentWeekRangeTv);
        forwardArrow = view.findViewById(R.id.weekViewFragmentNextWeekBt);

        /*FindCallback onWeekEventsFound = weekEventsCallback(view);
        EventQueries.queryWeekEvents(context, currentUser, displayedStartDate, displayedEndDate, onWeekEventsFound);*/
        //writeWeekRange(displayedStartDate, displayedEndDate);

        for(int i = 0; i<7; i++){
            Date dayOfWeek = getDayOfWeek(i);
            FindCallback onDayEventsFound = dayEventsCallback(view, i);
            EventQueries.queryDayEvents(context, currentUser, dayOfWeek, onDayEventsFound);
        }
        writeWeekRange(displayedStartDate, displayedEndDate);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Date> newRange = computeLastWeekRange();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, CalendarViewFragment.newInstance(newRange.get(0), newRange.get(1)))
                        .commit();
            }
        });

        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Date> newRange = computeNextWeekRange();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, CalendarViewFragment.newInstance(newRange.get(0), newRange.get(1)))
                        .commit();
            }
        });

    }

    private FindCallback dayEventsCallback(View view, int i) {
        ArrayList<Events> dayEvents = new ArrayList<>();
        return new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if(i == 6){
                    Toast.makeText(context, R.string.events_loaded_succesfully, Toast.LENGTH_SHORT).show();
                }
                if(e!=null){
                    Toast.makeText(context, R.string.loading_events_problem, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(objects.size() == 0){
                    return;
                }
                dayEvents.addAll(objects);
                CalendarViewsGenerator.generateDayView(view, context, dayEvents,CalendarViewFragment.this, view.findViewById(Events.dayInt2Layout.get(i)) );
            }
        };
    }

    private Date getDayOfWeek(int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(displayedStartDate);
        c.add(Calendar.DATE, i);
        Date result = c.getTime();
        return result;
    }

    private ArrayList<Date> computeLastWeekRange(){
        ArrayList<Date> result = new ArrayList<>();
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();

        cStart.setTime(displayedStartDate);
        cStart.add(Calendar.DATE, -7);
        result.add(cStart.getTime());

        cEnd.setTime(displayedEndDate);
        cEnd.add(Calendar.DATE, -7);
        result.add(cEnd.getTime());

        return result;
    }

    private ArrayList<Date> computeNextWeekRange(){
        ArrayList<Date> result = new ArrayList<>();
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();

        cStart.setTime(displayedStartDate);
        cStart.add(Calendar.DATE, 7);
        result.add(cStart.getTime());


        cEnd.setTime(displayedEndDate);
        cEnd.add(Calendar.DATE, 7);
        result.add(cEnd.getTime());
        return result;
    }

    private void writeWeekRange(Date weekStart, Date weekEnding) {
        Calendar c = Calendar.getInstance();
        c.setTime(weekEnding);
        c.add(Calendar.DATE, -1);
        Date userWeekEnding = c.getTime();

        weekRangeTv.setText(DateTime.onlyDate(weekStart) + " - " + DateTime.onlyDate(userWeekEnding));
    }


}
