package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class ActivitySignUp extends AppCompatActivity {
    private final String TAG = "ActivitySignUp";
    private final String NAME_KEY = "name";
    private final String SURNAME_KEY = "surname";
    EditText etNmae;
    EditText etSurname;
    EditText etUsername;
    EditText etEmail;
    EditText etPassword;
    Button signUpButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNmae = findViewById(R.id.signupName);
        etSurname = findViewById(R.id.signupSurname);
        etUsername = findViewById(R.id.signupUsername);
        etEmail = findViewById(R.id.signupEmail);
        etPassword = findViewById(R.id.signupPassword);
        signUpButt = findViewById(R.id.signupButtonFinal);

        signUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String name = etNmae.getText().toString();
                String surname = etSurname.getText().toString();

                createUser(username, email, password, name, surname);
            }
        });

    }

    public void createUser(String username, String email, String password, String name, String surname) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(NAME_KEY, name);
        user.put(SURNAME_KEY, surname);

        // Other fields can be set just like any other ParseObject,
        // using the "put" method, like this: user.put("attribute", "its value");
        // If this field does not exists, it will be automatically created

        user.signUpInBackground(e -> {
            if (e == null) {
                // Hooray! Let them use the app now.
                Log.e(TAG, "new user created");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                Log.e(TAG, "st went wrong when creating your user");
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}