package com.collengine.sulu.rideshare.login;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.collengine.sulu.rideshare.model.UserInformation;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;

import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;

import com.facebook.FacebookException;

import com.facebook.login.LoginManager;

import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private String userName, userEmail;

    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;

    private static final String TAG = "logsinActivity";
    private static final int RC_SIGN_IN = 9001;

    private RequestQueue queue;
    private String TOKENS = "tokens";

    private TokenObject tokenObject;
    public static final String FOLLOWING = "following";
    private MySharedPreference mySharedPreference;
    private String userUID;
    FirebaseAuth mFirebaseAuth;


    private UserInformation userInformation;
    private SignInButton mSignInButton;


    public static final String USERS_CHILD = "Vaultusers";
    private DatabaseReference databaseReference ;

    private String UIDCONSTANT = "uid";
    public static final String idPreference = "idPrefs";
    private SharedPreferences idSharedpreferences;
    private String verified ;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//Get Firebase auth instance
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        // set the view now
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (auth.getCurrentUser() != null) {

            //  userLoggedIn = firebaseAuth .getCurrentUser() != null;
            userUID = mFirebaseAuth .getCurrentUser().getUid();




            Intent myIntent = new Intent(LoginActivity.this, com.collengine.sulu.rideshare.SelectActivity.class);
            myIntent.putExtra("USER_ID", userUID);
            startActivity( myIntent);
            return;
        }
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        btnSignup.setOnClickListener( new
                                              View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      startActivity(new Intent(LoginActivity. this , RegisterActivity. class));
                                                  }
                                              });

        btnLogin.setOnClickListener(new
                                            View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final String email = inputEmail.getText().toString();
                                                    final String password = inputPassword.getText().toString();

                                                    if (TextUtils.isEmpty(email)) {
                                                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    if(email.contains("gmail")){
                                                        inputEmail.setError("No need to create an account if you have a gmail account click on sign in with Google button in login page");
                                                        return;
                                                    }
                                                    if (TextUtils.isEmpty(password)) {
                                                        Toast.makeText(getApplicationContext(), "Enter password!" , Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    progressBar.setVisibility(View.VISIBLE);
//authenticate user
                                                    auth.signInWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener(LoginActivity. this , new
                                                                    OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete( @NonNull
                                                                                                        Task<AuthResult> task) {
// If sign in fails, display a message to the user. If sign in succeeds
// the auth state listener will be notified and logic to handle the
// signed in user can be handled in the listener.
                                                                            progressBar.setVisibility(View.GONE);
                                                                            if(email.contains("gmail")){
                                                                                inputEmail.setError("To login with your gmail click on sign in with Google button below");
                                                                            }
                                                                            if (!task.isSuccessful()) {
// there was an error
                                                                                if (password.length() < 6) {
                                                                                    inputPassword.setError(getString(R.string.minimum_password));
                                                                                } else {

                                                                                    //   Log.e("LoginActivity", "the error is" +  task.getException());
                                                                                    Toast.makeText(LoginActivity. this , getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                                                }
                                                                            } else {

                                                                                checkIfEmailVerified(email);
                                                                            }
                                                                        }
                                                                    });
                                                }
                                            });
    }
    private void handleFirebaseAuthResult(AuthResult authResult) {
        if (authResult != null) {
            // Welcome the user
            FirebaseUser user = authResult.getUser();
            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();


            Intent myIntent = new Intent(LoginActivity.this, com.collengine.sulu.rideshare.SelectActivity.class);
            myIntent.putExtra("USER_ID", userUID);
            startActivity( myIntent);

            finish();
            // Go back to the main activity
            //    startActivity(new Intent(this, MainActivity.class));
        }
    }
    private void sendTheRegisteredTokenToWebServer(final String token, final String userUID){
        queue = Volley.newRequestQueue(getApplicationContext());
        mySharedPreference = new MySharedPreference(getApplicationContext());

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //      Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);

                if (null == tokenObject) {
                    //        Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(false);
                    //   Log.e(TAG, "The server HAS NOT YET taken your Token " );

                } else {
                    //       Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
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
        stringPostRequest.setRetryPolicy(new DefaultRetryPolicy(3* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        queue.add(stringPostRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            default:
                return;
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = result.getSignInAccount();
                String id = account.getId();
                userName= account.getDisplayName();
                userEmail = account.getEmail();
                firebaseAuthWithGoogle(account);

                //   Log.e(TAG, "Google id" + id + "Google name" + name +  "Google email" + email);
            } else {
                // Google Sign In failed
                //   Log.e(TAG, "Google Sign In failed.");
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        //   Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //   Log.e(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //   Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {



                            userUID = mFirebaseAuth .getCurrentUser().getUid();
                            userUID = mFirebaseAuth .getCurrentUser().getUid();
                            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();



                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            sendTheRegisteredTokenToWebServer( refreshedToken, userUID);



                            HashMap<String, Object> userInformation = new HashMap<>();
                            userInformation.put("email", userEmail);
                            userInformation.put("uid", userUID);
                            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).updateChildren(userInformation);

                            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //    Log.e("SnapShot", dataSnapshot.toString());
                                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                    verified = userInformation.getFirstName();
                                    confirm(verified);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }
                    }
                });
    }

    private void confirm(String verified) {

        if(verified!=null ){
            Log.e("Login Error", "Secondly We are looking for the verified:  " + verified);


            Intent myIntent = new Intent(LoginActivity.this, com.collengine.sulu.rideshare.SelectActivity.class);
            myIntent.putExtra("USER_ID", userUID);
            startActivity(myIntent);

            finish();

        }else{
            Intent myIntent = new Intent(LoginActivity.this, com.collengine.sulu.rideshare.login.UserDataActivity.class);
            myIntent.putExtra("USER_ID", userUID);
               Log.e(TAG, "The uid as from the login   "+  userUID);
            startActivity(myIntent);

            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();

    }

    private void checkIfEmailVerified(String userEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user.isEmailVerified()){
            Toast.makeText(LoginActivity.this, "Succesfully Logged in", Toast.LENGTH_LONG).show();

            userUID = mFirebaseAuth .getCurrentUser().getUid();
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            sendTheRegisteredTokenToWebServer( refreshedToken, userUID);

            HashMap<String, Object> userInformation = new HashMap<>();
            userInformation.put("email", userEmail);
            userInformation.put("uid", userUID);
            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).updateChildren(userInformation);
            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //    Log.e("SnapShot", dataSnapshot.toString());
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    verified = userInformation.getFirstName();
                    confirm(verified);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });











        }
        else{

            inputEmail.setError("Please verifiy your email account");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }



    private void handleFacebookAccessToken(AccessToken token){
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        //showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "signInWithCredential:success");
                    FirebaseUser user = auth.getCurrentUser();
                    userEmail = user.getEmail();
                    userUID = user.getUid();
                    HashMap<String, Object> userInformation = new HashMap<>();
                    userInformation.put("email", userEmail);
                    userInformation.put("uid", userUID);
                    FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).updateChildren(userInformation);
                    FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(userUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("SnapShot", dataSnapshot.toString());
                            UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                            verified = userInformation.getFirstName();
                            confirm(verified);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    updateUI(user);

                }else {
                    Log.e(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                //hideProgressDialog();
            }
        });
    }


    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            //mStatusTextView.setText(getString(R.string.action_signout, user.getDisplayName()));
            Log.e(TAG, "signIn   " + user.getDisplayName().toString());
            Log.e(TAG, "signIn   "+  user.getUid().toString());
        }else {
            Log.e(TAG, "user still null");
            Log.e(TAG, "user still null");
        }
    }
}