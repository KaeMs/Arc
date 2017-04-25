package com.med.fast.management.surgery;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementModel {
    private String surgery_procedure;
    private String surgery_physician_name;
    private String surgery_hospital_name;
    private String surgery_date;
    private String surgery_note;

    public String getSurgery_procedure() {
        return surgery_procedure;
    }

    public void setSurgery_procedure(String surgery_procedure) {
        this.surgery_procedure = surgery_procedure;
    }

    public String getSurgery_physician_name() {
        return surgery_physician_name;
    }

    public void setSurgery_physician_name(String surgery_physician_name) {
        this.surgery_physician_name = surgery_physician_name;
    }

    public String getSurgery_hospital_name() {
        return surgery_hospital_name;
    }

    public void setSurgery_hospital_name(String surgery_hospital_name) {
        this.surgery_hospital_name = surgery_hospital_name;
    }

    public String getSurgery_date() {
        return surgery_date;
    }

    public void setSurgery_date(String surgery_date) {
        this.surgery_date = surgery_date;
    }

    public String getSurgery_note() {
        return surgery_note;
    }

    public void setSurgery_note(String surgery_note) {
        this.surgery_note = surgery_note;
    }
}
