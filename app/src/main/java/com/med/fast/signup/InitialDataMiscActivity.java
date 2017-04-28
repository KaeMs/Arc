package com.med.fast.signup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.Utils;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.disease.DiseaseManagementAdapter;
import com.med.fast.management.disease.DiseaseManagementModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataMiscActivity extends FastBaseActivity {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Step Indicator
    @BindView(R.id.initialdata_step_imgview)
    ImageView step;

    // RecyclerView
    @BindView(R.id.initialdata_recycler)
    RecyclerView recyclerView;

    // Btns
    @BindView(R.id.initialdata_add_btn)
    CustomFontButton addBtn;
    @BindView(R.id.initialdata_skip_btn)
    CustomFontTextView skipBtn;
    @BindView(R.id.initialdata_next_btn)
    CustomFontTextView nextBtn;

    private DiseaseManagementAdapter diseaseManagementAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_mainlayout);

        toolbarTitle.setText(getString(R.string.step_4_miscellaneous));

        diseaseManagementAdapter = new DiseaseManagementAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(diseaseManagementAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(InitialDataMiscActivity.this);
                dialog.setContentView(R.layout.management_disease_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText diseaseName = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_name);
                final CustomFontRadioButton hereditaryY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_hereditary_y_rb);
                final CustomFontEditText inheritedFrom = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_inherited_from);

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(InitialDataMiscActivity.this);
                mAwesomeValidation.addValidation(diseaseName, RegexTemplate.NOT_EMPTY, getString(R.string.full_accident_details_required));

                // Setting Hereditary RB so when it is at "no", inheritance won't be added
                hereditaryY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        inheritedFrom.setEnabled(isChecked);
                        if (isChecked){
                            mAwesomeValidation.addValidation(inheritedFrom, RegexTemplate.NOT_EMPTY, getString(R.string.disease_hereditary_inherited_from_required));
                        } else {
                            mAwesomeValidation.clear();
                        }
                    }
                });

                final CustomFontRadioButton ongoingY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_currently_having_y_rb);
                final CustomFontTextView historicDate = (CustomFontTextView) dialog.findViewById(R.id.disease_popup_historic_date_tv);
                final Spinner approximateDateSpinner = (Spinner) dialog.findViewById(R.id.disease_popup_date_spinner);

                String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
                final ArrayAdapter<String> approximateSpinnerAdapter = new ArrayAdapter<>(InitialDataMiscActivity.this, android.R.layout.simple_spinner_item, approximates);
                approximateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                approximateDateSpinner.setAdapter(approximateSpinnerAdapter);

                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                historicDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatePickerDialog datePickerDialog = new DatePickerDialog(InitialDataMiscActivity.this, null, year, month, day);
                        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                        datePickerDialog.getDatePicker().updateDate(year, month, day);
                        datePickerDialog.show();
                        datePickerDialog.setCanceledOnTouchOutside(true);

                        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Formatting date from MM to MMM
                                        SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
                                        Date newDate = null;
                                        try {
                                            newDate = format.parse(String.valueOf(month) + " " + String.valueOf(day) + " " + String.valueOf(year));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        format = new SimpleDateFormat(Constants.dateFormatSpace, Locale.getDefault());
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

                CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
                CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (mAwesomeValidation.validate()){
                            DiseaseManagementModel diseaseManagementModel = new DiseaseManagementModel();
                            diseaseManagementModel.setDisease_name(diseaseName.getText().toString());
                            if (hereditaryY.isChecked()){
                                diseaseManagementModel.setDisease_hereditary("yes");
                                diseaseManagementModel.setDisease_hereditary_carriers(inheritedFrom.getText().toString());
                            } else {
                                diseaseManagementModel.setDisease_hereditary("no");
                                diseaseManagementModel.setDisease_hereditary_carriers(getString(R.string.sign_dash));
                            }

                            if (ongoingY.isChecked()){
                                diseaseManagementModel.setDisease_ongoing("yes");
                            } else {
                                diseaseManagementModel.setDisease_ongoing("no");
                            }

                            diseaseManagementModel.setDate_last_visit("-");
                            if (!historicDate.getText().toString().equals("")){
                                diseaseManagementModel.setDate_historic(historicDate.getText().toString());
                            } else if (approximateDateSpinner.getSelectedItemPosition() > 0){
                                diseaseManagementModel.setDate_approximate(approximateSpinnerAdapter.getItem(approximateDateSpinner.getSelectedItemPosition()));
                            }
                            diseaseManagementModel.setDate_created(Utils.getCurrentDate());

                            diseaseManagementAdapter.addSingle(diseaseManagementModel);

                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataMiscActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataMiscActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
