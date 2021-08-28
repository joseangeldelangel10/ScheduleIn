package com.example.schedulein_20.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TodoItems")
public class TodoItems extends ParseObject {
    public static final String KEY_USER = "User";
    public static final String KEY_DESCRIPTION = "Description";

    public TodoItems(){ }

    public void setDescription(String description){ put(KEY_DESCRIPTION, description); }

    public String getDescription() { return getString(KEY_DESCRIPTION); }

    public void setUser(ParseUser user) { put(KEY_USER, user);}

    public ParseUser getUser() { return getParseUser(KEY_USER); }
}
