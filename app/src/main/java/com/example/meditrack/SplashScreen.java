package com.example.meditrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

/**
 * SplashScreen.java
 */

public class SplashScreen extends Activity   {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startSplash();
    }
    private void startSplash() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, com.example.meditrack.MedsActivity.class); //starts activity after 3 seconds
                startActivity(intent);
                finish();
            }
        },3000); //splash screen loads for 3 seconds
    }
}
