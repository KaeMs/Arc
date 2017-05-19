package com.med.fast.management.medicine;

import com.med.fast.Utils;
import com.med.fast.api.APIConstants;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementModel {
    private String id;
    private String name;
    private String form;
    private String administration_method;
    private String administration_dose;
    private String frequency;
    private String medication_reason;
    private String medication_status;
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

    public String getAdministration_method() {
        return administration_method;
    }

    public String getAdministration_method_display() {
        return Utils.formatAPIDefault(administration_method);
    }

    public void setAdministration_method(String administration_method) {
        this.administration_method = administration_method;
    }

    public String getAdministration_dose() {
        return administration_dose;
    }

    public String getAdministration_dose_display() {
        return Utils.formatAPIDefault(administration_dose);
    }

    public void setAdministration_dose(String administration_dose) {
        this.administration_dose = administration_dose;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFrequency_display() {
        return Utils.formatAPIDefault(frequency);
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getMedication_reason() {
        return medication_reason;
    }

    public void setMedication_reason(String medication_reason) {
        this.medication_reason = medication_reason;
    }

    public String getMedication_status() {
        return medication_status;
    }

    public void setMedication_status(String medication_status) {
        this.medication_status = medication_status;
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
