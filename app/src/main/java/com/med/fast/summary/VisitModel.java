package com.med.fast.summary;

import com.med.fast.management.visit.VisitImageItem;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 25-Apr-17. FM
 */

public class VisitModel {
    private String visit_id = "";
    private String owner_id = "";
    private String created_date = "";
    private String hospital_name = "";
    private String doctor_name = "";
    private String diagnose = "";
    private String disease = "";
    private List<VisitImageItem> image_list;

    public String getVisit_id() {
        if (visit_id == null){
            return "-";
        }
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public String getOwner_id() {
        if (owner_id == null){
            return "-";
        }
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCreated_date() {
        if (created_date == null){
            return "-";
        }
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getHospital_name() {
        if (hospital_name == null){
            return "-";
        }
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getDoctor_name() {
        if (doctor_name == null){
            return "-";
        }
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDiagnose() {
        if (diagnose == null){
            return "-";
        }
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getDisease() {
        if (disease == null){
            return "-";
        }
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public List<VisitImageItem> getImage_list() {
        if (image_list == null){
            return new ArrayList<>();
        }
        return image_list;
    }

    public void setImage_list(List<VisitImageItem> image_list) {
        this.image_list = image_list;
    }
}
