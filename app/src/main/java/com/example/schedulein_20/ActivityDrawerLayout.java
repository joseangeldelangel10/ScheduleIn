package com.example.schedulein_20;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulein_20.fragments.CalendarView;
import com.example.schedulein_20.fragments.EditProfile;
import com.example.schedulein_20.fragments.Groups;
import com.example.schedulein_20.fragments.Relations;
import com.example.schedulein_20.fragments.Settings;
import com.example.schedulein_20.fragments.UserProfile;
import com.example.schedulein_20.fragments.UserSearchFragment;
import com.example.schedulein_20.models.DateTime;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

public class ActivityDrawerLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "ActivityDrawerLayout";
    public ParseUser currentUser = ParseUser.getCurrentUser();
    public Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private View navHeader;
    private TextView headNavUser;
    private TextView headNavDate;
    public static MenuItem searchItem;
    public static SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        /* ---------------------------------------------------------------------------------------------
                                      REPLACING ACTION BAR FOR TOOLBAR TO USE NAV DRAWER
        --------------------------------------------------------------------------------------------- */
        toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.calendar_icon);
        //toolbar.setDisplayUseLogoEnabled(true);

        /* ---------------------------------------------------------------------------------------------
                                                DRAWER CONFIGURATION
        --------------------------------------------------------------------------------------------- */
        drawer = findViewById(R.id.my_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_opened, R.string.drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // in this class we declare the interface method onNavigationItemSelected;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.host_frame, new UserProfile()).commit();

        /* ---------------------------------------------------------------------------------------------
                                      NAVIGATION HEADER ITEM CONTROL
        --------------------------------------------------------------------------------------------- */
        navHeader =  navigationView.getHeaderView(0);
        headNavUser = navHeader.findViewById(R.id.nav_menu_mail);
        headNavDate = navHeader.findViewById(R.id.nav_menu_date);
        headNavUser.setText("Signed in as:\n" + currentUser.getString("name") + " " + currentUser.getString("surname"));
        headNavDate.setText(DateTime.getFormalCurrentDate());
        /*Glide.with(ActivityDrawerLayout.this)
                .load(R.drawable.welcome_pic)
                .into(headNavPicture);*/

        /* --------------------------------------------------------------------------------------------- */

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_menu_home) {
            fragment = new UserProfile();
        }
        if (id == R.id.nav_menu_my_day || id == R.id.nav_menu_my_week || id == R.id.nav_menu_my_month ) {
            fragment = new CalendarView();
        }
        if (id == R.id.nav_menu_log_out){
            logout();
            return true;
        }
        if (id == R.id.nav_menu_edit_profile){
            fragment = new EditProfile();
        }
        if (id == R.id.nav_menu_privacy_settings){
            fragment = new Settings();
        }
        if (id == R.id.nav_menu_see_my_relations) {
            fragment = new Relations();
        }
        else if (id == R.id.nav_menu_see_my_groups) {
            fragment = new Groups();
        }
        fragmentManager.beginTransaction().replace(R.id.host_frame, fragment).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        ParseUser.logOut();
        Toast.makeText(ActivityDrawerLayout.this, "logout successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, loginOrSignup.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* ----------------- WE INFLATE A MENU CONTAINING THE SEARCH BAR ----------------- */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile, menu);

        /* -------------------------------------------------------------------------------------
        *             IF SEARCH BAR IS PRESSED WE INITIALIZE USER SEARCH FRAGMENT
        ------------------------------------------------------------------------------------- */
        searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UserSearchFragment();
                fragmentManager.beginTransaction().replace(R.id.host_frame, fragment).commit();
            }
        });
        return true;
    }

}