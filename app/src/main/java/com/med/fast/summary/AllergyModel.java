package com.med.fast.summary;

/**
 * Created by kevindreyar on 25-Apr-17.
 */

public class AllergyModel {
    public String id;
    public String agent;
    public boolean is_drug;
    public String reaction;
    public String first_experience;
    public String created_date;

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

    public boolean isIs_drug() {
        return is_drug;
    }

    public void setIs_drug(boolean is_drug) {
        this.is_drug = is_drug;
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
