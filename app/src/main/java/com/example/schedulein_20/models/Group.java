package com.example.schedulein_20.models;

import com.example.schedulein_20.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@ParseClassName("Group")
public class Group  extends ParseObject{
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_NAME = "name";
    public static final String KEY_MEMBERS = "members";

    public Group(){}

    public String getTitle() { return getString(KEY_NAME); }

    public void  setTitle(String name) { put(KEY_NAME, name); }

    public ParseUser getCreator() { return (ParseUser) get(KEY_CREATOR); }

    public void  setCreator(ParseUser creator) { put(KEY_CREATOR, creator); }

    public ArrayList<String> getMembers(){
        return (ArrayList<String>) get(KEY_MEMBERS);
    }

    public void  setMembers(ArrayList<String> members){
        put(KEY_MEMBERS,members);
    }

}
