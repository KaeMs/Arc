package com.med.fast.management.surgery;

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
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.management.surgery.api.SurgeryManagementEditShowAPI;
import com.med.fast.management.surgery.api.SurgeryManagementEditShowAPIFunc;
import com.med.fast.management.surgery.api.SurgeryManagementEditSubmitAPI;
import com.med.fast.management.surgery.api.SurgeryManagementEditSubmitAPIFunc;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class SurgeryEditActivity extends FastBaseActivity implements SurgeryManagementEditIntf {
    @BindView(R.id.surgery_popup_procedure)
    CustomFontEditText surgProcedure;
    @BindView(R.id.surgery_popup_physician_name)
    CustomFontEditText surgPhyName;
    @BindView(R.id.surgery_popup_hospital_name)
    CustomFontEditText surgHospitalName;
    @BindView(R.id.surgery_popup_surgery_date)
    CustomFontEditText surgDate;
    @BindView(R.id.surgery_popup_note)
    CustomFontEditText surgNote;
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String surgeryId = "";
    private SurgeryManagementModel surgery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            surgeryId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(surgProcedure, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(surgHospitalName, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
        mAwesomeValidation.addValidation(surgDate, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
        mAwesomeValidation.addValidation(surgNote, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
        mAwesomeValidation.addValidation(surgPhyName, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

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
                    String surgProcedureString = surgProcedure.getText().toString();
                    String surgHospitalNameString = surgHospitalName.getText().toString();
                    String surgDateString = surgDate.getText().toString();
                    String surgNoteString = surgNote.getText().toString();
                    String surgPhyNameString = surgPhyName.getText().toString();

                    surgery = new SurgeryManagementModel();
                    surgery.setSurgery_procedure(surgProcedureString);
                    surgery.setSurgery_hospital_name(surgHospitalNameString);
                    surgery.setSurgery_date(surgDateString);
                    surgery.setSurgery_note(surgNoteString);
                    surgery.setSurgery_physician_name(surgPhyNameString);
                    surgery.setProgress_status("0");

                    SurgeryManagementEditSubmitAPI surgeryManagementEditSubmitAPI = new SurgeryManagementEditSubmitAPI();
                    surgeryManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(SurgeryEditActivity.this);
                    surgeryManagementEditSubmitAPI.data.query.surgery_id = surgeryId;
                    surgeryManagementEditSubmitAPI.data.query.procedure = surgProcedureString;
                    surgeryManagementEditSubmitAPI.data.query.hospital = surgHospitalNameString;
                    surgeryManagementEditSubmitAPI.data.query.date = surgDateString;
                    surgeryManagementEditSubmitAPI.data.query.notes = surgNoteString;
                    surgeryManagementEditSubmitAPI.data.query.physician = surgPhyNameString;

                    SurgeryManagementEditSubmitAPIFunc surgeryManagementEditSubmitAPIFunc = new SurgeryManagementEditSubmitAPIFunc(SurgeryEditActivity.this);
                    surgeryManagementEditSubmitAPIFunc.setDelegate(SurgeryEditActivity.this);
                    surgeryManagementEditSubmitAPIFunc.execute(surgeryManagementEditSubmitAPI);
                }
            }
        });
    }

    private void refreshView() {
        SurgeryManagementEditShowAPI surgeryManagementEditShowAPI = new SurgeryManagementEditShowAPI();
        surgeryManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        surgeryManagementEditShowAPI.data.query.surgery_id = surgeryId;

        SurgeryManagementEditShowAPIFunc surgeryManagementEditShowAPIFunc = new SurgeryManagementEditShowAPIFunc(this);
        surgeryManagementEditShowAPIFunc.setDelegate(this);
        surgeryManagementEditShowAPIFunc.execute(surgeryManagementEditShowAPI);
    }

    @Override
    public void onFinishSurgeryManagementEditShow(ResponseAPI responseAPI) {

        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                surgDate.setText(output.data.results.date);
                surgHospitalName.setText(output.data.results.hospital);
                surgNote.setText(output.data.results.notes);
                surgProcedure.setText(output.data.results.procedure);
                surgPhyName.setText(output.data.results.physician);
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
    public void onFinishSurgeryManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(surgery);
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
