package com.collengine.sulu.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CheckpointsFinalActivity extends AppCompatActivity {

    private TextView finalCkeck;
    private String redeem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoints_final);
        finalCkeck = (TextView)findViewById(R.id.final_check);
        Intent intent = getIntent();
        redeem = intent.getStringExtra("REDEEM");


        finalCkeck.setText(redeem);
    }
}
