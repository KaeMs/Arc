package com.med.fast.management.disease;

import com.med.fast.Utils;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class DiseaseManagementModel {
    private String id;
    private String name;
    private String is_hereditary;
    private String hereditary_carriers;
    private String last_visit;
    private String is_ongoing;
    private String created_date;
    private String historic_date;
    private String approximate_date;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Add Failed
    private String tag;

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

    public String getIs_hereditary() {
        return is_hereditary;
    }

    public String getIs_hereditary_display() {
        return is_hereditary.toLowerCase().equals("true") ||
                is_hereditary.toLowerCase().equals("yes") ? "yes" : "no";
    }

    public void setIs_hereditary(String is_hereditary) {
        this.is_hereditary = is_hereditary;
    }

    public String getHereditary_carriers() {
        return hereditary_carriers;
    }

    public String getHereditary_carriers_display() {
        return hereditary_carriers.equals("default") ? "-" : hereditary_carriers;
    }

    public void setHereditary_carriers(String hereditary_carriers) {
        this.hereditary_carriers = hereditary_carriers;
    }

    public String getIs_ongoing() {
        return is_ongoing;
    }

    public String getIs_ongoing_display() {
        return is_ongoing.equals("true") ||
                is_ongoing.toLowerCase().equals("yes") ? "yes" : "no";
    }

    public void setIs_ongoing(String is_ongoing) {
        this.is_ongoing = is_ongoing;
    }

    public String getLast_visit() {
        return last_visit;
    }

    public void setLast_visit(String last_visit) {
        this.last_visit = last_visit;
    }

    public String getHistoric_date() {
        return historic_date;
    }

    public String getHistoric_date_display() {
        return Utils.processStringFromAPI(historic_date);
    }

    public void setHistoric_date(String historic_date) {
        this.historic_date = historic_date;
    }

    public String getApproximate_date() {
        return approximate_date;
    }

    public String getApproximate_date_display() {
        return approximate_date.equals("default") ? "-" : approximate_date;
    }

    public void setApproximate_date(String approximate_date) {
        this.approximate_date = approximate_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
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
