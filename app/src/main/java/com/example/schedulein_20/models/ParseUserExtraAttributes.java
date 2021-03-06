package com.example.schedulein_20.models;

import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ParseUserExtraAttributes {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";
    public static final String KEY_RELATIONS = "relations";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_GOOGLE_USER = "googleUser";

    public static ArrayList<String> parseUsers2Ids(ArrayList<ParseUser> parseUsers){
        ArrayList<String> result = new ArrayList<>();
        for(ParseUser user: parseUsers){
            result.add(user.getObjectId());
        }
        return result;
    }

    public static void Ids2ParseUsers(ArrayList<String> membersIds, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", membersIds);
        query.findInBackground(callback);
    }


}
