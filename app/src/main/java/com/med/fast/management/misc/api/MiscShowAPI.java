package com.med.fast.management.misc.api;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
        }

        public class Status
        {
            public String code;
            public String description;
        }

        public class Results
        {
            public boolean is_female;
            public String voluptuary_habits;
            public String pregnancy;
            public String pregnancy_weeks;
            public String had_miscarriage;
            public String last_time_miscarriage;
            public String cycle_alteration;
        }
    }
}
