package com.example.schedulein_20.parseDatabaseComms;

import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserSearchQueries {

    public static void searchForAnyUser(String text, FindCallback<ParseUser> callback) {
        // we define several queries in order to search a user by username, name, or surname

        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereContains(ParseUserExtraAttributes.KEY_USERNAME, text);

        ParseQuery<ParseUser> nameQuery = ParseUser.getQuery();
        nameQuery.whereContains(ParseUserExtraAttributes.KEY_NAME, text);

        ParseQuery<ParseUser> surnameQuery = ParseUser.getQuery();
        surnameQuery.whereStartsWith(ParseUserExtraAttributes.KEY_SURNAME, text);

        // we apply an or operator to all this queries
        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
        queries.add(usernameQuery);
        queries.add(nameQuery);
        queries.add(surnameQuery);
        ParseQuery<ParseUser> mainQuery = ParseUser.getQuery().or(queries);

        mainQuery.findInBackground(callback);
    }
}
