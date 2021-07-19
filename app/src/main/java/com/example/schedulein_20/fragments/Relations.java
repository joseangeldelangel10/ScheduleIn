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
    private RecyclerView rvRelations;
    private RecyclerView rvRelationReq;
    private UserRelationsAdapter relAdapter;
    private RelationRequestsAdapter reqAdapter;
    List<ParseUser> relations;
    List<ParseUser> requests;
    Context context;


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
        context = getContext();
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

        /* --------------------------------------------------------------------------------
                                    WE REFERENCE RECYCLER VIEWS
        * -------------------------------------------------------------------------------- */
        rvRelations = view.findViewById(R.id.RelationsFragmentUserRelRv);
        rvRelationReq = view.findViewById(R.id.RelationsFragmentRelReq);


        /* --------------------------------------------------------------------------------
                               WE CONFIGURE AND POPULATE RELATIONS RV
        * -------------------------------------------------------------------------------- */
        relations = new ArrayList<>();
        relAdapter = new UserRelationsAdapter(context, relations);
        LinearLayoutManager relLinearLayoutManager = new LinearLayoutManager(context);
        rvRelations.setLayoutManager(relLinearLayoutManager); // we bind a layout manager to RV
        rvRelations.setAdapter(relAdapter);

        queryRelations(ParseUser.getCurrentUser());

        /* --------------------------------------------------------------------------------
                               WE CONFIGURE AND POPULATE REQUESTS RV
        * -------------------------------------------------------------------------------- */
        requests = new ArrayList<>();
        reqAdapter = new RelationRequestsAdapter(context, requests, Relations.this);
        LinearLayoutManager reqLinearLayoutManager = new LinearLayoutManager(context);
        rvRelationReq.setLayoutManager(reqLinearLayoutManager); // we bind a layout manager to RV
        rvRelationReq.setAdapter(reqAdapter);

        queryRequests(ParseUser.getCurrentUser());
    }

    private void queryRelations(ParseUser currentUser) {
        ArrayList<String> relationsIds = (ArrayList<String>) currentUser.get("relations");
        Log.e(TAG, relationsIds.toString());

        // WE QUERY FOR ALL USERS INCLUDED IN CURRENT USER RELATIONS ARRAY
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", relationsIds);

        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d(TAG,"Query Relations - userlist = " + user1.getUsername());
                    relations.add(user1);
                }
                relAdapter.notifyDataSetChanged();
            } else {
                // Something went wrong.
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryRequests(ParseUser currentUser) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("relations", currentUser.getObjectId() );// we check for all users where current user id is contained in their relations
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d(TAG, "Query requests - userlist = " + user1.getUsername());
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

    public static void AcceptRequest(Context context,ParseUser currentUser, ParseUser user){
        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        relations.add( user.getObjectId() );

        currentUser.put("relations", relations);

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

    public static void DeclineRequest(Context context, ParseUser currentUser, ParseUser relatingUser) {
        ArrayList<String> relations = (ArrayList<String>) relatingUser.get("relations");
        relations.remove(currentUser.getObjectId());

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("objectId", relatingUser.getObjectId());
        params.put("newRelations", relations );
        ParseCloud.callFunctionInBackground("editUserRelations", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if(e == null){
                    Log.e(TAG, "Decline Request = Success");
                    Toast.makeText(context, "Request declined", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e(TAG, "Decline Request = fail " + e);
                    Toast.makeText(context, "There was a problem declining request", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public static void unrelate(Context context, ParseUser currentUser, ParseUser user) {
        String otherUserName = user.getString("username");

        ArrayList<String> currentUserRelations = (ArrayList<String>) currentUser.get("relations");
        currentUserRelations.remove(user.getObjectId());

        currentUser.put("relations", currentUserRelations);
        currentUser.saveInBackground(e -> {
            if(e==null){
                //Save successful
                Toast.makeText(context, "You are no longer related to " + otherUserName, Toast.LENGTH_SHORT).show();
            }else{
                // Something went wrong while saving
                Toast.makeText(context, "There was a problem unrelating you from  " + otherUserName, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> otherUserRelations = (ArrayList<String>) user.get("relations");
        // if related decline request else (request sent) do nothing else
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
                        Toast.makeText(context, otherUserName + " is no longer related to you", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.e(TAG, "fail on other user relation deleted" + e);
                        Toast.makeText(context, "There was a problem unrelating " + otherUserName + " from you", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void sendRelateRequest(Context context, ParseUser currentUser, ParseUser user) {

        ArrayList<String> relations = (ArrayList<String>) currentUser.get("relations");
        relations.add( user.getObjectId() );

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

        /*if ( CUrelations.contains(user.getObjectId()) && OUserRelations.contains(currentUser.getObjectId()) ){
            return 1; // related
        }else if( CUrelations.contains(user.getObjectId()) ){
            return 0; // request sent
        }else {
            return -1; // not related
        }*/
    }

    @Override
    public void onRequestItemAccepted(ParseUser user) {
        relations.add(user);
        relAdapter.notifyDataSetChanged();
    }

}