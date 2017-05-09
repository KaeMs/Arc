package com.med.fast.management.labresult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.UtilityUriHelper;
import com.med.fast.UtilsRealPath;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.api.AllergyManagementEditSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementEditShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementEditShowAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementEditSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementEditSubmitAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementEditIntf;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class LabResultEditActivity extends FastBaseActivity implements LabResultManagementEditIntf {
    @BindView(R.id.labresult_popup_test_type)
    CustomFontEditText testType;
    @BindView(R.id.labresult_popup_test_location)
    CustomFontEditText testLocation;
    @BindView(R.id.labresult_popup_test_description)
    CustomFontEditText testDescription;
    @BindView(R.id.labresult_popup_image_recycler)
    RecyclerView imgRecycler;
    LabResultImageAdapter labResultImageAdapter;
    @BindView(R.id.labresult_popup_test_finished_date)
    CustomFontTextView testFinishedDate;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private LabResultManagementModel labResultManagementModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_labresult_popup);

        backBtn.setText(getString(R.string.cancel));
        createBtn.setText(getString(R.string.confirm));

        try {
            Gson gson = new Gson();
            labResultManagementModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA),
                    LabResultManagementModel.class);
        } catch (NullPointerException npe){
            finish();
        }

        refreshView();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imgRecycler.setLayoutManager(linearLayoutManager);
        imgRecycler.setAdapter(labResultImageAdapter);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(this);
        mAwesomeValidation.addValidation(testType, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_type_question_required));
        mAwesomeValidation.addValidation(testLocation, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_location_question_required));
        mAwesomeValidation.addValidation(testDescription, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_description_question_required));

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
                if (!testFinishedDate.equals("")){
                    if (mAwesomeValidation.validate()) {
                        String testTypeString = testType.getText().toString();
                        String testLocationString = testLocation.getText().toString();
                        String descriptionString = testDescription.getText().toString();
                        String finishedDateString = testFinishedDate.getText().toString();

                        labResultManagementModel.setTest_type(testTypeString);
                        labResultManagementModel.setTest_location(testLocationString);
                        labResultManagementModel.setTest_description(descriptionString);
                        labResultManagementModel.setTest_date(finishedDateString);
                        labResultManagementModel.setProgress_status("0");

                        LabResultManagementEditSubmitAPI labResultManagementEditSubmitAPI = new LabResultManagementEditSubmitAPI();
                        labResultManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(LabResultEditActivity.this);
                        labResultManagementEditSubmitAPI.data.query.lab_result_id = labResultManagementModel.getTest_id();
                        labResultManagementEditSubmitAPI.data.query.test_name = testTypeString;
                        labResultManagementEditSubmitAPI.data.query.place = testLocationString;
                        labResultManagementEditSubmitAPI.data.query.desc_result = descriptionString;
                        labResultManagementEditSubmitAPI.data.query.date = finishedDateString;

                        LabResultManagementEditSubmitAPIFunc labResultManagementEditSubmitAPIFunc = new LabResultManagementEditSubmitAPIFunc(LabResultEditActivity.this);
                        labResultManagementEditSubmitAPIFunc.setDelegate(LabResultEditActivity.this);
                        labResultManagementEditSubmitAPIFunc.execute(labResultManagementEditSubmitAPI);
                    }
                } else {
                    Toast.makeText(LabResultEditActivity.this, getString(R.string.lab_test_date_question_required), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshView() {
        LabResultManagementEditShowAPI labResultManagementEditShowAPI = new LabResultManagementEditShowAPI();
        labResultManagementEditShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(this);
        labResultManagementEditShowAPI.data.query.lab_result_id = labResultManagementModel.getTest_id();

        LabResultManagementEditShowAPIFunc labResultManagementEditShowAPIFunc = new LabResultManagementEditShowAPIFunc(this);
        labResultManagementEditShowAPIFunc.setDelegate(this);
        labResultManagementEditShowAPIFunc.execute(labResultManagementEditShowAPI);
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
                labResultImageItem.setImage_id(String.valueOf(labResultImageAdapter.getItemCount()));
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mDestinationUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAdapter.updatemDataset(labResultImageItem);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UtilityUriHelper.getPath(this, data.getData());
                Uri mediaUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                LabResultImageItem labResultImageItem = new LabResultImageItem();
                labResultImageItem.setImage_id(String.valueOf(labResultImageAdapter.getItemCount()));
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mediaUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAdapter.updatemDataset(labResultImageItem);
            }
        }
    }

    @Override
    public void onFinishLabResultManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                testType.setText(output.data.results.lab_result.getTest_type());
                testLocation.setText(output.data.results.lab_result.getTest_location());
                testFinishedDate.setText(output.data.results.lab_result.getTest_date());
                testDescription.setText(output.data.results.lab_result.getTest_description());
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

    @Override
    public void onFinishLabResultManagementEditSubmit(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                String allergyModelString = gson.toJson(labResultManagementModel);
                intent.putExtra(ConstantsManagement.ALLERGY_MODEL_EXTRA, allergyModelString);
                setResult(RESULT_OK, intent);
                finish();
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
}
