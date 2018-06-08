package com.ssverma.showtime.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ssverma.showtime.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME = 2000;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MoviesListingActivity.launch(SplashActivity.this);
                finish();
            }
        }, SPLASH_TIME);

    }
}
