package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
/*------------------------------------------------------------------------
This activity's functionality consists only in one on click listener,
wich runs validateLocalCredentials function and triggers an intent
------------------------------------------------------------------------*/

public class loginActivity extends AppCompatActivity {
    private final String TAG = "loginActivity";
    private EditText etEmail;
    private EditText etPassword;
    private Button buttLoginFinal;
    private Button buttLoginWithGoogle;
    private Button buttLoginWithFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* ----------------------------------------------------------------------------------------
                                        VIEW REFERENCING
      ---------------------------------------------------------------------------------------- */

        etEmail = findViewById(R.id.etEmailLoginAct);
        etPassword = findViewById(R.id.etPasswordLoginAct);
        buttLoginFinal = findViewById(R.id.buttFinalLogin);
        buttLoginWithGoogle = findViewById(R.id.buttLoginWithGoogle);
        buttLoginWithFb = findViewById(R.id.buttLoginWithFb);

        /* ----------------------------------------------------------------------------------------
                                    CREATING TOOLBAR
      ---------------------------------------------------------------------------------------- */

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ----------------------------------------------------------------------------------------
                                 IF LOGIN PRESSED VALIDATE CREDENTIALS
      ---------------------------------------------------------------------------------------- */
        buttLoginFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOrUsernameText = etEmail.getText().toString();
                String passwordText = etPassword.getText().toString();
                Log.i(TAG, "email: " + emailOrUsernameText + "password: " + passwordText);
                loginUser(emailOrUsernameText, passwordText);
            }
        });
        /* ------------------------------------------------------------------------------------ */

    }

    /* ----------------------------------------------------------------------------------------
                                    VALIDATING CREDENTIALS
      ---------------------------------------------------------------------------------------- */

    private void loginUser(String username, String password) {
        Log.i(TAG, "attempting to login");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with login" + e, e);
                    Toast.makeText(loginActivity.this, "problem occurred while logging in", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(loginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}