package com.collengine.sulu.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TakeChallengeActivity extends AppCompatActivity {

    private Button backBtn;
    private Button acceptBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_challenge);
        backBtn = (Button)findViewById(R.id.back_btn);
        acceptBtn = (Button)findViewById(R.id.accept_btn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeChallengeActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeChallengeActivity.this, SelfieActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
