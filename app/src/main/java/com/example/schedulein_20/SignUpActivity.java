package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulein_20.models.ParseUserExtraAttributes;
import com.example.schedulein_20.parseDatabaseComms.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "ActivitySignUp";
    private final String NAME_KEY = "name";
    private final String SURNAME_KEY = "surname";
    public static final int G_SIGNIN_RC = 100;
    private EditText etName;
    private EditText etSurname;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button signUpButt;
    private Button signInWithGoogleButton;
    Context context;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;

        /* ----------------------------------------------------------------------------------------
                                    CREATING TOOLBAR
      ---------------------------------------------------------------------------------------- */

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_my_awesome_toolbar);
        setSupportActionBar(toolbar);

        /* ----------------------------------------------------------------------------------------
                                        VIEW REFERENCING
      ---------------------------------------------------------------------------------------- */

        etName = findViewById(R.id.signupName);
        etSurname = findViewById(R.id.signupSurname);
        etUsername = findViewById(R.id.signupUsername);
        etEmail = findViewById(R.id.signupEmail);
        etPassword = findViewById(R.id.signupPassword);
        signUpButt = findViewById(R.id.signupButtonFinal);
        signInWithGoogleButton = findViewById(R.id.buttSignInWithGoogleSU);


        /* ----------------------------------------------------------------------------------------
                                        GOOGLE SIGN IN CONFIGURATION
      ---------------------------------------------------------------------------------------- */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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


        signInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, G_SIGNIN_RC);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == G_SIGNIN_RC && resultCode == RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            Log.e(TAG, "  -------------------------------------------------------- ");
            Log.e(TAG, "  ----------- GOOGLE USER SUCCESSFULLY SIGNED IN ----------- ");
            Log.e(TAG, "Email: " + account.getEmail() );
            Log.e(TAG, "Name: " + account.getDisplayName() );
            Log.e(TAG, "Name2: " + account.getGivenName() ); // Jose Angel
            Log.e(TAG, "Surname: " + account.getFamilyName() ); // Del Angel
            Log.e(TAG, "ID: " + account.getId() ); // Del Angel
            Log.e(TAG, "ID: " + account.getAccount() ); // Del Angel
            String email = account.getEmail();
            String name = account.getGivenName();
            String surname = account.getFamilyName();
            String password = account.getId();

            checkIfGoogleUserIsNew(email, password, name, surname);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void checkIfGoogleUserIsNew(String email, String password, String name, String surname){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseUserExtraAttributes.KEY_GOOGLE_USER, true);
        query.whereEqualTo(ParseUserExtraAttributes.KEY_EMAIL,email);
        query.whereEqualTo(ParseUserExtraAttributes.KEY_NAME,name);
        query.whereEqualTo(ParseUserExtraAttributes.KEY_SURNAME,surname);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Toast.makeText(context, "something went wrong connecting with google", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (objects.size() == 0){
                    //Uri googlePhoto = account.getPhotoUrl();
                    //File fileFromPhoto = getPhotoFileUri(googlePhoto.getPath());
                    //ParseFile newProfilePic = new ParseFile(fileFromPhoto);

                    UserSession.createUser(context, email, email, password, name, surname,true, scheduleInUserSignUpCallback());
                    return;
                }
                UserSession.loginUser(email, password, loginGoogleUserCallback() );
            }
        });
    }

    private LogInCallback loginGoogleUserCallback(){
        return new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with login" + e, e);
                    Toast.makeText(context, "problem occurred while logging in with google", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Login with google successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

}
