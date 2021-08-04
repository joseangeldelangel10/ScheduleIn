package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.GoogleCalendarClient;
import com.example.schedulein_20.LoginActivity;
import com.example.schedulein_20.LoginOrSignupActivity;
import com.example.schedulein_20.R;
import com.example.schedulein_20.ScheduleInGCalendarAPIApp;
import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;

public class UserSession {

    public static void logout(Context context) {
        if (ScheduleInGCalendarAPIApp.getRestClient(context).checkAccessToken() != null){
            ScheduleInGCalendarAPIApp.getRestClient(context).clearAccessToken();
        }
        ParseUser.logOut();
        Toast.makeText(context, context.getResources().getString(R.string.logout_successful), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, LoginOrSignupActivity.class);
        context.startActivity(intent);
    }

    public static void createUser(Context context, String username, String email, String password, String name, String surname, boolean googleUser, SignUpCallback callback) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(ParseUserExtraAttributes.KEY_NAME, name);
        user.put(ParseUserExtraAttributes.KEY_SURNAME, surname);
        user.put(ParseUserExtraAttributes.KEY_GOOGLE_USER, googleUser);

        if (callback != null){
            user.signUpInBackground(callback);
        }else {
            user.signUpInBackground();
        }
    }

    public static void loginUser(String username, String password, LogInCallback callback) {
        ParseUser.logInInBackground(username, password, callback);
    }


    public static void updateCurrentUser(Context context, String name, String surname, String username, String email, File newPhoto, SaveCallback callback) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("name", name);
            currentUser.put("surname", surname);
            currentUser.put("username", username);
            currentUser.put("email", email);
            currentUser.put("profilePic", new ParseFile(newPhoto));
            // Saves the object.

            if(callback != null){
                currentUser.saveInBackground(callback);
            }else {
                currentUser.saveInBackground(e -> {
                    if (e == null) {
                        //Save successfull
                        Toast.makeText(context, "user updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // Something went wrong while saving
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }


}
