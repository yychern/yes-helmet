package com.collengine.sulu.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class  SelectActivity extends AppCompatActivity {

    private ImageView unlockImage;
    private ImageView challengeImage;
    private ImageView pointsImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        unlockImage = (ImageView)findViewById(R.id.unlock_img);
        challengeImage = (ImageView)findViewById(R.id.challenge_img);
        pointsImage = (ImageView)findViewById(R.id.yes_points_img);

        unlockImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, SelfieActivity.class);
                startActivity(intent);
            }
        });


        challengeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, ChallengeActivity.class);
                startActivity(intent);
            }
        });

        pointsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, CheckPointActivity.class);
                startActivity(intent);
            }
        });
    }
}
