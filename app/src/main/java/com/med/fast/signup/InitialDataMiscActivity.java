package com.med.fast.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.misc.api.MiscCreateAPI;
import com.med.fast.management.misc.api.MiscCreateAPIFunc;
import com.med.fast.management.misc.api.MiscShowAPI;
import com.med.fast.management.misc.api.MiscShowAPIFunc;
import com.med.fast.management.misc.miscinterface.MiscShowCreateIntf;
import com.med.fast.signup.api.SkipInitialAPI;
import com.med.fast.signup.api.SkipInitialAPIFunc;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataMiscActivity extends FastBaseActivity implements MiscShowCreateIntf, SkipInitialIntf {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Step Indicator
    @BindView(R.id.initialdata_misc_step_imgview)
    ImageView step;

    // Content
    @BindView(R.id.misc_popup_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.misc_popup_voluptuary_habit)
    CustomFontEditText voluptuaryHabit;
    @BindView(R.id.misc_popup_progress)
    ProgressBar progressBar;
    @BindView(R.id.misc_popup_female_wrapper)
    LinearLayout femaleWrapper;
    @BindView(R.id.misc_popup_pregnantY)
    CustomFontRadioButton pregnantY;
    @BindView(R.id.misc_popup_pregnancy_weeks_wrapper)
    LinearLayout pregnancyWeeksWrapper;
    @BindView(R.id.misc_popup_pregnancy_weeks)
    CustomFontEditText pregnancyWeeks;
    @BindView(R.id.misc_popup_miscarriageY)
    CustomFontRadioButton miscarriageY;
    @BindView(R.id.misc_popup_miscarriage_date_wrapper)
    LinearLayout miscarriageDateWrapper;
    @BindView(R.id.misc_popup_miscarriage_date)
    CustomFontEditText miscarriageDate;
    @BindView(R.id.misc_popup_cycle_alterations)
    CustomFontEditText cycleAlterations;
    @BindView(R.id.management_operations_gravitystart_left_btn)
    CustomFontButton saveBtn;
    @BindView(R.id.management_operations_gravitystart_right_btn)
    CustomFontButton backBtn;
    // Btns
    @BindView(R.id.initialdata_misc_skip_btn)
    CustomFontTextView skipBtn;
    @BindView(R.id.initialdata_misc_next_btn)
    CustomFontTextView nextBtn;
    private String userId;
    private boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_misclayout);

        toolbarTitle.setText(getString(R.string.step_4_miscellaneous));
        /*toolbarBack.setVisibility(View.VISIBLE);
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });*/

        userId = SharedPreferenceUtilities.getUserId(this);

        String gender = SharedPreferenceUtilities.getFromSessionSP(this, SharedPreferenceUtilities.USER_GENDER);
        if (gender != null) {
            if (gender.equals("0")) {
                femaleWrapper.setVisibility(View.GONE);
            } else {
                femaleWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            femaleWrapper.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });
        SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.INIT_DATA_STEP, "4");
        refreshView(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });

        pregnantY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) pregnancyWeeksWrapper.setVisibility(View.VISIBLE);
                else pregnancyWeeksWrapper.setVisibility(View.GONE);
            }
        });

        miscarriageY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) miscarriageDateWrapper.setVisibility(View.VISIBLE);
                else miscarriageDateWrapper.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    MiscCreateAPI miscCreateAPI = new MiscCreateAPI();
                    miscCreateAPI.data.query.user_id = userId;
                    miscCreateAPI.data.query.voluptuary_habits = Utils.processStringForAPI(voluptuaryHabit.getText().toString());
                    miscCreateAPI.data.query.pregnancy = pregnantY.isChecked() ? "true" : "false";
                    miscCreateAPI.data.query.pregnancy_weeks = pregnantY.isChecked() ? pregnancyWeeks.getText().toString() : "default";
                    miscCreateAPI.data.query.had_miscarriage = miscarriageY.isChecked() ? "true" : "false";
                    miscCreateAPI.data.query.last_time_miscarriage = miscarriageY.isChecked() ? miscarriageDate.getText().toString() : "default";
                    miscCreateAPI.data.query.cycle_alteration = Utils.processStringForAPI(cycleAlterations.getText().toString());

                    MiscCreateAPIFunc miscCreateAPIFunc = new MiscCreateAPIFunc(InitialDataMiscActivity.this, InitialDataMiscActivity.this);
                    miscCreateAPIFunc.execute(miscCreateAPI);
                    isLoading = true;
                }
            }
        });
        backBtn.setVisibility(View.GONE);
        skipBtn.setVisibility(View.GONE);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipInitialAPI skipInitialAPI = new SkipInitialAPI();
                skipInitialAPI.data.query.user_id = userId;

                SkipInitialAPIFunc skipInitialAPIFunc = new SkipInitialAPIFunc(InitialDataMiscActivity.this, InitialDataMiscActivity.this);
                skipInitialAPIFunc.execute(skipInitialAPI);
            }
        });

        nextBtn.setText(getString(R.string.finish));
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataMiscActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    void refreshView(boolean setIsRefreshing) {
        MiscShowAPI miscShowAPI = new MiscShowAPI();
        miscShowAPI.data.query.user_id = userId;
        miscShowAPI.data.query.is_initial = "true";

        MiscShowAPIFunc miscShowAPIFunc = new MiscShowAPIFunc(this, this);
        miscShowAPIFunc.execute(miscShowAPI);
        if (setIsRefreshing) {
            swipeRefreshLayout.setRefreshing(true);
            progressBar.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        isLoading = true;
    }

    private void back(){
        Intent intent = new Intent(InitialDataMiscActivity.this, InitialDataMedicationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_left_to_start, R.anim.translate_start_to_right);
    }

    @Override
    public void onBackPressed() {
//        back();
    }

    @Override
    public void onFinishMiscShow(ResponseAPI responseAPI) {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MiscShowAPI output = gson.fromJson(responseAPI.status_response, MiscShowAPI.class);
            if (output.data.status.code.equals("200")) {
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_GENDER, output.data.results.is_female ? "1" : "0");
                voluptuaryHabit.setText(output.data.results.voluptuary_habits);
                pregnantY.setChecked(output.data.results.pregnancy.equals("true"));
                pregnancyWeeks.setText(output.data.results.pregnancy_weeks);
                miscarriageY.setChecked(output.data.results.had_miscarriage.equals("true"));
                miscarriageDate.setText(output.data.results.last_time_miscarriage);
                cycleAlterations.setText(output.data.results.cycle_alteration);
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
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
    public void onFinishMiscCreateSubmit(ResponseAPI responseAPI) {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MiscCreateAPI output = gson.fromJson(responseAPI.status_response, MiscCreateAPI.class);
            if (output.data.status.code.equals("200")) {
                /*Intent intent = new Intent(InitialDataMiscActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();*/
                Toast.makeText(this, getString(R.string.misc_settings_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
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
    public void onFinishSkip(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SkipInitialAPI output = gson.fromJson(responseAPI.status_response, SkipInitialAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent(InitialDataMiscActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
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
}