package com.example.schedulein_20.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.schedulein_20.ActivityDrawerLayout;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.UserSearchAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearchFragment extends Fragment {
    private final String TAG = "SearchFragment";
    Context context = getContext();
    List<ParseUser> searchResults;
    List<String> searchResultsIds;
    UserSearchAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSearchFragment newInstance(String param1, String param2) {
        UserSearchFragment fragment = new UserSearchFragment();
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
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        LinearLayout banner = view.findViewById(R.id.UserSearchBanner);
        RecyclerView rvUserSearch = view.findViewById(R.id.UserSearchRv);
        context = getContext();

        banner.setVisibility(View.VISIBLE);
        banner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        rvUserSearch.setVisibility(View.INVISIBLE);
        rvUserSearch.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0));

        searchResults = new ArrayList<>();
        searchResultsIds = new ArrayList<>();
        adapter = new UserSearchAdapter(context, searchResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvUserSearch.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvUserSearch.setAdapter(adapter);

        MenuItem searchItem = ActivityDrawerLayout.searchItem;
        SearchView searchView = ActivityDrawerLayout.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                banner.setVisibility(View.INVISIBLE);
                banner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0));
                rvUserSearch.setVisibility(View.VISIBLE);
                rvUserSearch.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                searchForUsers(query);
                //------ collapse search view -----
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //--------------------

    }

    private void searchForUsers(String text) {
        ActivityDrawerLayout.showProgressBar();
        // we clear recycler view so that previous search results won't show up anymore
        searchResults.clear();
        searchResultsIds.clear();
        adapter.notifyDataSetChanged();

        // we define several queries in order to search a user by username, name, or surname

        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereStartsWith("username", text);

        ParseQuery<ParseUser> nameQuery = ParseUser.getQuery();
        nameQuery.whereStartsWith("name", text);

        ParseQuery<ParseUser> surnameQuery = ParseUser.getQuery();
        nameQuery.whereStartsWith("surname", text);

        List<ParseQuery<ParseUser>> queries = new ArrayList<>();
        queries.add(usernameQuery);
        queries.add(nameQuery);
        //queries.add(surnameQuery);

        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);

        //mainQuery.whereNotContainedIn("objectId", searchResultsIds);
        //mainQuery.setLimit(50);

        mainQuery.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d(TAG, "Username: " + user1.getUsername());
                    searchResults.add(user1);
                    searchResultsIds.add(user1.getObjectId());
                }

                if (searchResults.size() == 0){
                    Toast.makeText(context, "No results found :(", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            } else {
                // Something went wrong.
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            ActivityDrawerLayout.hideProgressBar();
        });
    }

}