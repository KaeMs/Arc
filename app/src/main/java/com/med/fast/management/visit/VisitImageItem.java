package com.med.fast.management.visit;

import android.net.Uri;

import io.realm.RealmObject;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageItem extends RealmObject {
    private String id;
    private String path;
    private boolean is_deleted;
    private String uri;

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

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public void setUri(String uriString) {
        this.uri = uriString;
    }
}
