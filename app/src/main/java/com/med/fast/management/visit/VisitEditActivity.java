package com.med.fast.management.visit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.med.fast.management.accidenthistory.AccidentHistoryManagementModel;
import com.med.fast.management.visit.api.VisitManagementEditShowAPI;
import com.med.fast.management.visit.api.VisitManagementEditShowAPIFunc;
import com.med.fast.management.visit.api.VisitManagementEditSubmitAPI;
import com.med.fast.management.visit.api.VisitManagementEditSubmitAPIFunc;
import com.med.fast.management.visit.visitinterface.VisitEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class VisitEditActivity extends FastBaseActivity implements VisitEditIntf {
    @BindView(R.id.visit_popup_doctor_name)
    CustomFontEditText visitDoctor;
    @BindView(R.id.surgery_popup_hospital_name)
    CustomFontEditText visitHospital;
    @BindView(R.id.visit_popup_diagnose)
    CustomFontEditText visitDiagnose;
    @BindView(R.id.visit_popup_imagerecycler)
    RecyclerView visitImageList;
    @BindView(R.id.visit_popup_disease_history_recycler)
    RecyclerView visitDiseaseHistory;
    @BindView(R.id.visit_popup_disease_input_recycler)
    RecyclerView visitDiseaseInput;

    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String visitId = "";
    private VisitModel visit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            visitId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(visitDoctor, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(visitDiagnose, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
        mAwesomeValidation.addValidation(visitHospital, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

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
                    String visitDiagnoseString = visitDiagnose.getText().toString();
                    String visitDoctorString = visitDoctor.getText().toString();
                    String visitHospitalString = visitHospital.getText().toString();
                    //String firstExpString = visitDiseaseHistory.getTe

                    visit = new VisitModel();
                    visit.setDiagnose(visitDiagnoseString);
                    visit.setDoctor_name(visitDoctorString);
                    visit.setHospital_name(visitHospitalString);
                    visit.setProgress_status("0");

                    VisitManagementEditSubmitAPI visitManagementEditSubmitAPI = new VisitManagementEditSubmitAPI();
                    visitManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(VisitEditActivity.this);
                    visitManagementEditSubmitAPI.data.query.visit_id = visitId;
                    visitManagementEditSubmitAPI.data.query.diagnose = visitDiagnoseString;
                    visitManagementEditSubmitAPI.data.query.doctor = visitDoctorString;
                    visitManagementEditSubmitAPI.data.query.hospital = visitHospitalString;
                    VisitManagementEditSubmitAPIFunc visitManagementEditSubmitAPIFunc = new VisitManagementEditSubmitAPIFunc(VisitEditActivity.this);
                    visitManagementEditSubmitAPIFunc.setDelegate(VisitEditActivity.this);
                    visitManagementEditSubmitAPIFunc.execute(visitManagementEditSubmitAPI);
                }
            }
        });
    }

    private void refreshView() {
        VisitManagementEditShowAPI visitManagementEditShowAPI = new VisitManagementEditShowAPI();
        visitManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        visitManagementEditShowAPI.data.query.visit_id = visitId;

       VisitManagementEditShowAPIFunc visitManagementEditShowAPIFunc = new VisitManagementEditShowAPIFunc(this);
        visitManagementEditShowAPIFunc.setDelegate(this);
        visitManagementEditShowAPIFunc.execute(visitManagementEditShowAPI);
    }

    @Override
    public void onFinishVisitEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, VisitManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                visitDiagnose.setText(output.data.results.visit.getDiagnose());
                visitDoctor.setText(output.data.results.visit.getDoctor_name());
                visitHospital.setText(output.data.results.visit.getHospital_name());
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
    public void onFinishVisitEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(visit);
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
