package com.med.fast.management.visit;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.med.fast.ConstantsManagement;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseActivity;
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
import com.med.fast.management.disease.DiseaseListAdapter;
import com.med.fast.management.visit.api.VisitManagementEditShowAPI;
import com.med.fast.management.visit.api.VisitManagementEditShowAPIFunc;
import com.med.fast.management.visit.api.VisitManagementEditSubmitAPI;
import com.med.fast.management.visit.api.VisitManagementEditSubmitAPIFunc;
import com.med.fast.management.visit.visitinterface.VisitEditIntf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 5/5/2017. FM
 */

public class VisitEditActivity extends FastBaseActivity implements VisitEditIntf {

    @BindView(R.id.visit_popup_doctor_name)
    CustomFontEditText doctorName;
    @BindView(R.id.visit_popup_hospital_name)
    CustomFontEditText hospitalName;
    @BindView(R.id.visit_popup_diagnose)
    CustomFontEditText diagnose;
    @BindView(R.id.visit_popup_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.visit_popup_imagerecycler)
    RecyclerView imageRecycler;
    @BindView(R.id.visit_popup_disease_history_recycler)
    ListView diseaseHistoryListView;
    @BindView(R.id.visit_popup_disease_input_recycler)
    ListView diseaseInputListView;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;
    private String userId;
    private VisitImageEditAdapter visitImageEditAdapter;
    private List<VisitDiseaseModel> leftDataset;
    private DiseaseListAdapter diseasesLVAdapter;
    private List<VisitDiseaseModel> rightDataset;
    private DiseaseListAdapter selectedLVAdapter;
    private VisitModel visitModel;
    private Uri mDestinationUri;
    private String currentMediaPath;
    private CreatedImageModel createdImageModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_visit_popup);

        userId = SharedPreferenceUtilities.getUserId(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        try {
            Gson gson = new Gson();
            visitModel = gson.fromJson(getIntent().getStringExtra(ConstantsManagement.VISIT_MODEL_EXTRA),
                    VisitModel.class);
        } catch (NullPointerException npe) {
            finish();
        }

        visitImageEditAdapter = new VisitImageEditAdapter(this);
        visitImageEditAdapter.setWidth(displayMetrics.widthPixels);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        imageRecycler.setLayoutManager(linearLayoutManager);
        imageRecycler.setAdapter(visitImageEditAdapter);
        leftDataset = new ArrayList<>();
        this.diseasesLVAdapter = new DiseaseListAdapter(this, R.layout.layout_textview, R.id.textview_tv, leftDataset);
        rightDataset = new ArrayList<>();
        this.selectedLVAdapter = new DiseaseListAdapter(this, R.layout.layout_textview, R.id.textview_tv, rightDataset);

        imageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
        refreshView();

        diseaseHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VisitDiseaseModel tempHistoryStr = leftDataset.get(position);
                rightDataset.add(tempHistoryStr);
                selectedLVAdapter.notifyDataSetChanged();
                leftDataset.remove(position);
                diseasesLVAdapter.notifyDataSetChanged();
            }
        });

        diseaseInputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VisitDiseaseModel tempDiseaseStr = rightDataset.get(position);
                leftDataset.add(tempDiseaseStr);
                diseasesLVAdapter.notifyDataSetChanged();
                rightDataset.remove(position);
                selectedLVAdapter.notifyDataSetChanged();
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
        mAwesomeValidation.addValidation(doctorName, RegexTemplate.NOT_EMPTY, getString(R.string.doctor_name_required));
        mAwesomeValidation.addValidation(hospitalName, RegexTemplate.NOT_EMPTY, getString(R.string.hospital_name_required));
        mAwesomeValidation.addValidation(diagnose, RegexTemplate.NOT_EMPTY, getString(R.string.diagnose_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    String doctorNameString = doctorName.getText().toString();
                    String hospitalNameString = hospitalName.getText().toString();
                    String diagnoseString = diagnose.getText().toString();
                    String currentDate = Utils.getCurrentDate();

//                    List<VisitImageItem> image_list;

                    visitModel.setOwner_id(userId);
                    visitModel.setHospital_name(hospitalNameString);
                    visitModel.setDoctor_name(doctorNameString);
                    visitModel.setDiagnose(diagnoseString);
                    List<VisitDiseaseModel> diseases = new ArrayList<>();
                    for (int i = 0; i < selectedLVAdapter.getCount(); i++) {
                        diseases.add(selectedLVAdapter.getItem(i));
                    }
                    visitModel.setDiseases(diseases);
                    visitModel.setProgress_status(APIConstants.PROGRESS_ADD);
                    visitModel.setTag(doctorNameString + currentDate);

                    VisitManagementEditSubmitAPI visitManagementCreateSubmitAPI = new VisitManagementEditSubmitAPI();
                    visitManagementCreateSubmitAPI.data.query.user_id = userId;
                    visitManagementCreateSubmitAPI.data.query.visit_id = visitModel.getId();
                    visitManagementCreateSubmitAPI.data.query.doctor = doctorNameString;
                    visitManagementCreateSubmitAPI.data.query.hospital = hospitalNameString;
                    visitManagementCreateSubmitAPI.data.query.diagnose = diagnoseString;
                    visitManagementCreateSubmitAPI.data.query.disease_id_list = visitModel.getDiseases_for_api();
                    visitManagementCreateSubmitAPI.data.query.image_json_str = visitImageEditAdapter.getGson();
                    visitManagementCreateSubmitAPI.data.query.is_image_uploaded = String.valueOf(visitImageEditAdapter.checkNewImage());
                    visitManagementCreateSubmitAPI.data.query.image_files = visitImageEditAdapter.getSortedFileUpload();

                    VisitManagementEditSubmitAPIFunc visitManagementCreateSubmitAPIFunc =
                            new VisitManagementEditSubmitAPIFunc(VisitEditActivity.this, VisitEditActivity.this);
                    visitManagementCreateSubmitAPIFunc.execute(visitManagementCreateSubmitAPI);
                }
            }
        });
    }

    void refreshView() {
        VisitManagementEditShowAPI visitManagementEditShowAPI = new VisitManagementEditShowAPI();
        visitManagementEditShowAPI.data.query.user_id = userId;
        visitManagementEditShowAPI.data.query.visit_id = visitModel.getId();

        VisitManagementEditShowAPIFunc visitManagementCreateShowAPIFunc = new VisitManagementEditShowAPIFunc(this, this);
        visitManagementCreateShowAPIFunc.execute(visitManagementEditShowAPI);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.CAMERA) {
            if (resultCode == RESULT_OK) {
                mDestinationUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                VisitImageItem visitImageItem = new VisitImageItem();
                visitImageItem.setId(String.valueOf(visitImageEditAdapter.getItemCount()));
                visitImageItem.setPath(currentMediaPath);
                visitImageItem.setUri(mDestinationUri);
                visitImageItem.setIs_deleted(false);
                visitImageEditAdapter.updateImage(visitImageItem);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UriUtils.getPath(this, data.getData());
                VisitImageItem visitImageItem = new VisitImageItem();
                visitImageItem.setId(String.valueOf(visitImageEditAdapter.getItemCount()));
                visitImageItem.setPath(currentMediaPath);
                visitImageItem.setUri(mDestinationUri);
                visitImageItem.setIs_deleted(false);
                visitImageEditAdapter.updateImage(visitImageItem);
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
    public void onFinishVisitEditShow(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementEditShowAPI output = gson.fromJson(responseAPI.status_response, VisitManagementEditShowAPI.class);
            if (output.data.status.code.equals("200")) {
                visitModel = output.data.results.visit;

                hospitalName.setText(visitModel.getHospital_name());
                doctorName.setText(visitModel.getDoctor_name());
                diagnose.setText(visitModel.getDiagnose());

                diseasesLVAdapter.addAll(visitModel.getDiseases());
                selectedLVAdapter.addAll(visitModel.getDiseases());
                visitImageEditAdapter.addList(visitModel.getImage_list());
                visitImageEditAdapter.addSingle(null);
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        } else if (responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onFinishVisitEditSubmit(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementEditSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementEditSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                visitModel.setProgress_status(APIConstants.PROGRESS_NORMAL);
                /*Gson gsonBuilder = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriUtils.UriSerializer())
                        .create();*/
                intent.putExtra(ConstantsManagement.VISIT_MODEL_EXTRA, gson.toJson(visitModel));
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
