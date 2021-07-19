package com.example.schedulein_20.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.Events;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    private final String TAG = "UserProfile";
    private ImageView ivUserImage;
    private TextView greeting;
    private TextView user_info;
    Button cancel_next_event;
    //ScrollView week_schedule;
    Button new_event;
    ParseUser currentUser = ParseUser.getCurrentUser();
    public ArrayList<Events> weekEvents = new ArrayList<>();
    Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfile() {
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
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
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
        //super.onViewCreated(view, savedInstanceState);
        context = getContext();
        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        VIEW REFERENCING
        ------------------------------------------------------------------------------------------------------------------------------------*/
        ivUserImage = view.findViewById(R.id.ivUserImage);
        greeting = view.findViewById(R.id.ProfileFragmentName);
        user_info = view.findViewById(R.id.ProfileFragmentExtraInfo);
        cancel_next_event = view.findViewById(R.id.ProfileFragmentRelateButt);
        new_event = view.findViewById(R.id.create_new_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        RETRIEVING THE DATA TO GENERATE THE VIEWS
        ------------------------------------------------------------------------------------------------------------------------------------*/
        String user_name = currentUser.getString("name");
        String current_event = "current event";
        String next_event = "other event";

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        BINDING DATA TO THE HEADER
        ------------------------------------------------------------------------------------------------------------------------------------*/

        ParseFile currentUserProfileImage = (ParseFile) currentUser.getParseFile("profilePic");
        if (currentUserProfileImage != null) {
            Glide.with(context).load(currentUserProfileImage.getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        }else {
            Glide.with(context)
                    .load(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        }

        greeting.setText(DateTime.timeBasedGreeting() + " " + user_name +"!"); // good morning-afternoon-night user
        user_info.setText("- Now attending to: " + current_event + "\n- Next event: " + next_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                       WE GENERATE USER'S WEEK PREVIEW
        ------------------------------------------------------------------------------------------------------------------------------------*/

        queryWeekEvents(view);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        ADD CREATE EVENT FUNCTIONALITY
        ------------------------------------------------------------------------------------------------------------------------------------*/


        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = "Create";
                Intent intent = new Intent(context, CUeventActivity.class);
                intent.putExtra( "Flag", flag); // this flag lets the activity know that we are creating an event instead of updating it
                startActivity(intent);
            }
        });

    }

    private void generateWeekView(@NonNull View view) {
        /* --------------------------------------------------------------------------------
        to generate the week preview we generate a button for each event in weekEvents and
        we place the button in the corresponding day column (colums are Relative Layouts)
        at the corresponding height based in the event starting time and ending time
        -------------------------------------------------------------------------------- */

        Float titleOffset = getResources().getDimension(R.dimen.week_view_header_ofset); // dp height occupied by day tags (Monday, tuesday, wednesday, ...)
        Float heightWDuration; // dp height of the event button based in its duration
        Float marginTop; // dp height at which the event button is placed based event starting time
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = getResources().getDimension(R.dimen.week_view_hour_row_height) * 24; // height of a day column (hour block height times 24 hrs)

        for(Events event: weekEvents) {
            RelativeLayout layout = view.findViewById(  Events.dayInt2Str.get(event.getWeekDay())  ); // we evaluate the corresponding day column using a hash map
            Log.e(TAG, event.getTitle() + String.valueOf(event.getDurationInMins()));

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
            btnTag.setText(event.getTitle());
            btnTag.setTextSize(0, 18);

            // We bind a listener to each button which allows the user to update or delete an event by taping on it
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Intent for event: " + event.getTitle());
                    String flag = "UpdateDelete";
                    Intent intent = new Intent(context, CUeventActivity.class);
                    intent.putExtra( "Flag", flag);
                    intent.putExtra("Event", Parcels.wrap(event) );
                    startActivity(intent);
                }
            });
            // ----------------------------

            layout.addView(btnTag);
        }

    }

    public void queryWeekEvents(View view) {
        /* --------------------------------------------------------------------------------
        we query this week's events in database, we store each event as an event object in
        "weekEvents" and finally we use such event objects to generate week preview
        -------------------------------------------------------------------------------- */

        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);

        query.include(Events.KEY_USER);
        query.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        query.whereLessThan(Events.KEY_START_DATE, DateTime.weekEnding());
        query.whereEqualTo(Events.KEY_USER, ParseUser.getCurrentUser());
        query.addAscendingOrder(Events.KEY_START_DATE);

        query.findInBackground(new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    Toast.makeText(getContext(), "There was a problem loading your events", Toast.LENGTH_LONG).show();
                    return;
                }
                if (objects.size() == 0){
                    Log.i(TAG, "week events query empty");
                }
                // for debugging purposes let's print every post description to logcat
                for (Events event : objects) {
                    Log.i(TAG, "Event: " + event.getTitle() + ", username: " +
                            event.getUser().getUsername() + "\nstarts: " + event.getStartDate() +
                            " ends: " + event.getEndDate() + "\nonday: " + event.getWeekDay()
                    );
                    weekEvents.add(event);
                }
                Toast.makeText(getContext(), "Events loaded successfully!", Toast.LENGTH_LONG).show();
                generateWeekView(view);
            }
        });
    }


}