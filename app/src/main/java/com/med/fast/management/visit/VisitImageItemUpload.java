package com.med.fast.management.visit;

import android.net.Uri;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageItemUpload {
    private int image_id;
    private String image_name;
    private String image_path;
    private String image_description;
    private boolean image_is_main;
    private boolean image_is_deleted;
    private Uri image_uri;

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getImage_description() {
        return image_description;
    }

    public void setImage_description(String image_description) {
        this.image_description = image_description;
    }

    public boolean isImage_is_main() {
        return image_is_main;
    }

    public void setImage_is_main(boolean image_is_main) {
        this.image_is_main = image_is_main;
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
