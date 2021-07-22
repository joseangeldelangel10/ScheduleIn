package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.LoginOrSignupActivity;
import com.example.schedulein_20.R;
import com.parse.ParseUser;

public class UserSession {

    public static void logout(Context context) {
        ParseUser.logOut();
        Toast.makeText(context, context.getResources().getString(R.string.logout_successful), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, LoginOrSignupActivity.class);
        context.startActivity(intent);
    }
}
