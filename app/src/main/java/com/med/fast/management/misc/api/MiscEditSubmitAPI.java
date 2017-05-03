package com.med.fast.management.misc.api;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class MiscEditSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String voluptiary_habits;
            public String pregnancy;
            public String pregnancy_weeks;
            public String had_miscarriage;
            public String last_time_miscarriage;
            public String cycle_alteration;

        }

        public class Status
        {
            public String code;
            public String description;
        }

        public class Results
        {
            public String result_status;
            public String description;
        }
    }
}
