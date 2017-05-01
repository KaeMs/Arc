package com.med.fast.setting;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.setting.api.SettingSubmitAPI;
import com.med.fast.setting.api.SettingSubmitAPIFunc;
import com.med.fast.signup.SignupActivity;
import com.med.fast.signup.api.RegisterSubmitAPI;
import com.med.fast.signup.api.RegisterSubmitAPIFunc;
import com.med.fast.summary.SummaryFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

/**
 * Created by kevindreyar on 01-May-17. FM
 */

public class SettingActivity extends FastBaseActivity implements SettingSubmitAPIIntf {
    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Name
    @BindView(R.id.setting_firstNameET)
    CustomFontEditText firstNameET;
    @BindView(R.id.setting_lastNameET)
    CustomFontEditText lastNameET;

    // Date of Birth
    @BindView(R.id.setting_dobTV)
    CustomFontTextView dobTV;
    // Gender
    @BindView(R.id.setting_maleRB)
    CustomFontRadioButton maleRB;
    @BindView(R.id.setting_femaleRB)
    CustomFontRadioButton femaleRB;
    // Confirm
    @BindView(R.id.signup_confirmBtn)
    CustomFontButton confirmBtn;
    private int year, month, day;
    private String fullDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbarTitle.setText(getString(R.string.signup_title));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        fullDate = day + "/" + month + "/" + year;

        dobTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(SettingActivity.this, null, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getDatePicker().updateDate(year, month, day);
                datePickerDialog.show();
                datePickerDialog.setCanceledOnTouchOutside(true);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDate(datePickerDialog.getDatePicker().getYear(),
                                        datePickerDialog.getDatePicker().getMonth(),
                                        datePickerDialog.getDatePicker().getDayOfMonth());
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

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(COLORATION);
//        mAwesomeValidation.setContext(SignupActivity.this);

        String regexName = "^[a-zA-Z]+$";
        String regexPassword = "[^-\\s]{8,50}"; // No whitespace, min 8 max 50

        mAwesomeValidation.addValidation(firstNameET, regexName, "Format nama salah");
        mAwesomeValidation.addValidation(lastNameET, regexName, "Format nama belakang salah");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    SettingSubmitAPI settingSubmitAPI = new SettingSubmitAPI();
                    settingSubmitAPI.data.query.first_name = firstNameET.getText().toString();
//                    settingSubmitAPI.data.query.profil_image_path = firstNameET.getText().toString();
                    settingSubmitAPI.data.query.last_name = lastNameET.getText().toString();
                    settingSubmitAPI.data.query.date_of_birth = fullDate;
                    settingSubmitAPI.data.query.gender = maleRB.isChecked()? "0" : "1" ;

                    SettingSubmitAPIFunc settingSubmitAPIFunc = new SettingSubmitAPIFunc(SettingActivity.this);
                    settingSubmitAPIFunc.setDelegate(SettingActivity.this);
                    settingSubmitAPIFunc.execute(settingSubmitAPI);
                } else {
                    Toast.makeText(SettingActivity.this, "Lohlohloh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Function to show date
    private void showDate(int year, int month, int day) {
        SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(String.valueOf(month) + " " + String.valueOf(day) + " " + String.valueOf(year));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String date = format.format(newDate);

        this.year = year;
        this.month = month;
        this.day = day;
        fullDate = date;
        // StringBuilder for dd/mm/yyyy
        dobTV.setText(date);
    }

    @Override
    public void onFinishSettingSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SettingSubmitAPI output = gson.fromJson(responseAPI.status_response, SettingSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent(this, SummaryFragment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            setResult(RequestCodeList.forceLogout);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
