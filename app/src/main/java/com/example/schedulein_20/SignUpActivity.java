package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulein_20.parseDatabaseComms.UserSession;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "ActivitySignUp";
    private final String NAME_KEY = "name";
    private final String SURNAME_KEY = "surname";
    private EditText etName;
    private EditText etSurname;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button signUpButt;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;

        /* ----------------------------------------------------------------------------------------
                                        VIEW REFERENCING
      ---------------------------------------------------------------------------------------- */

        etName = findViewById(R.id.signupName);
        etSurname = findViewById(R.id.signupSurname);
        etUsername = findViewById(R.id.signupUsername);
        etEmail = findViewById(R.id.signupEmail);
        etPassword = findViewById(R.id.signupPassword);
        signUpButt = findViewById(R.id.signupButtonFinal);

        /* ----------------------------------------------------------------------------------------
                                 WHEN SIGNUP IS PRESSED CREATE A NEW USER
      ---------------------------------------------------------------------------------------- */

        signUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();

                UserSession.createUser(context, username, email, password, name, surname,  false,  scheduleInUserSignUpCallback());
                //createUser(username, email, password, name, surname);
            }
        });
    }


    private SignUpCallback scheduleInUserSignUpCallback(){
        return new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "new user created");
                    Toast.makeText(context, "User created!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(context, "there was a problem at sign up", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "st went wrong when creating your user");
                }
            }
        };
    }

}
