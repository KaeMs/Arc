package com.med.fast.management.medicine;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementModel {
    private String medicine_id;
    private String medicine_name;
    private String medicine_form;
    private String medicine_administration_method;
    private String medicine_administration_dose;
    private String medicine_frequency;
    private String medicine_medication_reason;
    private String medicine_medication_status;
    private String medicine_additional_instruction;
    private String medicine_created_date;
    private String progress_status; // 0 = Normal, 1 = Add progress, 2 = Delete progress
    private String tag;

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicine_form() {
        return medicine_form;
    }

    public void setMedicine_form(String medicine_form) {
        this.medicine_form = medicine_form;
    }

    public String getMedicine_administration_method() {
        return medicine_administration_method;
    }

    public void setMedicine_administration_method(String medicine_administration_method) {
        this.medicine_administration_method = medicine_administration_method;
    }

    public String getMedicine_administration_dose() {
        return medicine_administration_dose;
    }

    public void setMedicine_administration_dose(String medicine_administration_dose) {
        this.medicine_administration_dose = medicine_administration_dose;
    }

    public String getMedicine_frequency() {
        return medicine_frequency;
    }

    public void setMedicine_frequency(String medicine_frequency) {
        this.medicine_frequency = medicine_frequency;
    }

    public String getMedicine_medication_reason() {
        return medicine_medication_reason;
    }

    public void setMedicine_medication_reason(String medicine_medication_reason) {
        this.medicine_medication_reason = medicine_medication_reason;
    }

    public String getMedicine_medication_status() {
        return medicine_medication_status;
    }

    public void setMedicine_medication_status(String medicine_medication_status) {
        this.medicine_medication_status = medicine_medication_status;
    }

    public String getMedicine_additional_instruction() {
        return medicine_additional_instruction;
    }

    public void setMedicine_additional_instruction(String medicine_additional_instruction) {
        this.medicine_additional_instruction = medicine_additional_instruction;
    }

    public String getMedicine_created_date() {
        return medicine_created_date;
    }

    public void setMedicine_created_date(String medicine_created_date) {
        this.medicine_created_date = medicine_created_date;
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
