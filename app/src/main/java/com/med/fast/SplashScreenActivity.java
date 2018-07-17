package com.med.fast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.med.fast.login.LoginActivity;
import com.med.fast.login.WelcomeActivity;
import com.med.fast.signup.InitialDataAllergyActivity;
import com.med.fast.signup.InitialDataDiseaseActivity;
import com.med.fast.signup.InitialDataMedicationActivity;
import com.med.fast.signup.InitialDataMiscActivity;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/2/2017. FM
 */

public class SplashScreenActivity extends FastBaseActivity {

    @BindView(R.id.splash_screen_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.splash_screen_dimmer)
    View dimmer;
    private Animation animFadeIn;
    private Animation animFadeOut;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        animFadeIn = AnimationUtils.loadAnimation(this,
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(this,
                R.anim.fade_out);

        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dimmer.clearAnimation();
                progressBar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dimmer.clearAnimation();
                progressBar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(true);
            }
        }, 2000);

        userId = SharedPreferenceUtilities.getFromSessionSP(this, SharedPreferenceUtilities.USER_ID);

        if (userId != null) {
            String initialDataStep = SharedPreferenceUtilities.getFromSessionSP(this, SharedPreferenceUtilities.INIT_DATA_STEP);
            if (initialDataStep != null) {
                switch (initialDataStep) {
                    case "1": {
                        Intent intent = new Intent(this, InitialDataAllergyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "2": {
                        Intent intent = new Intent(this, InitialDataDiseaseActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "3": {
                        Intent intent = new Intent(this, InitialDataMedicationActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "4": {
                        Intent intent = new Intent(this, InitialDataMiscActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    default: {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }
                }
            } else {
                Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    void showProgress(boolean show) {
        if (dimmer != null) {
            if (show) {
                dimmer.setAnimation(animFadeIn);
                dimmer.getAnimation().start();
                progressBar.setAnimation(animFadeIn);
                progressBar.getAnimation().start();
                dimmer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                dimmer.setAnimation(animFadeOut);
                dimmer.getAnimation().start();
                progressBar.setAnimation(animFadeOut);
                progressBar.getAnimation().start();
                dimmer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}