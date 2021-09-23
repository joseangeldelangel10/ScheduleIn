package com.example.schedulein_20;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.fragments.CalendarDialogFragment;
import com.example.schedulein_20.fragments.HourDialogFragment;
import com.example.schedulein_20.fragments.RepeatEventDialogFragment;
import com.example.schedulein_20.fragments.RepeatEventUntilDialogFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.notificationCreators.NotificationActions;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CUeventActivity extends AppCompatActivity implements CalendarDialogFragment.EditCalendarDateListener, HourDialogFragment.EditTimeListener, GroupMembersSearchAdapter.onUserSelectedListener, RepeatEventUntilDialogFragment.EditRepeatListener {
    private final String TAG = "CreateEventAct";
    public static final String FLAG_EDIT_START_DATE = "ChangeStartDate";
    public static final String FLAG_EDIT_END_DATE = "ChangeEndDate";
    public static final String FLAG_EDIT_START_TIME = "ChangeStartTime";
    public static final String FLAG_EDIT_END_TIME = "ChangeEndTime";
    ParseUser currentUser;
    EditText etTitle;
    TextView tvStartDate;
    TextView tvStartTime;
    TextView tvEndDate;
    TextView tvEndTime;
    TextView tvRepeat;
    TextView tvRepeatUntil;
    LinearLayout UDeventBt;
    Button btCreateEv;
    Button btUpdateEv;
    Button btDeleteEv;
    Button checkAvailability;
    Switch publicSwitch;
    Date startDate;
    Date endDate;
    String eventTitle;
    private int eventColor;
    private ArrayList<ImageButton> colorButtons;
    Events event2update;
    Group joinedEventGroup;
    RecyclerView rvInvitees;
    SearchView inviteesSearchV;
    TextView creatorTv;
    List<ParseUser> possibleInvitees;
    List<ParseUser> selectedInvitees;
    boolean eventIsPublic;
    GroupMembersSearchAdapter adapter;
    Context context;
    String flag;
    String repeatFlag;
    Date repeatUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cu_event);
        context = this;
        currentUser = ParseUser.getCurrentUser();
        repeatUntil = new Date();
        possibleInvitees = new ArrayList<>();
        selectedInvitees = new ArrayList<>();
        colorButtons = new ArrayList<>();
        flag = (String) getIntent().getExtras().get("Flag");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            eventColor = getColor(R.color.primary);
        }
        Glide.with(context).load(R.drawable.accept_icon).into((ImageButton) findViewById(R.id.CUEventColorPrimary));

        /* ----------------------------------------------------------------------------------------
                                    CREATING TOOLBAR
      ---------------------------------------------------------------------------------------- */

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ----------------------------------------------------------------------------------------
                                    REFERENCING VIEWS
      ---------------------------------------------------------------------------------------- */

        etTitle = findViewById(R.id.etCUeventTitle);
        tvStartDate = findViewById(R.id.CUeventStartDateTv);
        tvStartTime = findViewById(R.id.CUeventStartTimeTv);
        tvEndDate = findViewById(R.id.CUeventEndDateTv);
        tvEndTime = findViewById(R.id.CUeventEndTimeTv);
        publicSwitch = findViewById(R.id.CUeventPublicSwitch);
        tvRepeat = findViewById(R.id.CUeventsRepeatTv);
        tvRepeatUntil = findViewById(R.id.CUeventsRepeatUntilTv);
        rvInvitees = findViewById(R.id.CUeventInviteesRv);
        btCreateEv = findViewById(R.id.CUeventsCUFinalButt);
        btDeleteEv = findViewById(R.id.CUeventsDeleteEventBt);
        btUpdateEv = findViewById(R.id.CUeventsUpdateEventBt);
        UDeventBt = findViewById(R.id.CUeventsUDLinearLayout);
        inviteesSearchV = findViewById(R.id.CUeventSearchView);
        checkAvailability = findViewById(R.id.CUeventCheckAvailabilityButton);
        creatorTv = findViewById(R.id.CUeventsCreatorTv);

        loadColorButtons();
        colorButtonsCallbacks();

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
                            SETTING TV ONCLICK LISTENERS FOR REPEAT EDIT
      ---------------------------------------------------------------------------------------- */

        tvRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatDialogOne();
            }
        });

        tvRepeatUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatDialogOne();
            }
        });

        /* ----------------------------------------------------------------------------------------
                            SETTING INVITEE SEARCH LOGIC
      ---------------------------------------------------------------------------------------- */
        inviteesSearchV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cleanRv();
                FindCallback callback = findRelatedUsersCallback();
                RelationRelatedQueries.queryRelatedUsersWhere(currentUser, query, callback, ParseUserExtraAttributes.parseUsers2Ids((ArrayList<ParseUser>) selectedInvitees));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    cleanRv();
                }
                return true;
            }
        });

        /* ----------------------------------------------------------------------------------------
                            SETTING CHECK AVAILABILITY LISTENER
      ---------------------------------------------------------------------------------------- */

        checkAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CheckAvailabilityActivity.class);
                intent.putExtra("Flag", flag);
                intent.putExtra("selectedInvitees", Parcels.wrap(selectedInvitees));
                intent.putExtra("eventStartDate", startDate.getTime());
                intent.putExtra("eventEndDate", endDate.getTime());
                startActivity(intent);
            }
        });

        /* ----------------------------------------------------------------------------------------
                            SETTING ONCLICK LISTENER TO CREATE EVENT
      ---------------------------------------------------------------------------------------- */
        btCreateEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = etTitle.getText().toString();
                eventIsPublic = publicSwitch.isChecked();
                EventQueries.createEventInDB(context,
                        eventTitle,
                        startDate,
                        endDate,
                        eventIsPublic,
                        repeatFlag,
                        repeatUntil,
                        eventColor,
                        ParseUserExtraAttributes.parseUsers2Ids((ArrayList<ParseUser>) selectedInvitees),
                        createEventCallback(eventTitle, startDate, repeatFlag));
            }
        });

        btUpdateEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = etTitle.getText().toString();
                eventIsPublic = publicSwitch.isChecked();
                EventQueries.updateEventInDB(context,
                        event2update,
                        eventTitle,
                        startDate,
                        endDate,
                        eventIsPublic,
                        repeatFlag,
                        repeatUntil,
                        eventColor,
                        ParseUserExtraAttributes.parseUsers2Ids((ArrayList<ParseUser>) selectedInvitees),
                        null,
                        updateEventCallback());
            }
        });

        btDeleteEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventQueries.deleteEventInDB(context, event2update, deleteEventCallback());
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

        if(flag.equals("Create")){
            UDeventBt.setVisibility(View.INVISIBLE);
            UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            repeatFlag = "none";
        }else if (flag.equals("UpdateDelete")){
            event2update = Parcels.unwrap(getIntent().getParcelableExtra("Event"));
            etTitle.setText(event2update.getTitle());
            startDate = event2update.getStartDate();
            endDate = event2update.getEndDate();
            publicSwitch.setChecked(event2update.hasPublicAccess());
            selectCurrentEventColor(event2update);
            updateDatesText();
            ParseUserExtraAttributes.Ids2ParseUsers(event2update.getInvitees(), ids2ParseUsersCallback());
            repeatFlag = event2update.getRepeat();
            repeatUntil = event2update.getRepeatUntil();
            if(!(event2update.getGoogleEventId().equals("0"))){
                UDeventBt.setVisibility(View.INVISIBLE);
                UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
                etTitle.setEnabled(false);
                checkAvailability.setEnabled(false);
                inviteesSearchV.setEnabled(false);
                publicSwitch.setEnabled(false);
                disableTimeTvs();
                creatorTv.setText("your google calendar");
            }
            btCreateEv.setVisibility(View.INVISIBLE);
            btCreateEv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            evaluateRvSize();
        }else if( flag.equals("InviteeView") ){
            event2update = Parcels.unwrap(getIntent().getParcelableExtra("Event"));
            etTitle.setText(event2update.getTitle());
            startDate = event2update.getStartDate();
            endDate = event2update.getEndDate();
            publicSwitch.setChecked(event2update.hasPublicAccess());
            selectCurrentEventColor(event2update);
            updateDatesText();
            ParseUserExtraAttributes.Ids2ParseUsers(event2update.getInvitees(), ids2ParseUsersCallback());
            repeatFlag = event2update.getRepeat();
            repeatUntil = event2update.getRepeatUntil();
            btCreateEv.setVisibility(View.INVISIBLE);
            btCreateEv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            UDeventBt.setVisibility(View.INVISIBLE);
            UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            checkAvailability.setVisibility(View.INVISIBLE);
            checkAvailability.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            ParseUser creator = null;
            try {
                creator = event2update.getUser().fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            creatorTv.setText(creator.getString("name") + " " + creator.getString("surname"));
            evaluateRvSize();
        }else if (flag.equals("CreateJoinedEvent")){
            joinedEventGroup = Parcels.unwrap(getIntent().getParcelableExtra("Group"));
            ParseUserExtraAttributes.Ids2ParseUsers(joinedEventGroup.getMembers(), ids2ParseUsersCallback());
            UDeventBt.setVisibility(View.INVISIBLE);
            UDeventBt.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            repeatFlag = "none";
            evaluateRvSize();
        }


        updateRepeatTv();
    }

    private void disableTimeTvs() {
        tvStartTime.setEnabled(false);
        tvEndTime.setEnabled(false);
        tvEndDate.setEnabled(false);
        tvStartDate.setEnabled(false);
    }

    private void selectCurrentEventColor(Events event){
        int currentColor = event.getColor();
        for(ImageButton button: colorButtons){
            ColorDrawable buttonBackground;
            buttonBackground = (ColorDrawable) button.getBackground();
            if (buttonBackground.getColor() == currentColor){
                unselectColors();
                eventColor = currentColor;
                Glide.with(context).load(R.drawable.accept_icon).into(button);
            }
        }
    }

    private void colorButtonsCallbacks(){
        for(ImageButton button: colorButtons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unselectColors();
                    ColorDrawable buttonBackground;
                    buttonBackground = (ColorDrawable) button.getBackground();
                    eventColor = buttonBackground.getColor();
                    Glide.with(context).load(R.drawable.accept_icon).into(button);
                }
            });
        }
    }

    private void loadColorButtons() {
        colorButtons.add(findViewById(R.id.CUEventColorPrimary));
        colorButtons.add(findViewById(R.id.CUEventColorSecondary));
        colorButtons.add(findViewById(R.id.CUEventColorEmphasis2));
        colorButtons.add(findViewById(R.id.CUEventColorGray));
        colorButtons.add(findViewById(R.id.CUEventColorPurple));
        colorButtons.add(findViewById(R.id.CUEventColorOrange));
        colorButtons.add(findViewById(R.id.CUEventColorYellow));
        colorButtons.add(findViewById(R.id.CUEventColorPink));
    }

    private void unselectColors(){
        for(ImageButton button: colorButtons){
            Glide.with(context).load((Drawable) null).into(button);
        }
    }

    /* ---------------------------------------------------------------------------------------------------
                                                UI METHODS
    ------------------------------------------------------------------------------------------------------*/


    private void evaluateRvSize(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rvInvitees.getLayoutParams();
        if (possibleInvitees.size() > 2){
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = 500;
            //params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200);
        }else {
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            //params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        rvInvitees.setLayoutParams(params);
    }

    public void cleanRv(){
        possibleInvitees.clear();
        possibleInvitees.addAll(selectedInvitees);
        adapter.notifyDataSetChanged();
        evaluateRvSize();
    }

    private void updateDatesText() {
        tvStartDate.setText(DateTime.onlyDate( startDate ));
        tvEndDate.setText(DateTime.onlyDate( endDate ));
        tvStartTime.setText( DateTime.onlyTime(startDate) );
        tvEndTime.setText( DateTime.onlyTime(endDate) );
    }

    /* ---------------------------------------------------------------------------------------------------
                                                HELPER CALLBACKS
    ------------------------------------------------------------------------------------------------------*/

    private FindCallback<ParseUser> ids2ParseUsersCallback(){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "there was a problem retrieving invitees", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedInvitees.addAll(objects);
                cleanRv();
            }
        };
    }

    private FindCallback<ParseUser> findRelatedUsersCallback(){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0){
                        Toast.makeText(context, "No results :(", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    possibleInvitees.addAll(objects);
                    adapter.notifyDataSetChanged();
                    evaluateRvSize();
                    return;
                }
                Log.e(TAG, "problem ocurred when looking for possible invitees");
            }
        };
    }

    /* ---------------------------------------------------------------------------------------------------
                                                CRUD OPERATIONS
    ------------------------------------------------------------------------------------------------------*/

    private SaveCallback createEventCallback(String eventTitle, Date startDate, String repeatFlag){
        return new SaveCallback() {
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
                Calendar reminderTime = Calendar.getInstance();
                reminderTime.setTime(startDate);
                reminderTime.add(Calendar.MINUTE, -10);
                NotificationActions.createOneTimeNotification(context, currentUser, "your event is about to start!", "good luck with " + eventTitle, reminderTime.getTimeInMillis());
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private DeleteCallback deleteEventCallback(){
        return new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    //Something went wrong while deleting the Object
                    Toast.makeText(context, "Error deleting event", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    private SaveCallback updateEventCallback(){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(context, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    Toast.makeText(context, "Failed updating event", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }



    /* !_!_!_!_!_!_!_!_ FOLLOWING METHODS ARE NECESSARY TO EDIT DATE WITH CALENDAR VIEW AS WELL AS TIME !_!_!_!_!_!_!_!_*/

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

    private void showRepeatDialogOne(){
        FragmentManager fm = getSupportFragmentManager();

        RepeatEventDialogFragment repeatEventDialogFragment = RepeatEventDialogFragment.newInstance(repeatFlag);
        repeatEventDialogFragment.show(fm, "fragment_edit_repeat");
    }

    /* ---------------------------------------------------------------
     GET NEW DAY-MONTH-YEAR BIND IT TO START DATE AND UPDATE TEXT
    ------------------------------------------------------------------*/
    @Override
    public void onFinishStartDateEdit(Calendar selectedDate) {

        startDate.setYear(selectedDate.get(Calendar.YEAR) - 1900);
        startDate.setMonth(selectedDate.get(Calendar.MONTH));
        startDate.setDate(selectedDate.get(Calendar.DAY_OF_MONTH));

        updateDatesText();
    }

    @Override
    public void onFinishEndDateEdit(Calendar selectedDate) {
        endDate.setYear(selectedDate.get(Calendar.YEAR) - 1900);
        endDate.setMonth(selectedDate.get(Calendar.MONTH));
        endDate.setDate(selectedDate.get(Calendar.DAY_OF_MONTH));

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

        updateDatesText();
    }

    @Override
    public void onFinishEndTimeEdit(Calendar calendar) {
        endDate.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        endDate.setMinutes(calendar.get(Calendar.MINUTE));
        endDate.setSeconds(calendar.get(Calendar.SECOND));

        updateDatesText();
    }


    @Override
    public void userSelected(ParseUser user) {
        selectedInvitees.add(user);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void userUnselected(ParseUser user) {
        selectedInvitees.remove(user);
        possibleInvitees.remove(user);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishRepeatEdit(String repeatResult, Calendar calendar) {
        this.repeatFlag = repeatResult;
        if(!repeatFlag.equals("none")) {
            repeatUntil.setYear(calendar.get(Calendar.YEAR) - 1900);
            repeatUntil.setMonth(calendar.get(Calendar.MONTH));
            repeatUntil.setDate(calendar.get(Calendar.DAY_OF_MONTH));
            repeatUntil.setHours(23);
            repeatUntil.setMinutes(59);
        }

        updateRepeatTv();
    }

    private void updateRepeatTv() {
        tvRepeat.setText(repeatFlag);
        if(!repeatFlag.equals("none")){
            tvRepeatUntil.setText("until " + DateTime.onlyDate(repeatUntil).toLowerCase());
        }else {
            tvRepeatUntil.setText("");
        }
    }
}
