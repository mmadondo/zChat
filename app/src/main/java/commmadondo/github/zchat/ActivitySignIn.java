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

import static android.app.Activity.RESULT_OK;

public class ActivitySignIn extends AppCompatActivity {
       // implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    MainActivity mainAct;
    private static final String TAG = "ActivitySignIn";
    private static final int SIGN_IN_REQUEST_CODE = 9001;

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonCreateLogin;
    //TextView textViewStatus;
    private FirebaseAuth mFirebaseAuth; // Firebase instance variables

    // private SignInButton mSignInButton;
    //private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);

        //setting up the views
        //textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        mFirebaseAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        setupButtonLogin();
        setupCreateAccount();

    }

    private void setupButtonLogin(){
        //setup the button to enable a user to log in
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(editTextEmail.getText().toString().equals("")){ //email is required
                    editTextEmail.setError("Can't be blank!");
                }

                else if(editTextPassword.getText().toString().equals("")){ //password is required
                    editTextPassword.setError("Can't be blank");
                }

                else{
                    if(!(editTextEmail.length()<5) || !(editTextPassword.length()>5)){

                        signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                        mainAct.onActivityResult(SIGN_IN_REQUEST_CODE, RESULT_OK, null); //want to display messages after login

                    }else{

                        if(editTextEmail.length()<5){
                            editTextEmail.setError("At Least 5 characters required");
                        }

                        else{
                            editTextPassword.setError("At Least 5 characters required");
                        }
                    }
  }
            }
        });
    }

    private void setupCreateAccount(){
        //setup a button so user creates account using email and password
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);
        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d("CIS3334", "Create Account ");
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }

        });
    }

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
                        Log.d("CIS3334", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()){
                            //textViewStatus.setText("Account creation successful");
                            Toast.makeText(ActivitySignIn.this, "Account creation successful",
                                    Toast.LENGTH_SHORT).show(); //display message
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                             Toast.makeText(ActivitySignIn.this, "Authentication failed",
                                   Toast.LENGTH_SHORT).show(); //display message

                            //textViewStatus.setText("Authentication failed. Please try again");
                        }

                    }
                });
    }
    /*
 * sign in existing user with email and password after validating them
 * @param email is the email address used for sign in
 * @param password is the password used to sign user in
 */
    private void signIn(String email, String password){
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Sign in success, update UI with the signed-in user's information
                        if(task.isSuccessful()){
                            Log.d("CIS3334", "signInWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(ActivitySignIn.this, "Sign in successful!", Toast.LENGTH_SHORT );
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else if (!task.isSuccessful()) {
                            Log.w("CIS3334", "signInWithEmail:failed", task.getException());
                             Toast.makeText(ActivitySignIn.this, "Authentication failed. Please try again", Toast.LENGTH_SHORT).show();  //display message
                        }

                    }
                });
    }
}
