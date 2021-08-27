package com.example.schedulein_20;

import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.example.schedulein_20.models.DateTime;
import com.example.schedulein_20.models.Events;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.BaseApi;
import com.google.api.services.calendar.CalendarScopes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

// Uses CodePath's OAuth Handler: https://github.com/codepath/android-oauth-handler
public class GoogleCalendarClient extends OAuthBaseClient {
    private static final String TAG = "GoogleCalendarClient";

    public static final BaseApi REST_API_INSTANCE = GoogleApi20.instance();
    public static final String REST_URL = "https://www.googleapis.com/calendar/v3";
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;
    public static final String OAUTH2_SCOPE = CalendarScopes.CALENDAR;

    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";
    //public static final String REST_CALLBACK_URL_TEMPLATE = "https://localhost";
    public static final String REST_CALLBACK_URL_TEMPLATE = "https://localhost";

    private MediaType JSONObject = MediaType.parse("application/json; charset=utf-8");

    public GoogleCalendarClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                OAUTH2_SCOPE,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    public void getPrimaryCalendar(JsonHttpResponseHandler handler){
        String apiUrl = getApiUrl("users/me/calendarList");
        RequestParams params = new RequestParams();
        params.put("minAccessRole", "owner");
        client.get(apiUrl, params, handler);
    }

    public void getCurrentWeekCalendarEvents(String calendarId, JsonHttpResponseHandler handler){
        String weekStart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(DateTime.weekStart());
        String weekEnd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(DateTime.weekEnding());
        String apiUrl = getApiUrl("calendars/" + calendarId + "/events");
        RequestParams params = new RequestParams();
        //params.put("maxResults", 20);
        params.put("showDeleted", false);
        params.put("timeMin", weekStart );
        params.put("timeMax", weekEnd );
        client.get(apiUrl, params, handler);
    }



    // Can't delete or update Google Calendar events using this library

}
