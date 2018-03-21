package com.med.fast.management.accidenthistory;

import com.med.fast.Utils;

import io.realm.RealmObject;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class AccidentHistoryManagementModel extends RealmObject {
    private String detail;
    private String id;
    private String injury_nature;
    private String injury_location;
    private String injury_date;
    private String injury_date_tmp;
    private String injury_date_custom;
    private String created_date;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInjury_nature() {
        return injury_nature;
    }

    public void setInjury_nature(String injury_nature) {
        this.injury_nature = injury_nature;
    }

    public String getInjury_location() {
        return injury_location;
    }

    public void setInjury_location(String injury_location) {
        this.injury_location = injury_location;
    }

    public String getInjury_date() {
        return injury_date;
    }

    public void setInjury_date(String injury_date) {
        this.injury_date = injury_date;
    }

    public String getInjury_date_tmp() {
        return injury_date_tmp;
    }

    public void setInjury_date_tmp(String injury_date_tmp) {
        this.injury_date_tmp = injury_date_tmp;
    }

    public String getInjury_date_custom() {
        return injury_date_custom;
    }

    public void setInjury_date_custom(String injury_date_custom) {
        this.injury_date_custom = injury_date_custom;
    }

    public String getProgress_status() {
        return progress_status;
    }

    public void setProgress_status(String progress_status) {
        this.progress_status = progress_status;
    }

    public String getTag() {
        return Utils.processStringFromAPI(tag);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
