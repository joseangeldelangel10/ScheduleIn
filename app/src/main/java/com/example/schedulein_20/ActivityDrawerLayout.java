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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ActivityDrawerLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "ActivityDrawerLayout";
    NavigationView navigationView;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        userImage = findViewById(R.id.nav_menu_image);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.calendar_icon);
        //toolbar.setDisplayUseLogoEnabled(true);

        DrawerLayout drawer = findViewById(R.id.my_drawer_layout);
        //drawerLayout.setStatusBarBackgroundColor(getColor(R.color.purple_200));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationView.setNavigationItemSelectedListener(this);

        //navigationView.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.host_frame, new UserProfile()).commit();

        /*Glide.with(ActivityDrawerLayout.this)
                .load("https://static.vecteezy.com/system/resources/previews/002/275/847/original/male-avatar-profile-icon-of-smiling-caucasian-man-vector.jpg")
                .into(userImage);*/
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_menu_home) {
            fragment = new UserProfile();
        }
        if (id == R.id.nav_menu_my_day || id == R.id.nav_menu_my_week || id == R.id.nav_menu_my_month ) {
            fragment = new CalendarView();
        }
        if (id == R.id.nav_menu_log_out){
            ParseUser.logOut();
            Toast.makeText(ActivityDrawerLayout.this, "logout succesfull", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, loginOrSignup.class);
            startActivity(intent);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout() {
        Log.e(TAG, "logging out");
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