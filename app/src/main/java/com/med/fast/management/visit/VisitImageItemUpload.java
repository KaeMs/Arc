package com.med.fast.management.visit;

import android.net.Uri;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageItemUpload {
    private int id;
    private String path;
    private boolean is_deleted;

    public VisitImageItemUpload(VisitImageItem item){
        this.id = item.getId();
        this.path = item.getPath();
        this.is_deleted = item.isIs_deleted();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
