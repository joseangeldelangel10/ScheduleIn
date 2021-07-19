package com.example.schedulein_20.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.CUeventActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragments";
    private ParseUser user;
    private ParseUser currentUser;
    List<Events> weekEvents;
    Context context;

    TextView userName;
    TextView userDetails;
    ImageView userProfilePic;
    Button relate;
    TextView whatIsIn;
    NestedScrollView calView;
    RelativeLayout banner;
    TextView textBanner;
    //ParseUser currentUser =

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
        //this.user = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(ParseUser param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", param1);
        //args.putString(ARG_PARAM1, param1);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /* --------------------------------------------------------------------------------
         This fragment is initialized when the user taps on a search result and thus is
         called on the UserSearchAdapter onBindViewHolder method
        -------------------------------------------------------------------------------- */
        super.onViewCreated(view, savedInstanceState);


        userName = view.findViewById(R.id.ProfileFragmentName);
        userDetails = view.findViewById(R.id.ProfileFragmentExtraInfo);
        userProfilePic = view.findViewById(R.id.ProfileFragmentProfilePic);
        relate = view.findViewById(R.id.ProfileFragmentRelateButt);
        whatIsIn = view.findViewById(R.id.ProfileFragmentCalTitle);
        calView = view.findViewById(R.id.ProfileFragmentCalView);
        banner = view.findViewById(R.id.ProfileFragmentNonRelatedBanner);
        textBanner = view.findViewById(R.id.ProfileFragmentNonRelatedBannerText);

        user = getArguments().getParcelable("user");

        if(user != null) {
            currentUser = ParseUser.getCurrentUser();
            context = getContext();

            /* --------------------------------------------------------------------------------
                            WE FILL OUR PROFILE VIEW HEADER WITH USER DATA
            --------------------------------------------------------------------------------*/

            userName.setText( user.getString("name") + " " + user.getString("surname") );
            String details = "Username: " + user.getString("username") +
                    "\nEmail: " + user.getString("email");
            userDetails.setText( details );
            whatIsIn.setText("What is in " + user.get("name").toString() + "'s week");
            Glide.with(getContext()).load(user.getParseFile("profilePic").getUrl())
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(userProfilePic);

            /* --------------------------------------------------------------------------------
                            WE EVALUATE IF USER IS RELATED WITH CURRENT USER AND
                            WE SHOW THE CORRESPONDING WEEK PREVIEW AND "RELATE BUTTON"
            --------------------------------------------------------------------------------*/

            if (Relations.userIsRelated(currentUser, user) == -1){
                calView.setVisibility(View.INVISIBLE);
                calView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                textBanner.setText("Relate with " + user.get("name").toString() + " to see each others availability");


                relate.setBackground(  new ColorDrawable(getResources().getColor(R.color.emphasis1))  );
                relate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Relations.relateUsers(context,currentUser, user);
                    }
                });
            }else if (Relations.userIsRelated(currentUser, user) == 0){
                calView.setVisibility(View.INVISIBLE);
                calView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                textBanner.setText("Request sent!");


                relate.setText("Request sent");
                relate.setBackground(  new ColorDrawable(getResources().getColor(R.color.gray))  );
                relate.setClickable(false);
            }else {
                banner.setVisibility(View.INVISIBLE);
                banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                weekEvents = new ArrayList<>();
                queryWeekEvents(view, user);

                relate.setText("Related");
                relate.setBackground(  new ColorDrawable(getResources().getColor(R.color.emphasis2))  );
                relate.setClickable(false);
            }

        }else {
            Toast.makeText(context, "sorry we couldn't retrieve this user's data", Toast.LENGTH_LONG).show();
        }

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
            btnTag.setText("Event");
            btnTag.setTextSize(0, 18);
            btnTag.setClickable(false);

            layout.addView(btnTag);

        }

    }

    public void queryWeekEvents(View view, ParseUser user) {

        ParseQuery<Events> query = ParseQuery.getQuery(Events.class);
        // include data referred by user key
        query.include(Events.KEY_USER);
        query.whereGreaterThan(Events.KEY_START_DATE, DateTime.weekStart());
        query.whereLessThan(Events.KEY_START_DATE, DateTime.weekEnding());
        query.whereEqualTo(Events.KEY_USER, user);
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
                    Log.i(TAG, user.getString("name") + "'s week events query empty");
                }
                // for debugging purposes let's print every post description to logcat
                for (Events event : objects) {
                    Log.i(TAG, "Event: " + event.getTitle() + ", username: " +
                            event.getUser().getUsername() + "\nstarts: " + event.getStartDate() +
                            " ends: " + event.getEndDate() + "\nonday: " + event.getWeekDay()
                    );
                    weekEvents.add(event);
                }
                Toast.makeText(getContext(), user.getString("name") +"'s week view loaded successfully!", Toast.LENGTH_LONG).show();
                generateWeekView(view);
            }
        });
    }

    /*public static int userIsRelated(ParseUser currentUser, ParseUser user) {
        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        ArrayList<String> OUserRelations = (ArrayList<String>) user.get("relations");
        if ( relations.contains(user.getObjectId()) && OUserRelations.contains(currentUser.getObjectId())){
            return 1; // related
        }else if( relations.contains(user.getObjectId()) ){
            return 0; // request sent
        }else {
            return -1; // not related
        }
    }*/
}