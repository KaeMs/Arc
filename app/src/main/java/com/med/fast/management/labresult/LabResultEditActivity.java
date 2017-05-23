package com.med.fast.management.labresult;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.med.fast.UriUtils;
import com.med.fast.api.APIConstants;
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
    LabResultImageAddAdapter labResultImageAddAdapter;
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
        setContentView(R.layout.management_labresult_add_activity);

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
        imgRecycler.setAdapter(labResultImageAddAdapter);

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

                        labResultManagementModel.setTest_name(testTypeString);
                        labResultManagementModel.setPlace(testLocationString);
                        labResultManagementModel.setDesc_result(descriptionString);
                        labResultManagementModel.setDate(finishedDateString);
                        labResultManagementModel.setProgress_status(APIConstants.PROGRESS_NORMAL);

                        LabResultManagementEditSubmitAPI labResultManagementEditSubmitAPI = new LabResultManagementEditSubmitAPI();
                        labResultManagementEditSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(LabResultEditActivity.this);
                        labResultManagementEditSubmitAPI.data.query.lab_result_id = labResultManagementModel.getId();
                        labResultManagementEditSubmitAPI.data.query.test_name = testTypeString;
                        labResultManagementEditSubmitAPI.data.query.place = testLocationString;
                        labResultManagementEditSubmitAPI.data.query.desc_result = descriptionString;
                        labResultManagementEditSubmitAPI.data.query.date = finishedDateString;
                        labResultManagementEditSubmitAPI.data.query.img_obj_json = labResultImageAddAdapter.getGson();
                        labResultManagementEditSubmitAPI.data.query.added_img_obj_json = labResultImageAddAdapter.getAddedImgGson();

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
        labResultManagementEditShowAPI.data.query.lab_result_id = labResultManagementModel.getId();

        LabResultManagementEditShowAPIFunc labResultManagementEditShowAPIFunc = new LabResultManagementEditShowAPIFunc(this);
        labResultManagementEditShowAPIFunc.setDelegate(this);
        labResultManagementEditShowAPIFunc.execute(labResultManagementEditShowAPI);
    }

    private Uri mDestinationUri;
    private String currentMediaPath;
    private CreatedImageModel createdImageModel;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.CAMERA) {
            if (resultCode == RESULT_OK) {
                mDestinationUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                LabResultImageItem labResultImageItem = new LabResultImageItem();
                labResultImageItem.setImage_id(String.valueOf(labResultImageAddAdapter.getItemCount()));
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mDestinationUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAddAdapter.updatemDataset(labResultImageItem);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UriUtils.getPath(this, data.getData());
                Uri mediaUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                LabResultImageItem labResultImageItem = new LabResultImageItem();
                labResultImageItem.setImage_id(String.valueOf(labResultImageAddAdapter.getItemCount()));
                labResultImageItem.setImage_path(currentMediaPath);
                labResultImageItem.setImage_uri(mediaUri);
                labResultImageItem.setImage_is_deleted(false);
                labResultImageAddAdapter.updatemDataset(labResultImageItem);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodeList.PHOTO_OPERATIONS){
            for (int i = 0; i < permissions.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            createImagePickerDialog(this, createdImageModel.image, getString(R.string.select_image_source));
        }
    }

    @Override
    public void onFinishLabResultManagementEditShow(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                testType.setText(output.data.results.test_name);
                testLocation.setText(output.data.results.place);
                testFinishedDate.setText(output.data.results.date);
                testDescription.setText(output.data.results.desc);
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
                intent.putExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA, allergyModelString);
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
