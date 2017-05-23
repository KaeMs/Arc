package com.med.fast.management.labresult;

import android.net.Uri;

/**
 * Created by Kevin Murvie on 5/3/2017. FM
 */

public class LabResultImgModel {
    private String id;
    private String name;
    private String path;
    private String date_taken;
    private boolean is_deleted;
    private Uri image_uri;
    private LabResultImgUploadModel labResultImgUploadModel = new LabResultImgUploadModel();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public boolean is_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Uri getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(Uri image_uri) {
        this.image_uri = image_uri;
    }

    public LabResultImgUploadModel getLabResultImgUploadModel() {
        return labResultImgUploadModel;
    }

    public void setLabResultImgUploadModel(LabResultImgUploadModel labResultImgUploadModel) {
        this.labResultImgUploadModel = labResultImgUploadModel;
    }
}
