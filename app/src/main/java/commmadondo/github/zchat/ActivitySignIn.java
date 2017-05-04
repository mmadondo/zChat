package commmadondo.github.zchat;
/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class ActivitySignIn extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    MainActivity mainAct;
    private static final String TAG = "ActivitySignIn";
    private static final int SIGN_IN_REQUEST_CODE = 9001; //SIGN_IN_REQUEST_CODE = RC_SIGN_IN
    private EditText editTextEmail;
    private EditText editTextPassword;
    private SignInButton mSignInButton;
    private Button buttonLogin;
    private Button buttonCreateLogin;
    private TextView textViewStatus;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);

        //setting up the views
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button); //google sign in btn

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        /*
        * user login
        */
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("CIS3334", "normal login ");
                signIn1(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                mainAct.displayChatMessages();
            }
        });
                /*
        * creates account using user email and password
         */
        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Log.d("CIS3334", "Create Account ");
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    //Initiate sign in with google
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                break;
        }
    }
    /**
     * Add the required signIn method
     * that actually presents the user with the Google Sign-In UI.
     *
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }
    //to handle the sign in result and use the account to authenticate with firebase if sign in was successful
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    //firebaseAuthWithGoogle method to authenticate with the signed in Google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(ActivitySignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(ActivitySignIn.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    //==========================================================
    /*
 *  create account using user email and password after validating them
 *  @param email is the email address used to create user account
 *  @param password is the password used to create user account
 */
    private void createAccount(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // account creation successful, update UI with the signed-in user's information
                        // Log.d("CIS3334", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        textViewStatus.setText("Account creation successful");

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //    Toast.makeText(MainActivity.this, "Authentication failed",
                            //        Toast.LENGTH_SHORT).show(); //display message

                            textViewStatus.setText("Authentication failed. Please try again");
                        }

                        // ...
                    }
                });
    }
        /*
     * sign in existing user with email and password after validating them
     * @param email is the email address used for sign in
     * @param password is the password used to sign user in
     */
    private void signIn1(String email, String password){
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in success, update UI with the signed-in user's information

                        //======Removing log call:
                        // Log.d("CIS3334", "signInWithEmail:onComplete:" + task.isSuccessful());
                        textViewStatus.setText("Sign in successful!");


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            //========Removing Log and Toast calls
                            // Log.w("CIS3334", "signInWithEmail:failed", task.getException());
                            //  Toast.makeText(MainActivity.this, "Authentication failed",
                            //         Toast.LENGTH_SHORT).show();  //display message

                            textViewStatus.setText("Authentication failed. Please try again");
                        }

                        // ...
                    }
                });
    }
}
