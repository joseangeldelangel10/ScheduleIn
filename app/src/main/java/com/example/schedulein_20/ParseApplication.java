package com.example.schedulein_20;

import android.app.Application;
import com.parse.Parse;



public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId( getString(R.string.application_id))
                .clientKey( getString(R.string.client_key))
                .server(getString(R.string.server))
                .build()
        );
    }
}
