package com.movietrailer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import movietrailer.auth.CurrentUser;
import movietrailer.auth.login.LoginActivity;
import movietrailer.screens.mainscreen.MainScreen;

/**
 * Created by a7medM on 3/22/2016.
 */

public class Splash extends AppCompatActivity {

    private static int time = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        doSplash();
    }

    private void doSplash() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(time);
                } catch (Exception e) {
                } finally {
                    CurrentUser currentUser = new CurrentUser(Splash.this);
                    if (!currentUser.isAuthenticated()) {
                        startLoginActivity();
                    } else {
                        startMainActivity();
                    }
                }
            }
        };
        thread.start();
    }

    private void startLoginActivity() {
        startActivity(new Intent(Splash.this,
                LoginActivity.class));
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(Splash.this,
                MainScreen.class));
        finish();
    }
}