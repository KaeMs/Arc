package com.med.fast.management.visit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitModel {
    private String id;
    private String owner_id;
    private String created_date;
    private String hospital_name;
    private String doctor_name;
    private String diagnose;
    private List<VisitDiseaseModel> disease_list;
    private List<VisitImageItem> image_list;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
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

    public List<VisitDiseaseModel> getDisease_list() {
        if (disease_list != null) return disease_list;
        else return new ArrayList<>();
    }

    public String getDiseases_display() {
        if (disease_list != null && disease_list.size() > 0){
            StringBuilder sb = new StringBuilder();
            for (VisitDiseaseModel model :
                    disease_list) {
                if (model.is_selected()){
                    sb.append(model.getName());
                    sb.append(", ");
                }
            }

            if (sb.length() > 0) return sb.substring(0, sb.length() - 2);
            return "-";
        } else {
            return "-";
        }
    }

    public String getDiseases_for_api() {
        if (disease_list != null && disease_list.size() > 0){
            StringBuilder sb = new StringBuilder();
            for (VisitDiseaseModel model :
                    disease_list) {
                sb.append(model.getName());
                sb.append(";");
            }

            return sb.substring(0, sb.length() - 1);
        } else {
            return "default";
        }
    }

    public void setDisease_list(List<VisitDiseaseModel> disease_list) {
        this.disease_list = disease_list;
    }

    public List<VisitImageItem> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<VisitImageItem> image_list) {
        this.image_list = image_list;
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
