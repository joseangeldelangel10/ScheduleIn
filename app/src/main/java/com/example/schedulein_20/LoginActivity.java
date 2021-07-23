package com.example.schedulein_20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.List;
/*------------------------------------------------------------------------
This activity's functionality consists only in one on click listener,
wich runs validateLocalCredentials function and triggers an intent
------------------------------------------------------------------------*/

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "loginActivity";
    public static final int G_SIGNIN_RC = 100;
    private EditText etEmail;
    private EditText etPassword;
    private Button buttLoginFinal;
    private Button buttLoginWithGoogle;
    private Button buttLoginWithFb;
    Context context;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        /* ----------------------------------------------------------------------------------------
                                        VIEW REFERENCING
      ---------------------------------------------------------------------------------------- */

        etEmail = findViewById(R.id.etEmailLoginAct);
        etPassword = findViewById(R.id.etPasswordLoginAct);
        buttLoginFinal = findViewById(R.id.buttFinalLogin);
        buttLoginWithGoogle = findViewById(R.id.buttLoginWithGoogle);
        buttLoginWithFb = findViewById(R.id.buttLoginWithFb);

        /* ----------------------------------------------------------------------------------------
                                        GOOGLE SIGN IN CONFIGURATION
      ---------------------------------------------------------------------------------------- */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                UserSession.loginUser(emailOrUsernameText, passwordText, loginUserCallback());
            }
        });

        buttLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, G_SIGNIN_RC);
            }
        });
        /* ------------------------------------------------------------------------------------ */

    }

    /* ----------------------------------------------------------------------------------------
                                    VALIDATING CREDENTIALS
      ---------------------------------------------------------------------------------------- */

    private LogInCallback loginUserCallback(){
        return new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with login" + e, e);
                    Toast.makeText(LoginActivity.this, "problem occurred while logging in", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
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

                    UserSession.createUser(context, email, email, password, name, surname,true, googleUserSignUpCallback());

                    //ParseFile googlePhotoFile = new ParseFile(fileFromPhoto);
                    return;
                }
                UserSession.loginUser(email, password, loginUserCallback());
            }
        });
    }

    private SignUpCallback googleUserSignUpCallback(){
        return new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with login" + e, e);
                    Toast.makeText(LoginActivity.this, "problem occurred while logging in with google", Toast.LENGTH_LONG).show();
                    return;
                }
                //ParseUser.getCurrentUser().put("profilePic", googlePhotoFile);
                Toast.makeText(LoginActivity.this, "Login with google successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


}
