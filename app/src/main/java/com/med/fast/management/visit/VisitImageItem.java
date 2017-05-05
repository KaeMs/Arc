package com.med.fast.management.visit;

import android.net.Uri;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageItem {
    private int image_id;
    private String image_path;
    private boolean image_is_deleted;
    private Uri image_uri;

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public boolean isImage_is_deleted() {
        return image_is_deleted;
    }

    public void setImage_is_deleted(boolean image_is_deleted) {
        this.image_is_deleted = image_is_deleted;
    }

    public Uri getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(Uri image_uri) {
        this.image_uri = image_uri;
    }
}
