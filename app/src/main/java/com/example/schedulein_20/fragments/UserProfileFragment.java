package com.example.schedulein_20.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.LayoutGenerators.CalendarViewsGenerator;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.OnPinchListener;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.parseDatabaseComms.EventQueries;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
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
    public Context context;
    ScaleGestureDetector mScaleDetector;

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
        weekEvents = new ArrayList<>();
        context = getContext();

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        VIEW REFERENCING
        ------------------------------------------------------------------------------------------------------------------------------------*/
        ivUserImage = view.findViewById(R.id.ivUserImage);
        greeting = view.findViewById(R.id.ProfileFragmentName);
        userInfo = view.findViewById(R.id.ProfileFragmentExtraInfo);
        cancelNextEvent = view.findViewById(R.id.ProfileFragmentRelateButt);
        newEvent = view.findViewById(R.id.create_new_event);
        //calView = view.findViewById(R.id.week_view_HorizontalScrollView);
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

        ParseFile currentUserProfileImage = (ParseFile) currentUser.getParseFile(ParseUserExtraAttributes.KEY_PROFILE_PIC);
        if (currentUserProfileImage != null) {
            Glide.with(context)
                    .load(currentUserProfileImage.getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.profile_picture_placeholder)
                    .into(ivUserImage);
        }


        greeting.setText(DateTime.timeBasedGreeting(context) + " " + user_name +"!"); // good morning-afternoon-night user
        userInfo.setText(getString(R.string.now_attending_to) + current_event + "\n" + getString(R.string.next_event) + next_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                       WE GENERATE USER'S WEEK PREVIEW
        ------------------------------------------------------------------------------------------------------------------------------------*/

        FindCallback onWeekEventsFound = weekEventsCallback(context, view);
        EventQueries.queryWeekEvents(context, currentUser, onWeekEventsFound);

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

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        ADD WEEK VIEW GESTURE
        ------------------------------------------------------------------------------------------------------------------------------------*/

        calView.setOnTouchListener(new OnPinchListener(context){
            @Override
            public void onPinchZoom() {
                Fragment fragment = new CalendarViewFragment();

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

    private FindCallback<Events> weekEventsCallback(Context context, View view){
        return new FindCallback<Events>() {
            @Override
            public void done(List<Events> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    Toast.makeText(context, getString(R.string.loading_events_problem), Toast.LENGTH_LONG).show();
                    return;
                }
                if (events.size() == 0){
                    Toast.makeText(context, currentUser.getString(ParseUserExtraAttributes.KEY_NAME) + " " + getString(R.string.no_events_loaded_this_week), Toast.LENGTH_SHORT).show();
                }else {
                    weekEvents.addAll(events);
                    Toast.makeText(context, currentUser.getString(ParseUserExtraAttributes.KEY_NAME) + " " +  getString(R.string.events_loaded_succesfully), Toast.LENGTH_SHORT).show();
                    CalendarViewsGenerator.generateWeekView(view, context, weekEvents, UserProfileFragment.this);
                }
            }
        };
    }


}
