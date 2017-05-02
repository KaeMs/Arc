package com.med.fast.management.labresult;

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
import com.med.fast.management.allergy.AllergyEditActivity;
import com.med.fast.management.allergy.AllergyManagementModel;
import com.med.fast.management.allergy.api.AllergyManagementEditShowAPI;
import com.med.fast.management.allergy.api.AllergyManagementEditSubmitAPI;
import com.med.fast.management.allergy.api.AllergyManagementEditSubmitAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementEditShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementEditShowAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementEditSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementEditSubmitAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class LabResultEditActivity extends FastBaseActivity implements LabResultManagementEditIntf {
    @BindView(R.id.labresult_popup_test_type)
    CustomFontEditText testType;
    @BindView(R.id.labresult_popup_test_location)
    CustomFontEditText testLocation;
    @BindView(R.id.labresult_popup_test_description)
    CustomFontEditText description;
    @BindView(R.id.labresult_popup_test_finished_date)
    CustomFontEditText finishedDate;
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String labresultId = "";
    private LabResultManagementModel labresult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_labresult_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            labresultId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(testType, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(testLocation, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

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
                    String testTypeString = testType.getText().toString();
                    String testLocationString = testLocation.getText().toString();
                    String descriptionString = description.getText().toString();
                    String finishedDateString = finishedDate.getText().toString();

                    labresult = new LabResultManagementModel();
                    labresult.setTest_type(testTypeString);
                    labresult.setTest_location(testLocationString);
                    labresult.setTest_description(descriptionString);
                    labresult.setTest_date(finishedDateString);
                    labresult.setProgress_status("0");

                    LabResultManagementEditSubmitAPI labResultManagementEditSubmitAPI = new LabResultManagementEditSubmitAPI();
                    labResultManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(LabResultEditActivity.this);
                    labResultManagementEditSubmitAPI.data.query.lab_result_id = labresultId;
                    labResultManagementEditSubmitAPI.data.query.test_name = testTypeString;
                    labResultManagementEditSubmitAPI.data.query.place = testLocationString;
                    labResultManagementEditSubmitAPI.data.query.desc_result = descriptionString;
                    labResultManagementEditSubmitAPI.data.query.date = finishedDateString;

                    LabResultManagementEditSubmitAPIFunc labResultManagementEditSubmitAPIFunc = new LabResultManagementEditSubmitAPIFunc(LabResultEditActivity.this);
                    labResultManagementEditSubmitAPIFunc.setDelegate(LabResultEditActivity.this);
                    labResultManagementEditSubmitAPIFunc.execute(labResultManagementEditSubmitAPI);
                }
            }
        });
    }

    private void refreshView() {
        LabResultManagementEditShowAPI labResultManagementEditShowAPI = new LabResultManagementEditShowAPI();
        labResultManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        labResultManagementEditShowAPI.data.query.lab_result_id = labresultId;

        LabResultManagementEditShowAPIFunc labResultManagementEditShowAPIFunc = new LabResultManagementEditShowAPIFunc(this);
        labResultManagementEditShowAPIFunc.setDelegate(this);
        labResultManagementEditShowAPIFunc.execute(labResultManagementEditShowAPI);
    }

    @Override
    public void onFinishLabResultManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                testType.setText(output.data.results.lab_result.getTest_type());
                testLocation.setText(output.data.results.lab_result.getTest_location());
                finishedDate.setText(output.data.results.lab_result.getTest_date());
                description.setText(output.data.results.lab_result.getTest_description());
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
    public void onFinishLabResultManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(labresult);
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
