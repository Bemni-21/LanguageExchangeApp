package com.example.languageexchange;
// SplashScreenActivity.java


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class SplashScreenActivity extends AppCompatActivity {

    private static final long ANIMATION_DURATION = 2000;

    private ImageView logoImageView;
    private TextView appNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        logoImageView = findViewById(R.id.imageViewLogo);
        appNameTextView = findViewById(R.id.textViewAppName);

        animateLogo();
    }

    private void animateLogo() {

        logoImageView.setTranslationY(800f);
        appNameTextView.setAlpha(0f);

        ObjectAnimator logoAnimator = ObjectAnimator.ofFloat(
                logoImageView, View.TRANSLATION_Y,
                800f, 0f
        );
        logoAnimator.setDuration(ANIMATION_DURATION);
        logoAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator textAnimator = ObjectAnimator.ofFloat(
                appNameTextView, View.ALPHA,
                0f, 1f
        );
        textAnimator.setDuration(ANIMATION_DURATION / 2);
        textAnimator.setStartDelay(ANIMATION_DURATION / 2);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(logoAnimator, textAnimator);

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000); // Delay for 1 second
            }
        });

        animatorSet.start();
    }
}
