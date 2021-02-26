package com.example.applicationqr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.applicationqr.MainActivity;
import com.example.applicationqr.R;

public class SplashActivity extends AppCompatActivity {

    //Le temps de chargement de notre SplashScreen
    private final int SPLASH_DELAY = 10000;

    //Qui va contenir l'image du Logo de notre App
    private ImageView imageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initializeView();
        animateLogo();
        goToMainActivity();
    }

    // Cette methode utiliser afin de se rediriger vers notre MainActivity
    private void goToMainActivity() {
        new Handler().postDelayed(()-> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        },SPLASH_DELAY);
    }


    //Pour initialiser notre SplashScreen avec le Logo de notre App
    private void initializeView() {
        imageView = findViewById(R.id.imageView);
    }

    //Cette methode utiliser pour animer le logo de notre App
    private void animateLogo() {
        Animation fadingInAnimation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fadingInAnimation.setDuration(SPLASH_DELAY);

        imageView.startAnimation(fadingInAnimation);
    }
}