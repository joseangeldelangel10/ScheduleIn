package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    Button buttLoginFinal;
    Button buttLoginWithGoogle;
    Button buttLoginWithFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailLoginAct);
        etPassword = findViewById(R.id.etPasswordLoginAct);
        buttLoginFinal = findViewById(R.id.buttFinalLogin);
        buttLoginWithGoogle = findViewById(R.id.buttLoginWithGoogle);
        buttLoginWithFb = findViewById(R.id.buttLoginWithFb);

        buttLoginFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOrUsernameText = etEmail.getText().toString();
                String passwordText = etPassword.getText().toString();
                Log.e("loginAct", "email: " + emailOrUsernameText + "password: " + passwordText);
                if ( validateLocalCredentials(emailOrUsernameText, passwordText) ){
                    Intent intent = new Intent(loginActivity.this, userProfile.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateLocalCredentials(String emailOrUsernameText, String passwordText) {
        //Log.e("loginAct", "entered method");
        if( emailOrUsernameText.equals("joseangeldelangel10@gmail.com") && passwordText.equals("12345") ){
            //Log.e("loginAct", "entered conditional");
            return true;
        }
        return false;
    }


}