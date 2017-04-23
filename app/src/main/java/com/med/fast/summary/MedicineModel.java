package com.med.fast.summary;

/**
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class MedicineModel {
    public String medicine_id;
    public String medicine_name;
    public String medicine_form;
    public String medicine_route;
    public String medicine_dose;
    public String medicine_frequency;
    public String medicine_created_date;

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

    public String getMedicine_route() {
        return medicine_route;
    }

    public void setMedicine_route(String medicine_route) {
        this.medicine_route = medicine_route;
    }

    public String getMedicine_dose() {
        return medicine_dose;
    }

    public void setMedicine_dose(String medicine_dose) {
        this.medicine_dose = medicine_dose;
    }

    public String getMedicine_frequency() {
        return medicine_frequency;
    }

    public void setMedicine_frequency(String medicine_frequency) {
        this.medicine_frequency = medicine_frequency;
    }

    public String getMedicine_created_date() {
        return medicine_created_date;
    }

    public void setMedicine_created_date(String medicine_created_date) {
        this.medicine_created_date = medicine_created_date;
    }
}
