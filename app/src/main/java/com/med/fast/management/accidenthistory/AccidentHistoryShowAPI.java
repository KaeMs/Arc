package com.med.fast.management.accidenthistory;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

class AccidentHistoryShowAPI
{
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String counter;
            public String flag;
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
            public String saved_user_id;
        }
    }
}
