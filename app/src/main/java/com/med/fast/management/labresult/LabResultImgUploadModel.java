package com.med.fast.management.labresult;

/**
 * Created by Kevin Murvie on 5/3/2017. FM
 */

public class LabResultImgUploadModel {
    private String id;
    private String path;
    private boolean is_deleted;

    public LabResultImgUploadModel(LabResultImgModel item){
        this.id = item.getId();
        this.path = item.getPath();
        this.is_deleted = item.is_deleted();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean is_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
