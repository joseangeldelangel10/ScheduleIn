package com.example.schedulein_20.LayoutGenerators;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
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
import com.example.schedulein_20.models.TimeSuggestions;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class CalendarViewsGenerator {
    public static final int UPDATE_EVENT_REQUEST_CODE = 20;

    public static void generateWeekView(@NonNull View view, Context context, ArrayList<Events> weekEvents, Fragment activity) {
        /* --------------------------------------------------------------------------------
        to generate the week view we generate a button for each event in eventsList and
        we place the button in the corresponding day column (colums are Relative Layouts)
        at the corresponding height based in the event starting time and ending time
        -------------------------------------------------------------------------------- */
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        Float titleOffset = context.getResources().getDimension(R.dimen.week_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
        Float heightWDuration; // dp height of the event button based in its duration
        Float marginTop; // dp height at which the event button is placed based event starting time
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.week_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)
        Float viewDividersOffset = context.getResources().getDimension(R.dimen.week_view_hour_divider);

        for(Events event: weekEvents) {
            RelativeLayout layout = view.findViewById(  Events.dayInt2Layout.get(event.getWeekDay())  ); // we evaluate the corresponding day column using a hash map

            //we make a ratio to calculate button height
            Float eventDurationMins = Float.valueOf(event.getDurationInMins());
            heightWDuration = new Float(RelativeLayoutHeightDP*eventDurationMins );
            heightWDuration = heightWDuration/minsInDay;
            heightWDuration += (eventDurationMins/60)*viewDividersOffset;

            //we make a ratio to calculate margin top
            Float startInMins = new Float(event.getStartInMins());
            marginTop = startInMins*RelativeLayoutHeightDP;
            marginTop = marginTop/minsInDay;
            marginTop += (startInMins/60)*viewDividersOffset;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue() );
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setTextSize(0, 25);

            if (event.getColor() != 0 ){
                btnTag.setBackgroundColor(event.getColor());
            }

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
            }else if( event.getInvitees().contains(currentUserId) ){
                btnTag.setText(event.getTitle());

                // We bind a listener to each button which allows the user to update or delete an event by taping on it
                btnTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String flag = "InviteeView";
                        Intent intent = new Intent(context, CUeventActivity.class);
                        intent.putExtra("Flag", flag);
                        intent.putExtra("Event", Parcels.wrap(event));
                        activity.startActivityForResult(intent, UPDATE_EVENT_REQUEST_CODE);
                    }
                });
            }else if ( event.hasPublicAccess() ){
                btnTag.setText( event.getTitle() );
                btnTag.setClickable(false);
            } else {
                btnTag.setBackgroundColor(context.getColor(R.color.gray));
                btnTag.setText( context.getResources().getString(R.string.Event) );
                btnTag.setClickable(false);
            }
            // ----------------------------

            layout.addView(btnTag);
        }
    }



    public static void generateDayView(@NonNull View view, Context context, ArrayList<Events> dayEvents, Fragment activity, RelativeLayout layout) {
        /* --------------------------------------------------------------------------------
        to generate the day view we generate a button for each event in eventsList and
        we place the button in the corresponding day column (colums are Relative Layouts)
        at the corresponding height based in the event starting time and ending time
        -------------------------------------------------------------------------------- */
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        Float titleOffset = context.getResources().getDimension(R.dimen.day_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
        Float heightWDuration; // dp height of the event button based in its duration
        Float marginTop; // dp height at which the event button is placed based event starting time
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.day_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)
        Float viewDividersOffset = context.getResources().getDimension(R.dimen.day_view_hour_divider);

        if (layout == null){
            layout = view.findViewById(  R.id.day_view_users_day  );
        }

        for(Events event: dayEvents) {
            //ArrayList<Float> widthAndMargin = new ArrayList<>();
            //widthAndMargin = generateWidthAndMargin(context, event, dayEvents, context.getResources().getDimension(R.dimen.day_view_day_column_width));

            //Float columnWidth = widthAndMargin.get(0);
            //Float marginLeft = widthAndMargin.get(1);
            Float columnWidth = Float.valueOf(RelativeLayout.LayoutParams.MATCH_PARENT);
            Float marginLeft = Float.valueOf(0);

            //we make a ratio to calculate button height
            Float eventDurationMins = Float.valueOf(event.getDurationInMins());
            heightWDuration = RelativeLayoutHeightDP*eventDurationMins ;
            heightWDuration = heightWDuration/minsInDay;
            heightWDuration += (eventDurationMins/60)*viewDividersOffset;

            //we make a ratio to calculate margin top
            Float eventStartMins = Float.valueOf(event.getStartInMins());
            marginTop = eventStartMins*RelativeLayoutHeightDP;
            marginTop = marginTop/minsInDay;
            marginTop += (eventStartMins/60)*viewDividersOffset;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( columnWidth.intValue(), heightWDuration.intValue() );
            params.setMargins(marginLeft.intValue(), marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setTextSize(0, 28);
            if (event.getColor() != 0){
                btnTag.setBackgroundColor(event.getColor());
            }

            if( currentUserId.equals(event.getUser().getObjectId()) ) {
                btnTag.setText(event.getTitle());
                // We bind a listener to each button which allows the user to update or delete an event by taping on it

                if (activity != null) {
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
                }
            }else if( event.getInvitees().contains(currentUserId) ){
                btnTag.setText(event.getTitle());
                // We bind a listener to each button which allows the user to update or delete an event by taping on it
                if (activity != null) {
                    btnTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String flag = "InviteeView";
                            Intent intent = new Intent(context, CUeventActivity.class);
                            intent.putExtra("Flag", flag);
                            intent.putExtra("Event", Parcels.wrap(event));
                            activity.startActivityForResult(intent, UPDATE_EVENT_REQUEST_CODE);
                        }
                    });
                }
            }else if (event.hasPublicAccess() ){
                btnTag.setText( event.getTitle() );
                btnTag.setClickable(false);
            } else {
                btnTag.setBackgroundColor(context.getColor(R.color.gray));
                btnTag.setText( context.getResources().getString(R.string.Event) );
                btnTag.setClickable(false);
            }
            // ----------------------------

            layout.addView(btnTag);
        }
    }

    private static ArrayList<Float> generateWidthAndMargin(Context context, Events event, ArrayList<Events> dayEvents, Float defaultWidth) {
        Float columnWidth = defaultWidth;
        Float marginLeft = Float.valueOf(0);

        /*
        -1 -> error
        0 -> Above unrelated
        1 -> Below unrelated
        2 -> Above cross
        3 -> Below cross
        4 -> ev2 inside ev1
        5 -> ev1 inside ev2
         */

        for (Events otherEvent:dayEvents){
            int relation = TimeSuggestions.evaluateCrossings(event, otherEvent);
            if (event != otherEvent){
                if(relation == 2 || relation == 5){
                    columnWidth = columnWidth/2;
                    marginLeft += columnWidth;
                }else if(relation == 3 || relation == 4){
                    columnWidth = columnWidth/2;
                }
            }
        }

        ArrayList<Float> res = new ArrayList<>();
        res.add(columnWidth);
        res.add(marginLeft);
        return res;


    }


    public static void generateNewEventPreview(@NonNull View view, Context context, Events event, ArrayList<Events> dayEvents, Activity activity, RelativeLayout layout) {

        if (event != null) {
            Float titleOffset = context.getResources().getDimension(R.dimen.day_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
            Float heightWDuration; // dp height of the event button based in its duration
            Float marginTop; // dp height at which the event button is placed based event starting time
            Float minsInDay = new Float(24 * 60);
            Float RelativeLayoutHeightDP = context.getResources().getDimension(R.dimen.day_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)
            Float viewDividersOffset = context.getResources().getDimension(R.dimen.day_view_hour_divider);


            //we make a ratio to calculate button height
            Float eventDurationMins = Float.valueOf(event.getDurationInMins());
            heightWDuration = RelativeLayoutHeightDP * eventDurationMins;
            heightWDuration = heightWDuration / minsInDay;
            heightWDuration += (eventDurationMins/60)*viewDividersOffset;

            //we make a ratio to calculate margin top
            Float eventStartInMins = Float.valueOf(event.getStartInMins());
            marginTop = eventStartInMins * RelativeLayoutHeightDP;
            marginTop = marginTop / minsInDay;
            marginTop += (eventStartInMins/60)*viewDividersOffset;

            //we set the properties for the button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue());
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setTextSize(0, 28);
            btnTag.setText(event.getTitle());
            btnTag.setClickable(false);

            // --------------- WE CHECK IF EVENT CROSSES WITH ANOTHER EVENT ------------
            boolean crossesWithOtherEv = false;
            for(Events dayEvent:dayEvents){
                if(TimeSuggestions.compareForCrossings(event, dayEvent)){
                    crossesWithOtherEv = true;
                    break;
                }
            }
            if(crossesWithOtherEv){
                btnTag.setBackground(new ColorDrawable(activity.getColor(R.color.red_transparent)));
            }else {
                btnTag.setBackground(new ColorDrawable(activity.getColor(R.color.emphasis1_transparent)));
            }
            // ----------------------------

            layout.addView(btnTag);
        }
    }
}
