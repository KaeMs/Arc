package com.med.fast.management.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.medicine.api.MedicineManagementEditShowAPI;
import com.med.fast.management.medicine.api.MedicineManagementEditShowAPIFunc;
import com.med.fast.management.medicine.api.MedicineManagementEditSubmitAPI;
import com.med.fast.management.medicine.api.MedicineManagementEditSubmitAPIFunc;
import com.med.fast.management.medicine.medicineinterface.MedicineEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class MedicineEditActivity extends FastBaseActivity implements MedicineEditIntf {
    @BindView(R.id.medicine_popup_medicine_name)
    CustomFontEditText medName;
    @BindView(R.id.medicine_popup_medicine_form)
    CustomFontEditText medForm;
    @BindView(R.id.medicine_popup_administration_method)
    CustomFontEditText medMethod;
    @BindView(R.id.medicine_popup_administration_dose)
    CustomFontEditText medDose;
    @BindView(R.id.medicine_popup_frequency)
    CustomFontEditText medFreq;
    @BindView(R.id.medicine_popup_reason)
    CustomFontEditText medReason;
    @BindView(R.id.medicine_popup_status)
    CustomFontEditText medStatus;
    @BindView(R.id.medicine_popup_additional_instruction)
    CustomFontEditText medInstruction;

    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private MedicineManagementModel medicineManagementModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_medicine_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            Gson gson = new Gson();
            medicineManagementModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.MEDICINE_MODEL_EXTRA),
                    MedicineManagementModel.class);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(medName, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(medForm, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    String medNameString = medName.getText().toString();
                    String medDoseString = Utils.processStringForAPI(medDose.getText().toString());
                    String medFormString = medForm.getText().toString();
                    String medFreqString = Utils.processStringForAPI(medFreq.getText().toString());
                    String medInstructionString = Utils.processStringForAPI(medInstruction.getText().toString());
                    String medMethodString = Utils.processStringForAPI(medMethod.getText().toString());
                    String medStatusString = Utils.processStringForAPI(medStatus.getText().toString());
                    String medReasonString = Utils.processStringForAPI(medReason.getText().toString());

                    medicineManagementModel.setName(medNameString);
                    medicineManagementModel.setAdministration_dose(medDoseString);
                    medicineManagementModel.setForm(medFormString);
                    medicineManagementModel.setFrequency(medFreqString);
                    medicineManagementModel.setAdditional_instruction(medInstructionString);
                    medicineManagementModel.setAdministration_method(medMethodString);
                    medicineManagementModel.setMedication_status(medStatusString);
                    medicineManagementModel.setMedication_reason(medReasonString);
                    medicineManagementModel.setProgress_status("0");

                    MedicineManagementEditSubmitAPI medicineManagementEditSubmitAPI = new MedicineManagementEditSubmitAPI();
                    medicineManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(MedicineEditActivity.this);
                    medicineManagementEditSubmitAPI.data.query.medicine_id = medicineManagementModel.getId();
                    medicineManagementEditSubmitAPI.data.query.name = medNameString;
                    medicineManagementEditSubmitAPI.data.query.dose = medDoseString;
                    medicineManagementEditSubmitAPI.data.query.form = medFormString;
                    medicineManagementEditSubmitAPI.data.query.frequency = medFreqString;
                    medicineManagementEditSubmitAPI.data.query.additional_instruction = medInstructionString;
                    medicineManagementEditSubmitAPI.data.query.route = medMethodString;
                    medicineManagementEditSubmitAPI.data.query.status = medStatusString;
                    medicineManagementEditSubmitAPI.data.query.reason = medReasonString;

                    MedicineManagementEditSubmitAPIFunc medicineManagementEditSubmitAPIFunc = new MedicineManagementEditSubmitAPIFunc(MedicineEditActivity.this);
                    medicineManagementEditSubmitAPIFunc.setDelegate(MedicineEditActivity.this);
                    medicineManagementEditSubmitAPIFunc.execute(medicineManagementEditSubmitAPI);
                }
            }
        });
    }

    void refreshView(){
        MedicineManagementEditShowAPI medicineManagementEditShowAPI = new MedicineManagementEditShowAPI();
        medicineManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        medicineManagementEditShowAPI.data.query.medicine_id = medicineManagementModel.getId();

        MedicineManagementEditShowAPIFunc medicineManagementEditShowAPIFunc = new MedicineManagementEditShowAPIFunc(this);
        medicineManagementEditShowAPIFunc.setDelegate(this);
        medicineManagementEditShowAPIFunc.execute(medicineManagementEditShowAPI);
    }
    @Override
    public void onFinishMedicineEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MedicineManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                medName.setText(output.data.results.name);
                medReason.setText(output.data.results.medication_reason);
                medMethod.setText(output.data.results.administration_method);
                medDose.setText(output.data.results.administration_dose);
                medStatus.setText(output.data.results.medication_status);
                medForm.setText(output.data.results.form);
                medFreq.setText(output.data.results.frequency);
                medInstruction.setText(output.data.results.additional_instruction);
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

    @Override
    public void onFinishMedicineEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MedicineManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String medicineModelString = gson.toJson(medicineManagementModel);
                intent.putExtra(ConstantsManagement.MEDICINE_MODEL_EXTRA, medicineModelString);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
