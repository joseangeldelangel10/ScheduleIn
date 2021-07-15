package com.example.schedulein_20.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    private final String TAG = "UserProfile";
    ImageView ivUserImage;
    TextView greeting;
    TextView user_info;
    Button cancel_next_event;
    ScrollView week_schedule;
    Button new_event;
    ParseUser currentUser = ParseUser.getCurrentUser();
    public ArrayList<Events> weekEvents = new ArrayList<>();
    Context context;

    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfile() {
        // Required empty public constructor
    }*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    /*public static UserProfile newInstance(String param1, String param2) {
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
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        ivUserImage = view.findViewById(R.id.ivUserImage);
        greeting = view.findViewById(R.id.greeting);
        user_info = view.findViewById(R.id.user_info);
        cancel_next_event = view.findViewById(R.id.cancel_next_event);
        new_event = view.findViewById(R.id.create_new_event);
        context = getContext();

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        RETRIEVING THE DATA TO GENERATE THE VIEWS
        ------------------------------------------------------------------------------------------------------------------------------------*/
        String user_name = currentUser.getString("name");
        String current_event = "current event";
        String next_event = "other event";

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        BINDING DATA TO THE VIEWS
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

        greeting.setText(DateTime.timeBasedGreeting() + " " + user_name +"!");
        user_info.setText("- Now attending to: " + current_event + "\n- Next event: " + next_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------*/

        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CUeventActivity.class);
                startActivity(intent);
            }
        });

        queryWeekEvents(view);
        //generateWeekView(view);

    }

    private void generateWeekView(@NonNull View view) {
        //int RelativeLayoutId = R.id.week_view_mon;
        Float titleOffset = getResources().getDimension(R.dimen.week_view_header_ofset);
        Float heightWDuration;
        Float marginTop;
        Float minsInDay = new Float(24*60);
        Float RelativeLayoutHeightDP = getResources().getDimension(R.dimen.week_view_hour_row_height) * 24;

        for(Events event: weekEvents) {
            RelativeLayout layout = view.findViewById(  Events.dayInt2Str.get(event.getWeekDay())  );

            Log.e(TAG, event.getTitle() + String.valueOf(event.getDurationInMins()));
            heightWDuration = new Float(RelativeLayoutHeightDP*event.getDurationInMins() );
            heightWDuration = heightWDuration/minsInDay;
            //heightWDuration = heightWDuration*displayMetrics.density;

            marginTop = new Float(event.getStartInMins());
            marginTop = marginTop*RelativeLayoutHeightDP;
            marginTop = marginTop/minsInDay;

            //set the properties for button
            Button btnTag = new Button(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, heightWDuration.intValue() );
            params.setMargins(0, marginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            btnTag.setText(event.getTitle());
            btnTag.setTextSize(0, 18);

            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Intent for event: " + event.getTitle());
                    Intent intent = new Intent(context, CUeventActivity.class);
                    intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(pack));
                    startActivity(intent);
                }
            });

            layout.addView(btnTag);
        }

    }

    public void queryWeekEvents(View view) {

        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        // include data referred by user key
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
                generateWeekView(view);
            }
        });
    }


}