package com.med.fast.signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.medicine.MedicineManagementAdapter;
import com.med.fast.management.medicine.MedicineManagementModel;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataMedicationActivity extends FastBaseActivity {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Step Indicator
    @BindView(R.id.initialdata_step_imgview)
    ImageView step;

    // RecyclerView
    @BindView(R.id.initialdata_recycler)
    RecyclerView recyclerView;

    // Btns
    @BindView(R.id.initialdata_add_btn)
    CustomFontButton addBtn;
    @BindView(R.id.initialdata_skip_btn)
    CustomFontTextView skipBtn;
    @BindView(R.id.initialdata_next_btn)
    CustomFontTextView nextBtn;

    private MedicineManagementAdapter medicineManagementAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_mainlayout);

        toolbarTitle.setText(getString(R.string.step_3_medication));

        medicineManagementAdapter = new MedicineManagementAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(medicineManagementAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(InitialDataMedicationActivity.this);
                dialog.setContentView(R.layout.management_medicine_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText medicineName = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_name);
                final CustomFontEditText medicineForm = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_form);
                final CustomFontEditText administrationMethod = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_method);
                final CustomFontEditText administrationDose = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_dose);
                final CustomFontEditText frequency = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_frequency);
                final CustomFontEditText reason = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_reason);
                final CustomFontEditText medicationStatus = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_status);
                final CustomFontEditText additionalInstruction = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_additional_instruction);

                CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_back_btn);
                CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_create_btn);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(InitialDataMedicationActivity.this);
                mAwesomeValidation.addValidation(medicineName, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_name_required));
                mAwesomeValidation.addValidation(medicineForm, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_form_required));

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (mAwesomeValidation.validate()){
                            MedicineManagementModel medicineManagementModel = new MedicineManagementModel();
                            medicineManagementModel.setMedicine_name(medicineName.getText().toString());
                            medicineManagementModel.setMedicine_form(medicineForm.getText().toString());
                            medicineManagementModel.setMedicine_administration_method(administrationMethod.getText().toString());
                            medicineManagementModel.setMedicine_administration_dose(administrationDose.getText().toString());
                            medicineManagementModel.setMedicine_frequency(frequency.getText().toString());
                            medicineManagementModel.setMedicine_medication_reason(reason.getText().toString());
                            medicineManagementModel.setMedicine_medication_status(medicationStatus.getText().toString());
                            medicineManagementModel.setMedicine_additional_instruction(additionalInstruction.getText().toString());

                            medicineManagementAdapter.addSingle(medicineManagementModel);
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
                Intent intent = new Intent(InitialDataMedicationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataMedicationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
