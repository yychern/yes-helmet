package com.collengine.sulu.rideshare;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class FinalActivity extends AppCompatActivity {

    private RelativeLayout acceptRel;
    private RelativeLayout declineRel;
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent intent = getIntent();
        String confidence = intent.getStringExtra("CONFIDENCE_HELMET");
        String verified = intent.getStringExtra("VERIFY");
        //     Log.e("FinalActivity", "the confidence: " + confidence + "   the verify " + verified);

        acceptRel = (RelativeLayout)findViewById(R.id.final_accept_holder);
        declineRel = (RelativeLayout)findViewById(R.id.final_decline_holder);

        if(verified.equalsIgnoreCase("not-verified")){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Safety Gear Verification")
                    .setContentText("Please Put On a Helmet").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                    declineRel.setVisibility(RelativeLayout.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(FinalActivity.this, SelectActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    }, SPLASH_TIME_OUT);

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
                    acceptRel.setVisibility(RelativeLayout.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(FinalActivity.this, SelectActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    }, SPLASH_TIME_OUT);

                }
            })
                    .show();
        }

    }
}
