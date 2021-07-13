package com.example.schedulein_20;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

/*------------------------------------------------------------------------
This activity's functionality consists only in two on click listeners,
one triggers an intent and the other triggers a log
------------------------------------------------------------------------*/

public class loginOrSignup extends AppCompatActivity {
    private final String TAG = "loginOrSignup";
    public final int LOGIN_REQUEST_CODE = 10;
    public final int SiGNUP_REQUEST_CODE = 10;
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

        Glide.with(loginOrSignup.this)
                .load("https://miviaje.com/wp-content/uploads/2016/05/glaciar-mendehall.jpg")
                .into(ivLoginImages);

        tvTip.setText("tip of the day: try to debug your apps while running them");

        /* ------------------ WE EVALUATE IF THERE IS ALREADY A USER --------------------- */
        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }
        /* ------------------------------------------------------------------------------- */

        /* -------------------------------------- IF LOGIN PRESSED -------------------------------------------- */
        buttLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginOrSignup.this, loginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
        /* -------------------------------------------------------------------------------------------------- */

        /* -------------------------------------- IF SIGNUP PRESSED -------------------------------------------- */
        buttSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Sign up pressed");
                Intent intent = new Intent(loginOrSignup.this, ActivitySignUp.class);
                startActivityForResult(intent, SiGNUP_REQUEST_CODE);
            }
        });
        /* -------------------------------------------------------------------------------------------------- */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE || requestCode == SiGNUP_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                goMainActivity();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goMainActivity(){
        Intent intent = new Intent(loginOrSignup.this, ActivityDrawerLayout.class);
        startActivity(intent);
        finish();
    }
}