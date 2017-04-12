package com.med.fast.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataAllergyActivity extends FastBaseActivity {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // RecyclerView
    @BindView(R.id.initialdata_allergy_recyclerview)
    RecyclerView recyclerView;

    // Btns
    @BindView(R.id.initialdata_btn_cftextview)
    CustomFontTextView skipBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialdata_allergy_activity);

        toolbarTitle.setText(getString(R.string.step_1_allergy));
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataAllergyActivity.this, InitialDataAllergyActivity.class);
                startActivity(intent);
            }
        });
    }
}
