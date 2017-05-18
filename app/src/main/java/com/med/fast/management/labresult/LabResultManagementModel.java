package com.med.fast.management.labresult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class LabResultManagementModel {
    private String id;
    private String user_id;
    private String test_name;
    private String desc_result;
    private String place;
    private String date;
    private List<LabResultImageItem> list_img_uploadeds = new ArrayList<>();
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public String getDesc_result() {
        return desc_result;
    }

    public void setDesc_result(String desc_result) {
        this.desc_result = desc_result;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<LabResultImageItem> getList_img_uploadeds() {
        return list_img_uploadeds;
    }

    public void setList_img_uploadeds(List<LabResultImageItem> list_img_uploadeds) {
        this.list_img_uploadeds = list_img_uploadeds;
    }

    public String getProgress_status() {
        return progress_status;
    }

    public void setProgress_status(String progress_status) {
        this.progress_status = progress_status;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
