package com.example.schedulein_20.RESTclientOpetations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.example.schedulein_20.DrawerLayoutActivity;
import com.example.schedulein_20.GoogleCalendarClient;
import com.example.schedulein_20.R;
import com.example.schedulein_20.ScheduleInGCalendarAPIApp;
import com.example.schedulein_20.parseDatabaseComms.UserSession;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

public class APISession extends OAuthLoginActionBarActivity<GoogleCalendarClient> {

    private final String TAG = "APISession";
    Context context;

    public APISession(){
        this.context = this;
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_a_p_i_session);
    }*/

    @Override
    public void onLoginSuccess() {
        //Toast.makeText(context, "connected succesfully with google calendar", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "connected succesfully with google calendar");

        ParseUser currentUser  = ParseUser.getCurrentUser();
        ArrayList<String> calendarsLinked = (ArrayList<String>) currentUser.get("calendarsLinked");
        if (!calendarsLinked.contains("google")){
            calendarsLinked.add("google");
            UserSession.updateCurrentUserLinkedCalendars(context, calendarsLinked, registerGoogleCalendarLinkCallback());
        }

        Log.i("OAuth", "logged in successfully");
        Intent i = new Intent(context, DrawerLayoutActivity.class);
        startActivity(i);

    }

    private SaveCallback registerGoogleCalendarLinkCallback(){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.e(TAG, "google calendar registered in current user");
                }else {
                    Log.e(TAG, "problem registering in current user");
                }
            }
        };
    }

    @Override
    public void onLoginFailure(Exception e) {
        //Toast.makeText(context, "something went wrong when conecting with google", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "failed when connecting with calendar");
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest() {
        ScheduleInGCalendarAPIApp.getRestClient(context).connect();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        return;
    }
}
