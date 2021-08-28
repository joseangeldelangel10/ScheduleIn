package com.example.schedulein_20.parseDatabaseComms;

import android.content.Context;
import android.widget.Toast;

import com.example.schedulein_20.models.Group;
import com.example.schedulein_20.models.TodoItems;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class TodoQueries {

    public static void getTodoItems(Context context, ParseUser user, FindCallback<TodoItems> callback){
        ParseQuery<TodoItems> query = ParseQuery.getQuery(TodoItems.class);

        query.whereEqualTo(TodoItems.KEY_USER, user);
        query.findInBackground(callback);
    }

    public static void saveTodoItem(Context context, ParseUser user, String description){
        TodoItems newItem = new TodoItems();
        newItem.setUser(user);
        newItem.setDescription(description);

        newItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(context, "item saved successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "we couldn't save your event :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void removeTodoItem(Context context, TodoItems item) {
        ParseQuery<TodoItems> query = ParseQuery.getQuery(TodoItems.class);
        query.getInBackground(item.getObjectId(), (object, e) -> {
            if (e == null) {
                object.deleteInBackground();
            }else{
                //Something went wrong while retrieving the Object
                Toast.makeText(context, "Error completing item", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
