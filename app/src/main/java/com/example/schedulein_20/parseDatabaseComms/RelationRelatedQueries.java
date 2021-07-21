package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.schedulein_20.DrawerLayoutActivity;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelationRelatedQueries {
    public static final String TAG = "RelationRelatedQueries";

    public static void AcceptRequest(Context context, ParseUser currentUser, ParseUser user, SaveCallback callback){
        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        relations.add( user.getObjectId() );

        // we must receive a SaveCallback
        if (callback != null){
            currentUser.saveInBackground(callback);
        }else {
            //default callback makes toasts
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successful
                    Log.e(TAG, "Accepting request = success");
                    Toast.makeText(context, "Request accepted!", Toast.LENGTH_SHORT).show();
                }else{
                    Log.e(TAG, "Accepting request = error");
                    Toast.makeText(context, "There was a problem accepting the request", Toast.LENGTH_SHORT).show();
                    // Something went wrong while saving
                    //Toast.makeText(getContext(), "relate request failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void DeclineRequest(Context context, ParseUser currentUser, ParseUser relatingUser, FunctionCallback callback) {
        ArrayList<String> relations = (ArrayList<String>) relatingUser.get("relations");
        relations.remove(currentUser.getObjectId());

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("objectId", relatingUser.getObjectId());
        params.put("newRelations", relations );

        if (callback != null){
            ParseCloud.callFunctionInBackground("editUserRelations", params, callback);
        }else {
            ParseCloud.callFunctionInBackground("editUserRelations", params, new FunctionCallback<String>() {
                @Override
                public void done(String object, ParseException e) {
                    if (e == null) {
                        Log.e(TAG, "Decline Request = Success");
                        Toast.makeText(context, "Request declined", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Decline Request = fail " + e);
                        Toast.makeText(context, "There was a problem declining request", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public static void unrelate(Context context, ParseUser currentUser, ParseUser user, SaveCallback unrelatinCurrentUserCallback, FunctionCallback unrelatingOtherUserCallback) {
        String otherUserName = user.getString("username");

        // we unrelate current user fro other user
        ArrayList<String> currentUserRelations = (ArrayList<String>) currentUser.get("relations");
        currentUserRelations.remove(user.getObjectId());
        currentUser.put("relations", currentUserRelations);
        if (unrelatinCurrentUserCallback != null){
            currentUser.saveInBackground(unrelatinCurrentUserCallback);
        }else{
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successful
                    Toast.makeText(context, "You are no longer related to " + otherUserName, Toast.LENGTH_SHORT).show();
                }else{
                    // Something went wrong while saving
                    Toast.makeText(context, "There was a problem unrelating you from  " + otherUserName, Toast.LENGTH_SHORT).show();
                }
            });
        }


        ArrayList<String> otherUserRelations = (ArrayList<String>) user.get("relations");
        // if related, delete relation else (request sent) do nothing else
        if ( otherUserRelations.contains( currentUser.getObjectId() )) {

            otherUserRelations.remove(currentUser.getObjectId());
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", user.getObjectId());
            params.put("newRelations", otherUserRelations);

            if (unrelatingOtherUserCallback != null) {
                ParseCloud.callFunctionInBackground("editUserRelations", params, unrelatingOtherUserCallback);
            } else {
                ParseCloud.callFunctionInBackground("editUserRelations", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if (e == null) {
                            Log.e(TAG, "other user relation deleted");
                            Toast.makeText(context, otherUserName + " is no longer related to you", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "fail on other user relation deleted" + e);
                            Toast.makeText(context, "There was a problem unrelating " + otherUserName + " from you", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    public static void sendRelateRequest(Context context, ParseUser currentUser, ParseUser user, SaveCallback callback) {

        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        relations.add( user.getObjectId() );
        currentUser.put("relations", relations);

        if (callback != null){
            currentUser.saveInBackground(callback);
        }else {
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successful
                    Toast.makeText(context, "relate request sent!", Toast.LENGTH_SHORT).show();
                }else{
                    // Something went wrong while saving
                    Toast.makeText(context, "relate request failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static int getUsersRelation(ParseUser currentUser, ParseUser user) {
        /*
         * IN THIS FUNCTION WE EVALUATE THE RELATION BETWEEN THE CURRENT USER AND ANOTHER USER,
         * BASED ON 4 RELATION STATUSES THAT WILL BE RETURNED, SUCH STATUSES ARE THE FOLLOWING
         *   0 - NOT RELATED
         *   1 - REQUEST RECEIVED
         *   2 - REQUEST SENT
         *   3 - RELATED
         * THIS NUMBERS ARE THE DECIMAL REPRESENTATION OF THE BINARY-BOOLEAN CONDITION:
         *                 BIT 1                     |            BIT 2
         * CURRENT USER RELATIONS CONTAIN OTHER USER | OTHER USER RELATIONS CONTAIN OTHER USER
         * */

        ArrayList<String> CUrelations = (ArrayList<String>) currentUser.get("relations");
        ArrayList<String> OUserRelations = (ArrayList<String>) user.get("relations");
        String Ouser = user.getObjectId();
        String Cuser = currentUser.getObjectId();
        if (!CUrelations.contains(Ouser) && !OUserRelations.contains(Cuser)){
            return 0;
        }else if (!CUrelations.contains(Ouser) && OUserRelations.contains(Cuser)){
            return 1;
        }else if (CUrelations.contains(Ouser) && !OUserRelations.contains(Cuser)){
            return 2;
        }else if (CUrelations.contains(Ouser) && OUserRelations.contains(Cuser)){
            return 3;
        }else {
            return -1;
        }
    }

    public static void queryRelatedUsersWhere(ParseUser currentUser, String query, FindCallback<ParseUser> callback, List<String> selectedUsersIds) {
        DrawerLayoutActivity.showProgressBar();
        List<ParseUser> searchResults = new ArrayList<>();
        ArrayList<String> currentUserRelations = (ArrayList<String>) currentUser.get("relations");


        ParseQuery<ParseUser> mainQuery = ParseUser.getQuery();
        mainQuery.whereStartsWith("name", query);
        mainQuery.whereContainedIn("objectId", currentUserRelations);
        mainQuery.whereEqualTo("relations", currentUser.getObjectId());
        mainQuery.whereNotContainedIn("objectId", selectedUsersIds);
        //mainQuery.whereNotContainedIn("objectId", searchResultsIds);
        //mainQuery.setLimit(50);

        if (callback != null) {
            mainQuery.findInBackground(callback);
        } else {
            mainQuery.findInBackground((users, e) -> {
                if (e == null) {
                    // The query was successful, returns the users that matches
                    // the criteria.
                    for (ParseUser user1 : users) {
                        Log.d(TAG, "Username: " + user1.getUsername());
                        searchResults.add(user1);
                    }
                    Log.e("searchingPossibleMembers", searchResults.toString());

                } else {
                    // Something went wrong.
                    Log.e("searchingPossibleMembers", "st went wrong");

                }
                DrawerLayoutActivity.hideProgressBar();
            });
        }
    }
}
