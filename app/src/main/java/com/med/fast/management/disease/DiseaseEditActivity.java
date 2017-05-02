package com.med.fast.management.disease;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.management.allergy.api.AllergyManagementEditShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementEditShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementEditShowAPIFunc;
import com.med.fast.management.disease.api.DiseaseManagementEditSubmitAPI;
import com.med.fast.management.disease.api.DiseaseManagementEditSubmitAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class DiseaseEditActivity extends FastBaseActivity implements DiseaseManagementEditIntf {
    @BindView(R.id.disease_popup_name)
    CustomFontEditText diseaseName;
    @BindView(R.id.disease_popup_hereditary_y_rb)
    CustomFontRadioButton hereditaryYes;
    @BindView(R.id.disease_popup_hereditary_n_rb)
    CustomFontRadioButton hereditaryNo;
    @BindView(R.id.disease_popup_inherited_from)
    CustomFontEditText inheritedFrom;
    CustomFontButton backBtn;
    @BindView(R.id.disease_popup_currently_having_y_rb)
    CustomFontRadioButton curHavingYes;
    @BindView(R.id.disease_popup_currently_having_n_rb)
    CustomFontRadioButton curHavingNo;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    @BindView(R.id.disease_popup_date_spinner)
    Spinner dateSpinner;
    ArrayAdapter<String> approximateSpinnerAdapter;
    @BindView(R.id.disease_popup_historic_date_tv)
    CustomFontEditText historicDate;

    private String diseaseId = "";
    private DiseaseManagementModel disease;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            diseaseId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
        approximateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, approximates);
        dateSpinner.setAdapter(approximateSpinnerAdapter);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(diseaseName, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
//        mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

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
                    String diseaseNameString = diseaseName.getText().toString();
                    String hereditaryType = hereditaryYes.isChecked() ? "yes" : "no";
                    String inheritedFromString = inheritedFrom.getText().toString();
                    String historicDateString = historicDate.getText().toString();
                    String dateSpinnerString = dateSpinner.getSelectedItem().toString();

                    disease = new DiseaseManagementModel();
                    disease.setDisease_name(diseaseNameString);
                    disease.setDisease_hereditary(hereditaryType);
                    disease.setDisease_hereditary_carriers(inheritedFromString);
                    disease.setDate_historic(historicDateString);
                    disease.setDate_approximate(dateSpinnerString);
                    disease.setProgress_status("0");

                    DiseaseManagementEditSubmitAPI diseaseManagementEditSubmitAPI = new DiseaseManagementEditSubmitAPI();
                    diseaseManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPI.data.query.disease_id = diseaseId;
                    diseaseManagementEditSubmitAPI.data.query.disease_name = diseaseNameString;
                    diseaseManagementEditSubmitAPI.data.query.is_hereditary = hereditaryType;
                    diseaseManagementEditSubmitAPI.data.query.hereditary_carrier = inheritedFromString;
                    diseaseManagementEditSubmitAPI.data.query.history_date_text = historicDateString;
                    diseaseManagementEditSubmitAPI.data.query.date = dateSpinnerString;

                    DiseaseManagementEditSubmitAPIFunc diseaseManagementEditSubmitAPIFunc = new DiseaseManagementEditSubmitAPIFunc(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPIFunc.setDelegate(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPIFunc.execute(diseaseManagementEditSubmitAPI);
                }
            }
        });
    }

    private void refreshView() {
        DiseaseManagementEditShowAPI diseaseManagementEditShowAPI = new DiseaseManagementEditShowAPI();
        diseaseManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        diseaseManagementEditShowAPI.data.query.disease_id = diseaseId;

        DiseaseManagementEditShowAPIFunc diseaseManagementEditShowAPIFunc = new DiseaseManagementEditShowAPIFunc(this);
        diseaseManagementEditShowAPIFunc.setDelegate(this);
        diseaseManagementEditShowAPIFunc.execute(diseaseManagementEditShowAPI);
    }

    @Override
    public void onFinishDiseaseManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                diseaseName.setText(output.data.results.disease_item.getDisease_name());
                hereditaryYes.setSelected(output.data.results.disease_item.getDisease_hereditary().equals("true"));
                historicDate.setText(output.data.results.disease_item.getDate_historic());
                int spinnerPos = approximateSpinnerAdapter.getPosition(output.data.results.disease_item.getDate_approximate());
                if (spinnerPos >= 0){
                    dateSpinner.setSelection(approximateSpinnerAdapter.getPosition(output.data.results.disease_item.getDate_approximate()));
                } else {
                    dateSpinner.setSelection(0);
                }
                inheritedFrom.setText(output.data.results.disease_item.getDisease_hereditary_carriers());
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
    public void onFinishDiseaseManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(disease);
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
