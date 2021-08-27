package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.fragments.DayViewFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.models.TimeSuggestions;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.parceler.converter.ArrayListParcelConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CheckAvailabilityActivity extends AppCompatActivity {
    private final String TAG = "CheckAvailabilityActivity";
    public Context context;
    private ParseUser currentUser;
    //--- views and layouts ---
    View currentView;
    LinearLayout scheduleHolder;
    RelativeLayout userDay;
    TextView title;
    TextView userDayHeader;
    Integer columnWidth;
    ArrayList<Drawable> colors = new ArrayList<>();
    //-- time suggestions data ---
    ArrayList<Integer> suggestedTime;
    ArrayList<ArrayList<Events>> inviteesSchedules;
    //--- new event data ---
    private Date eventStartDate;
    private Date eventEndDate;
    private ArrayList<ParseUser> selectedInvitees;
    public Events eventPreview;
    //--- flags ---
    String flag;
    Boolean suggestionsGenerated;
    Integer callbacksCompleted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        colors.add( new ColorDrawable( getColor(R.color.primary)) );
        colors.add( new ColorDrawable( getColor(R.color.secondary)) );
        colors.add( new ColorDrawable( getColor(R.color.emphasis1)) );
        colors.add( new ColorDrawable( getColor(R.color.emphasis2)) );

        /* ----------------------------------------------------------------------------------------------------
                                        WE ASSIGN VALUES TO MAIN VARS
         ---------------------------------------------------------------------------------------------------- */
        context = this;
        currentUser = ParseUser.getCurrentUser();
        flag = getIntent().getStringExtra("Flag");
        selectedInvitees = Parcels.unwrap(getIntent().getParcelableExtra("selectedInvitees"));
        eventStartDate = new Date(getIntent().getLongExtra("eventStartDate", DateTime.currentDate().getTime()));
        eventEndDate = new Date(getIntent().getLongExtra("eventEndDate", DateTime.currentDate().getTime()));
        inviteesSchedules = new ArrayList<>();
        suggestionsGenerated = false;
        callbacksCompleted = 0;
        currentView = findViewById(android.R.id.content);

        if (selectedInvitees.size() < 2){
            columnWidth = (int) getResources().getDimension(R.dimen.check_availability_column_width_large);
        }else {
            columnWidth = (int) getResources().getDimension(R.dimen.check_availability_column_width_small);
        }

        /* ----------------------------------------------------------------------------------------------------
                                        VIEW REFERENCING
         ---------------------------------------------------------------------------------------------------- */
        title = findViewById(R.id.check_availability_title);
        scheduleHolder = findViewById(R.id.day_view_day_columns_container);
        userDay = findViewById(R.id.day_view_users_day);
        userDayHeader = findViewById(R.id.day_view_column_header_title);
        title.setText("Availability for your invitees on " + DateTime.onlyDate(eventStartDate));
        /* ----------------------------------------------------------------------------------------------------
                                        WE GENERATE EVENT PREVIEW
         ---------------------------------------------------------------------------------------------------- */

        eventPreview = new Events();
        eventPreview.setStartDate(eventStartDate);
        eventPreview.setEndDate(eventEndDate);
        eventPreview.setTitle("new event");

        /* ----------------------------------------------------------------------------------------------------
                                        WE SET UP USER DAY VIEW (DEFAULT)
         ---------------------------------------------------------------------------------------------------- */
        userDayHeader.setText("Your day");
        LinearLayout.LayoutParams userDayParams = new LinearLayout.LayoutParams(columnWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        userDay.setLayoutParams(userDayParams);
        EventQueries.queryDayEvents(context, currentUser, eventStartDate, queryCurrentUserEventsCallback( currentView ));

        /* ----------------------------------------------------------------------------------------------------
                                        WE CREATE A NEW DAY VIEW FOR EACH INVITEE
         ---------------------------------------------------------------------------------------------------- */

        if (selectedInvitees != null & selectedInvitees.size() > 0){
            for (ParseUser invitee: selectedInvitees) {
                RelativeLayout inviteeScheduleRL = new RelativeLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( columnWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
                inviteeScheduleRL.setLayoutParams(params);
                inviteeScheduleRL.setBackground( new ColorDrawable(getColor(R.color.white)));

                TextView columnHeader = new TextView(context);
                RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT , (int) getResources().getDimension(R.dimen.day_view_header_ofset));
                columnHeader.setLayoutParams(textViewParams);
                columnHeader.setText( invitee.getString("name") + " " + invitee.getString("surname") );
                Random rand = new Random();
                columnHeader.setBackground( colors.get(rand.nextInt(colors.size())) );
                columnHeader.setGravity(Gravity.CENTER);

                View divider = new View(context);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams( 2, RelativeLayout.LayoutParams.MATCH_PARENT);
                divider.setLayoutParams(dividerParams);
                divider.setBackground(new ColorDrawable(getColor(R.color.gray)));

                scheduleHolder.addView(divider);
                inviteeScheduleRL.addView(columnHeader);
                scheduleHolder.addView(inviteeScheduleRL);

                EventQueries.queryDayEvents(context, invitee, eventStartDate, queryInviteesEventsCallback(currentView, inviteeScheduleRL));
            }
        }

        /* ----------------------------------------------------------------------------------------------------
                                        WE GENERATE TIME SUGGESTIONS
         ---------------------------------------------------------------------------------------------------- */

        generateTimeSuggestions();

    }

    private void generateTimeSuggestions() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(callbacksCompleted == selectedInvitees.size()+1 && !suggestionsGenerated){
                    Log.i(TAG, "generating time suggestions");
                    TimeSuggestions ts = new TimeSuggestions(inviteesSchedules, eventPreview.getDurationInMins());
                    suggestedTime = ts.generateSuggestions();
                    plotSuggestedTime();
                    suggestionsGenerated = true;
                }else {
                    Log.e(TAG, "suggestions conditions haven't met");
                    Log.e(TAG, "callbacks: " + callbacksCompleted);
                    Log.e(TAG, "suggestions: " + suggestionsGenerated);
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void plotSuggestedTime() {
        if (suggestedTime.size() != 0) {
            Float titleOffset = context.getResources().getDimension(R.dimen.day_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
            Float heightWDuration; // dp height of the event button based in its duration
            Float marginTop; // dp height at which the event button is placed based event starting time
            Float minsInDay = new Float(24 * 60);
            Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.day_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)
            Float viewDividersOffset = context.getResources().getDimension(R.dimen.day_view_hour_divider);

            //we make a ratio to calculate button height
            Float suggestedTimeDuration = Float.valueOf(suggestedTime.get(1) - suggestedTime.get(0));
            heightWDuration = new Float(RelativeLayoutHeightDP * suggestedTimeDuration);
            heightWDuration = heightWDuration / minsInDay;
            heightWDuration += (suggestedTimeDuration/60)*viewDividersOffset;

            //we make a ratio to calculate margin top
            Float suggestedTimeStart = Float.valueOf(suggestedTime.get(0));
            marginTop = suggestedTimeStart * RelativeLayoutHeightDP;
            marginTop = marginTop / minsInDay;
            marginTop += (suggestedTimeStart/60)*viewDividersOffset;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue());
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            //btnTag.setTextSize(0, 28);
            btnTag.setBackground(getDrawable(R.drawable.custom_border));

            //Log.e(TAG, "title: " + event.getTitle() + " duration: " + event.getDurationInMins());

            btnTag.setText("suggested");
            btnTag.setTextColor(getColor(R.color.primary_green));
            btnTag.setClickable(false);
            // ----------------------------

            userDay.addView(btnTag);
        }
    }

    private FindCallback queryInviteesEventsCallback(View view, RelativeLayout layout) {
        ArrayList<Events> dayEvents = new ArrayList<>();
        return new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if(e!=null){
                    Toast.makeText(context, R.string.loading_events_problem, Toast.LENGTH_SHORT).show();
                    return;
                }
                dayEvents.addAll(objects);
                CalendarViewsGenerator.generateDayView(view, context, dayEvents, null, layout );
                if(flag.equals("Create") || flag.equals("CreateJoinedEvent")) {
                    CalendarViewsGenerator.generateNewEventPreview(currentView, context, eventPreview, dayEvents, CheckAvailabilityActivity.this, layout);
                }
                inviteesSchedules.add(dayEvents);
                callbacksCompleted++;
            }
        };
    }

    private FindCallback queryCurrentUserEventsCallback(View view) {
        ArrayList<Events> dayEvents = new ArrayList<>();
        return new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if(e!=null){
                    Toast.makeText(context, R.string.loading_events_problem, Toast.LENGTH_SHORT).show();
                    return;
                }

                dayEvents.addAll(objects);
                CalendarViewsGenerator.generateDayView(view, context, dayEvents, null, null );
                if(flag.equals("Create") || flag.equals("CreateJoinedEvent")) {
                    CalendarViewsGenerator.generateNewEventPreview(currentView, context, eventPreview, dayEvents, CheckAvailabilityActivity.this, userDay);
                }
                inviteesSchedules.add(dayEvents);
                callbacksCompleted++;
            }
        };
    }
}
