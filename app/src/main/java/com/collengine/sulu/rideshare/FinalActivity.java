package com.collengine.sulu.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class FinalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent intent = getIntent();
        String confidence = intent.getStringExtra("CONFIDENCE_HELMET");
        String verified = intent.getStringExtra("VERIFY");
        //     Log.e("FinalActivity", "the confidence: " + confidence + "   the verify " + verified);


        if(verified.equalsIgnoreCase("not-verified")){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Safety Gear Verification")
                    .setContentText("Please Put On a Helmet").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                    Intent intent1 = new Intent(FinalActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            })
                    .show();
        }

        if(verified.equalsIgnoreCase("verified")){
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Safety Gear Verification")
                    .setContentText("Your Bike will be unlocked \n Confidence: " + confidence).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                    Intent intent1 = new Intent(FinalActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            })
                    .show();
        }

    }
}
