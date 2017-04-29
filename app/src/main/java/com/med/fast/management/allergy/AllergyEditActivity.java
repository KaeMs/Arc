package com.med.fast.management.allergy;

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
import com.med.fast.management.allergy.allergyinterface.AllergyManagementEditIntf;
import com.med.fast.management.allergy.api.AllergyManagementEditShowAPI;
import com.med.fast.management.allergy.api.AllergyManagementEditShowAPIFunc;
import com.med.fast.management.allergy.api.AllergyManagementEditSubmitAPI;
import com.med.fast.management.allergy.api.AllergyManagementEditSubmitAPIFunc;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class AllergyEditActivity extends FastBaseActivity implements AllergyManagementEditIntf {
    @BindView(R.id.allergy_popup_causative_et)
    CustomFontEditText causative;
    @BindView(R.id.allergy_popup_drugtype_rb_yes)
    CustomFontRadioButton drugTypeYes;
    @BindView(R.id.allergy_popup_reaction_et)
    CustomFontEditText reaction;
    @BindView(R.id.allergy_popup_firsttime_et)
    CustomFontEditText firstTimeExp;
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String allergyId = "";
    private AllergyManagementModel allergy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            allergyId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));

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
                    String causativeString = causative.getText().toString();
                    String drugTypeString = drugTypeYes.isChecked() ? "yes" : "no";
                    String reactionString = reaction.getText().toString();
                    String firstExpString = firstTimeExp.getText().toString();

                    allergy = new AllergyManagementModel();
                    allergy.setAgent(causativeString);
                    allergy.setDrug(drugTypeString);
                    allergy.setReaction(reactionString);
                    allergy.setFirst_experience(firstExpString);
                    allergy.setProgress_status("0");

                    AllergyManagementEditSubmitAPI allergyManagementEditSubmitAPI = new AllergyManagementEditSubmitAPI();
                    allergyManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(AllergyEditActivity.this);
                    allergyManagementEditSubmitAPI.data.query.allergy_id = allergyId;
                    allergyManagementEditSubmitAPI.data.query.allergy_agent = causativeString;
                    allergyManagementEditSubmitAPI.data.query.allergy_is_drug = drugTypeString;
                    allergyManagementEditSubmitAPI.data.query.allergy_reaction = reactionString;
                    allergyManagementEditSubmitAPI.data.query.allergy_first_experience = firstExpString;

                    AllergyManagementEditSubmitAPIFunc allergyManagementEditSubmitAPIFunc = new AllergyManagementEditSubmitAPIFunc(AllergyEditActivity.this);
                    allergyManagementEditSubmitAPIFunc.setDelegate(AllergyEditActivity.this);
                    allergyManagementEditSubmitAPIFunc.execute(allergyManagementEditSubmitAPI);
                }
            }
        });
    }

    void refreshView(){
        AllergyManagementEditShowAPI allergyManagementEditShowAPI = new AllergyManagementEditShowAPI();
        allergyManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        allergyManagementEditShowAPI.data.query.allergy_id = allergyId;

        AllergyManagementEditShowAPIFunc allergyManagementEditShowAPIFunc = new AllergyManagementEditShowAPIFunc(this);
        allergyManagementEditShowAPIFunc.setDelegate(this);
        allergyManagementEditShowAPIFunc.execute(allergyManagementEditShowAPI);
    }

    @Override
    public void onFinishAllergyManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                causative.setText(output.data.results.allergy_agent);
                drugTypeYes.setSelected(output.data.results.allergy_is_drug);
                reaction.setText(output.data.results.allergy_reaction);
                firstTimeExp.setText(output.data.results.allergy_first_experience);
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
    public void onFinishAllergyManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(allergy);
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
