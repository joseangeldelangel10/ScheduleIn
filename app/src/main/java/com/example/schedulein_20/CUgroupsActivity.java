package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CUgroupsActivity extends AppCompatActivity implements GroupMembersSearchAdapter.onUserSelectedListener{
    private final String TAG = "DefineGroupMembersActivity";
    EditText etName;
    SearchView searchView;
    RecyclerView rvSearchResults;
    Button createGroup;
    List<ParseUser> possibleMembers;
    List<ParseUser> selectedUsers;
    List<String> selectedUsersIds;
    Context context;
    GroupMembersSearchAdapter adapter;
    ParseUser currentUser;
    RelativeLayout udButtons;
    Button deleteGroup;
    Button updateGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cu_group);

        context = this;
        possibleMembers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        selectedUsersIds = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();

        String flag = (String) getIntent().getExtras().get("Flag");
        Toast.makeText(context, flag, Toast.LENGTH_SHORT).show();

        /* ---------------------------------------------------------------------------------------------
                                      REPLACING ACTION BAR FOR TOOLBAR TO USE NAV DRAWER
        --------------------------------------------------------------------------------------------- */
        //onPrepareOptionsMenu((Menu) this);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.groups_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        etName = findViewById(R.id.CUGroupName);
        searchView = findViewById(R.id.DGroupMembersSv);
        createGroup = findViewById(R.id.CUGroupCreateGroup);
        rvSearchResults = findViewById(R.id.DGroupMembersRv);
        udButtons = findViewById(R.id.CUGroupUDbuttons);
        deleteGroup = findViewById(R.id.CUGroupDeleteGroup);
        updateGroup = findViewById(R.id.CUGroupUpdateGroup);


        adapter = new GroupMembersSearchAdapter(context, possibleMembers, selectedUsers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSearchResults.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvSearchResults.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cleanRv();
                FindCallback callback = new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() == 0){
                                Toast.makeText(context, "No results :(", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (ParseUser user : objects) {
                                possibleMembers.add(user);
                            }
                            adapter.notifyDataSetChanged();
                            DrawerLayoutActivity.hideProgressBar();
                            return;
                        }
                        Log.e(TAG, "problem ocurred when looking for possible members");
                    }
                };

                RelationRelatedQueries.queryRelatedUsersWhere(currentUser, query, callback, selectedUsersIds);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    cleanRv();
                }
                return false;
            }
        });

        if (flag.equals("Create") ){
            //udButtons.setVisibility(View.INVISIBLE);
            udButtons.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            //createGroup.setVisibility(View.VISIBLE);
            createGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createGroupInDB(context, currentUser, etName.getText().toString(), (ArrayList<String>) selectedUsersIds);
                }
            });
        }else if(flag.equals("UpdateDelete") ){
            Log.e(TAG, "eneterin UD mode of a group");

            //udButtons.setVisibility(View.VISIBLE);
            //createGroup.setVisibility(View.INVISIBLE);
            createGroup.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));

            Group updatingGroup = Parcels.unwrap(getIntent().getExtras().getParcelable("Group"));
            etName.setText(updatingGroup.getTitle());

            ArrayList<String> updatingEventSelectedUsersIds = updatingGroup.getMembers();
            querySelectedUsers(updatingEventSelectedUsersIds);

            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEventFromDB(context, updatingGroup);
                }
            });

            updateGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateEventInDB(context, updatingGroup, etName.getText().toString(), selectedUsersIds);
                }
            });

        }

    }

    private void updateEventInDB(Context context, Group updatingGroup, String newName, List<String> selectedUsersIds) {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);

        // Retrieve the object by id
        query.getInBackground(updatingGroup.getObjectId(), (object, e) -> {
            if (e == null) {
                // Update the fields we want to
                object.put(Group.KEY_NAME, newName);
                object.put(Group.KEY_MEMBERS, selectedUsersIds);

                // All other fields will remain the same
                object.saveInBackground();
                Toast.makeText(context, "Group updated successfully", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent();
                //setResult(RESULT_OK, intent);
                finish();

            } else {
                // something went wrong
                Toast.makeText(this, "Failed when updating group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEventFromDB(Context context, Group updatingGroup) {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.getInBackground(updatingGroup.getObjectId(), (object, e) -> {
            if (e == null) {
                // Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent();
                        //setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        //Something went wrong while deleting the Object
                        Toast.makeText(this, "Error deleting group", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void querySelectedUsers(ArrayList<String> updatingEventSelectedUsersIds) {
        ParseQuery<ParseUser> mainQuery = ParseUser.getQuery();
        mainQuery.whereContainedIn("objectId", updatingEventSelectedUsersIds);

        mainQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "st went wrong while retrieveing group members", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "error getting users" + e);
                    return;
                }
                for (ParseUser user: objects){
                    selectedUsers.add(user);
                    selectedUsersIds.add(user.getObjectId());
                    Toast.makeText(context, "success while retrieving group members", Toast.LENGTH_LONG).show();
                }
                cleanRv();
            }
        });
    }

    private void createGroupInDB(Context context, ParseUser creator, String groupName, ArrayList<String> membersIds) {
        Group group = new Group();
        group.setCreator(creator);
        group.setTitle(groupName);
        group.setMembers(membersIds);

        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "group saving failed!\n" + e);
                    Toast.makeText(context, "group saving failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "group saving succeeded");
                Toast.makeText(context, "Saving succeeded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void cleanRv(){
        possibleMembers.clear();
        possibleMembers.addAll(selectedUsers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void userSelected(ParseUser user) {
        selectedUsers.add(user);
        selectedUsersIds.add(user.getObjectId());
    }

}


















