package commmadondo.github.zchat;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import static android.app.Activity.RESULT_OK;

/**
 * Group chat app with one chat room or channel open to all users (with identities hidden)
 */
public class MainActivity extends AppCompatActivity {

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseListAdapter<ChatMessage> adapter;

    public static final String ANONYMOUS = "anonymous";


    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_REQUEST_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //Alternative: mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()


        if (mFirebaseUser == null) {
            // Start sign in/sign up activity
            startActivityForResult(new Intent(MainActivity.this, ActivitySignIn.class), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in. Therefore, display a welcome Toast
            try {

                Toast.makeText(this, "Welcome to zChat! " + mFirebaseUser.getEmail(), Toast.LENGTH_LONG).show();

                displayChatMessages(); // Load chat room contents

            } catch (NullPointerException e) {

                System.out.print("NullPointerException caught");
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                try{
                    // Read the input field and push a new instance of ChatMessage to the Firebase database
                    FirebaseDatabase.getInstance().getReference().push().setValue(
                            new ChatMessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    );

                    input.setText("");  // Clear the input

                } catch(NullPointerException e){
                    System.out.print("NullPointerException caught");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show(); //display message
                displayChatMessages();  // Load chat room contents

            } else {

                Toast.makeText(this, "Sign in failed. Please try again later.", Toast.LENGTH_LONG).show(); //display message
                // Close the app
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); //instantiate menu XML file into a Menu object.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                mFirebaseAuth.signOut(); //log user out
                String signOutStr = "Hey " +  mFirebaseUser.getEmail() + "! You have been signed out.";
                Toast.makeText(MainActivity.this, signOutStr, Toast.LENGTH_LONG).show(); //display message

                startActivity(new Intent(MainActivity.this, ActivitySignIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


   public void displayChatMessages() {
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages); //display list of messages posted

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                model.setMessageUser(mFirebaseUser.getEmail()); //display email instead

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

}

