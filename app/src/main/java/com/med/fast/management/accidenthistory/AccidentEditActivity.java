package com.med.fast.management.accidenthistory;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryEditIntf;
import com.med.fast.management.accidenthistory.api.AccidentHistoryEditShowAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryEditShowAPIFunc;
import com.med.fast.management.accidenthistory.api.AccidentHistoryEditSubmitAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryEditSubmitAPIFunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class AccidentEditActivity extends FastBaseActivity implements AccidentHistoryEditIntf {
    @BindView(R.id.accident_popup_accident_details)
    CustomFontEditText accidentDetails;
    @BindView(R.id.accident_popup_injury_nature)
    CustomFontEditText injuryNature;
    @BindView(R.id.accident_popup_injury_location)
    CustomFontEditText injuryLocation;
    @BindView(R.id.accident_popup_accident_date_tv)
    CustomFontTextView accidentDateTV;
    @BindView(R.id.accident_popup_accident_date_spinner)
    Spinner accidentDateSpinner;
    @BindView(R.id.accident_popup_accident_other_date_wrapper)
    LinearLayout injuryDateCustomWrapper;
    @BindView(R.id.accident_popup_accident_other_date)
    CustomFontEditText injuryDateCustomTV;
    ArrayAdapter<String> accidentSpinnerAdapter;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    int year, month, day;

    private AccidentHistoryManagementModel accidentHistoryManagementModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_accident_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            Gson gson = new Gson();
            accidentHistoryManagementModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.ACCIDENT_MODEL_EXTRA),
                    AccidentHistoryManagementModel.class);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
        accidentSpinnerAdapter = new ArrayAdapter<>(AccidentEditActivity.this, android.R.layout.simple_spinner_item, approximates);
        accidentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accidentDateSpinner.setAdapter(accidentSpinnerAdapter);
        accidentDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == accidentSpinnerAdapter.getCount() - 1){
                    injuryDateCustomWrapper.setVisibility(View.VISIBLE);
                } else {
                    injuryDateCustomWrapper.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        accidentDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(AccidentEditActivity.this, null, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getDatePicker().updateDate(year, month, day);
                datePickerDialog.show();
                datePickerDialog.setCanceledOnTouchOutside(true);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                year = datePickerDialog.getDatePicker().getYear();
                                month = datePickerDialog.getDatePicker().getMonth();
                                day = datePickerDialog.getDatePicker().getDayOfMonth();
                                // Formatting date from MM to MMM
                                SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
                                Date newDate = null;
                                try {
                                    newDate = format.parse(String.valueOf(month + 1) + " " + String.valueOf(day) + " " + String.valueOf(year));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                format = new SimpleDateFormat(Constants.dateFormatComma, Locale.getDefault());
                                String date = format.format(newDate);
                                accidentDateTV.setText(date);
                            }
                        });

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    // Do Stuff
                                    datePickerDialog.dismiss();
                                }
                            }
                        });
            }
        });

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(accidentDetails, RegexTemplate.NOT_EMPTY, getString(R.string.full_accident_details_required));

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
                if (mAwesomeValidation.validate()){

                    String detail = Utils.processStringForAPI(accidentDetails.getText().toString());
                    String injury_nature = Utils.processStringForAPI(injuryNature.getText().toString());
                    String injury_location = Utils.processStringForAPI(injuryLocation.getText().toString());
                    String injury_date = Utils.processStringForAPI(accidentDateTV.getText().toString());
                    String injury_date_tmp = Utils.processStringForAPI(accidentSpinnerAdapter.getItem(accidentDateSpinner.getSelectedItemPosition()));
                    String injuryDateCustom = Utils.processStringForAPI(injuryDateCustomTV.getText().toString());

                    accidentHistoryManagementModel.setDetail(accidentDetails.getText().toString());
                    accidentHistoryManagementModel.setInjury_nature(injuryNature.getText().toString());
                    accidentHistoryManagementModel.setInjury_location(injuryLocation.getText().toString());
                    if (!accidentDateTV.getText().toString().equals("")){
                        accidentHistoryManagementModel.setInjury_date(accidentDateTV.getText().toString());
                    } else if (accidentDateSpinner.getSelectedItemPosition() > 0){
                        accidentHistoryManagementModel.setInjury_date(accidentSpinnerAdapter.getItem(accidentDateSpinner.getSelectedItemPosition()));
                    }
                    accidentHistoryManagementModel.setInjury_date_tmp(injury_date_tmp);
                    accidentHistoryManagementModel.setInjury_date_custom(injuryDateCustom);

                    accidentHistoryManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);

                    AccidentHistoryEditSubmitAPI accidentHistoryEditSubmitAPI = new AccidentHistoryEditSubmitAPI();
                    accidentHistoryEditSubmitAPI.data.query.id = accidentHistoryManagementModel.getId();
                    accidentHistoryEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(AccidentEditActivity.this);
                    accidentHistoryEditSubmitAPI.data.query.detail = detail;
                    accidentHistoryEditSubmitAPI.data.query.injury_nature = injury_nature;
                    accidentHistoryEditSubmitAPI.data.query.injury_location = injury_location;
                    accidentHistoryEditSubmitAPI.data.query.injury_date = injury_date;
                    accidentHistoryEditSubmitAPI.data.query.injury_date_tmp = injury_date_tmp;
                    accidentHistoryEditSubmitAPI.data.query.injury_date_custom = injuryDateCustom;

                    AccidentHistoryEditSubmitAPIFunc accidentHistoryEditSubmitAPIFunc = new AccidentHistoryEditSubmitAPIFunc(AccidentEditActivity.this);
                    accidentHistoryEditSubmitAPIFunc.setDelegate(AccidentEditActivity.this);
                    accidentHistoryEditSubmitAPIFunc.execute(accidentHistoryEditSubmitAPI);
                }
            }
        });
    }

    void refreshView(){
        AccidentHistoryEditShowAPI accidentHistoryEditShowAPI = new AccidentHistoryEditShowAPI();
        accidentHistoryEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        accidentHistoryEditShowAPI.data.query.accident_id = accidentHistoryManagementModel.getId();

        AccidentHistoryEditShowAPIFunc accidentHistoryEditShowAPIFunc = new AccidentHistoryEditShowAPIFunc(this);
        accidentHistoryEditShowAPIFunc.setDelegate(this);
        accidentHistoryEditShowAPIFunc.execute(accidentHistoryEditShowAPI);
    }

    @Override
    public void onFinishAccidentHistoryEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AccidentHistoryEditShowAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                accidentDetails.setText(output.data.results.detail);
                injuryNature.setText(output.data.results.injury_nature);
                injuryLocation.setText(output.data.results.injury_location);
                if (output.data.results.injury_date != null &&
                        !output.data.results.injury_date.equals(APIConstants.DEFAULT)) {
                    accidentDateTV.setText(output.data.results.injury_date);
                } else {
                    accidentDateTV.setText(getString(R.string.sign_dash));
                }

                if (output.data.results.injury_date_custom != null &&
                        !output.data.results.injury_date_custom.equals(APIConstants.DEFAULT)) {
                    accidentDateSpinner.setSelection(accidentSpinnerAdapter.getPosition(output.data.results.injury_date_custom));
                } else {
                    accidentDateSpinner.setSelection(0);
                }
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
    public void onFinishAccidentHistoryEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AccidentHistoryEditSubmitAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String accidentHistoryModelString = gson.toJson(accidentHistoryManagementModel);
                intent.putExtra(ConstantsManagement.ACCIDENT_MODEL_EXTRA, accidentHistoryModelString);
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
