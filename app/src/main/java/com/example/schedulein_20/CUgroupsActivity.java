package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.GroupMembersSearchAdapter;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_group_members);

        context = this;
        possibleMembers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        selectedUsersIds = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();

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

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupInDB(context, currentUser, etName.getText().toString(), (ArrayList<String>) selectedUsersIds);
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
