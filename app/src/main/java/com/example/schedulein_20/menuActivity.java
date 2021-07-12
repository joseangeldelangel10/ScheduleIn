package com.example.schedulein_20;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class menuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setLogo(R.drawable.calendar_icon);
        actionBar.setDisplayUseLogoEnabled(true);

        //RelativeLayout relative= findViewById(R.id.custom_toolbar_userp);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relative = (RelativeLayout) inflator.inflate(R.layout.custom_toolbar_menu, null);
        //relative.addView(new SearchView(getApplicationContext()));
        actionBar.setCustomView(relative);

        /* -----------------------------------ACTION BAR BUTTONS HANDLING ----------------------------- */

        // FLAG EXTRACTION:
        String origin_flag = this.getIntent().getExtras().getString("origin_flag");
        Log.e("menu", origin_flag);

        ImageButton butt_menu_icon = findViewById(R.id.menu_forward_arrow);
        butt_menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin_flag.equals("user_profile")) {
                    Intent intent = new Intent(menuActivity.this, userProfile.class);
                    startActivity(intent);
                }else {
                    Log.e("menu", "no flag available");
                }
            }
        });

        /* -------------------------------------------------------------------------------------------- */


    }
}