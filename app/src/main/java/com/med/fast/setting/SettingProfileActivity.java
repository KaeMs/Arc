package com.med.fast.setting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
import com.med.fast.IntentNames;
import com.med.fast.MainActivity;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.UtilityUriHelper;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.setting.api.SettingShowAPI;
import com.med.fast.setting.api.SettingShowAPIFunc;
import com.med.fast.setting.api.SettingSubmitAPI;
import com.med.fast.setting.api.SettingSubmitAPIFunc;

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

public class SettingProfileActivity extends FastBaseActivity implements SettingAPIIntf {
    // Toolbar
    @BindView(R.id.toolbar_main_top_title)
    CustomFontTextView toolbarTitle;

    // Swipe Refresh
    @BindView(R.id.setting_profile_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;


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
    @BindView(R.id.setting_profile_submit_progress)
    ProgressBar submitProgressBar;

    private int year, month, day;
    private boolean photoChanged = false;
    private String userId;
    private Uri createdPhotoUri;
    private String currentMediaPath = "";
    private CreatedImageModel createdImageModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_profile_layout);

        toolbarTitle.setText(getString(R.string.setting_edit_account));
        userId = SharedPreferenceUtilities.getUserId(this);

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
                    settingSubmitAPI.data.query.user_id = userId;
                    settingSubmitAPI.data.query.first_name = firstNameET.getText().toString();
                    settingSubmitAPI.data.query.last_name = lastNameET.getText().toString();
                    settingSubmitAPI.data.query.date_of_birth = dobTV.getText().toString();
                    settingSubmitAPI.data.query.gender = maleRB.isChecked() ? "0" : "1";

                    if (photoChanged) {
                        String[] imagePaths = currentMediaPath.split("/");
//                        settingSubmitAPI.data.query.profil_image_path = imagePaths[imagePaths.length - 1];
                        settingSubmitAPI.data.query.profile_image_file = new File(UtilityUriHelper.getPath(SettingProfileActivity.this, createdPhotoUri));
                        settingSubmitAPI.data.query.is_avatar_changed = "true";
                    } else {
                        settingSubmitAPI.data.query.is_avatar_changed = "false";
                    }

                    SettingSubmitAPIFunc settingSubmitAPIFunc = new SettingSubmitAPIFunc(SettingProfileActivity.this, SettingProfileActivity.this);
                    settingSubmitAPIFunc.execute(settingSubmitAPI);
                    setLoading(true, false);
                }
            }
        });

        refreshView(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });
    }

    void setLoading(boolean isLoading, boolean enableSubmit) {
        if (isLoading) {
            submitProgressBar.setVisibility(View.VISIBLE);
        } else {
            submitProgressBar.setVisibility(View.INVISIBLE);
        }
        submitBtn.setEnabled(enableSubmit);
    }

    void refreshView(boolean setRefreshing) {
        SettingShowAPI settingShowAPI = new SettingShowAPI();
        settingShowAPI.data.query.user_id = userId;

        SettingShowAPIFunc settingShowAPIFunc = new SettingShowAPIFunc(this, this);
        settingShowAPIFunc.execute(settingShowAPI);
        setLoading(true, false);
        swipeRefreshLayout.setRefreshing(setRefreshing);
    }

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
//                    profilePhoto.setImageURI(createdPhotoUri);
                    Glide.with(this)
                            .load(createdPhotoUri)
                            .into(profilePhoto);
                    photoChanged = true;
                } catch (Exception e) {
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
                    if (createdPhotoUri != null) {
                        Glide.with(this)
                                .load(createdPhotoUri)
                                .into(profilePhoto);
//                        profilePhoto.setImageURI(data.getData());
                    } else {
                        Toast.makeText(this, getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                        new File(currentMediaPath).delete();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                    new File(currentMediaPath).delete();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodeList.PHOTO_OPERATIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            createImagePickerDialog(this, createdImageModel.image, getString(R.string.select_image_source));
        }
    }

    @Override
    public void onFinishSettingShow(ResponseAPI responseAPI) {
        swipeRefreshLayout.setRefreshing(false);
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SettingShowAPI output = gson.fromJson(responseAPI.status_response, SettingShowAPI.class);
            if (output != null) {
                if (output.data.status.code.equals("200")) {
                    setLoading(false, true);

                    firstNameET.setText(output.data.results.first_name);
                    lastNameET.setText(output.data.results.last_name);
                    dobTV.setText(output.data.results.date_of_birth);
                    maleRB.setChecked(output.data.results.gender == 0);
                    femaleRB.setChecked(output.data.results.gender == 1);

                    Glide.with(this)
                            .load(APIConstants.WEB_URL + output.data.results.profil_image_path)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .skipMemoryCache(true)
                            .placeholder(MediaUtils.image_placeholder_black)
                            .error(MediaUtils.image_error_black)
                            .into(profilePhoto);

                } else {
                    setLoading(false, false);
                    Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                setLoading(false, false);
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            setLoading(false, false);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            setResult(RequestCodeList.forceLogout);
            finish();
        } else {
            setLoading(false, false);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishSettingSubmit(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SettingSubmitAPI output = gson.fromJson(responseAPI.status_response, SettingSubmitAPI.class);
            if (output != null) {
                if (output.data.status.code.equals("200")) {
                    /*Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(IntentNames.SETTING_FINISHED, IntentNames.SETTING_FINISHED);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();*/
                    setLoading(false, true);
                    Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
                } else {
                    setLoading(false, true);
                    Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                setLoading(false, true);
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }

        } else if (responseAPI.status_code == 504) {
            setLoading(false, true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            setResult(RequestCodeList.forceLogout);
            finish();
        } else {
            setLoading(false, true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
