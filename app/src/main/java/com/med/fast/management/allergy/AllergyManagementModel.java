package com.med.fast.management.allergy;

import com.med.fast.Utils;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementModel {
    private String id;
    private String agent;
    private boolean is_drug;
    private String reaction;
    private String first_experience;
    private String created_date;
    private String progress_status = "0"; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public boolean getIs_drug() {
        return is_drug;
    }

    public String getDrug_display() {
        return Utils.processBoolFromAPI(is_drug);
    }

    public void setIs_drug(boolean is_drug) {
        this.is_drug = is_drug;
    }

    public String getReaction() {
        return reaction;
    }

    public String getReaction_display() {
        return Utils.processStringFromAPI(reaction);
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getFirst_experience() {
        return first_experience;
    }

    public String getFirst_experience_display() {
        return Utils.processStringFromAPI(first_experience);
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
