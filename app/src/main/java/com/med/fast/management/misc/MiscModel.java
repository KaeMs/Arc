package com.med.fast.management.misc;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscModel {
    public boolean is_female() {
        return is_female;
    }

    public void setIs_female(boolean is_female) {
        this.is_female = is_female;
    }

    public String getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(String pregnancy) {
        this.pregnancy = pregnancy;
    }

    public String getPregnancy_weeks() {
        return pregnancy_weeks;
    }

    public void setPregnancy_weeks(String pregnancy_weeks) {
        this.pregnancy_weeks = pregnancy_weeks;
    }

    public String getHad_miscarriage() {
        return had_miscarriage;
    }

    public void setHad_miscarriage(String had_miscarriage) {
        this.had_miscarriage = had_miscarriage;
    }

    public String getLast_time_miscarriage() {
        return last_time_miscarriage;
    }

    public void setLast_time_miscarriage(String last_time_miscarriage) {
        this.last_time_miscarriage = last_time_miscarriage;
    }

    public String getCycle_alteration() {
        return cycle_alteration;
    }

    public void setCycle_alteration(String cycle_alteration) {
        this.cycle_alteration = cycle_alteration;
    }

    public boolean is_female;
    public String pregnancy;
    public String pregnancy_weeks;
    public String had_miscarriage;
    public String last_time_miscarriage;
    public String cycle_alteration;

    public String voluptuary_habit;

    public String getVoluptuary_habit() {
        return voluptuary_habit;
    }

    public void setVoluptuary_habit(String voluptuary_habit) {
        this.voluptuary_habit = voluptuary_habit;
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

    private String progress_status; // 0 = Normal, 1 = Add progress, 2 = Delete progress, 3 = Failed
    private String tag;
}
