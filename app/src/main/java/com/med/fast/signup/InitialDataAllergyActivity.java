package com.med.fast.signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.adapters.AllergyAdapter;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.models.Allergy;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

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
    @BindView(R.id.initialdata_allergy_add_btn)
    CustomFontButton addBtn;

    private AllergyAdapter allergyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialdata_allergy_activity);

        toolbarTitle.setText(getString(R.string.step_1_allergy));

        allergyAdapter = new AllergyAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allergyAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(InitialDataAllergyActivity.this);
                dialog.setContentView(R.layout.allergy_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
                final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
                final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
                final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
                CustomFontButton cancelBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_cancel_btn);
                CustomFontButton addBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_add_btn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(InitialDataAllergyActivity.this);
                mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
                mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAwesomeValidation.validate()) {
                            Allergy allergy = new Allergy();
                            allergy.allergy_causative = causative.getText().toString();
                            allergy.allergy_drug = drugTypeYes.isChecked() ? "yes" : "no";
                            allergy.allergy_reaction = reaction.getText().toString();
                            allergy.allergy_first_experience = firstExp.getText().toString();

                            allergyAdapter.addSingle(allergy);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataAllergyActivity.this, InitialDataAllergyActivity.class);
                startActivity(intent);
            }
        });
    }
}
