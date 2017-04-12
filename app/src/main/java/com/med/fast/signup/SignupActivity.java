package com.med.fast.signup;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.med.fast.FastBaseActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin on 4/10/2017. F
 */

public class SignupActivity extends FastBaseActivity {

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
                    Toast.makeText(SignupActivity.this, "Lalala", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "Lohlohloh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Function to show date
    private void showDate(int year, int month, int day) {
        String monthName;
        switch (month) {
            case 0:
                monthName = "Jan";
                break;
            case 1:
                monthName = "Feb";
                break;
            case 2:
                monthName = "Mar";
                break;
            case 3:
                monthName = "Apr";
                break;
            case 4:
                monthName = "May";
                break;
            case 5:
                monthName = "Jun";
                break;
            case 6:
                monthName = "Jul";
                break;
            case 7:
                monthName = "Aug";
                break;
            case 8:
                monthName = "Sep";
                break;
            case 9:
                monthName = "Oct";
                break;
            case 10:
                monthName = "Nov";
                break;
            case 11:
                monthName = "Dec";
                break;
            default:
                monthName = "Invalid month";
                break;
        }

        String days;
        if (day < 10) {
            days = "0" + String.valueOf(day);
        } else {
            days = String.valueOf(day);
        }
        String months;
        if (month < 10) {
            months = "0" + String.valueOf(month);
        } else {
            months = String.valueOf(month);
        }
        this.year = year;
        this.month = month;
        this.day = day;
        fullDate = days + "/" + months + "/" + year;
        // StringBuilder for dd/mm/yyyy
        dobTV.setText(new StringBuilder().append(day).append(" ")
                .append(monthName).append(" ").append(year));
    }
}
