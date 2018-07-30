/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */



package com.collengine.sulu.rideshare.service;

//
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.myduka.app.util.Helper;
//import com.myduka.app.util.MySharedPreference;
//import com.myduka.app.util.TokenObject;
//
//
//public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
//
//    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
//
//    private RequestQueue queue;
//    private String TOKENS = "tokens";
//
//    private TokenObject tokenObject;
//
//    private MySharedPreference mySharedPreference;
//    private String userUID = "1";
//
//
//
//    @Override
//    public void onTokenRefresh() {
//
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        //Log.e(TAG, "Token Value customServices: " + refreshedToken);
//        SharedPreferences.Editor editor = getSharedPreferences(TOKENS, MODE_PRIVATE).edit();
//        editor.putString("token", refreshedToken);
//        editor.commit();
//
//
//
//
//
//        sendTheRegisteredTokenToWebServer(refreshedToken);
//    }
//
//    private void sendTheRegisteredTokenToWebServer(final String token){
//        queue = Volley.newRequestQueue(getApplicationContext());
//        mySharedPreference = new MySharedPreference(getApplicationContext());
//
//        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                // Log.e(TAG, "Token Value customServices: " + response.toString());
//                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
//                GsonBuilder builder = new GsonBuilder();
//                Gson gson = builder.create();
//                tokenObject = gson.fromJson(response, TokenObject.class);
//
//                if (null == tokenObject) {
//                    //       Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
//                    mySharedPreference.saveNotificationSubscription(false);
//                    //   Log.e(TAG, "The server HAS NOT YET taken your Token " );
//
//                } else {
//                    //     Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
//                    mySharedPreference.saveNotificationSubscription(true);
//                    //   Log.e(TAG, "The server HAS YOUR Token " );
//                }
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //   Log.e(TAG, "Error from the custom services " );
//                        //         Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put(Helper.TOKEN_TO_SERVER, token);
//                params.put(Helper.UID_TO_SERVER, userUID);
//                return params;
//            }
//        };
//        stringPostRequest.setRetryPolicy(new DefaultRetryPolicy(3* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
//        queue.add(stringPostRequest);
//    }
//}

//
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.collengine.sulu.rideshare.util.Helper;
import com.collengine.sulu.rideshare.util.TokenObject;

import java.util.HashMap;
import java.util.Map;

import static com.collengine.sulu.rideshare.util.AppConstants.REGISTRATION_COMPLETE;
import static com.collengine.sulu.rideshare.util.AppConstants.SHARED_PREF;

/**
 * Created  on 6/30/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private RequestQueue queue;
    private String userUID = "1";
    private String TOKENS = "tokens";
    private TokenObject tokenObject;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e("Firebase Id", refreshedToken);

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendTheRegisteredTokenToWebServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendTheRegisteredTokenToWebServer(final String token){
        queue = Volley.newRequestQueue(getApplicationContext());


        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.e(TAG, "Token Value customServices: " + response.toString());
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);

                if (null == tokenObject) {
                    //       Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                   // mySharedPreference.saveNotificationSubscription(false);
                       Log.e(TAG, "The server HAS NOT YET taken your Token " );

                } else {
                    //     Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
                    //mySharedPreference.saveNotificationSubscription(true);
                       Log.e(TAG, "The server HAS YOUR Token " );
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Log.e(TAG, "Error from the custom services " );
                        //         Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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

    /**
     * Setting values in Preference:
     */

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}
