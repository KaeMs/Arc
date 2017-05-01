package com.med.fast.setting;

import java.sql.Date;

/**
 * Created by kevindreyar on 01-May-17.
 */

public class SettingModel {
    public String id;
    public String first_name;
    public String last_name;
    public Date date_of_birth;
    public int gender; //0="Male", 1= "Female"

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProfil_image_path() {
        return profil_image_path;
    }

    public void setProfil_image_path(String profil_image_path) {
        this.profil_image_path = profil_image_path;
    }

    public String profil_image_path;
}
