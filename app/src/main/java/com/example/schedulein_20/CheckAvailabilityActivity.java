package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.fragments.DayViewFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.parceler.converter.ArrayListParcelConverter;

import java.util.ArrayList;
import java.util.List;

public class CheckAvailabilityActivity extends AppCompatActivity {
    private final String TAG = "CheckAvailabilityActivity";
    ArrayListParcelConverter arrayListParcelConverter;
    ArrayList<ParseUser> selectedInvitees;
    Context context;
    ParseUser currentUser;
    LinearLayout scheduleHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        context = this;
        currentUser = ParseUser.getCurrentUser();

        scheduleHolder = findViewById(R.id.day_view_day_columns_container);

        View currentView = findViewById(android.R.id.content);
        EventQueries.queryDayEvents(context, currentUser, DateTime.currentDate(), queryCurrentUserEventsCallback( currentView ));

        //Events event = Parcels.unwrap(getIntent().getParcelableExtra("Group"));
        //ParseUserExtraAttributes.Ids2ParseUsers(joinedEventGroup.getMembers(), ids2ParseUsersCallback());
        selectedInvitees = Parcels.unwrap(getIntent().getParcelableExtra("selectedInvitees"));

        Log.e(TAG, selectedInvitees.toString());


        /*
        <RelativeLayout
                    android:layout_width="@dimen/day_view_day_column_width"
                    android:layout_height="match_parent"
                    android:id="@+id/day_view_users_day"
                    android:background="@color/white">
                    <TextView
                        android:id="@+id/day_view_column_header_title"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/day_view_header_ofset"
                        android:text="Sunday"
                        android:layout_alignParentTop="true"
                        android:background="@color/secondary"
                        android:gravity="center"></TextView>

                </RelativeLayout>
         */
        if (selectedInvitees != null & selectedInvitees.size() > 0){
            for (ParseUser invitee: selectedInvitees) {
                RelativeLayout inviteeScheduleRL = new RelativeLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.day_view_day_column_width), RelativeLayout.LayoutParams.MATCH_PARENT);
                inviteeScheduleRL.setLayoutParams(params);
                //params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
                TextView columnHeader = new TextView(context);
                RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT , (int) getResources().getDimension(R.dimen.day_view_header_ofset));
                columnHeader.setLayoutParams(textViewParams);
                columnHeader.setText( invitee.getUsername() );
                columnHeader.setBackground( new ColorDrawable(getColor(R.color.emphasis2)));
                columnHeader.setGravity(Gravity.CENTER);

                inviteeScheduleRL.addView(columnHeader);
                scheduleHolder.addView(inviteeScheduleRL);

                EventQueries.queryDayEvents(context, invitee, DateTime.currentDate(), queryInviteesEventsCallback(currentView, inviteeScheduleRL));
            }
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
                if(objects.size() == 0){
                    Toast.makeText(context, getString(R.string.no_events_loaded_this_week), Toast.LENGTH_SHORT).show();
                    return;
                }
                dayEvents.addAll(objects);
                Toast.makeText(context, getString(R.string.events_loaded_succesfully), Toast.LENGTH_SHORT).show();
                CalendarViewsGenerator.generateDayView(view, context, dayEvents, CheckAvailabilityActivity.this, layout );
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
                if(objects.size() == 0){
                    Toast.makeText(context, getString(R.string.no_events_loaded_this_week), Toast.LENGTH_SHORT).show();
                    return;
                }
                dayEvents.addAll(objects);
                Toast.makeText(context, getString(R.string.events_loaded_succesfully), Toast.LENGTH_SHORT).show();
                CalendarViewsGenerator.generateDayView(view, context, dayEvents, CheckAvailabilityActivity.this, null );
            }
        };
    }
}
