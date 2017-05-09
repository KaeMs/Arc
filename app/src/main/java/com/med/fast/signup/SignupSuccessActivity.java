package com.med.fast.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/9/2017. FM
 */

public class SignupSuccessActivity extends FastBaseActivity {
    @BindView(R.id.success_register_message)
    CustomFontTextView successMssg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_success);
        String email = getIntent().getStringExtra("email");

        successMssg.append(" ");
        successMssg.append(email);
    }
}
