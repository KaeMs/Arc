package com.med.fast.management.accidenthistory.api;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class AccidentHistoryEditShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String accident_id;
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
            public String detail;
            public String injury_nature;
            public String injury_location;
            public String injury_date;
            public String injury_date_tmp;
            public String injury_date_custom;
        }
    }
}
