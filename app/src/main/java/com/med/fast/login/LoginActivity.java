package com.med.fast.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.signup.SignupActivity;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

public class LoginActivity extends FastBaseActivity implements LoginIntf {

    @BindView(R.id.loginactivity_logo)
    ImageView logo;
    @BindView(R.id.loginactivity_email_et)
    CustomFontEditText email;
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

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.addValidation(email, Patterns.EMAIL_ADDRESS, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(password, Constants.REGEX_PASSWORD, getString(R.string.wrong_password_format_mssg));

        mAwesomeValidation.setContext(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()){
                    LoginAPI loginAPI = new LoginAPI();
                    loginAPI.data.query.email = email.getText().toString();
                    loginAPI.data.query.password = password.getText().toString();

                    LoginAPIFunc loginAPIFunc = new LoginAPIFunc(LoginActivity.this, LoginActivity.this);
                    loginAPIFunc.execute(loginAPI);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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

    @Override
    public void onFinishLogin(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LoginAPI output = gson.fromJson(responseAPI.status_response, LoginAPI.class);
            if (output.data.status.code.equals("200")) {
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_ID, output.data.results.user_id);
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_FIRST_NAME, output.data.results.first_name);
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_LAST_NAME, output.data.results.last_name);
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_EMAIL, output.data.results.email);
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_EMAIL_IS_VERIFIED, String.valueOf(output.data.results.is_verified_email));
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_DOB, output.data.results.dob);
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_GENDER, output.data.results.gender);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }
}