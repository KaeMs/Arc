package com.med.fast.management.misc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.management.misc.api.MiscCreateAPI;
import com.med.fast.management.misc.api.MiscCreateAPIFunc;
import com.med.fast.management.misc.api.MiscShowAPI;
import com.med.fast.management.misc.api.MiscShowAPIFunc;
import com.med.fast.management.misc.miscinterface.MiscShowCreateIntf;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscManagementFragment extends FastBaseFragment implements MiscShowCreateIntf {

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
    private String userId;
    private boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_misc_popup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).changeTitle("MISC MANAGEMENT");
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        String gender = SharedPreferenceUtilities.getFromSessionSP(getActivity(), SharedPreferenceUtilities.USER_GENDER);
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

        refreshView(false);

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

                    MiscCreateAPIFunc miscCreateAPIFunc = new MiscCreateAPIFunc(getActivity(), MiscManagementFragment.this);
                    miscCreateAPIFunc.execute(miscCreateAPI);
                    isLoading = true;
                }
            }
        });
        backBtn.setVisibility(View.GONE);
    }

    public void refreshView(boolean setRefreshing) {
        MiscShowAPI miscShowAPI = new MiscShowAPI();
        miscShowAPI.data.query.user_id = userId;
        miscShowAPI.data.query.is_initial = "false";

        MiscShowAPIFunc miscShowAPIFunc = new MiscShowAPIFunc(getActivity(), MiscManagementFragment.this);
        miscShowAPIFunc.execute(miscShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;
    }

    @Override
    public void onFinishMiscShow(ResponseAPI responseAPI) {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            final MiscShowAPI output = gson.fromJson(responseAPI.status_response, MiscShowAPI.class);
            if (output.data.status.code.equals("200")) {
                SharedPreferenceUtilities.setUserInformation(getActivity(), SharedPreferenceUtilities.USER_GENDER, output.data.results.is_female ? "1" : "0");
                if (output.data.results.is_female) {
                    femaleWrapper.setVisibility(View.VISIBLE);
                } else {
                    femaleWrapper.setVisibility(View.GONE);
                }
                voluptuaryHabit.setText(Utils.processStringFromAPI(output.data.results.voluptuary_habits));
                pregnantY.setChecked(Utils.processBoolStringFromAPI(output.data.results.pregnancy));
                pregnancyWeeks.setText(Utils.processStringFromAPI(output.data.results.pregnancy_weeks));
                miscarriageY.setChecked(Utils.processBoolStringFromAPI(output.data.results.had_miscarriage));
                miscarriageDate.setText(Utils.processStringFromAPI(output.data.results.last_time_miscarriage));
                cycleAlterations.setText(Utils.processStringFromAPI(output.data.results.cycle_alteration));

                /*FastAppController.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        SummaryWrapperModel realmSummaryWrapper = realm.where(SummaryWrapperModel.class).findFirst();
                        if (realmSummaryWrapper != null){
                            realmSummaryWrapper.voluptuary_habits = output.data.results.voluptuary_habits;

                        }
                    }
                });*/
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) getActivity()).forceLogout();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishMiscCreateSubmit(ResponseAPI responseAPI) {
        if (this.isVisible()){
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                MiscCreateAPI output = gson.fromJson(responseAPI.status_response, MiscCreateAPI.class);
                if (output.data.status.code.equals("200")) {
                    Toast.makeText(getActivity(), getString(R.string.misc_settings_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity) getActivity()).forceLogout();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
