package com.example.schedulein_20.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schedulein_20.R;
import com.example.schedulein_20.models.RelationRequestsAdapter;
import com.example.schedulein_20.models.UserRelationsAdapter;
import com.example.schedulein_20.models.UserSearchAdapter;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Relations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Relations extends Fragment implements RelationRequestsAdapter.OnItemChangeListener{
    public static final String TAG = "RelationsFragment";
    RecyclerView rvRelations;
    RecyclerView rvRelationReq;
    List<ParseUser> relations;
    List<ParseUser> requests;
    Context context;
    UserRelationsAdapter adapter;
    RelationRequestsAdapter reqAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Relations() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Relations.
     */
    // TODO: Rename and change types and number of parameters
    public static Relations newInstance(String param1, String param2) {
        Relations fragment = new Relations();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_relations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();

        rvRelations = view.findViewById(R.id.RelationsFragmentUserRelRv);
        rvRelationReq = view.findViewById(R.id.RelationsFragmentRelReq);

        relations = new ArrayList<>();
        adapter = new UserRelationsAdapter(context, relations);
        LinearLayoutManager relLinearLayoutManager = new LinearLayoutManager(context);
        rvRelations.setLayoutManager(relLinearLayoutManager); // we bind a layout manager to RV
        rvRelations.setAdapter(adapter);

        queryRelations(ParseUser.getCurrentUser());


        requests = new ArrayList<>();
        reqAdapter = new RelationRequestsAdapter(context, requests, Relations.this);
        LinearLayoutManager reqLinearLayoutManager = new LinearLayoutManager(context);
        rvRelationReq.setLayoutManager(reqLinearLayoutManager); // we bind a layout manager to RV
        rvRelationReq.setAdapter(reqAdapter);

        queryRequests(ParseUser.getCurrentUser());
    }

    private void queryRequests(ParseUser currentUser) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("relations", currentUser.getObjectId() );// we check for all users where current user id is contained in their relations
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d("User List ",(user1.getUsername()));
                    // we add to the requests only the users that aren't part of current user relations
                    if (notPartOfUserRelations(user1)){
                        requests.add(user1);
                    }
                }
                reqAdapter.notifyDataSetChanged();
            } else {
                // Something went wrong.
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean notPartOfUserRelations(ParseUser user) {
        ArrayList<String> currentUserRelations = (ArrayList<String>) ParseUser.getCurrentUser().get("relations");
        if( currentUserRelations.contains(user.getObjectId()) ){
            return false;
        }
        return true;
    }

    private void queryRelations(ParseUser currentUser) {
        ArrayList<String> relationsIds = (ArrayList<String>) currentUser.get("relations");
        Log.e(TAG, relationsIds.toString());

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", relationsIds);
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d("User List ",(user1.getUsername()));
                    relations.add(user1);
                }
                adapter.notifyDataSetChanged();
            } else {
                // Something went wrong.
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void AcceptRequest(Context context,ParseUser currentUser, ParseUser user){
        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        relations.add( user.getObjectId() );

        currentUser.put("relations", relations);

        currentUser.saveInBackground(e -> {
            if(e==null){
                //Save successful
                Log.e("acceptingReq", "success");
                Toast.makeText(context, "Request accepted!", Toast.LENGTH_SHORT).show();
            }else{
                Log.e("acceptingReq", "error");
                Toast.makeText(context, "There was a problem accepting the request", Toast.LENGTH_SHORT).show();
                // Something went wrong while saving
                //Toast.makeText(getContext(), "relate request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void DeclineRequest(Context context, ParseUser currentUser, ParseUser relatingUser) {
        ArrayList<String> relations = (ArrayList<String>) relatingUser.get("relations");
        relations.remove(currentUser.getObjectId());

        //relatingUser.put("relations", relations);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("objectId", relatingUser.getObjectId());
        params.put("newRelations", relations );
        ParseCloud.callFunctionInBackground("editUserRelations", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if(e == null){
                    Log.e("DeclineRelation", "Success");
                    Toast.makeText(context, "Request declined", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e("DeclineRelation", "fail " + e);
                    Toast.makeText(context, "There was a problem declining request", Toast.LENGTH_LONG).show();
                }
            }
        });

        //ParseQuery<ParseUser> query = ParseUser.getQuery();
    }


    public static void unrelate(ParseUser currentUser, ParseUser user) {
        ArrayList<String> currentUserRelations = (ArrayList<String>) currentUser.get("relations");
        currentUserRelations.remove(user.getObjectId());

        currentUser.put("relations", currentUserRelations);
        currentUser.saveInBackground(e -> {
            if(e==null){
                //Save successful
                //Toast.makeText(context, "relate request sent!", Toast.LENGTH_SHORT).show();
            }else{
                // Something went wrong while saving
                //Toast.makeText(context, "relate request failed", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> otherUserRelations = (ArrayList<String>) user.get("relations");
        if ( otherUserRelations.contains( currentUser.getObjectId() )){
            otherUserRelations.remove(currentUser.getObjectId());

            //relatingUser.put("relations", relations);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", user.getObjectId());
            params.put("newRelations", otherUserRelations );
            ParseCloud.callFunctionInBackground("editUserRelations", params, new FunctionCallback<String>() {
                @Override
                public void done(String object, ParseException e) {
                    if(e == null){
                        Log.e(TAG, "other user relation deleted");
                        //Toast.makeText(context, "Request declined", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.e(TAG, "fail on other user relation deleted" + e);
                        //Toast.makeText(context, "There was a problem declining request", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public static void relateUsers(Context context, ParseUser currentUser, ParseUser user) {

        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        //Log.e(TAG, "previous relations: " + relations.toString());
        relations.add( user.getObjectId() );
        //Log.e(TAG, "current relations: " + relations.toString());

        currentUser.put("relations", relations);

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

    public static int userIsRelated(ParseUser currentUser, ParseUser user) {
        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        ArrayList<String> OUserRelations = (ArrayList<String>) user.get("relations");
        if ( relations.contains(user.getObjectId()) && OUserRelations.contains(currentUser.getObjectId())){
            return 1; // related
        }else if( relations.contains(user.getObjectId()) ){
            return 0; // request sent
        }else {
            return -1; // not related
        }
    }

    @Override
    public void onRequestItemAccepted(ParseUser user) {
        relations.add(user);
        adapter.notifyDataSetChanged();
    }

}