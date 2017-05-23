package com.med.fast.management.labresult;

import android.net.Uri;

/**
 * Created by Kevin Murvie on 5/09/2017. FM
 */

public class LabResultImgUploadModel {
    private String id = "";
    private String path = "";
    private String date_taken = "";
    private boolean is_deleted = false;

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
}
