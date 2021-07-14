package com.example.schedulein_20.fragments;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.DateTime;
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
    ArrayList<Events> weekEvents = new ArrayList<>();

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
            Glide.with(getContext()).load(currentUserProfileImage.getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        }else {
            Glide.with(getContext())
                    .load(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        }

        greeting.setText(DateTime.timeBasedGreeting() + " " + user_name +"!");
        user_info.setText("- Now attending to: " + current_event + "\n- Next event: " + next_event);

        queryWeekEvents(view);
        //generateWeekView(view);
    }

    private void generateWeekView(@NonNull View view) {
        //int RelativeLayoutId = R.id.week_view_mon;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        RelativeLayout saturday = view.findViewById(R.id.week_view_sat);
        RelativeLayout sunday = view.findViewById(R.id.week_view_sun);

        //saturday.setVisibility(View.INVISIBLE);
        //sunday.setVisibility(View.INVISIBLE);

        for(Events event: weekEvents) {
            RelativeLayout layout = view.findViewById(  Events.dayInt2Str.get(event.getWeekDay())  );
            Log.e(TAG, event.getTitle() + String.valueOf(event.getDurationInMins()));
            int heightWDuration = 1200*event.getDurationInMins();
            heightWDuration = heightWDuration/24;
            heightWDuration = heightWDuration/60;
            Float height = heightWDuration*displayMetrics.density;
            //set the properties for button
            Button btnTag = new Button(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height.intValue()  );
            //btnTag.setLayoutParams(params);
            btnTag.setText(event.getTitle());
            btnTag.setTextSize(0, 15);

            Float marginTop = new Float(event.getStartInMins());
            marginTop = marginTop*1200;
            marginTop = marginTop/1440;
            Float finalMarginTop = marginTop*displayMetrics.density;
            Float titleOffset = 30*displayMetrics.density;

            params.setMargins(0, finalMarginTop.intValue() + titleOffset.intValue(), 0, 0);
            btnTag.setLayoutParams(params);
            //btnTag.setWidth(10);
            //btnTag.setId("programatic_button");

            //add button to the layout
            layout.addView(btnTag);
        }

    }

    public void queryWeekEvents(View view) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        // include data referred by user key
        query.include(Events.KEY_USER);
        // limit query to latest 20 items
        query.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        query.whereLessThan(Events.KEY_START_DATE, DateTime.weekEnding());
        query.whereEqualTo(Events.KEY_USER, ParseUser.getCurrentUser());
        query.addAscendingOrder(Events.KEY_START_DATE);
        // order posts by creation date (newest first)
        //query.addDescendingOrder(Events.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Events>() {
            @Override
            public void done(List<Events> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                if (objects.size() == 0){
                    Log.i(TAG, "empty query");
                }
                // for debugging purposes let's print every post description to logcat
                for (Events event : objects) {
                    Log.i(TAG, "Event: " + event.getTitle() + ", username: " +
                            event.getUser().getUsername() + "\nstarts: " + event.getStartDate() +
                            " ends: " + event.getEndDate() + "\nonday: " + event.getWeekDay()
                    );
                    weekEvents.add(event);
                    generateWeekView(view);
                }
            }
        });
    }

    public void setViewMargins(Context con, ViewGroup.LayoutParams params,
                               int left, int top , int right, int bottom, View view) {

        final float scale = con.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
    }

}