package com.med.fast;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.med.fast.customviews.CustomFontEditText;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * Created by Kevin on 4/10/2017.
 */

public class SignupActivity extends FastBaseActivity {

    @BindView(R.id.signup_firstNameET)
    CustomFontEditText firstNameET;
    @BindView(R.id.signup_lastNameET)
    CustomFontEditText lastNameET;

    @BindView(R.id.signup_emailAddressET)
    CustomFontEditText emailAddressET;

    @BindView(R.id.signup_dobTV)
    CustomFontEditText dobTV;
    private int year, month, day;
    private String fullDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,-10);
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
                            public void onClick (DialogInterface dialog,int which){
                                if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    // Do Stuff
                                    datePickerDialog.dismiss();
                                }
                            }
                        });
            }
        });
    }

    //Function to show date
    private void showDate(int year, int month, int day) {
        String monthName;
        switch (month) {
            case 1:  monthName = "Jan";
                break;
            case 2:  monthName = "Feb";
                break;
            case 3:  monthName = "Mar";
                break;
            case 4:  monthName = "Apr";
                break;
            case 5:  monthName = "May";
                break;
            case 6:  monthName = "Jun";
                break;
            case 7:  monthName = "Jul";
                break;
            case 8:  monthName = "Aug";
                break;
            case 9:  monthName = "Sep";
                break;
            case 10: monthName = "Oct";
                break;
            case 11: monthName = "Nov";
                break;
            case 12: monthName = "Dec";
                break;
            default: monthName = "Invalid month";
                break;
        }
        // StringBuilder for dd/mm/yyyy
        dobTV.setText(new StringBuilder().append(day).append(" ")
                .append(monthName).append(" ").append(year));

        String days;
        if(day < 10){
            days = "0" + String.valueOf(day);
        }else{
            days = String.valueOf(day);
        }
        String months;
        if(month < 10){
            months = "0" + String.valueOf(month);
        }else{
            months = String.valueOf(month);
        }
        fullDate = days + "/" + months + "/" + year;
    }
}
