package com.codingchallenge.android.dropboximplementation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.codingchallenge.android.dropboximplementation.R;

public class SplashActivity extends Activity implements Runnable {
    private static final String LOG_TAG = SplashActivity.class
            .getSimpleName();
    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.splash_layout);

            new Handler().postDelayed(this, SPLASH_TIME);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        // should not close spash screen if BACK key pressed
    }

    public void run() {
        finish();
        Intent intent = null;
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
