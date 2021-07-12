package com.example.schedulein_20;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class userProfile extends AppCompatActivity {
    ImageView ivUserImage;
    TextView greeting;
    TextView user_info;
    Button cancel_next_event;
    ScrollView week_schedule;
    Button new_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ivUserImage = findViewById(R.id.ivUserImage);
        greeting = findViewById(R.id.greeting);
        user_info = findViewById(R.id.user_info);
        cancel_next_event = findViewById(R.id.cancel_next_event);
        new_event = findViewById(R.id.create_new_event);

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        CUSTOM ACTION BAR SECTION
        ------------------------------------------------------------------------------------------------------------------------------------*/
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setLogo(R.drawable.calendar_icon);
        //actionBar.setDisplayUseLogoEnabled(true);

        //RelativeLayout relative= findViewById(R.id.custom_toolbar_userp);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relative = (RelativeLayout) inflator.inflate(R.layout.cutom_toolbar_userp, null);
        //relative.addView(new SearchView(getApplicationContext()));
        actionBar.setCustomView(relative);

                /* -----------------------------------ACTION BAR BUTTONS HANDLING ----------------------------- */

                ImageButton butt_menu_icon = findViewById(R.id.butt_menu_icon);
                butt_menu_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("menu pressed");
                        String origin_flag = "user_profile";
                        Intent intent = new Intent(userProfile.this, menuActivity.class);
                        intent.putExtra("origin_flag", origin_flag);
                        startActivity(intent);
                    }
                });

                /* -------------------------------------------------------------------------------------------- */

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        RETRIEVING THE DATA TO GENERATE THE VIEWS
        ------------------------------------------------------------------------------------------------------------------------------------*/

        String current_time = "morning";
        String user_name = "Jose";
        String current_event = "current event";
        String next_event = "other event";

        /* ------------------------------------------------------------------------------------------------------------------------------------
                                                        BINDING DATA TO THE VIEWS
        ------------------------------------------------------------------------------------------------------------------------------------*/

        Glide.with(userProfile.this)
                .load("https://static.vecteezy.com/system/resources/previews/002/275/847/original/male-avatar-profile-icon-of-smiling-caucasian-man-vector.jpg")
                .into(ivUserImage);

        greeting.setText("Good " + current_time + " " + user_name +"!");
        user_info.setText("- Now attending to: " + current_event + "\n- Next event: " + next_event);

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