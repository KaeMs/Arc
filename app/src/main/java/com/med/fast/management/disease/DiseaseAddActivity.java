package com.med.fast.management.disease;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.med.fast.management.disease.api.DiseaseCreateShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementCreateShowAPIFunc;
import com.med.fast.management.disease.api.DiseaseManagementCreateSubmitAPI;
import com.med.fast.management.disease.api.DiseaseManagementCreateSubmitAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementCreateIntf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by GMG-Developer on 7/19/2017.
 */

public class DiseaseAddActivity extends FastBaseActivity implements DiseaseManagementCreateIntf, DiseaseSearchAdapter.Listener {

    private String userId;

    private DiseaseSearchAdapter diseaseSearchAdapter;
    @BindView(R.id.disease_popup_name)
    AutoCompleteTextView diseaseName;
    @BindView(R.id.disease_popup_other)
    CustomFontEditText diseaseOtherName;
    @BindView(R.id.disease_popup_hereditary_y_rb)
    CustomFontRadioButton hereditaryY;
    @BindView(R.id.disease_popup_inherited_from)
    CustomFontEditText inheritedFrom;

    @BindView(R.id.disease_popup_currently_having_y_rb)
    CustomFontRadioButton ongoingY;
    @BindView(R.id.disease_popup_historic_date_tv)
    CustomFontTextView historicDate;
    @BindView(R.id.disease_popup_date_spinner)
    Spinner approximateDateSpinner;
    private int year, month, day;

    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    DiseaseManagementModel diseaseManagementModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_disease_popup);

        userId = SharedPreferenceUtilities.getUserId(this);

        diseaseSearchAdapter = new DiseaseSearchAdapter(this, R.layout.management_disease_search_item, this);
        diseaseName.setThreshold(1);
        diseaseName.setAdapter(diseaseSearchAdapter);
        DiseaseCreateShowAPI diseaseCreateShowAPI = new DiseaseCreateShowAPI();
        diseaseCreateShowAPI.data.query.user_id = userId;

        DiseaseManagementCreateShowAPIFunc diseaseManagementCreateShowAPIFunc = new DiseaseManagementCreateShowAPIFunc(this, this);
        diseaseManagementCreateShowAPIFunc.execute(diseaseCreateShowAPI);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(diseaseName, RegexTemplate.NOT_EMPTY, getString(R.string.full_accident_details_required));

        String[] approximates = getResources().getStringArray(R.array.disease_approximate_values);
        final ArrayAdapter<String> approximateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, approximates);
        approximateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        approximateDateSpinner.setAdapter(approximateSpinnerAdapter);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        historicDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(DiseaseAddActivity.this, null, year, month, day);
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
                if (diseaseName.getText().toString().toLowerCase().equals("other")){
                    if (TextUtils.isEmpty(diseaseOtherName.getText().toString())){
                        Toast.makeText(DiseaseAddActivity.this, "Other disease name needs to be filled", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (mAwesomeValidation.validate()) {
                    String diseaseNameString;
                    if (diseaseName.getText().toString().toLowerCase().equals("other")){
                        diseaseNameString = diseaseOtherName.getText().toString();
                    } else {
                        diseaseNameString = diseaseName.getText().toString();
                    }

                    String diseaseHereditaryString;
                    String diseaseHereditaryCarriersString;
                    if (hereditaryY.isChecked()) {
                        diseaseHereditaryString = "true";
                        diseaseHereditaryCarriersString = Utils.processStringForAPI(inheritedFrom.getText().toString());
                    } else {
                        diseaseHereditaryString = "false";
                        diseaseHereditaryCarriersString = APIConstants.DEFAULT;
                    }
                    String historicDateString = Utils.processStringForAPI(historicDate.getText().toString());
                    String approximateDateString = approximateDateSpinner.getSelectedItemPosition() > 0 ?
                            approximateSpinnerAdapter.getItem(approximateDateSpinner.getSelectedItemPosition()) :
                            APIConstants.DEFAULT;

                    DiseaseManagementModel diseaseManagementModel = new DiseaseManagementModel();
                    diseaseManagementModel.setName(diseaseNameString);
                    diseaseManagementModel.setIs_hereditary(diseaseHereditaryString);
                    diseaseManagementModel.setHereditary_carriers(diseaseHereditaryCarriersString);
                    diseaseManagementModel.setIs_ongoing(String.valueOf(ongoingY.isChecked()));

                    diseaseManagementModel.setLast_visit(getString(R.string.sign_dash));
                    if (!historicDate.getText().toString().equals("")) {
                        diseaseManagementModel.setHistoric_date(historicDate.getText().toString());
                    }

                    diseaseManagementModel.setApproximate_date(approximateDateString);
                    diseaseManagementModel.setCreated_date(Utils.getCurrentDate());
                    diseaseManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);
                    diseaseManagementModel.setTag(diseaseNameString + Utils.getCurrentDate());

                    DiseaseManagementCreateSubmitAPI diseaseManagementCreateSubmitAPI = new DiseaseManagementCreateSubmitAPI();
                    diseaseManagementCreateSubmitAPI.data.query.name = diseaseNameString;
                    diseaseManagementCreateSubmitAPI.data.query.user_id = userId;
                    diseaseManagementCreateSubmitAPI.data.query.is_hereditary = diseaseHereditaryString;
                    diseaseManagementCreateSubmitAPI.data.query.is_ongoing = String.valueOf(ongoingY.isChecked());
                    diseaseManagementCreateSubmitAPI.data.query.hereditary_carriers = diseaseHereditaryCarriersString;
                    diseaseManagementCreateSubmitAPI.data.query.historic_date = historicDateString;
                    diseaseManagementCreateSubmitAPI.data.query.approximate_date = approximateDateString;

                    DiseaseManagementCreateSubmitAPIFunc diseaseManagementCreateSubmitAPIFunc = new DiseaseManagementCreateSubmitAPIFunc(DiseaseAddActivity.this, diseaseManagementModel.getTag(), false);
                    diseaseManagementCreateSubmitAPIFunc.setDelegate(DiseaseAddActivity.this);
                    diseaseManagementCreateSubmitAPIFunc.execute(diseaseManagementCreateSubmitAPI);
                }
            }
        });
    }

    @Override
    public void onFinishDiseaseManagementCreateShow(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseCreateShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseCreateShowAPI.class);
            if (output.data.status.code.equals("200")) {
                if (output.data.results.disease_name_list.size() > 0){
                    diseaseSearchAdapter.changeItem(output.data.results.disease_name_list);
                }
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
    public void onFinishDiseaseManagementCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                intent.putExtra(ConstantsManagement.DISEASE_ID_EXTRA, output.data.results.new_disease_id);
                diseaseManagementModel.setId(output.data.results.new_disease_id);
                diseaseManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);
                intent.putExtra(ConstantsManagement.DISEASE_MODEL_EXTRA, gson.toJson(diseaseManagementModel));
                setResult(RESULT_OK, intent);
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

    @Override
    public void onExampleModelClicked(DiseaseNameModel model) {
        if (model.getName().toLowerCase().startsWith("other")){
            // TODO show and hide other
            diseaseOtherName.setVisibility(View.VISIBLE);
        } else {
            diseaseOtherName.setVisibility(View.GONE);
        }
    }
}
