package com.med.fast.summary;

/**
 * Created by kevin on 25-Apr-17.
 */

public class DiseaseModel {
    public String disease_id ;
    public String disease_name ;
    public String owner_id ;
    public String disease_is_hereditary ;//0=No, 1=Yes
    public String disease_hereditary_carrier ;
    public String last_visit ;
    public String disease_is_ongoing ;//0=No, 1=Yes
    public String disease_created_date ;
    public String disease_history_date ;
    public String disease_history_text_date ;

    public String getDisease_id() {
        return disease_id;
    }

    public void setDisease_id(String disease_id) {
        this.disease_id = disease_id;
    }

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getDisease_is_hereditary() {
        return disease_is_hereditary;
    }

    public void setDisease_is_hereditary(String disease_is_hereditary) {
        this.disease_is_hereditary = disease_is_hereditary;
    }

    public String getDisease_hereditary_carrier() {
        return disease_hereditary_carrier;
    }

    public void setDisease_hereditary_carrier(String disease_hereditary_carrier) {
        this.disease_hereditary_carrier = disease_hereditary_carrier;
    }

    public String getLast_visit() {
        return last_visit;
    }

    public void setLast_visit(String last_visit) {
        this.last_visit = last_visit;
    }

    public String getDisease_is_ongoing() {
        return disease_is_ongoing;
    }

    public void setDisease_is_ongoing(String disease_is_ongoing) {
        this.disease_is_ongoing = disease_is_ongoing;
    }

    public String getDisease_created_date() {
        return disease_created_date;
    }

    public void setDisease_created_date(String disease_created_date) {
        this.disease_created_date = disease_created_date;
    }

    public String getDisease_history_date() {
        return disease_history_date;
    }

    public void setDisease_history_date(String disease_history_date) {
        this.disease_history_date = disease_history_date;
    }

    public String getDisease_history_text_date() {
        return disease_history_text_date;
    }

    public void setDisease_history_text_date(String disease_history_text_date) {
        this.disease_history_text_date = disease_history_text_date;
    }
}
