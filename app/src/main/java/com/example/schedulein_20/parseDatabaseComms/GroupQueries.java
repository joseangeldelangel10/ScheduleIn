package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.schedulein_20.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class GroupQueries {
    public static void queryUserGroups(ParseUser currentUser, FindCallback<Group> callback){
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.whereEqualTo(Group.KEY_CREATOR, currentUser);

        query.findInBackground(callback);
    }
}
