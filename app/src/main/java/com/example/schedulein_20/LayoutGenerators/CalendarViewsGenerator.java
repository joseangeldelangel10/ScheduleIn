package com.example.schedulein_20.LayoutGenerators;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.fragments.UserProfileFragment;
import com.example.schedulein_20.models.Events;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class CalendarViewsGenerator {
    public static final int UPDATE_EVENT_REQUEST_CODE = 20;

    public static void generateWeekView(@NonNull View view, Context context, ArrayList<Events> weekEvents, Fragment activity) {
        /* --------------------------------------------------------------------------------
        to generate the week preview we generate a button for each event in eventsList and
        we place the button in the corresponding day column (colums are Relative Layouts)
        at the corresponding height based in the event starting time and ending time
        -------------------------------------------------------------------------------- */
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        Float titleOffset = context.getResources().getDimension(R.dimen.week_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
        Float heightWDuration; // dp height of the event button based in its duration
        Float marginTop; // dp height at which the event button is placed based event starting time
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.week_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)

        for(Events event: weekEvents) {
            RelativeLayout layout = view.findViewById(  Events.dayInt2Layout.get(event.getWeekDay())  ); // we evaluate the corresponding day column using a hash map

            //we make a ratio to calculate button height
            heightWDuration = new Float(RelativeLayoutHeightDP*event.getDurationInMins() );
            heightWDuration = heightWDuration/minsInDay;

            //we make a ratio to calculate margin top
            marginTop = new Float(event.getStartInMins());
            marginTop = marginTop*RelativeLayoutHeightDP;
            marginTop = marginTop/minsInDay;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue() );
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setTextSize(0, 18);

            if( currentUserId.equals(event.getUser().getObjectId()) ) {
                btnTag.setText(event.getTitle());
                // We bind a listener to each button which allows the user to update or delete an event by taping on it
                btnTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String flag = "UpdateDelete";
                        //Activity activity = (Activity) context;
                        Intent intent = new Intent(context, CUeventActivity.class);
                        intent.putExtra("Flag", flag);
                        intent.putExtra("Event", Parcels.wrap(event));
                        activity.startActivityForResult(intent, UPDATE_EVENT_REQUEST_CODE);
                    }
                });
            }else if (event.hasPublicAccess() ){
                btnTag.setText( event.getTitle() );
                btnTag.setClickable(false);
            } else {
                btnTag.setText( context.getResources().getString(R.string.Event) );
                btnTag.setClickable(false);
            }
            // ----------------------------

            layout.addView(btnTag);
        }
    }



    public static void generateDayView(@NonNull View view, Context context, ArrayList<Events> dayEvents, Fragment activity) {
        /* --------------------------------------------------------------------------------
        to generate the week preview we generate a button for each event in eventsList and
        we place the button in the corresponding day column (colums are Relative Layouts)
        at the corresponding height based in the event starting time and ending time
        -------------------------------------------------------------------------------- */
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        Float titleOffset = context.getResources().getDimension(R.dimen.day_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
        Float heightWDuration; // dp height of the event button based in its duration
        Float marginTop; // dp height at which the event button is placed based event starting time
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.day_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)

        for(Events event: dayEvents) {
            // !_!_!_!_!_!_!_! CONSIDER WRITING THIS NEXT LINE OUT OF THE FOR LOOP !_!_!_!_!_!_!_!
            RelativeLayout layout = view.findViewById(  R.id.day_view_users_day  ); // we evaluate the corresponding day column using a hash map

            //we make a ratio to calculate button height
            heightWDuration = new Float(RelativeLayoutHeightDP*event.getDurationInMins() );
            heightWDuration = heightWDuration/minsInDay;

            //we make a ratio to calculate margin top
            marginTop = new Float(event.getStartInMins());
            marginTop = marginTop*RelativeLayoutHeightDP;
            marginTop = marginTop/minsInDay;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue() );
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setTextSize(0, 18);

            if( currentUserId.equals(event.getUser().getObjectId()) ) {
                btnTag.setText(event.getTitle());
                // We bind a listener to each button which allows the user to update or delete an event by taping on it
                btnTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String flag = "UpdateDelete";
                        //Activity activity = (Activity) context;
                        Intent intent = new Intent(context, CUeventActivity.class);
                        intent.putExtra("Flag", flag);
                        intent.putExtra("Event", Parcels.wrap(event));
                        activity.startActivityForResult(intent, UPDATE_EVENT_REQUEST_CODE);
                    }
                });
            }else if (event.hasPublicAccess() ){
                btnTag.setText( event.getTitle() );
                btnTag.setClickable(false);
            } else {
                btnTag.setText( context.getResources().getString(R.string.Event) );
                btnTag.setClickable(false);
            }
            // ----------------------------

            layout.addView(btnTag);
        }
    }


}
