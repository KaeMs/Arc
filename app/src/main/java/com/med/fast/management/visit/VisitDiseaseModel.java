package com.med.fast.management.visit;

/**
 * Created by Kevin Murvie on 5/5/2017. FM
 */

public class VisitDiseaseModel {
    private String id;
    private String name;
    private boolean is_selected;

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

    public boolean is_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
