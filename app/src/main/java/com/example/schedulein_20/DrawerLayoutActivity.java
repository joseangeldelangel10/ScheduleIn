package com.example.schedulein_20;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.schedulein_20.RESTclientOpetations.APISession;
import com.example.schedulein_20.fragments.CalendarViewFragment;
import com.example.schedulein_20.fragments.DayViewFragment;
import com.example.schedulein_20.fragments.EditProfileFragment;
import com.example.schedulein_20.fragments.GroupsFragment;
import com.example.schedulein_20.fragments.RelationsFragment;
import com.example.schedulein_20.fragments.SettingsFragment;
import com.example.schedulein_20.fragments.TodosFragment;
import com.example.schedulein_20.fragments.UserProfileFragment;
import com.example.schedulein_20.fragments.UserSearchFragment;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.parseDatabaseComms.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class DrawerLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "ActivityDrawerLayout";
    public ParseUser currentUser = ParseUser.getCurrentUser();
    public Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private View navHeader;
    private TextView headNavUser;
    public static MenuItem searchItem;
    public static MenuItem progressItem;
    public static SearchView searchView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        context = this;
        /* ---------------------------------------------------------------------------------------------
                                      REPLACING ACTION BAR FOR TOOLBAR TO USE NAV DRAWER
        --------------------------------------------------------------------------------------------- */
        toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ---------------------------------------------------------------------------------------------
                         REQUESTING USER ACCESS TO GOOGLE CALENDAR IF CONNECTED WITH G CALENDAR
        --------------------------------------------------------------------------------------------- */
        ArrayList<String> linkedCalendars = (ArrayList<String>) currentUser.get("calendarsLinked");
        if(linkedCalendars.contains("google") && ScheduleInGCalendarAPIApp.getRestClient(context).checkAccessToken() == null){
            APISession apiSession = new APISession();
            apiSession.loginToRest();
        }

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
        fragmentManager.beginTransaction().replace(R.id.host_frame, new UserProfileFragment()).commit();

        /* ---------------------------------------------------------------------------------------------
                                      NAVIGATION HEADER ITEM CONTROL
        --------------------------------------------------------------------------------------------- */
        navHeader =  navigationView.getHeaderView(0);
        headNavUser = navHeader.findViewById(R.id.nav_menu_mail);
        headNavUser.setText("Signed in as:\n" + currentUser.getString("name") + " " + currentUser.getString("surname"));

        /* --------------------------------------------------------------------------------------------- */


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_menu_home) {
            fragment = new UserProfileFragment();
        }
        else if (id == R.id.nav_menu_my_week || id == R.id.nav_menu_my_month ) {
            fragment = CalendarViewFragment.newInstance(DateTime.weekStart(), DateTime.weekEnding());
        }
        else if (id == R.id.nav_menu_my_day) {
            fragment = DayViewFragment.newInstance(DateTime.currentDate());
        }
        else if (id == R.id.nav_menu_todo_list) {
            fragment = new TodosFragment();
        }
        else if (id == R.id.nav_menu_log_out){
            if ((boolean) currentUser.get("googleUser")){
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut();
            }
            UserSession.logout(context);
            finish();
            return true;
        }
        else if (id == R.id.nav_menu_edit_profile){
            fragment = new EditProfileFragment();
        }
        else if (id == R.id.nav_menu_privacy_settings){
            fragment = new SettingsFragment();
        }
        else if (id == R.id.nav_menu_see_my_relations) {
            fragment = new RelationsFragment();
        }
        else if (id == R.id.nav_menu_see_my_groups) {
            fragment = new GroupsFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.host_frame, fragment).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        progressItem = menu.findItem(R.id.miActionProgress);
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

    public static void showProgressBar() {
        progressItem.setVisible(true);
    }

    public static void hideProgressBar() {
        progressItem.setVisible(false);
    }


}
