package com.med.fast.management.misc;

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
import com.med.fast.management.misc.api.MiscEditShowAPIFunc;
import com.med.fast.management.misc.api.MiscEditSubmitAPI;
import com.med.fast.management.misc.api.MiscEditSubmitAPIFunc;
import com.med.fast.management.misc.api.MiscShowAPI;
import com.med.fast.management.misc.miscinterface.MiscEditIntf;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class MiscEditActivity extends FastBaseActivity implements MiscEditIntf {
    @BindView(R.id.misc_popup_voluptuary_habit)
    CustomFontEditText miscHabit;
    @BindView(R.id.misc_popup_pregnancy_weeks)
    CustomFontEditText miscPregnantWeeks;
    @BindView(R.id.misc_popup_miscarriage_date)
    CustomFontEditText miscMiscarriageWeeks;
    @BindView(R.id.misc_popup_cycle_alterations)
    CustomFontEditText miscCycleAlter;
    @BindView(R.id.misc_popup_pregnantY)
    CustomFontRadioButton miscIsPregnant;
    @BindView(R.id.misc_popup_miscarriageY)
    CustomFontRadioButton miscIsMiscarriage;

    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private String miscId = "";
    private MiscModel misc;

    @Override
    public void onFinishMiscEditShow(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MiscShowAPI output = gson.fromJson(responseAPI.status_response, MiscShowAPI.class);
            if (output.data.status.code.equals("200")) {
                miscHabit.setText(output.data.results.voluptuary_habits);
                miscPregnantWeeks.setText(output.data.results.pregnancy_weeks);
                miscCycleAlter.setText(output.data.results.cycle_alteration);
                miscIsPregnant.setText(output.data.results.pregnancy);
                miscIsMiscarriage.setText(output.data.results.had_miscarriage);
                miscMiscarriageWeeks.setText(output.data.results.last_time_miscarriage);
            }
        } else if (responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_misc_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

//        try {
//            miscId = getIntent().getStringExtra(ConstantsManagement.ALLERGY_ID_EXTRA);
//        } catch (NullPointerException npe){
//            finish();
//        }

        refreshView();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(miscHabit, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));

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
                    String miscHabitString = miscHabit.getText().toString();
                    String miscMiscarriageWeeksString = miscMiscarriageWeeks.getText().toString();
                    String miscCycleAlterString = miscCycleAlter.getText().toString();
                    String miscPregnantWeeksString = miscPregnantWeeks.getText().toString();
                    String miscIsPregnantString = miscIsPregnant.isChecked() ? "yes" : "no";
                    String miscIsMiscarriageString = miscIsMiscarriage.isChecked() ? "yes" : "no";

                    misc = new MiscModel();
                    misc.setVoluptuary_habit(miscHabitString);
                    misc.setLast_time_miscarriage(miscMiscarriageWeeksString);
                    misc.setCycle_alteration(miscCycleAlterString);
                    misc.setPregnancy_weeks(miscPregnantWeeksString);
                    misc.setPregnancy(miscIsPregnantString);
                    misc.setHad_miscarriage(miscIsMiscarriageString);
                    misc.setProgress_status("0");

                    MiscEditSubmitAPI miscEditSubmitAPI = new MiscEditSubmitAPI();
                    miscEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(MiscEditActivity.this);
                    miscEditSubmitAPI.data.query.voluptiary_habits = SharedPreferenceUtilities.getUserId(MiscEditActivity.this);

                    MiscEditSubmitAPIFunc MiscEditSubmitAPIFunc = new MiscEditSubmitAPIFunc(MiscEditActivity.this);
                    MiscEditSubmitAPIFunc.setDelegate(MiscEditActivity.this);
                    MiscEditSubmitAPIFunc.execute(miscEditSubmitAPI);
                }
            }
        });
    }

    private void refreshView() {
        MiscShowAPI miscShowAPI = new MiscShowAPI();
        miscShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        miscShowAPI.data.query.is_initial = "false";

        MiscEditShowAPIFunc miscEditShowAPIFunc = new MiscEditShowAPIFunc(this);
        miscEditShowAPIFunc.setDelegate(this);
        miscEditShowAPIFunc.execute(miscShowAPI);
    }

    @Override
    public void onFinishMiscEditSubmit(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MiscEditSubmitAPI output = gson.fromJson(responseAPI.status_response, MiscEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(misc);
                intent.putExtra(ConstantsManagement.MISC_MODEL_EXTRA, allergyModelString);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
