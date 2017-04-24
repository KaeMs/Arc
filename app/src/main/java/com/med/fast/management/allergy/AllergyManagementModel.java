package com.med.fast.management.allergy;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementModel {
    private String agent;
    private String drug;
    private String reaction;
    private String first_experience;
    private String created_date;

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {
        this.drug = drug;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getFirst_experience() {
        return first_experience;
    }

    public void setFirst_experience(String first_experience) {
        this.first_experience = first_experience;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
