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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.visit.api.VisitManagementCreateShowAPI;
import com.med.fast.management.visit.api.VisitManagementCreateShowAPIFunc;
import com.med.fast.management.visit.api.VisitManagementCreateSubmitAPI;
import com.med.fast.management.visit.api.VisitManagementCreateSubmitAPIFunc;
import com.med.fast.management.visit.visitinterface.VisitCreateIntf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 5/5/2017. FM
 */

public class VisitAddActivity extends FastBaseActivity implements VisitCreateIntf {

    private String userId;

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
    @BindView(R.id.visit_popup_disease_history_recycler_wrapper)
    LinearLayout listViewsWrapper;
    @BindView(R.id.management_operations_back_btn)
    CustomFontButton backBtn;
    @BindView(R.id.management_operations_create_btn)
    CustomFontButton createBtn;

    private VisitImageAddAdapter visitImageAddAdapter;
    private ArrayAdapter<VisitDiseaseModel> diseasesLVAdapter;
    private List<String> leftDataset;
    private ArrayAdapter<VisitDiseaseModel> selectedLVAdapter;
    private List<String> rightDataset;
    private VisitModel visitModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_visit_popup);

        userId = SharedPreferenceUtilities.getUserId(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        visitImageAddAdapter = new VisitImageAddAdapter(this,displayMetrics.widthPixels);
        visitImageAddAdapter.addSingle(null);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecycler.setLayoutManager(linearLayoutManager);
        imageRecycler.setAdapter(visitImageAddAdapter);
        this.diseasesLVAdapter = new ArrayAdapter<>(this, R.layout.layout_textview, R.id.textview_tv);
        leftDataset = new ArrayList<>();
        this.selectedLVAdapter = new ArrayAdapter<>(this, R.layout.layout_textview, R.id.textview_tv);
        rightDataset = new ArrayList<>();

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
                String tempHistoryStr = leftDataset.get(position);
                rightDataset.add(tempHistoryStr);
                selectedLVAdapter.notifyDataSetChanged();
                leftDataset.remove(position);
                diseasesLVAdapter.notifyDataSetChanged();
            }
        });

        diseaseInputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempDiseaseStr = rightDataset.get(position);
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

                    visitModel = new VisitModel();
                    visitModel.setVisit_id("");
                    visitModel.setOwner_id(userId);
                    visitModel.setCreated_date(currentDate);
                    visitModel.setHospital_name(hospitalNameString);
                    visitModel.setDoctor_name(doctorNameString);
                    visitModel.setDiagnose(diagnoseString);
                    List<VisitDiseaseModel> diseases = new ArrayList<>();
                    for (int i = 0; i < selectedLVAdapter.getCount(); i++) {
                        diseases.add(selectedLVAdapter.getItem(i));
                    }
                    visitModel.setDiseases(diseases);
                    visitModel.setImage_list(visitImageAddAdapter.getmDataset());

                    visitModel.setProgress_status("1");
                    visitModel.setTag(doctorNameString + currentDate);

                    VisitManagementCreateSubmitAPI visitManagementCreateSubmitAPI = new VisitManagementCreateSubmitAPI();
                    visitManagementCreateSubmitAPI.data.query.user_id = userId;
                    visitManagementCreateSubmitAPI.data.query.doctor = doctorNameString;
                    visitManagementCreateSubmitAPI.data.query.hospital = hospitalNameString;
                    visitManagementCreateSubmitAPI.data.query.diagnose = diagnoseString;
                    visitManagementCreateSubmitAPI.data.query.disease_id_list = userId;
                    visitManagementCreateSubmitAPI.data.query.tag = doctorNameString + currentDate;

                    visitManagementCreateSubmitAPI.data.query.is_image_uploaded = visitImageAddAdapter.getImageCount() > 0 ? "true" : "false";
                    visitManagementCreateSubmitAPI.data.query.image_list.addAll(visitImageAddAdapter.getUploadFile());

                    VisitManagementCreateSubmitAPIFunc visitManagementCreateSubmitAPIFunc =
                            new VisitManagementCreateSubmitAPIFunc(VisitAddActivity.this, VisitAddActivity.this, visitModel.getTag());
                    visitManagementCreateSubmitAPIFunc.execute(visitManagementCreateSubmitAPI);
                }
            }
        });
    }

    void refreshView() {
        VisitManagementCreateShowAPI visitManagementCreateShowAPI = new VisitManagementCreateShowAPI();
        visitManagementCreateShowAPI.data.query.user_id = userId;

        VisitManagementCreateShowAPIFunc visitManagementCreateShowAPIFunc = new VisitManagementCreateShowAPIFunc(this, this);
        visitManagementCreateShowAPIFunc.execute(visitManagementCreateShowAPI);
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
                VisitImageItem visitImageItem = new VisitImageItem();
                visitImageItem.setId(String.valueOf(visitImageAddAdapter.getItemCount()));
                visitImageItem.setPath(currentMediaPath);
                visitImageItem.setUri(mDestinationUri);
                visitImageItem.setIs_deleted(false);
                visitImageAddAdapter.updatemDataset(visitImageItem);
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == RESULT_OK) {
                currentMediaPath = UriUtils.getPath(this, data.getData());
                Uri mediaUri = MediaUtils.compressImage(this, Uri.parse(currentMediaPath));
                VisitImageItem visitImageItem = new VisitImageItem();
                visitImageItem.setId(String.valueOf(visitImageAddAdapter.getItemCount()));
                visitImageItem.setPath(currentMediaPath);
                visitImageItem.setUri(mediaUri);
                visitImageItem.setIs_deleted(false);
                visitImageAddAdapter.updatemDataset(visitImageItem);
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
    public void onFinishVisitCreateShow(ResponseAPI responseAPI) {
        swipeRefreshLayout.setRefreshing(false);
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementCreateShowAPI output = gson.fromJson(responseAPI.status_response, VisitManagementCreateShowAPI.class);
            if (output.data.status.code.equals("200")) {
                if (output.data.results.disease_list.size() > 0){
                    listViewsWrapper.setVisibility(View.VISIBLE);
                    diseasesLVAdapter.addAll(output.data.results.disease_list);
                } else {
                    listViewsWrapper.setVisibility(View.GONE);
                }
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

    @Override
    public void onFinishVisitCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent();
                intent.putExtra(ConstantsManagement.VISIT_ID_EXTRA, output.data.results.new_visit_id);
                visitModel.setVisit_id(output.data.results.new_visit_id);
                visitModel.setProgress_status(APIConstants.PROGRESS_NORMAL);
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
