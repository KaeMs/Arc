package com.med.fast.management.medicine;

import com.med.fast.Utils;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementModel {
    private String id;
    private String name;
    private String form;
    private String route;
    private String dose;
    private String frequency;
    private String reason;
    private String status;
    private String additional_instruction;
    private String created_date;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress
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

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getRoute() {
        return route;
    }

    public String getAdministration_method_display() {
        return Utils.processStringFromAPI(route);
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDose() {
        return dose;
    }

    public String getAdministration_dose_display() {
        return Utils.processStringFromAPI(dose);
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFrequency_display() {
        return Utils.processStringFromAPI(frequency);
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdditional_instruction() {
        return additional_instruction;
    }

    public void setAdditional_instruction(String additional_instruction) {
        this.additional_instruction = additional_instruction;
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
