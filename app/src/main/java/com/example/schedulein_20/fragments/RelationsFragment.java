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

import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.RelationRequestsAdapter;
import com.example.schedulein_20.models.UserRelationsAdapter;
import com.example.schedulein_20.parseDatabaseComms.RelationRelatedQueries;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelationsFragment extends Fragment implements RelationRequestsAdapter.OnItemChangeListener{
    public static final String TAG = "RelationsFragment";
    private RecyclerView rvRelations;
    private RecyclerView rvRelationReq;
    private UserRelationsAdapter relAdapter;
    private RelationRequestsAdapter reqAdapter;
    private List<ParseUser> relations;
    private List<ParseUser> requests;
    Context context;
    ParseUser currentUser;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RelationsFragment() {
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
    public static RelationsFragment newInstance(String param1, String param2) {
        RelationsFragment fragment = new RelationsFragment();
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
        //super.onViewCreated(view, savedInstanceState);
        currentUser = ParseUser.getCurrentUser();

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

        //queryRelations(ParseUser.getCurrentUser());
        RelationRelatedQueries.queryUserRelations(currentUser, queryRelationsCallback());

        /* --------------------------------------------------------------------------------
                               WE CONFIGURE AND POPULATE REQUESTS RV
        * -------------------------------------------------------------------------------- */
        requests = new ArrayList<>();
        reqAdapter = new RelationRequestsAdapter(context, requests, RelationsFragment.this);
        LinearLayoutManager reqLinearLayoutManager = new LinearLayoutManager(context);
        rvRelationReq.setLayoutManager(reqLinearLayoutManager); // we bind a layout manager to RV
        rvRelationReq.setAdapter(reqAdapter);

        queryRequests(ParseUser.getCurrentUser());
    }

    private FindCallback<ParseUser> queryRelationsCallback(){

        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(context, getString(R.string.loading_relations_problem), Toast.LENGTH_SHORT).show();
                    return;
                }if (objects.size() == 0){
                    Toast.makeText(context, getString(R.string.no_relations_found), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, getString(R.string.relations_loaded_succesfully), Toast.LENGTH_SHORT).show();
                relations.addAll(objects);
                relAdapter.notifyDataSetChanged();
            }
        };
    }

    // !-!_!_!_!_!_!_! IMPORTANT NOTE : WE'LL LEAVE THIS HERE FOR THE MOMENT SINCE WE WILL
    // !-!_!_!_!_!_!_! REDESIGN THE DB AGAIN
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

    @Override
    public void onRequestItemAccepted(ParseUser user) {
        relations.add(user);
        relAdapter.notifyDataSetChanged();
    }

}
