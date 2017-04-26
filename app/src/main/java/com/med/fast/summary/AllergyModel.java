package com.med.fast.summary;

/**
 * Created by kevindreyar on 25-Apr-17.
 */

public class AllergyModel {
    public String allergy_id;
    public String allergy_agent;
    public boolean allergy_is_drug;
    public String allergy_reaction;
    public String allergy_first_experience;
    public String allergy_created_date;

    public String getAllergy_id() {
        return allergy_id;
    }

    public void setAllergy_id(String allergy_id) {
        this.allergy_id = allergy_id;
    }

    public String getAllergy_agent() {
        return allergy_agent;
    }

    public void setAllergy_agent(String allergy_agent) {
        this.allergy_agent = allergy_agent;
    }

    public boolean isAllergy_is_drug() {
        return allergy_is_drug;
    }

    public void setAllergy_is_drug(boolean allergy_is_drug) {
        this.allergy_is_drug = allergy_is_drug;
    }

    public String getAllergy_reaction() {
        return allergy_reaction;
    }

    public void setAllergy_reaction(String allergy_reaction) {
        this.allergy_reaction = allergy_reaction;
    }

    public String getAllergy_first_experience() {
        return allergy_first_experience;
    }

    public void setAllergy_first_experience(String allergy_first_experience) {
        this.allergy_first_experience = allergy_first_experience;
    }

    public String getAllergy_created_date() {
        return allergy_created_date;
    }

    public void setAllergy_created_date(String allergy_created_date) {
        this.allergy_created_date = allergy_created_date;
    }
}
