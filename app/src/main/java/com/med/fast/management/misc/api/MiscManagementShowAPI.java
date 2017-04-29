package com.med.fast.management.misc.api;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscManagementShowAPI {
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
            public String result_status;
            public String description;
            public String voluptuary_habit;
        }
    }
}
