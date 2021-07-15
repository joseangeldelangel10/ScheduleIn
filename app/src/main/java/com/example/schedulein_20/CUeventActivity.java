package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.fragments.CalendarDialogFragment;
import com.example.schedulein_20.fragments.HourDialogFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;

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
    Button btCreateEv;
    Date startDate;
    Date endDate;
    String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cu_event);
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
        btCreateEv = findViewById(R.id.CUeventsCUFinalButt);

        /* ----------------------------------------------------------------------------------------
                            CALCULATING CURRENT DATE TO SET DEFAULT DATE FOR EVENT
      ---------------------------------------------------------------------------------------- */
        startDate = DateTime.currentDate();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR,2);
        endDate = c.getTime();

        Log.e(TAG,"start: "  + startDate.toString());
        Log.e(TAG,"end: "  + endDate.toString());

        tvStartDate.setText(DateTime.onlyDate( startDate ));
        tvEndDate.setText(DateTime.onlyDate( endDate ));
        tvStartTime.setText( DateTime.onlyTime(startDate) );
        tvEndTime.setText( DateTime.onlyTime(endDate) );

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
    }

    private void createEventInDB() {
        Events event = new Events();
        event.setUser(ParseUser.getCurrentUser());
        event.setTitle(eventTitle);
        event.setStartDate(startDate);
        event.setEndDate(endDate);

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

        tvStartDate.setText(DateTime.onlyDate(startDate));
    }

    @Override
    public void onFinishEndDateEdit(Calendar selectedDate) {
        endDate.setYear(selectedDate.get(Calendar.YEAR) - 1900);
        endDate.setMonth(selectedDate.get(Calendar.MONTH));
        endDate.setDate(selectedDate.get(Calendar.DAY_OF_MONTH));

        tvEndDate.setText(DateTime.onlyDate(endDate));
    }

    /* ---------------------------------------------------------------
     GET NEW HOUR-MIN-SEC BIND IT TO START DATE AND UPDATE TEXT
    ------------------------------------------------------------------*/
    @Override
    public void onFinishStartTimeEdit(Calendar calendar) {
        startDate.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        startDate.setMinutes(calendar.get(Calendar.MINUTE));
        startDate.setSeconds(calendar.get(Calendar.SECOND));

        tvStartTime.setText(DateTime.onlyTime(startDate));
    }

    @Override
    public void onFinishEndTimeEdit(Calendar calendar) {
        endDate.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        endDate.setMinutes(calendar.get(Calendar.MINUTE));
        endDate.setSeconds(calendar.get(Calendar.SECOND));

        tvEndTime.setText(DateTime.onlyTime(endDate));
    }
}