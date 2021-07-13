package com.example.schedulein_20.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.DateTime;
import com.example.schedulein_20.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    ImageView ivUserImage;
    TextView greeting;
    TextView user_info;
    Button cancel_next_event;
    ScrollView week_schedule;
    Button new_event;
    ParseUser currentUser = ParseUser.getCurrentUser();;

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
    }
}