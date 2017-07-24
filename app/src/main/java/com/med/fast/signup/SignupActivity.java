package com.med.fast.signup;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastWebViewActivity;
import com.med.fast.FontUtils;
import com.med.fast.IntentNames;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customclasses.NoUnderlineClickableSpan;
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
    @BindView(R.id.signup_legal_terms)
    CustomFontTextView legalTerms;
    @BindView(R.id.signup_confirmBtn)
    CustomFontButton confirmBtn;
    private int year, month, day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbarTitle.setText(getString(R.string.signup_title));

        SpannableStringBuilder legalSb = new SpannableStringBuilder(getString(R.string.legal_agreement_txt));
        legalSb.append(" ");
        String termsTxt = getString(R.string.terms);
        legalSb.append(FontUtils.colorify(SignupActivity.this, R.color.pink, termsTxt));
        NoUnderlineClickableSpan termsCSpan = new NoUnderlineClickableSpan() {
            @Override
            public void onClick(View tv) {
                Intent intent = new Intent(SignupActivity.this, FastWebViewActivity.class);
                intent.putExtra(IntentNames.WEBVIEW_URL, APIConstants.LEGAL_TERMS_URL);
                startActivity(intent);
            }
        };
        legalSb.setSpan(termsCSpan, legalSb.length() - termsTxt.length(), legalSb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        legalTerms.setText(legalSb);
        legalTerms.setMovementMethod(LinkMovementMethod.getInstance());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

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
                                dobTV.setText(date);
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

        firstNameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        lastNameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mAwesomeValidation.addValidation(firstNameET, Constants.REGEX_NAME, getString(R.string.first_name_wrong_format));
        mAwesomeValidation.addValidation(lastNameET, Constants.REGEX_NAME, getString(R.string.last_name_incorrect_format));
        mAwesomeValidation.addValidation(emailAddressET, Patterns.EMAIL_ADDRESS, getString(R.string.email_address_incorrect_format));
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_passwordET, Constants.REGEX_PASSWORD, R.string.password_wrong_format);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_confirmPassET, R.id.signup_passwordET, R.string.password_wrong_confirmation);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dobTV.getText().toString().equals("")){
                    Toast.makeText(SignupActivity.this, getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT).show();
                } else {
                    if (mAwesomeValidation.validate()) {
                        RegisterSubmitAPI registerSubmitAPI = new RegisterSubmitAPI();
                        registerSubmitAPI.data.query.first_name = firstNameET.getText().toString();
                        registerSubmitAPI.data.query.last_name = lastNameET.getText().toString();
                        registerSubmitAPI.data.query.email = emailAddressET.getText().toString();
                        registerSubmitAPI.data.query.dob = dobTV.getText().toString();
                        registerSubmitAPI.data.query.password = passwordET.getText().toString();
                        registerSubmitAPI.data.query.gender = maleRB.isChecked()? "0" : "1" ;

                        RegisterSubmitAPIFunc registerSubmitAPIFunc = new RegisterSubmitAPIFunc(SignupActivity.this);
                        registerSubmitAPIFunc.setDelegate(SignupActivity.this);
                        registerSubmitAPIFunc.execute(registerSubmitAPI);
                    }
                }
            }
        });
    }

    @Override
    public void onFinishRegisterSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            RegisterSubmitAPI output = gson.fromJson(responseAPI.status_response, RegisterSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_ID, output.data.results.saved_user_id);
//                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_FIRST_NAME, output.data.results.saved_user_id);
//                SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_GENDER, output.data.results.saved_user_id);
                Intent intent = new Intent(this, SignupSuccessActivity.class);
                intent.putExtra("email", emailAddressET.getText().toString());
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
