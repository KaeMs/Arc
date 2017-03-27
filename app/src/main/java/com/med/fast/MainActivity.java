package com.med.fast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

public class MainActivity extends FastBaseActivity {

    @BindView(R.id.mainactivity_title)
    CustomFontTextView title;
    @BindView(R.id.mainactivity_logo)
    ImageView logo;
    @BindView(R.id.mainactivity_username)
    CustomFontEditText username;
    @BindView(R.id.mainactivity_password)
    CustomFontEditText password;
    @BindView(R.id.mainactivity_loginBtn)
    CustomFontButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}