package com.med.fast.management.labresult;

/**
 * Created by Kevin Murvie on 5/3/2017. FM
 */

public class LabResultImgModel {
    private String id;
    private String name;
    private String path;
    private String date_taken;
    private boolean is_deleted;

    public LabResultImgModel(LabResultImageItem labResultImageItem){
        this.id = labResultImageItem.getImage_id();
        this.name = labResultImageItem.getImage_id();
        this.path = labResultImageItem.getImage_path();
        this.date_taken = labResultImageItem.getDate_taken();
        this.is_deleted = labResultImageItem.isImage_is_deleted();
    }

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
}
