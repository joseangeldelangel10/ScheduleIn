package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.fragments.CalendarDialogFragment;
import com.example.schedulein_20.fragments.HourDialogFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CUeventActivity extends AppCompatActivity implements CalendarDialogFragment.EditCalendarDateListener, HourDialogFragment.EditTimeListener {
    private final String TAG = "CreateEventAct";
    public static final String FLAG_EDIT_START_DATE = "ChangeStartDate";
    public static final String FLAG_EDIT_END_DATE = "ChangeEndDate";
    public static final String FLAG_EDIT_START_TIME = "ChangeStartTime";
    public static final String FLAG_EDIT_END_TIME = "ChangeEndTime";
    EditText etTitle;
    TextView tvStartDate;
    TextView tvStartTime;
    TextView tvEndDate;
    TextView tvEndTime;
    LinearLayout UDeventBt;
    Button btCreateEv;
    Button btUpdateEv;
    Button btDeleteEv;
    Date startDate;
    Date endDate;
    String eventTitle;
    Events event2update;
    Group joinedEventGroup;
    RecyclerView rvInvitees;
    List<ParseUser> possibleInvitees;
    List<ParseUser> selectedInvitees;
    GroupMembersSearchAdapter adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cu_event);
        context = this;
        possibleInvitees = new ArrayList<>();
        selectedInvitees = new ArrayList<>();
        /* ----------------------------------------------------------------------------------------
                                    CREATING TOOLBAR
      ---------------------------------------------------------------------------------------- */

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ----------------------------------------------------------------------------------------
                                    REFERENCING WINDOW VIEWS
      ---------------------------------------------------------------------------------------- */

        etTitle = findViewById(R.id.etCUeventTitle);
        tvStartDate = findViewById(R.id.CUeventStartDateTv);
        tvStartTime = findViewById(R.id.CUeventStartTimeTv);
        tvEndDate = findViewById(R.id.CUeventEndDateTv);
        tvEndTime = findViewById(R.id.CUeventEndTimeTv);
        rvInvitees = findViewById(R.id.CUeventInviteesRv);
        btCreateEv = findViewById(R.id.CUeventsCUFinalButt);
        btDeleteEv = findViewById(R.id.CUeventsDeleteEventBt);
        btUpdateEv = findViewById(R.id.CUeventsUpdateEventBt);
        UDeventBt = findViewById(R.id.CUeventsUDLinearLayout);

        /* ----------------------------------------------------------------------------------------
                            CALCULATING CURRENT DATE TO SET DEFAULT DATE FOR EVENT
      ---------------------------------------------------------------------------------------- */
        startDate = DateTime.currentDate();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR,2);
        endDate = c.getTime();

        Log.e(TAG,"start: "  + startDate.toString());
        Log.e(TAG,"end: "  + endDate.toString());

        updateDatesText();

        /* ----------------------------------------------------------------------------------------
                            SETTING TV ONCLICK LISTENERS FOR DATE EDIT
      ---------------------------------------------------------------------------------------- */

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDateDialog(FLAG_EDIT_START_DATE);
            }
        });

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTimeDialog(FLAG_EDIT_START_TIME);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDateDialog(FLAG_EDIT_END_DATE);
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTimeDialog(FLAG_EDIT_END_TIME);
            }
        });

        /* ----------------------------------------------------------------------------------------
                            SETTING ONCLICK LISTENER TO CREATE EVENT
      ---------------------------------------------------------------------------------------- */

        btCreateEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = etTitle.getText().toString();
                createEventInDB();
            }
        });

        btUpdateEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = etTitle.getText().toString();
                updateEventInDB(event2update);
            }
        });

        btDeleteEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventInDB(event2update);
            }
        });

        /* ----------------------------------------------------------------------------------------
                                             SET UP RV
      ---------------------------------------------------------------------------------------- */
        adapter = new GroupMembersSearchAdapter(context, possibleInvitees, selectedInvitees);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvInvitees.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvInvitees.setAdapter(adapter);

        /* ----------------------------------------------------------------------------------------
                            MODIFY BEHAVIOUR TO C-U-D EVENTS
      ---------------------------------------------------------------------------------------- */

        String flag = (String) getIntent().getExtras().get("Flag");

        if(flag.equals("Create")){
            UDeventBt.setVisibility(View.INVISIBLE);
            UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
        }else if (flag.equals("UpdateDelete")){
            event2update = Parcels.unwrap(getIntent().getParcelableExtra("Event"));
            etTitle.setText(event2update.getTitle());
            startDate = event2update.getStartDate();
            endDate = event2update.getEndDate();
            updateDatesText();
            queryInvitees(event2update);
            btCreateEv.setVisibility(View.INVISIBLE);
            btCreateEv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
        }else if (flag.equals("CreateJoinedEvent")){
            joinedEventGroup = Parcels.unwrap(getIntent().getParcelableExtra("Group"));
            queryGroupMembers(joinedEventGroup.getMembers());
            UDeventBt.setVisibility(View.INVISIBLE);
            UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
        }
    }

    public void cleanRv(){
        possibleInvitees.clear();
        possibleInvitees.addAll(selectedInvitees);
        adapter.notifyDataSetChanged();
    }

    private void queryGroupMembers(ArrayList<String> membersIds) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", membersIds);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "there was a problem retrieving invitees", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedInvitees.addAll(objects);
                cleanRv();
            }
        });

    }

    private void queryInvitees(Events event2update) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", event2update.getInvitees());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "there was a problem retrieving invitees", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedInvitees.addAll(objects);
                cleanRv();
            }
        });

    }

    private void deleteEventInDB(Events event2update) {
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        query.getInBackground(event2update.getObjectId(), (object, e) -> {
            if (e == null) {
                // Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        //Something went wrong while deleting the Object
                        Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEventInDB(Events event2update) {
            ParseQuery<Events> query = ParseQuery.getQuery(Events.class);

            // Retrieve the object by id
            query.getInBackground(event2update.getObjectId(), (object, e) -> {
                if (e == null) {
                    // Update the fields we want to
                    object.put(Events.KEY_START_DATE, startDate);
                    object.put(Events.KEY_END_DATE, endDate);
                    object.put(Events.KEY_TITLE, eventTitle);

                    // All other fields will remain the same
                    object.saveInBackground();
                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    // something went wrong
                    Toast.makeText(this, "Failed when updating event", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateDatesText() {
        tvStartDate.setText(DateTime.onlyDate( startDate ));
        tvEndDate.setText(DateTime.onlyDate( endDate ));
        tvStartTime.setText( DateTime.onlyTime(startDate) );
        tvEndTime.setText( DateTime.onlyTime(endDate) );
    }

    private void createEventInDB() {
        Events event = new Events();
        event.setUser(ParseUser.getCurrentUser());
        event.setTitle(eventTitle);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setInvitees(Group.parseUsers2Ids((ArrayList<ParseUser>) selectedInvitees));

        if (startDate.compareTo(endDate) == 1) {
            Toast.makeText(CUeventActivity.this, "starting date cannot be after end", Toast.LENGTH_LONG).show();
            Toast.makeText(CUeventActivity.this, "Please change the dates", Toast.LENGTH_SHORT).show();
            return;
        }

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "event saving failed!\n" + e);
                    Toast.makeText(CUeventActivity.this, "Saving failed try again", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CUeventActivity.this, "Make sure your event has a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "saving succeded");
                Toast.makeText(CUeventActivity.this, "Saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showEditTimeDialog(String flag) {
        FragmentManager fm = getSupportFragmentManager();
        if(flag.equals(FLAG_EDIT_START_TIME)) {
            HourDialogFragment hourDialogFragment = HourDialogFragment.newInstance(FLAG_EDIT_START_TIME);
            hourDialogFragment.show(fm, "fragment_edit_time");
        }else if(flag.equals(FLAG_EDIT_END_TIME)){
            HourDialogFragment hourDialogFragment = HourDialogFragment.newInstance(FLAG_EDIT_END_TIME);
            hourDialogFragment.show(fm, "fragment_edit_time");
        }
    }

    private void showEditDateDialog(String flag) {
        FragmentManager fm = getSupportFragmentManager();

        if (flag.equals(FLAG_EDIT_START_DATE)){
            CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.newInstance(FLAG_EDIT_START_DATE);
            calendarDialogFragment.show(fm, "fragment_edit_date");
        }else if(flag.equals(FLAG_EDIT_END_DATE)){
            CalendarDialogFragment calendarDialogFragment = CalendarDialogFragment.newInstance(FLAG_EDIT_END_DATE);
            calendarDialogFragment.show(fm, "fragment_edit_date");
        }
    }

    /* ---------------------------------------------------------------
     GET NEW DAY-MONTH-YEAR BIND IT TO START DATE AND UPDATE TEXT
    ------------------------------------------------------------------*/
    @Override
    public void onFinishStartDateEdit(Calendar selectedDate) {

        startDate.setYear(selectedDate.get(Calendar.YEAR) - 1900);
        startDate.setMonth(selectedDate.get(Calendar.MONTH));
        startDate.setDate(selectedDate.get(Calendar.DAY_OF_MONTH));

        //tvStartDate.setText(DateTime.onlyDate(startDate));
        updateDatesText();
    }

    @Override
    public void onFinishEndDateEdit(Calendar selectedDate) {
        endDate.setYear(selectedDate.get(Calendar.YEAR) - 1900);
        endDate.setMonth(selectedDate.get(Calendar.MONTH));
        endDate.setDate(selectedDate.get(Calendar.DAY_OF_MONTH));

        //tvEndDate.setText(DateTime.onlyDate(endDate));
        updateDatesText();
    }

    /* ---------------------------------------------------------------
     GET NEW HOUR-MIN-SEC BIND IT TO START DATE AND UPDATE TEXT
    ------------------------------------------------------------------*/
    @Override
    public void onFinishStartTimeEdit(Calendar calendar) {
        startDate.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        startDate.setMinutes(calendar.get(Calendar.MINUTE));
        startDate.setSeconds(calendar.get(Calendar.SECOND));

        //tvStartTime.setText(DateTime.onlyTime(startDate));
        updateDatesText();
    }

    @Override
    public void onFinishEndTimeEdit(Calendar calendar) {
        endDate.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        endDate.setMinutes(calendar.get(Calendar.MINUTE));
        endDate.setSeconds(calendar.get(Calendar.SECOND));

        //tvEndTime.setText(DateTime.onlyTime(endDate));
        updateDatesText();
    }


}
