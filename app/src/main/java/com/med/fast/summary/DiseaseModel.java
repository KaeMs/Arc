package com.med.fast.summary;

import io.realm.RealmObject;

/**
 * Created by kevin on 25-Apr-17. FM
 */

public class DiseaseModel extends RealmObject {
    public String id;
    public String name;
    public String is_hereditary;//0=No, 1=Yes
    public String hereditary_carriers;
    public String last_visit;
    public String is_ongoing;//0=No, 1=Yes
    public String created_date;
    public String historic_date;
    public String approximate_date;

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

    public String getIs_hereditary() {
        return is_hereditary;
    }

    public void setIs_hereditary(String is_hereditary) {
        this.is_hereditary = is_hereditary;
    }

    public String getHereditary_carriers() {
        return hereditary_carriers;
    }

    public void setHereditary_carriers(String hereditary_carriers) {
        this.hereditary_carriers = hereditary_carriers;
    }

    public String getLast_visit() {
        return last_visit;
    }

    public void setLast_visit(String last_visit) {
        this.last_visit = last_visit;
    }

    public String getIs_ongoing() {
        return is_ongoing;
    }

    public void setIs_ongoing(String is_ongoing) {
        this.is_ongoing = is_ongoing;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getHistoric_date() {
        return historic_date;
    }

    public void setHistoric_date(String historic_date) {
        this.historic_date = historic_date;
    }

    public String getApproximate_date() {
        return approximate_date;
    }

    public void setApproximate_date(String approximate_date) {
        this.approximate_date = approximate_date;
    }
}
