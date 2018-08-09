package com.collengine.sulu.rideshare.login;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.collengine.sulu.rideshare.MainActivity;
import com.collengine.sulu.rideshare.SelectActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


import com.collengine.sulu.rideshare.R;

public class UserDataActivity extends AppCompatActivity {
    private EditText usernameEv;
    private EditText firstnameEv;
    private EditText lastnameEv;
    private Button nextBtn;

    private String uid;
    private String username;
    private String firstname;
    private String lastname;

    private FirebaseAuth firebaseAuth;
    public static final String USERS_CHILD = "Vaultusers";
    //defining a database reference
    private DatabaseReference databaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        Intent myIntent = getIntent();
        uid = myIntent.getStringExtra("USER_ID");

        databaseReference = FirebaseDatabase.getInstance().getReference ();

        usernameEv = (EditText)findViewById(R.id.login_user_username);
        firstnameEv = (EditText)findViewById(R.id.loging_user_firstname);
        lastnameEv = (EditText)findViewById(R.id.login_user_lstname);
        nextBtn = (Button)findViewById(R.id.next_button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });


    }

    public void saveUserInformation( ) {

        username = usernameEv.getText().toString ().trim();
        firstname = firstnameEv.getText().toString ().trim();
        lastname = lastnameEv.getText().toString ().trim();

        if (TextUtils.isEmpty(username)) {
//            Toast.makeText(getApplicationContext(), "Enter user name!", Toast.LENGTH_SHORT).show();
            usernameEv.setError("Enter user name!");

            return;
        }
        if (TextUtils.isEmpty(firstname)) {
//            Toast.makeText(getApplicationContext(), "Enter your first name!", Toast.LENGTH_SHORT).show();
            firstnameEv.setError("Enter First name!");
            return;
        }
        if (TextUtils.isEmpty(lastname)) {
//            Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
            lastnameEv.setError("Enter Last name!");
            return;
        }



        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("userName",username );
        userData.put("firstName",firstname );
        userData.put("lastName",lastname );

         databaseReference.child(USERS_CHILD).child(uid).updateChildren(userData);

        Toast.makeText( this , "Information Saved..." ,
                Toast.LENGTH_LONG ). show ();

        Intent myIntent = new Intent(UserDataActivity.this, SelectActivity.class);
        myIntent.putExtra("USER_ID", uid);
        startActivity(myIntent);

        finish();
        return;



    }
}
