package com.med.fast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.signup.SignupActivity;

import butterknife.BindView;

public class LoginActivity extends FastBaseActivity {

    @BindView(R.id.loginactivity_logo)
    ImageView logo;
    @BindView(R.id.loginactivity_email_et)
    CustomFontEditText username;
    @BindView(R.id.loginactivity_password_et)
    CustomFontEditText password;
    @BindView(R.id.loginactivity_login_btn)
    CustomFontButton loginBtn;
    @BindView(R.id.loginactivity_forgotpassword_tv)
    CustomFontTextView forgotPassword;
    @BindView(R.id.loginactivity_signup_tv)
    CustomFontTextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*NoUnderlineClickableSpan forgotPassSpan = new NoUnderlineClickableSpan() {
            @Override
            public void onClick(View tv) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
        SpannableStringBuilder forgotPassSpb = new SpannableStringBuilder(getString(R.string.forgot_password_question));
        forgotPassSpb.setSpan(forgotPassSpan, 0, forgotPassSpb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPassword.setText(forgotPassSpb);
        forgotPassword.setMovementMethod(new LinkMovementMethod());*/

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /*NoUnderlineClickableSpan signupSpan = new NoUnderlineClickableSpan() {
            @Override
            public void onClick(View tv) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        };
        SpannableStringBuilder signupSpb = new SpannableStringBuilder(getString(R.string.sign_up));
        signupSpb.setSpan(signupSpan, 0, signupSpb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signup.setText(signupSpb);
        signup.setMovementMethod(new LinkMovementMethod());*/
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}