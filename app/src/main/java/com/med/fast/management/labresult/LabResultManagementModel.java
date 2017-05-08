package com.med.fast.management.labresult;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class LabResultManagementModel {
    private String test_id;
    private String test_type;
    private String test_location;
    private String test_description;
    private String test_date;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getTest_type() {
        return test_type;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public String getTest_location() {
        return test_location;
    }

    public void setTest_location(String test_location) {
        this.test_location = test_location;
    }

    public String getTest_description() {
        return test_description;
    }

    public void setTest_description(String test_description) {
        this.test_description = test_description;
    }

    public String getTest_date() {
        return test_date;
    }

    public void setTest_date(String test_date) {
        this.test_date = test_date;
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
