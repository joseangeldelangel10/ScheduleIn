package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.parseDatabaseComms.GroupQueries;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.DeleteCallback;
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
    private ArrayList<ImageButton> colorButtons;
    private int groupColor;
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
        colorButtons = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
        groupColor = getColor(R.color.primary);
        Glide.with(context).load(R.drawable.accept_icon).into((ImageButton) findViewById(R.id.CUGroupColorPrimary));

        /* ---------------------------------------------------------------------------------------------
                                    REPLACING ACTION BAR FOR TOOLBAR TO USE NAV DRAWER
        --------------------------------------------------------------------------------------------- */
        Toolbar toolbar = findViewById(R.id.groups_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ---------------------------------------------------------------------------------------------
                                                REFERENCING VIEWS
        --------------------------------------------------------------------------------------------- */

        etName = findViewById(R.id.CUGroupName);
        searchView = findViewById(R.id.DGroupMembersSv);
        createGroup = findViewById(R.id.CUGroupCreateGroup);
        rvSearchResults = findViewById(R.id.DGroupMembersRv);
        udButtons = findViewById(R.id.CUGroupUDbuttons);
        deleteGroup = findViewById(R.id.CUGroupDeleteGroup);
        updateGroup = findViewById(R.id.CUGroupUpdateGroup);

        loadColorButtons();
        colorButtonsCallbacks();

        /* ---------------------------------------------------------------------------------------------
                                            SETTING UP USER SEARCH RV
        --------------------------------------------------------------------------------------------- */
        adapter = new GroupMembersSearchAdapter(context, possibleMembers, selectedUsers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSearchResults.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvSearchResults.setAdapter(adapter);

        /* ---------------------------------------------------------------------------------------------
                                            DEFINING USER SEARCH LOGIC
        --------------------------------------------------------------------------------------------- */

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cleanRv();
                FindCallback callback = findRelatedUsersCallback();
                RelationRelatedQueries.queryRelatedUsersWhere(currentUser, query, callback, selectedUsersIds);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    cleanRv();
                }
                return true;
            }
        });

        /* ---------------------------------------------------------------------------------------------
                          WE EVALUATE FLAGS TO CHECK IF WE ARE UPDATING OR CREATING A GROUP
        --------------------------------------------------------------------------------------------- */
        String flag = (String) getIntent().getExtras().get("Flag");

        if (flag.equals("Create") ){
            // WE HIDE UPDATE BUTTONS
            udButtons.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            createGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupQueries.createGroupInDB(context,
                            currentUser,
                            etName.getText().toString(),
                            groupColor,
                            (ArrayList<String>) selectedUsersIds,
                            createGroupInDBCallback());
                }
            });
        }else if(flag.equals("UpdateDelete") ){
            // WE HIDE CREATE BUTTONS
            createGroup.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            // WE RETRIEVE THE GROUP PASSED AS AN EXTRA
            Group updatingGroup = Parcels.unwrap(getIntent().getExtras().getParcelable("Group"));
            // WE BIND GROUP DETAILS TO THE RESPECTIVE VIEWS
            etName.setText(updatingGroup.getTitle());
            ArrayList<String> groupMembersIds = updatingGroup.getMembers();
            ParseUserExtraAttributes.Ids2ParseUsers(groupMembersIds, queryGroupMembersCallback());

            // WE SET BUTTON LISTENERS
            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupQueries.deleteGroupFromDB(context, updatingGroup, deleteGroupInDBCallback());
                }
            });

            updateGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupQueries.updateGroupInDB(context,
                            updatingGroup,
                            etName.getText().toString(),
                            groupColor,
                            selectedUsersIds,
                            updateGroupInDBCallback());
                }
            });

        }
    }

    private void selectCurrentGroupColor(Group group){
        int currentColor = group.getColor();
        for(ImageButton button: colorButtons){
            ColorDrawable buttonBackground;
            buttonBackground = (ColorDrawable) button.getBackground();
            if (buttonBackground.getColor() == currentColor){
                unselectColors();
                groupColor = currentColor;
                Glide.with(context).load(R.drawable.accept_icon).into(button);
            }
        }
    }

    private void colorButtonsCallbacks(){
        for(ImageButton button: colorButtons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unselectColors();
                    ColorDrawable buttonBackground;
                    buttonBackground = (ColorDrawable) button.getBackground();
                    groupColor = buttonBackground.getColor();
                    Glide.with(context).load(R.drawable.accept_icon).into(button);
                }
            });
        }
    }

    private void loadColorButtons() {
        colorButtons.add(findViewById(R.id.CUGroupColorPrimary));
        colorButtons.add(findViewById(R.id.CUGroupColorSecondary));
        colorButtons.add(findViewById(R.id.CUGroupColorEmphasis2));
        colorButtons.add(findViewById(R.id.CUGroupColorGray));
    }

    private void unselectColors(){
        for(ImageButton button: colorButtons){
            Glide.with(context).load((Drawable) null).into(button);
        }
    }

    /* -----------------------------------------------------------------------------------------------------------------------
                                            UI METHODS
    ---------------------------------------------------------------------------------------------------------------------- */

    public void cleanRv(){
        possibleMembers.clear();
        possibleMembers.addAll(selectedUsers);
        adapter.notifyDataSetChanged();
    }

    // THE IMPLEMENTATION OF THIS METHOD ALLOWS US TO KEEP SELECTED USERS IN RV HIGHLIGHTED AT THE TOP AND ADD USERS TO GROUP
    @Override
    public void userSelected(ParseUser user) {
        selectedUsers.add(user);
        selectedUsersIds.add(user.getObjectId());
    }

    @Override
    public void userUnselected(ParseUser user) {
        selectedUsers.remove(user);
        possibleMembers.remove(user);
        selectedUsersIds.remove(user.getObjectId());
        adapter.notifyDataSetChanged();
    }


    /* -----------------------------------------------------------------------------------------------------------------------
                                            USER QUERYING CALLBACKS
    ---------------------------------------------------------------------------------------------------------------------- */

    // THIS CALLBACK ALLOWS US TO BIND SEARCH RESULTS TO THE RV
    private FindCallback<ParseUser> findRelatedUsersCallback(){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0){
                        Toast.makeText(context, "No results :(", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    possibleMembers.addAll(objects);
                    adapter.notifyDataSetChanged();
                    return;
                }
                Log.e(TAG, "problem ocurred when looking for possible members");
            }
        };
    }

    // THIS CALLBACK ALLOWS US TO BIND GROUP MEMBERS TO THE RV
    private FindCallback<ParseUser> queryGroupMembersCallback(){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "st went wrong while retrieveing group members", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error getting users" + e);
                    return;
                }
                for (ParseUser user: objects){
                    selectedUsers.add(user);
                    selectedUsersIds.add(user.getObjectId());
                    //Toast.makeText(context, "success while retrieving group members", Toast.LENGTH_SHORT).show();
                }
                cleanRv();
            }
        };
    }

    /* -----------------------------------------------------------------------------------------------------------------------
                                            CRUD OPERATIONS
    ---------------------------------------------------------------------------------------------------------------------- */

    private SaveCallback createGroupInDBCallback(){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "group saving failed!\n" + e);
                    Toast.makeText(context, "group saving failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "group saving succeeded");
                Toast.makeText(context, "Group created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private SaveCallback updateGroupInDBCallback(){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(context, "Group updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    Toast.makeText(context, "a problem occurred updating group", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private DeleteCallback deleteGroupInDBCallback(){
        return new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    //Something went wrong while deleting the Object
                    Toast.makeText(context, "Error deleting group", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }





}


















