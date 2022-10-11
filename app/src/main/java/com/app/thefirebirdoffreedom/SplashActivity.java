package com.app.thefirebirdoffreedom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 100;
    private static volatile boolean started = false;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onStart(){
        super.onStart();
        startMainActivity();
    }

    private void goToMainScreen() {
        try {
            if (!started) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                Bundle options = new Bundle();
                intent.putExtra("destination", url);
                options.putString("destination", url);
                startActivityForResult(intent, 102);
            }
        } catch (IllegalArgumentException e){
        }

    }

    public void startMainActivity(){
            new Handler().postDelayed(this::goToMainScreen, SPLASH_DISPLAY_LENGTH);

    }


}
