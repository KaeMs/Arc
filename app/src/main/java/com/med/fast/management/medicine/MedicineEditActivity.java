package com.med.fast.management.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.medicine.api.MedicineManagementEditShowAPI;
import com.med.fast.management.medicine.api.MedicineManagementEditShowAPIFunc;
import com.med.fast.management.medicine.api.MedicineManagementEditSubmitAPI;
import com.med.fast.management.medicine.api.MedicineManagementEditSubmitAPIFunc;
import com.med.fast.management.medicine.medicineinterface.MedicineEditIntf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String medicineId = "";
    private MedicineManagementModel medicine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            medicineId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
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
                    String medDoseString = medDose.getText().toString();
                    String medFormString = medForm.getText().toString();
                    String medFreqString = medFreq.getText().toString();
                    String medInstructionString = medInstruction.getText().toString();
                    String medMethodString = medMethod.getText().toString();
                    String medStatusString = medStatus.getText().toString();
                    String medReasonString = medReason.getText().toString();

                    medicine = new MedicineManagementModel();
                    medicine.setMedicine_name(medNameString);
                    medicine.setMedicine_administration_dose(medDoseString);
                    medicine.setMedicine_form(medFormString);
                    medicine.setMedicine_frequency(medFreqString);
                    medicine.setMedicine_additional_instruction(medInstructionString);
                    medicine.setMedicine_administration_method(medMethodString);
                    medicine.setMedicine_medication_status(medStatusString);
                    medicine.setMedicine_medication_reason(medReasonString);
                    medicine.setProgress_status("0");

                    MedicineManagementEditSubmitAPI medicineManagementEditSubmitAPI = new MedicineManagementEditSubmitAPI();
                    medicineManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(MedicineEditActivity.this);
                    medicineManagementEditSubmitAPI.data.query.medicine_id = medicineId;
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
        medicineManagementEditShowAPI.data.query.medicine_id = medicineId;

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
                medName.setText(output.data.results.medicine.getMedicine_name());
                medReason.setText(output.data.results.medicine.getMedicine_medication_reason());
                medMethod.setText(output.data.results.medicine.getMedicine_administration_method());
                medDose.setText(output.data.results.medicine.getMedicine_administration_dose());
                medStatus.setText(output.data.results.medicine.getMedicine_medication_status());
                medForm.setText(output.data.results.medicine.getMedicine_form());
                medFreq.setText(output.data.results.medicine.getMedicine_frequency());
                medInstruction.setText(output.data.results.medicine.getMedicine_additional_instruction());
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
                String allergyModelString = gson.toJson(medicine);
                intent.putExtra(ConstantsManagement.ALLERGY_MODEL_EXTRA, allergyModelString);
                setResult(RESULT_OK, intent);
                finish();
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
