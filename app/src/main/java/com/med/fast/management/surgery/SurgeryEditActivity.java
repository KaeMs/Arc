package com.med.fast.management.surgery;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
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
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.surgery.api.SurgeryManagementEditShowAPI;
import com.med.fast.management.surgery.api.SurgeryManagementEditShowAPIFunc;
import com.med.fast.management.surgery.api.SurgeryManagementEditSubmitAPI;
import com.med.fast.management.surgery.api.SurgeryManagementEditSubmitAPIFunc;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementEditIntf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class SurgeryEditActivity extends FastBaseActivity implements SurgeryManagementEditIntf {
    @BindView(R.id.surgery_popup_procedure)
    CustomFontEditText surgProcedure;
    @BindView(R.id.surgery_popup_physician_name)
    CustomFontEditText surgPhyName;
    @BindView(R.id.surgery_popup_hospital_name)
    CustomFontEditText surgHospitalName;
    @BindView(R.id.surgery_popup_surgery_date)
    CustomFontTextView surgeryDate;
    @BindView(R.id.surgery_popup_note)
    CustomFontEditText surgNote;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private SurgeryManagementModel surgeryManagementModel;

    private int year, month, day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_surgery_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            Gson gson = new Gson();
            surgeryManagementModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.SURGERY_MODEL_EXTRA),
                    SurgeryManagementModel.class);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        surgeryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(SurgeryEditActivity.this, null, year, month, day);
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
                                surgeryDate.setText(date);
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
        mAwesomeValidation.addValidation(surgProcedure, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));

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
                if (!surgeryDate.getText().toString().equals("")){
                    if (mAwesomeValidation.validate()) {
                        String surgProcedureString = surgProcedure.getText().toString();
                        String surgHospitalNameString = Utils.processStringForAPI(surgHospitalName.getText().toString());
                        String surgDateString = surgeryDate.getText().toString();
                        String surgNoteString = Utils.processStringForAPI(surgNote.getText().toString());
                        String surgPhyNameString = Utils.processStringForAPI(surgPhyName.getText().toString());

                        surgeryManagementModel.setProcedure(surgProcedureString);
                        surgeryManagementModel.setHospital(surgHospitalNameString);
                        surgeryManagementModel.setDate(surgDateString);
                        surgeryManagementModel.setNote(surgNoteString);
                        surgeryManagementModel.setPhysician(surgPhyNameString);
                        surgeryManagementModel.setProgress_status("0");

                        SurgeryManagementEditSubmitAPI surgeryManagementEditSubmitAPI = new SurgeryManagementEditSubmitAPI();
                        surgeryManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(SurgeryEditActivity.this);
                        surgeryManagementEditSubmitAPI.data.query.surgery_id = surgeryManagementModel.getId();
                        surgeryManagementEditSubmitAPI.data.query.procedure = surgProcedureString;
                        surgeryManagementEditSubmitAPI.data.query.hospital = surgHospitalNameString;
                        surgeryManagementEditSubmitAPI.data.query.date = surgDateString;
                        surgeryManagementEditSubmitAPI.data.query.notes = surgNoteString;
                        surgeryManagementEditSubmitAPI.data.query.physician = surgPhyNameString;

                        SurgeryManagementEditSubmitAPIFunc surgeryManagementEditSubmitAPIFunc = new SurgeryManagementEditSubmitAPIFunc(SurgeryEditActivity.this);
                        surgeryManagementEditSubmitAPIFunc.setDelegate(SurgeryEditActivity.this);
                        surgeryManagementEditSubmitAPIFunc.execute(surgeryManagementEditSubmitAPI);
                    }
                } else {
                    Toast.makeText(SurgeryEditActivity.this, getString(R.string.surgery_date_required), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshView() {
        SurgeryManagementEditShowAPI surgeryManagementEditShowAPI = new SurgeryManagementEditShowAPI();
        surgeryManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        surgeryManagementEditShowAPI.data.query.surgery_id = surgeryManagementModel.getId();

        SurgeryManagementEditShowAPIFunc surgeryManagementEditShowAPIFunc = new SurgeryManagementEditShowAPIFunc(this);
        surgeryManagementEditShowAPIFunc.setDelegate(this);
        surgeryManagementEditShowAPIFunc.execute(surgeryManagementEditShowAPI);
    }

    @Override
    public void onFinishSurgeryManagementEditShow(ResponseAPI responseAPI) {

        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                surgeryDate.setText(output.data.results.date);
                surgHospitalName.setText(output.data.results.hospital);
                surgNote.setText(output.data.results.notes);
                surgProcedure.setText(output.data.results.procedure);
                surgPhyName.setText(output.data.results.physician);
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
    public void onFinishSurgeryManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(surgeryManagementModel);
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
