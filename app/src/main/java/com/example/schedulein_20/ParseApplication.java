package com.example.schedulein_20;

import android.app.Application;

import com.example.schedulein_20.models.Events;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Events .class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId( getString(R.string.application_id))
                .clientKey( getString(R.string.client_key))
                .server(getString(R.string.server))
                .build()
        );
    }
}
