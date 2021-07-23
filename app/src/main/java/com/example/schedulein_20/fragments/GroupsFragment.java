package com.example.schedulein_20.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.schedulein_20.CUgroupsActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.example.schedulein_20.models.GroupsAdapter;
import com.example.schedulein_20.parseDatabaseComms.GroupQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    private final String TAG = "GroupsFragment";
    private RecyclerView rvGroups;
    private Button newGroupButt;
    Context context;
    GroupsAdapter adapter;
    List<Group> groups;
    ParseUser currentUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Groups.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        groups = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();

        /* ---------------------------------------------------------------------------
                                    VIEW REFERENCING
        --------------------------------------------------------------------------- */
        newGroupButt = view.findViewById(R.id.GroupsNewgroupButt);
        rvGroups = view.findViewById(R.id.GroupsRv);

        /* ---------------------------------------------------------------------------
                                    WE SET UP GROUPS RV
        --------------------------------------------------------------------------- */
        adapter = new GroupsAdapter(context, groups);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvGroups.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvGroups.setAdapter(adapter);

        /* ---------------------------------------------------------------------------
                              WE ADD CREATE GROUP FUNCTIONALITY
        --------------------------------------------------------------------------- */
        newGroupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, CUgroupsActivity.class);
                intent.putExtra("Flag", "Create");
                startActivity(intent);
            }
        });

        /* ---------------------------------------------------------------------------
                                    WE POPULATE GROUPS RV
        --------------------------------------------------------------------------- */

        GroupQueries.queryUserGroups(currentUser, queryUserGroupsCallback());

    }

    private FindCallback<Group> queryUserGroupsCallback(){
        return new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting groups", e);
                    Toast.makeText(context, getString(R.string.problem_loading_groups), Toast.LENGTH_LONG).show();
                    return;
                }
                if (objects.size() == 0){
                    Log.i(TAG, "groups query empty");
                    Toast.makeText(context, getString(R.string.no_groups_loaded), Toast.LENGTH_LONG).show();
                }
                // for debugging purposes let's print every post description to logcat
                for (Group group : objects) {
                    Log.i(TAG, "Group: " + group.getTitle() );
                    groups.add(group);
                }
                Toast.makeText(context, getString(R.string.groups_loaded_successfully), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                Log.e(TAG, "groups = " + groups.toString());
            }
        };
    }

}
