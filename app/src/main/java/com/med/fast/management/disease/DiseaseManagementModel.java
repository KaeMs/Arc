package com.med.fast.management.disease;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class DiseaseManagementModel {
    private String disease_name;
    private String disease_hereditary;
    private String disease_hereditary_carriers;
    private String disease_ongoing;
    private String date_last_visit;
    private String date_historic;
    private String date_approximate;
    private String date_created;
    private String progress_status; // 0 = Normal, 1 = Add progress, 2 = Delete progress

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public String getDisease_hereditary() {
        return disease_hereditary;
    }

    public void setDisease_hereditary(String disease_hereditary) {
        this.disease_hereditary = disease_hereditary;
    }

    public String getDisease_hereditary_carriers() {
        return disease_hereditary_carriers;
    }

    public void setDisease_hereditary_carriers(String disease_hereditary_carriers) {
        this.disease_hereditary_carriers = disease_hereditary_carriers;
    }

    public String getDisease_ongoing() {
        return disease_ongoing;
    }

    public void setDisease_ongoing(String disease_ongoing) {
        this.disease_ongoing = disease_ongoing;
    }

    public String getDate_last_visit() {
        return date_last_visit;
    }

    public void setDate_last_visit(String date_last_visit) {
        this.date_last_visit = date_last_visit;
    }

    public String getDate_historic() {
        return date_historic;
    }

    public void setDate_historic(String date_historic) {
        this.date_historic = date_historic;
    }

    public String getDate_approximate() {
        return date_approximate;
    }

    public void setDate_approximate(String date_approximate) {
        this.date_approximate = date_approximate;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getProgress_status() {
        return progress_status;
    }

    public void setProgress_status(String progress_status) {
        this.progress_status = progress_status;
    }
}
