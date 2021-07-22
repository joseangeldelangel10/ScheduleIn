package com.example.schedulein_20.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.models.UserSearchAdapter;
import com.example.schedulein_20.parseDatabaseComms.UserSearchQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearchFragment extends Fragment {
    private final String TAG = "SearchFragment";
    private Context context = getContext();
    public List<ParseUser> searchResults;
    private UserSearchAdapter adapter;

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
        context = getContext();
        /* --------------------------------------------------------------------------------
                            WE REFERENCE VIEWS & SHOW BANNER WHEN INITIALIZING
        * -------------------------------------------------------------------------------- */
        LinearLayout banner = view.findViewById(R.id.UserSearchBanner);
        RecyclerView rvUserSearch = view.findViewById(R.id.UserSearchRv);
        banner.setVisibility(View.VISIBLE);
        rvUserSearch.setVisibility(View.GONE);

         /* --------------------------------------------------------------------------------
                                            WE SET UP RV
        * -------------------------------------------------------------------------------- */
        searchResults = new ArrayList<>();
        adapter = new UserSearchAdapter(context, searchResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvUserSearch.setLayoutManager(linearLayoutManager); // we bind a layout manager to RV
        rvUserSearch.setAdapter(adapter);

         /* --------------------------------------------------------------------------------
                            WE REFERENCE SEARCH VIEW & BIND FUNCTIONALITY
        * -------------------------------------------------------------------------------- */

        MenuItem searchItem = DrawerLayoutActivity.searchItem;
        SearchView searchView = DrawerLayoutActivity.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                banner.setVisibility(View.GONE);
                rvUserSearch.setVisibility(View.VISIBLE);

                cleanRv();
                UserSearchQueries.searchForAnyUser(query, searchUsersCallback());
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

    private void cleanRv(){
        searchResults.clear();
        adapter.notifyDataSetChanged();
    }

    private FindCallback<ParseUser> searchUsersCallback(){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                    if (e != null) {
                        Toast.makeText(context, getString(R.string.problem_searching_users), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (users.size() == 0){
                        Toast.makeText(context, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    searchResults.addAll(users);
                    adapter.notifyDataSetChanged();
                }
        };
    }

}
