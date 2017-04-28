package com.med.fast.management.accidenthistory;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class AccidentHistoryManagementModel {
    private String detail;
    private String accident_id;
    private String injury_nature;
    private String injury_location;
    private String injury_date;
    private String created_date;
    private String progress_status;

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

    public String getAccident_id() {
        return accident_id;
    }

    public void setAccident_id(String accident_id) {
        this.accident_id = accident_id;
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

    public String getProgress_status() {
        return progress_status;
    }

    public void setProgress_status(String progress_status) {
        this.progress_status = progress_status;
    }
}
