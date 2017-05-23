package com.med.fast.management.disease;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.disease.api.DiseaseManagementEditShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementEditShowAPIFunc;
import com.med.fast.management.disease.api.DiseaseManagementEditSubmitAPI;
import com.med.fast.management.disease.api.DiseaseManagementEditSubmitAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementEditIntf;

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

public class DiseaseEditActivity extends FastBaseActivity implements DiseaseManagementEditIntf {
    @BindView(R.id.disease_popup_name)
    CustomFontEditText diseaseName;
    @BindView(R.id.disease_popup_hereditary_y_rb)
    CustomFontRadioButton hereditaryYes;
    @BindView(R.id.disease_popup_hereditary_n_rb)
    CustomFontRadioButton hereditaryNo;
    @BindView(R.id.disease_popup_inherited_from)
    CustomFontEditText inheritedFrom;
    @BindView(R.id.disease_popup_currently_having_y_rb)
    CustomFontRadioButton ongoingY;
    @BindView(R.id.disease_popup_currently_having_n_rb)
    CustomFontRadioButton ongoingN;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    @BindView(R.id.disease_popup_date_spinner)
    Spinner dateSpinner;
    ArrayAdapter<String> approximateSpinnerAdapter;
    @BindView(R.id.disease_popup_historic_date_tv)
    CustomFontTextView historicDate;
    private int year, month, day;

    private DiseaseManagementModel diseaseManagementModel;
    private DiseaseManagementEditShowAPIFunc diseaseManagementEditShowAPIFunc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_disease_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            Gson gson = new Gson();
            diseaseManagementModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.DISEASE_MODEL_EXTRA),
                    DiseaseManagementModel.class);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
        approximateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, approximates);
        dateSpinner.setAdapter(approximateSpinnerAdapter);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        historicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(DiseaseEditActivity.this, null, year, month, day);
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
                                historicDate.setText(date);
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
                    String hereditaryType = String.valueOf(hereditaryYes.isChecked());
                    String isOngoingStr = String.valueOf(ongoingY.isChecked());
                    String inheritedFromString = Utils.processStringForAPI(inheritedFrom.getText().toString());
                    String historicDateString = Utils.processStringForAPI(historicDate.getText().toString());
                    String dateSpinnerString = dateSpinner.getSelectedItem().toString();

                    diseaseManagementModel.setName(diseaseNameString);
                    diseaseManagementModel.setIs_hereditary(hereditaryType);
                    diseaseManagementModel.setIs_ongoing(isOngoingStr);
                    diseaseManagementModel.setHereditary_carriers(inheritedFromString);
                    diseaseManagementModel.setHistoric_date(historicDateString);
                    diseaseManagementModel.setApproximate_date(dateSpinnerString);
                    diseaseManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);

                    DiseaseManagementEditSubmitAPI diseaseManagementEditSubmitAPI = new DiseaseManagementEditSubmitAPI();
                    diseaseManagementEditSubmitAPI.data.query.name = diseaseNameString;
                    diseaseManagementEditSubmitAPI.data.query.disease_id = diseaseManagementModel.getId();
                    diseaseManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPI.data.query.is_hereditary = hereditaryType;
                    diseaseManagementEditSubmitAPI.data.query.is_ongoing = isOngoingStr;
                    diseaseManagementEditSubmitAPI.data.query.hereditary_carriers = inheritedFromString;
                    diseaseManagementEditSubmitAPI.data.query.historic_date = historicDateString;
                    diseaseManagementEditSubmitAPI.data.query.approximate_date = dateSpinnerString;

                    DiseaseManagementEditSubmitAPIFunc diseaseManagementEditSubmitAPIFunc = new DiseaseManagementEditSubmitAPIFunc(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPIFunc.setDelegate(DiseaseEditActivity.this);
                    diseaseManagementEditSubmitAPIFunc.execute(diseaseManagementEditSubmitAPI);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        diseaseManagementEditShowAPIFunc.cancel(true);
    }

    private void refreshView() {
        DiseaseManagementEditShowAPI diseaseManagementEditShowAPI = new DiseaseManagementEditShowAPI();
        diseaseManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        diseaseManagementEditShowAPI.data.query.disease_id = diseaseManagementModel.getId();

        diseaseManagementEditShowAPIFunc = new DiseaseManagementEditShowAPIFunc(this);
        diseaseManagementEditShowAPIFunc.setDelegate(this);
        diseaseManagementEditShowAPIFunc.execute(diseaseManagementEditShowAPI);
    }

    @Override
    public void onFinishDiseaseManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                diseaseName.setText(output.data.results.name);
                hereditaryYes.setChecked(Utils.processBoolStringFromAPI(output.data.results.is_hereditary));
                ongoingY.setChecked(Utils.processBoolStringFromAPI(output.data.results.is_ongoing));
                historicDate.setText(output.data.results.historic_date);
                int spinnerPos = approximateSpinnerAdapter.getPosition(output.data.results.approximate_date);
                if (spinnerPos >= 0){
                    dateSpinner.setSelection(approximateSpinnerAdapter.getPosition(output.data.results.approximate_date));
                } else {
                    dateSpinner.setSelection(0);
                }
                inheritedFrom.setText(output.data.results.hereditary_carriers);
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

    }

    @Override
    public void onFinishDiseaseManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String diseaseModelString = gson.toJson(diseaseManagementModel);
                intent.putExtra(ConstantsManagement.DISEASE_MODEL_EXTRA, diseaseModelString);
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
