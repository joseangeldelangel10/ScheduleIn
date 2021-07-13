package com.example.schedulein_20;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.fragments.CalendarView;
import com.example.schedulein_20.fragments.EditProfile;
import com.example.schedulein_20.fragments.Groups;
import com.example.schedulein_20.fragments.Relations;
import com.example.schedulein_20.fragments.Settings;
import com.example.schedulein_20.fragments.UserProfile;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import java.util.Date;

public class ActivityDrawerLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "ActivityDrawerLayout";
    ParseUser currentUser = ParseUser.getCurrentUser();
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    View navHeader;
    TextView headNavUser;
    ImageView headNavPicture;
    TextView headNavDate;

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
        navigationView.setNavigationItemSelectedListener(this); // binding onNavigationItemSelected();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.host_frame, new UserProfile()).commit();

        /* ---------------------------------------------------------------------------------------------
                                      NAVIGATION HEADER ITEM CONTROL
        --------------------------------------------------------------------------------------------- */
        navHeader =  navigationView.getHeaderView(0);
        headNavUser = navHeader.findViewById(R.id.nav_menu_mail);
        headNavPicture = navHeader.findViewById(R.id.nav_menu_image);
        headNavDate = navHeader.findViewById(R.id.nav_menu_date);
        headNavUser.setText("Signed in as:\n" + currentUser.getString("name") + " " + currentUser.getString("surname"));
        headNavDate.setText(DateTime.getFormalCurrentDate());
        Glide.with(ActivityDrawerLayout.this)
                .load("https://static.vecteezy.com/system/resources/previews/002/275/847/original/male-avatar-profile-icon-of-smiling-caucasian-man-vector.jpg")
                .into(headNavPicture);

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
        Toast.makeText(ActivityDrawerLayout.this, "logout succesfull", Toast.LENGTH_SHORT);
        Intent intent = new Intent(this, loginOrSignup.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // we inflate our menu layout
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile, menu);
        //miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }

}