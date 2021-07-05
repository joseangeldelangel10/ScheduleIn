package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class loginOrSignup extends AppCompatActivity {
    ImageView ivLoginImages;
    Button buttLogin;
    Button buttSignup;
    TextView tvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);

        ivLoginImages = findViewById(R.id.ivLogImages);
        buttLogin = findViewById(R.id.buttLoginAct);
        buttSignup = findViewById(R.id.buttSignup);
        tvTip = findViewById(R.id.tvTipOfTheDay);

        // R.drawable.ic_launcher_background
        Glide.with(loginOrSignup.this)
                .load("https://miviaje.com/wp-content/uploads/2016/05/glaciar-mendehall.jpg")
                .into(ivLoginImages);

        tvTip.setText("tip of the day: try to debug your apps while running them");


        /* -------------------------------------- IF LOGIN PRESSED -------------------------------------------- */
                buttLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(loginOrSignup.this, loginActivity.class);
                        startActivity(intent);
                    }
                });
        /* -------------------------------------------------------------------------------------------------- */

        /* -------------------------------------- IF SIGNUP PRESSED -------------------------------------------- */
        buttSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LofinOrSignUp", "Sign up pressed");
            }
        });
        /* -------------------------------------------------------------------------------------------------- */


    }
}