package com.med.fast.visit;

import java.sql.Date;
import java.util.List;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitModel {
    public String visit_id;
    public String owner_id;
    public Date created_date;
    public String hospital_name;
    public String doctor_name;
    public String diagnose;
    public String disease;
    public List<VisitImageItem> image_list;

    public String getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public List<VisitImageItem> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<VisitImageItem> image_list) {
        this.image_list = image_list;
    }
}
