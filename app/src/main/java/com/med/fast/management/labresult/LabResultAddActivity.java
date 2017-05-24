package com.med.fast.management.labresult;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
import com.med.fast.HorizontalItemDecoration;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.UriUtils;
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementCreateIntf;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 5/5/2017. FM
 */

public class LabResultAddActivity extends FastBaseActivity implements LabResultManagementCreateIntf {

    @BindView(R.id.labresult_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.labresult_popup_test_type)
    CustomFontEditText testType;
    @BindView(R.id.labresult_popup_test_location)
    CustomFontEditText testLocation;
    @BindView(R.id.labresult_popup_test_description)
    CustomFontEditText testDescription;
    @BindView(R.id.labresult_popup_test_finished_date)
    CustomFontTextView testFinishedDate;
    @BindView(R.id.labresult_popup_image_recycler)
    RecyclerView imageRecycler;
    LabResultImageAddAdapter labResultImageAddAdapter;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    int day, month, year;
    private String userId;
    private LabResultManagementModel labResultManagementModel;
    private Uri mDestinationUri;
    private String currentMediaPath;
    private CreatedImageModel createdImageModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_labresult_add_activity);

        userId = SharedPreferenceUtilities.getUserId(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        labResultImageAddAdapter = new LabResultImageAddAdapter(this);
        labResultImageAddAdapter.addSingle(null);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        HorizontalItemDecoration horizontalItemDecoration = new HorizontalItemDecoration(this);
        imageRecycler.addItemDecoration(horizontalItemDecoration);
        imageRecycler.setLayoutManager(linearLayoutManager);
        imageRecycler.setAdapter(labResultImageAddAdapter);

        swipeRefreshLayout.setEnabled(false);
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        testFinishedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(LabResultAddActivity.this, null, year, month, day);
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
                                testFinishedDate.setText(date);
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

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(testType, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_type_question_required));
        mAwesomeValidation.addValidation(testLocation, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_location_question_required));
        mAwesomeValidation.addValidation(testDescription, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_description_question_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (!testFinishedDate.getText().toString().equals("")) {
                    if (mAwesomeValidation.validate()) {
                        String testTypeString = testType.getText().toString();
                        String testLocationString = testLocation.getText().toString();
                        String testDescriptionString = testDescription.getText().toString();
                        String testFinishedDateString = testFinishedDate.getText().toString();

                        labResultManagementModel = new LabResultManagementModel();
                        labResultManagementModel.setTest_name(testTypeString);
                        labResultManagementModel.setPlace(testLocationString);
                        labResultManagementModel.setDesc_result(testDescriptionString);
                        labResultManagementModel.setDate(testFinishedDateString);
                        labResultManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);
                        labResultManagementModel.setTag(testTypeString + testFinishedDateString);

                        LabResultManagementCreateSubmitAPI labResultManagementCreateSubmitAPI = new LabResultManagementCreateSubmitAPI();
                        labResultManagementCreateSubmitAPI.data.query.user_id = userId;
                        labResultManagementCreateSubmitAPI.data.query.test_name = testTypeString;
                        labResultManagementCreateSubmitAPI.data.query.desc_result = testDescriptionString;
                        labResultManagementCreateSubmitAPI.data.query.place = testLocationString;
                        labResultManagementCreateSubmitAPI.data.query.date = testFinishedDateString;
                        labResultManagementCreateSubmitAPI.data.query.img_file_list = labResultImageAddAdapter.getUploadFile();
                        labResultManagementCreateSubmitAPI.data.query.img_obj_json = labResultImageAddAdapter.getGson();

//                    labResultManagementCreateSubmitAPI.data.query.img_list = testFinishedDateString;

                        LabResultManagementCreateSubmitAPIFunc labResultManagementCreateSubmitAPIFunc = new
                                LabResultManagementCreateSubmitAPIFunc(LabResultAddActivity.this, LabResultAddActivity.this, testTypeString + testFinishedDateString);
                        labResultManagementCreateSubmitAPIFunc.execute(labResultManagementCreateSubmitAPI);

                    }
                } else {
                    Toast.makeText(LabResultAddActivity.this, getString(R.string.lab_test_date_question_required), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addNewImage() {
        try {
            createdImageModel = createImageFile();
            File output = createdImageModel.image;
            currentMediaPath = createdImageModel.currentMediaPath;
            mDestinationUri = createdImageModel.mDestinationUri;
            createImagePickerDialog(this, output, getString(R.string.select_image_source));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImageAddedDialog(final Uri imageUri) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.management_labresult_imagedate_popup);
        dialog.setCanceledOnTouchOutside(false);

        ImageView photo = (ImageView) dialog.findViewById(R.id.labresult_imagedate_popup_selected_img);
        final CustomFontTextView dateTaken = (CustomFontTextView) dialog.findViewById(R.id.labresult_imagedate_popup_date_tv);
        CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
        CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

        photo.setImageURI(imageUri);

        dateTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.dateFormatComma)
                        .withLocale(Locale.getDefault());

                LocalDate localDate = new LocalDate();
                if (!dateTaken.getText().toString().equals("")) {
                    localDate = formatter.parseLocalDate(dateTaken.getText().toString());
                }

                final DatePickerDialog datePickerDialog = new DatePickerDialog(LabResultAddActivity.this, null,
                        localDate.getYear(),
                        localDate.getMonthOfYear() - 1,
                        localDate.getDayOfMonth());
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
//                datePickerDialog.getDatePicker().updateDate(year, month, day);
                datePickerDialog.show();
                datePickerDialog.setCanceledOnTouchOutside(true);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DateTimeFormatter datePickDialogFormatter = DateTimeFormat.forPattern("MM dd yyyy")
                                        .withLocale(Locale.getDefault());
                                LocalDate datePickDialogLocalDate = new LocalDate();
                                datePickDialogLocalDate = datePickDialogFormatter.parseLocalDate(
                                        String.valueOf(datePickerDialog.getDatePicker().getMonth() + 1) + " " +
                                                String.valueOf(datePickerDialog.getDatePicker().getDayOfMonth()) + " " +
                                                String.valueOf(datePickerDialog.getDatePicker().getYear()));
                                DateTimeFormatter datePickReformatter = DateTimeFormat.forPattern(Constants.dateFormatComma)
                                        .withLocale(Locale.getDefault());
                                dateTaken.setText(datePickReformatter.print(datePickDialogLocalDate));
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
                dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LabResultImgModel labResultImageItem = new LabResultImgModel();
                labResultImageItem.setId(String.valueOf(labResultImageAddAdapter.getItemCount()));
                labResultImageItem.setPath(currentMediaPath);
                labResultImageItem.setImage_uri(imageUri);
                labResultImageItem.setIs_deleted(false);
                LabResultImgUploadModel labResultImgUploadModel = new LabResultImgUploadModel();
                labResultImgUploadModel.setId(String.valueOf(labResultImageAddAdapter.getItemCount()) + userId);
                labResultImgUploadModel.setPath(currentMediaPath);
                labResultImgUploadModel.setDate_taken(Utils.processStringForAPI(dateTaken.getText().toString()));
                labResultImgUploadModel.setIs_deleted(false);
                labResultImageItem.setLabResultImgUploadModel(labResultImgUploadModel);
                labResultImageAddAdapter.updatemDataset(labResultImageItem);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.CAMERA) {
            if (resultCode == RESULT_OK) {
                mDestinationUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                showImageAddedDialog(mDestinationUri);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UriUtils.getPath(this, data.getData());
                Uri mediaUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                showImageAddedDialog(mediaUri);
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
    public void onFinishLabResultManagementCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
//                intent.putExtra(ConstantsManagement.LABRESULT_ID_EXTRA, output.data.results.new_lab_result_id);
                labResultManagementModel.setId(output.data.results.new_lab_result_id);
                labResultManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);
                intent.putExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA, gson.toJson(labResultManagementModel));
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
}
