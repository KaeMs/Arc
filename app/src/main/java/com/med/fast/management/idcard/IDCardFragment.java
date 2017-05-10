package com.med.fast.management.idcard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.med.fast.CreatedImageModel;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.UtilityUriHelper;
import com.med.fast.UtilsRealPath;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.management.idcard.api.IDCardShowAPI;
import com.med.fast.management.idcard.api.IDCardShowAPIFunc;
import com.med.fast.management.idcard.api.IDCardSubmitAPI;
import com.med.fast.management.idcard.api.IDCardSubmitAPIFunc;
import com.med.fast.management.idcard.intf.IDCardShowSubmitIntf;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/8/2017. FM
 */

public class IDCardFragment extends FastBaseFragment implements IDCardShowSubmitIntf {
    @BindView(R.id.idcard_photo)
    ImageView photo;
    @BindView(R.id.idcard_save_btn)
    CustomFontButton saveBtn;
    @BindView(R.id.idcard_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.idcard_save_progress)
    ProgressBar saveProgress;

    private String userId;
    private boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_idcard_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).changeTitle("UPLOAD YOUR ID");
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        refreshView(true);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading){
                    if (!currentMediaPath.equals("")){
                        IDCardSubmitAPI idCardSubmitAPI = new IDCardSubmitAPI();
                        idCardSubmitAPI.data.query.user_id = userId;
                        idCardSubmitAPI.data.query.image = new File(createdPhotoUri.getPath());

                        IDCardSubmitAPIFunc idCardSubmitAPIFunc = new IDCardSubmitAPIFunc(getActivity(), IDCardFragment.this);
                        idCardSubmitAPIFunc.execute(idCardSubmitAPI);
                        isLoading = true;
                        saveBtn.setEnabled(false);
                        saveProgress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImage();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(false);
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
            createImagePickerDialog(getActivity(), output, getString(R.string.select_image_source));
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
                    createdPhotoUri = MediaUtils.compressImage(getActivity(), Uri.parse(currentMediaPath));
                    photo.setImageURI(createdPhotoUri);
                    saveBtn.setEnabled(true);
                } catch (Exception e){
                    Toast.makeText(getActivity(), getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                    new File(currentMediaPath).delete();
                }
            }
        } else if (requestCode == RequestCodeList.GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    currentMediaPath = UtilityUriHelper.getPath(getActivity(), data.getData());
                    createdPhotoUri = MediaUtils.compressImage(getActivity(), Uri.parse(currentMediaPath));
                    if (createdPhotoUri != null){
                        photo.setImageURI(data.getData());
                        saveBtn.setEnabled(true);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                        new File(currentMediaPath).delete();
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), getString(R.string.image_retrieval_failed), Toast.LENGTH_SHORT).show();
                    new File(currentMediaPath).delete();
                }
            }
        } else if (requestCode == RequestCodeList.PHOTO_OPERATIONS){
            if (resultCode == Activity.RESULT_OK){
                createImagePickerDialog(getActivity(), createdImageModel.image, getString(R.string.select_image_source));
            }
        }
    }

    @Override
    public void refreshView(boolean setRefreshing) {
        isLoading = true;
        IDCardShowAPI idCardShowAPI = new IDCardShowAPI();
        idCardShowAPI.data.query.user_id = userId;

        IDCardShowAPIFunc idCardShowAPIFunc = new IDCardShowAPIFunc(getActivity(), IDCardFragment.this);
        idCardShowAPIFunc.execute(idCardShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
    }

    @Override
    public void onFinishIDCardShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                IDCardShowAPI output = gson.fromJson(responseAPI.status_response, IDCardShowAPI.class);
                if (output.data.status.code.equals("200")) {

                    Glide.with(getActivity())
                            .load(APIConstants.WEB_URL + output.data.results.card_id_image_path)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true)
                            .placeholder(MediaUtils.image_placeholder_black)
                            .error(MediaUtils.image_error_black)
                            .into(photo);

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity) getActivity()).forceLogout();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFinishIDCardSubmit(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            isLoading = false;
            saveProgress.setVisibility(View.INVISIBLE);
            saveBtn.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                IDCardSubmitAPI output = gson.fromJson(responseAPI.status_response, IDCardSubmitAPI.class);
                if (output.data.status.code.equals("200")) {

                    Glide.with(getActivity())
                            .load(APIConstants.WEB_URL + output.data.results.card_id_new_image_path)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true)
                            .placeholder(MediaUtils.image_placeholder_black)
                            .error(MediaUtils.image_error_black)
                            .into(photo);

                    Toast.makeText(getActivity(), getString(R.string.idcard_uploaded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity) getActivity()).forceLogout();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
