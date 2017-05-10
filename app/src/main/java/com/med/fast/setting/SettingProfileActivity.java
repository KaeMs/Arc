package com.med.fast.setting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.UtilityUriHelper;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.setting.api.SettingSubmitAPI;
import com.med.fast.setting.api.SettingSubmitAPIFunc;
import com.med.fast.summary.SummaryFragment;

import java.io.File;
import java.io.IOException;
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

public class SettingProfileActivity extends FastBaseActivity implements SettingSubmitAPIIntf {
    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Picture    
    @BindView(R.id.setting_profile_photo)
    ImageView profilePhoto;
    
    // Name
    @BindView(R.id.setting_profile_fname)
    CustomFontEditText firstNameET;
    @BindView(R.id.setting_profile_lname)
    CustomFontEditText lastNameET;

    // Date of Birth
    @BindView(R.id.setting_profile_dob)
    CustomFontTextView dobTV;
    // Gender
    @BindView(R.id.setting_profile_maleRB)
    CustomFontRadioButton maleRB;
    @BindView(R.id.setting_profile_femaleRB)
    CustomFontRadioButton femaleRB;
    // Confirm
    @BindView(R.id.setting_profile_submit_btn)
    CustomFontButton submitBtn;
    private int year, month, day;
    private boolean photoChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_profile_layout);

        toolbarTitle.setText(getString(R.string.setting_edit_account));

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImage();
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dobTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(SettingProfileActivity.this, null, year, month, day);
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

        String regexName = "^[a-zA-Z]+$";
        String regexPassword = "[^-\\s]{8,50}"; // No whitespace, min 8 max 50

        mAwesomeValidation.addValidation(firstNameET, regexName, "Format nama salah");
        mAwesomeValidation.addValidation(lastNameET, regexName, "Format nama belakang salah");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    SettingSubmitAPI settingSubmitAPI = new SettingSubmitAPI();
                    settingSubmitAPI.data.query.first_name = firstNameET.getText().toString();
//                    settingSubmitAPI.data.query.profil_image_path = firstNameET.getText().toString();
                    settingSubmitAPI.data.query.last_name = lastNameET.getText().toString();
                    settingSubmitAPI.data.query.date_of_birth = dobTV.getText().toString();
                    settingSubmitAPI.data.query.gender = maleRB.isChecked()? "0" : "1" ;

                    if (photoChanged){
                        settingSubmitAPI.data.query.profile_image_file = new File(UtilityUriHelper.getPath(SettingProfileActivity.this, createdPhotoUri));
                    }

                    SettingSubmitAPIFunc settingSubmitAPIFunc = new SettingSubmitAPIFunc(SettingProfileActivity.this);
                    settingSubmitAPIFunc.setDelegate(SettingProfileActivity.this);
                    settingSubmitAPIFunc.execute(settingSubmitAPI);
                }
            }
        });
    }

    private Uri createdPhotoUri;
    private String currentMediaPath = "";
    private CreatedImageModel createdImageModel;

    public void addNewImage() {
        try {
            createdImageModel = createImageFile();
            File output = createdImageModel.image;
            currentMediaPath = createdImageModel.currentMediaPath;
            createdPhotoUri = createdImageModel.mDestinationUri;
            createImagePickerDialog(this, output, getString(R.string.select_image_source));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    createdPhotoUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                    profilePhoto.setImageURI(createdPhotoUri);
                    photoChanged = true;
                } catch (Exception e){
                    Toast.makeText(this, getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                    new File(currentMediaPath).delete();
                }
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    currentMediaPath = UtilityUriHelper.getPath(this, data.getData());
                    createdPhotoUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                    photoChanged = true;
                    if (createdPhotoUri != null){
                        profilePhoto.setImageURI(data.getData());
                    } else {
                        Toast.makeText(this, getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                        new File(currentMediaPath).delete();
                    }
                } catch (Exception e){
                    Toast.makeText(this, getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                    new File(currentMediaPath).delete();
                }
            }
        } else if (requestCode == RequestCodeList.PHOTO_OPERATIONS){
            if (resultCode == Activity.RESULT_OK){
                createImagePickerDialog(this, createdImageModel.image, getString(R.string.select_image_source));
            }
        }
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
