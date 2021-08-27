package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.schedulein_20.models.Group;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class GroupQueries {
    public static void queryUserGroups(ParseUser currentUser, FindCallback<Group> callback){
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.whereEqualTo(Group.KEY_CREATOR, currentUser);

        query.findInBackground(callback);
    }

    public static void createGroupInDB(Context context, ParseUser creator, String groupName, int color, ArrayList<String> membersIds, SaveCallback callback) {
        Group group = new Group();
        group.setCreator(creator);
        group.setTitle(groupName);
        group.setMembers(membersIds);
        group.setColor(color);

        if (callback != null){
            group.saveInBackground(callback);
        }else{
            group.saveInBackground();
        }
    }

    public static void updateGroupInDB(Context context, Group updatingGroup, String newName, int color,  List<String> selectedUsersIds, SaveCallback callback) {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);

        query.getInBackground(updatingGroup.getObjectId(), (object, e) -> {
            if (e == null) {
                // Update the fields we want to
                object.put(Group.KEY_NAME, newName);
                object.put(Group.KEY_MEMBERS, selectedUsersIds);
                object.put(Group.KEY_COLOR, color);

                if(callback != null){
                    object.saveInBackground(callback);
                }else {
                    object.saveInBackground();
                }
            } else {
                // something went wrong
                Toast.makeText(context, "couldn't connect with database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteGroupFromDB(Context context, Group updatingGroup, DeleteCallback callback) {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.getInBackground(updatingGroup.getObjectId(), (object, e) -> {
            if (e == null) {
                if (callback != null){
                    object.deleteInBackground(callback);
                }else{
                    object.deleteInBackground();
                }
            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(context, "Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
