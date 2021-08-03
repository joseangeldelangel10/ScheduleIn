package com.example.schedulein_20.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private ImageView userProfilePic;
    private TextView userName;
    private TextView userSurname;
    private TextView userUsername;
    private TextView userEmail;
    private CheckBox userSIWGoogle;
    private Button updatePassword;
    private Button updateProfile;
    ParseUser currentUser;
    Context context;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = ParseUser.getCurrentUser();
        context = getContext();

        userProfilePic = view.findViewById(R.id.editProfileProfilePicture);
        userName = view.findViewById(R.id.editProfileName);
        userSurname = view.findViewById(R.id.editProfileSurname);
        userUsername = view.findViewById(R.id.editProfileUsername);
        userEmail = view.findViewById(R.id.editProfileEmail);
        userSIWGoogle = view.findViewById(R.id.editProfileSIWGoogleCheckBox);
        updatePassword = view.findViewById(R.id.editProfileUpdatePassword);
        updateProfile = view.findViewById(R.id.editProfileUpdateProfile);

        File profilePic = null;
        try {
            profilePic = currentUser.getParseFile("profilePic").getFile();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Uri image2load = Uri.parse(profilePic.getAbsolutePath());

        Glide.with(context).load(image2load).placeholder(R.drawable.profile_picture_placeholder).into(userProfilePic);
        userName.setText(currentUser.getString("name"));
        userSurname.setText(currentUser.getString("surname"));
        userUsername.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());


    }
}
