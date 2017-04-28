package com.med.fast.signup;

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
import com.med.fast.signup.api.RegisterSubmitAPI;
import com.med.fast.signup.api.RegisterSubmitAPIFunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

/**
 * Created by Kevin on 4/10/2017. F
 */

public class SignupActivity extends FastBaseActivity implements RegisterSubmitAPIIntf {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Name
    @BindView(R.id.signup_firstNameET)
    CustomFontEditText firstNameET;
    @BindView(R.id.signup_lastNameET)
    CustomFontEditText lastNameET;

    // Email
    @BindView(R.id.signup_emailAddressET)
    CustomFontEditText emailAddressET;

    // Date of Birth
    @BindView(R.id.signup_dobTV)
    CustomFontTextView dobTV;
    // Gender
    @BindView(R.id.signup_maleRB)
    CustomFontRadioButton maleRB;
    @BindView(R.id.signup_femaleRB)
    CustomFontRadioButton femaleRB;
    // Password
    @BindView(R.id.signup_passwordET)
    CustomFontEditText passwordET;
    @BindView(R.id.signup_confirmPassET)
    CustomFontEditText confirmPassET;
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, null, year, month, day);
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
        mAwesomeValidation.addValidation(emailAddressET, Patterns.EMAIL_ADDRESS, "Format email salah");
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_passwordET, regexPassword, R.string.wrong_password_format_mssg);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_confirmPassET, R.id.signup_passwordET, R.string.wrong_password_confirmation);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    RegisterSubmitAPI registerSubmitAPI = new RegisterSubmitAPI();
                    registerSubmitAPI.data.query.first_name = firstNameET.getText().toString();
                    registerSubmitAPI.data.query.last_name = lastNameET.getText().toString();
                    registerSubmitAPI.data.query.email = emailAddressET.getText().toString();
                    registerSubmitAPI.data.query.dob = fullDate;
                    registerSubmitAPI.data.query.password = passwordET.getText().toString();
                    registerSubmitAPI.data.query.gender = maleRB.isChecked()? "0" : "1" ;

                    RegisterSubmitAPIFunc registerSubmitAPIFunc = new RegisterSubmitAPIFunc(SignupActivity.this);
                    registerSubmitAPIFunc.setDelegate(SignupActivity.this);
                    registerSubmitAPIFunc.execute(registerSubmitAPI);
                } else {
                    Toast.makeText(SignupActivity.this, "Lohlohloh", Toast.LENGTH_SHORT).show();
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
    public void onFinishRegisterSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            RegisterSubmitAPI output = gson.fromJson(responseAPI.status_response, RegisterSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent(this, InitialDataAllergyActivity.class);
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
