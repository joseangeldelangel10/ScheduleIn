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
import com.example.schedulein_20.models.UserRelationsAdapter;
import com.example.schedulein_20.models.UserSearchAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Relations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Relations extends Fragment {
    private final String TAG = "RelationsFragment";
    RecyclerView rvRelations;
    RecyclerView rvRelationReq;
    List<ParseUser> relations;
    Context context;
    UserRelationsAdapter adapter;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvRelations.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvRelations.setAdapter(adapter);

        queryRelations(ParseUser.getCurrentUser());
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
}