package com.example.schedulein_20.fragments;

import static android.content.Context.ALARM_SERVICE;

import static com.parse.Parse.getApplicationContext;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.GoogleCalendarClient;
import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.RESTclientOpetations.MergingDiffCalendarsEvents;
import com.example.schedulein_20.ScheduleInGCalendarAPIApp;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.OnPinchListener;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.notificationCreators.NotificationReceiver;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment implements MergingDiffCalendarsEvents.OnMergeEvents {
    private final String TAG = "UserProfile";
    public final int CREATE_EVENT_REQUEST_CODE = 10;
    public final int UPDATE_EVENT_REQUEST_CODE = 20;
    private ImageView ivUserImage;
    private TextView greeting;
    private TextView userInfo;
    private Button cancelNextEvent;
    private Button newEvent;
    LinearLayout calView;
    private ParseUser currentUser;
    public ArrayList<Events> weekEvents;
    public ArrayList<Events> googleWeekEvents;
    public Context context;
    ScaleGestureDetector mScaleDetector;
    String primaryGCalendarId;
    GoogleCalendarClient gCalendarClient;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        currentUser = ParseUser.getCurrentUser();
        //weekEvents = new ArrayList<>();
        googleWeekEvents = new ArrayList<>();
        context = getContext();
        primaryGCalendarId = null;
        gCalendarClient = ScheduleInGCalendarAPIApp.getRestClient(context);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        VIEW REFERENCING
        ------------------------------------------------------------------------------------------------------------------------------------*/
        ivUserImage = view.findViewById(R.id.ivUserImage);
        greeting = view.findViewById(R.id.ProfileFragmentName);
        userInfo = view.findViewById(R.id.ProfileFragmentExtraInfo);
        cancelNextEvent = view.findViewById(R.id.ProfileFragmentRelateButt);
        newEvent = view.findViewById(R.id.create_new_event);
        calView = view.findViewById(R.id.week_view_days_container);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        RETRIEVING THE DATA TO GENERATE THE VIEWS
        ------------------------------------------------------------------------------------------------------------------------------------*/
        String user_name = currentUser.getString(ParseUserExtraAttributes.KEY_NAME);
        String current_event = "current event";
        String next_event = "other event";

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        BINDING DATA TO THE HEADER
        ------------------------------------------------------------------------------------------------------------------------------------*/

        int radius = getResources().getInteger(R.integer.profile_pic_radius);
        int margin = getResources().getInteger(R.integer.profile_pic_margin);

        ParseFile currentUserProfileImage = (ParseFile) currentUser.getParseFile(ParseUserExtraAttributes.KEY_PROFILE_PIC);
        if (currentUserProfileImage != null) {
            Glide.with(context)
                    .load(currentUserProfileImage.getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivUserImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.profile_picture_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivUserImage);
        }


        greeting.setText(DateTime.timeBasedGreeting(context) + " " + user_name +"!"); // good morning-afternoon-night user
        userInfo.setText(getString(R.string.now_attending_to) + current_event + "\n" + getString(R.string.next_event) + next_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                       WE GENERATE USER'S WEEK PREVIEW
        ------------------------------------------------------------------------------------------------------------------------------------*/

        // TODO: progress bar crash
        for(int i = 0; i<7; i++){
            Date dayOfWeek = getDayOfWeek(i);
            FindCallback onDayEventsFound = dayEventsCallback(view, i);
            EventQueries.queryParseDayEvents(context, currentUser, dayOfWeek, onDayEventsFound);
        }

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        ADD CREATE EVENT FUNCTIONALITY
        ------------------------------------------------------------------------------------------------------------------------------------*/

        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = "Create";
                Intent intent = new Intent(context, CUeventActivity.class);
                intent.putExtra( "Flag", flag); // this flag lets the activity know that we are creating an event instead of updating it
                startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
            }
        });

        /* ------------------------------------------------------------------------------------------------------------------------
        *                                               TESTING NOTIFICATIONS
        --------------------------------------------------------------------------------------------------------------------------*/

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Date currentDate = DateTime.currentDate();
            Calendar present = Calendar.getInstance();
            present.setTime(currentDate);
            for(int i=0;i<2;i++) {
                present.set(Calendar.MINUTE, present.get(Calendar.MINUTE) + 1);

                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                int notificationId = currentUser.getInt("notificationCount");
                intent.putExtra("notificationTitle", "Notification " + i);
                intent.putExtra("notificationContent", "content " + i);
                intent.putExtra("notificationId", notificationId);
                currentUser.put("notificationCount", notificationId+1);
                currentUser.saveInBackground();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,  present.getTimeInMillis(), pendingIntent);
            }
        }else{
            Toast.makeText(context, "sorry the api is less than oreo", Toast.LENGTH_SHORT).show();
            Log.e("Notification", "THE NOTIFICATION COUDN??T BE DISPLAYED BECAUSE OF THE API");
        }*/

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        ADD WEEK VIEW GESTURE
        ------------------------------------------------------------------------------------------------------------------------------------*/

        calView.setOnTouchListener(new OnPinchListener(context){

            @Override
            public void onPinchZoom() {
                Fragment fragment = CalendarViewFragment.newInstance(DateTime.weekStart(), DateTime.weekEnding());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Transition explodeTransform = TransitionInflater.from(context).
                            inflateTransition(android.R.transition.explode);


                    fragment.setEnterTransition(explodeTransform);
                }

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, fragment)
                        .addToBackStack("transaction")
                        .commit();
            }
        });

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        ADD GOOGLE CALENDAR EVENTS
        ------------------------------------------------------------------------------------------------------------------------------------*/


        //ScheduleInGCalendarAPIApp.getRestClient(context).clearAccessToken();
        if (gCalendarClient.checkAccessToken() != null){

            gCalendarClient.getPrimaryCalendar(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonResponse = json.jsonObject;
                    try {
                        Log.e(TAG, "connected with google calendar successfully");
                        JSONArray results = jsonResponse.getJSONArray("items");
                        if (results.length() != 0){
                            JSONObject primaryCalendar = (JSONObject) results.get(0);
                            primaryGCalendarId = primaryCalendar.getString("id");
                            Log.e(TAG, "\nPRIMARY GOOGLE CAL ID: " + primaryGCalendarId + "\n\n\n");
                            getCurrentWeekGoogleEvents(primaryGCalendarId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "Fetch calendars error, status code: " + statusCode + "response: " + response);
                    OnMergeEventsCompleted();
                }
            });
        }else {
            Log.e(TAG, "no calendar API access yet");
            OnMergeEventsCompleted();
        }



    }

    private void getCurrentWeekGoogleEvents(String primaryGCalendarId) {
        gCalendarClient.getCurrentWeekCalendarEvents(primaryGCalendarId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.e(TAG, "GOOGLE EVENTS: " + json.jsonObject.toString(4));
                    JSONArray listOfGoogleEvents = json.jsonObject.getJSONArray("items");
                    ArrayList<Events> googleRetrievedEvents = Events.fromJsonArray(context, currentUser, listOfGoogleEvents);
                    MergingDiffCalendarsEvents.checkGoogleEvents(context, googleRetrievedEvents, UserProfileFragment.this);
                    //CalendarViewsGenerator.generateWeekView(getView(), context, googleRetrievedEvents, UserProfileFragment.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "FAILED RETRIEVING USER GOOGLE EVENTS: " + statusCode + "      " + response);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE || requestCode == UPDATE_EVENT_REQUEST_CODE || requestCode == CalendarViewsGenerator.UPDATE_EVENT_REQUEST_CODE){
            Log.e(TAG, "req:" + String.valueOf(requestCode) + " res:" + String.valueOf(resultCode));
            if (resultCode == Activity.RESULT_OK){

                // WE CREATE A NEW FRAGMENT TO SHOW THE USER THE NEW EVENT HE HAS CREATED, DELETED OR UPDATED
                // GIVING USER INSTANT RESPONSE
                Fragment fragment = new UserProfileFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.host_frame, fragment)
                        .commit();
            }
        }
    }

    private Date getDayOfWeek(int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(DateTime.weekStart());
        c.add(Calendar.DATE, i);
        Date result = c.getTime();
        return result;
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
                    Log.e(TAG, "DAY NUMBER " + String.valueOf(i));
                    Log.e(TAG,"     no events this day");
                    return;
                }
                Log.e(TAG, "DAY NUMBER " + String.valueOf(i));
                dayEvents.addAll(objects);
                for(Events event:objects){
                    Log.e(TAG, event.toString());
                }
                CalendarViewsGenerator.generateDayView(view, context, dayEvents,UserProfileFragment.this, view.findViewById(Events.dayInt2Layout.get(i)) );
            }
        };
    }


    @Override
    public void OnMergeEventsCompleted() {
        EventQueries.queryGoogleWeekEvents(context, currentUser, new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                googleWeekEvents.addAll(objects);
                CalendarViewsGenerator.generateWeekView(calView, context, googleWeekEvents, UserProfileFragment.this);
            }
        });
    }
}
