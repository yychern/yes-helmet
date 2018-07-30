package com.collengine.sulu.rideshare.login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import com.collengine.sulu.rideshare.R;
import com.collengine.sulu.rideshare.service.MyFirebaseInstanceIDService;
import com.collengine.sulu.rideshare.util.Helper;
import com.collengine.sulu.rideshare.util.MySharedPreference;
import com.collengine.sulu.rideshare.util.TokenObject;

public class RegisterActivity extends
        AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    public String userUID;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private static final String TAG = "Register";

    private RequestQueue queue;
    private String TOKENS = "tokens";

    private TokenObject tokenObject;
    private String UIDCONSTANT = "uid";
    public static final String idPreference = "idPrefs";
    public static final String USERS_CHILD = "Vaultusers";

    private MySharedPreference mySharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
//Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.contains("gmail")){
                    inputEmail.setError("No need to create an account if you have a gmail account click on sign in with Google button in login page");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(),
                            "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
//create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new
                                OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull
                                                                   Task<AuthResult> task) {
                                        Toast.makeText(RegisterActivity.this,
                                                "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
// If sign in fails, display a message to the user. If sign in succeeds
// the auth state listener will be notified and logic to handle the
// signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Authentication failed." + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {

                                            final FirebaseUser user = auth.getCurrentUser();
                                            user.sendEmailVerification().addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(RegisterActivity.this, "verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                    }else{
                                                        //   Log.e("SignupActivity", "send Email verification", task.getException());
                                                        Toast.makeText(RegisterActivity.this, "Failed to send verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });


                                            FirebaseAuth.getInstance().signOut();


                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }
                                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    private void sendTheRegisteredTokenToWebServer(final String token, final String userUID){
        queue = Volley.newRequestQueue(getApplicationContext());
        mySharedPreference = new MySharedPreference(getApplicationContext());

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //     Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);

                if (null == tokenObject) {
                    //         Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(false);
                    //   Log.e(TAG, "The server HAS NOT YET taken your Token " );

                } else {
                    //         Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(true);
                    //   Log.e(TAG, "The server HAS YOUR Token " );
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Helper.TOKEN_TO_SERVER, token);
                params.put(Helper.UID_TO_SERVER, userUID);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }
}
