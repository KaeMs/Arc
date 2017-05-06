package com.med.fast.management.labresult;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.UtilsRealPath;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementCreateIntf;

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

    private String userId;

    @BindView(R.id.labresult_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.labresult_popup_test_type)
    CustomFontEditText testType;
    @BindView(R.id.labresult_popup_test_location)
    CustomFontEditText testLocation;
    @BindView(R.id.labresult_popup_test_description)
    CustomFontEditText testDescription;
    @BindView(R.id.labresult_popup_test_finished_date)
    CustomFontEditText testFinishedDate;

    @BindView(R.id.labresult_popup_image_recycler)
    RecyclerView imageRecycler;
    LabResultImageAdapter labResultImageAdapter;

    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private LabResultManagementModel labResultManagementModel;

    int day, month, year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_labresult_popup);

        userId = SharedPreferenceUtilities.getUserId(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        labResultImageAdapter = new LabResultImageAdapter(this);
        labResultImageAdapter.setWidth(displayMetrics.widthPixels);
        labResultImageAdapter.addSingle(null);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        imageRecycler.setLayoutManager(linearLayoutManager);
        imageRecycler.setAdapter(labResultImageAdapter);

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
        mAwesomeValidation.addValidation(testFinishedDate, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_date_question_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    String testTypeString = testType.getText().toString();
                    String testLocationString = testLocation.getText().toString();
                    String testDescriptionString = testDescription.getText().toString();
                    String testFinishedDateString = testFinishedDate.getText().toString();

                    labResultManagementModel = new LabResultManagementModel();
                    labResultManagementModel.setTest_type(testTypeString);
                    labResultManagementModel.setTest_location(testLocationString);
                    labResultManagementModel.setTest_description(testDescriptionString);
                    labResultManagementModel.setTest_date(testFinishedDateString);
                    labResultManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);
                    labResultManagementModel.setTag(testTypeString + testFinishedDateString);

                    LabResultManagementCreateSubmitAPI labResultManagementCreateSubmitAPI = new LabResultManagementCreateSubmitAPI();
                    labResultManagementCreateSubmitAPI.data.query.user_id = userId;
                    labResultManagementCreateSubmitAPI.data.query.test_name = testTypeString;
                    labResultManagementCreateSubmitAPI.data.query.desc_result = testDescriptionString;
                    labResultManagementCreateSubmitAPI.data.query.place = testLocationString;
                    labResultManagementCreateSubmitAPI.data.query.date = testFinishedDateString;
//                    labResultManagementCreateSubmitAPI.data.query.img_object = testFinishedDateString;

                    LabResultManagementCreateSubmitAPIFunc labResultManagementCreateSubmitAPIFunc = new
                            LabResultManagementCreateSubmitAPIFunc(LabResultAddActivity.this, LabResultAddActivity.this, testTypeString + testFinishedDateString);
                    labResultManagementCreateSubmitAPIFunc.execute(labResultManagementCreateSubmitAPI);

                }
            }
        });
    }

    private Uri mDestinationUri;
    private String currentMediaPath;

    public void addNewImage() {
        try {
            CreatedImageModel createdImageModel = createImageFile();
            File output = createdImageModel.image;
            currentMediaPath = createdImageModel.currentMediaPath;
            mDestinationUri = createdImageModel.mDestinationUri;
            createImagePickerDialog(this, output, getString(R.string.select_image_source));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.CAMERA) {
            if (resultCode == RESULT_OK) {
                mDestinationUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                LabResultImageItem labResultImageItem = new LabResultImageItem();
                labResultImageItem.setImage_id(labResultImageAdapter.getItemCount());
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mDestinationUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAdapter.updateImage(labResultImageItem);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UtilsRealPath.getRealPathFromURI(this, data.getData());
                LabResultImageItem labResultImageItem = new LabResultImageItem();
                labResultImageItem.setImage_id(labResultImageAdapter.getItemCount());
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mDestinationUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAdapter.updateImage(labResultImageItem);
            }
        }
    }

    @Override
    public void onFinishLabResultManagementCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                intent.putExtra(ConstantsManagement.LABRESULT_ID_EXTRA, output.data.results.new_lab_result_id);
                labResultManagementModel.setTest_id(output.data.results.new_lab_result_id);
                labResultManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);
                intent.putExtra(ConstantsManagement.VISIT_MODEL_EXTRA, gson.toJson(labResultManagementModel));
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
