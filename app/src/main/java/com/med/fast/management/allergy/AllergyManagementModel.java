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
    private String progress_status; // 0 = Normal, 1 = Add progress, 2 = Delete progress

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

    public String getProgress_status() {
        return progress_status;
    }

    public void setProgress_status(String progress_status) {
        this.progress_status = progress_status;
    }
}
