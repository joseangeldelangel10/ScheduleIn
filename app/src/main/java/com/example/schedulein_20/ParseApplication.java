package com.example.schedulein_20;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.schedulein_20.models.Events;
import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.TodoItems;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ParseObject.registerSubclass(Events .class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(TodoItems.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId( getString(R.string.application_id))
                .clientKey( getString(R.string.client_key))
                .server(getString(R.string.server))
                .build()
        );
    }
}
