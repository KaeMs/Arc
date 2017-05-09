package com.med.fast.management.surgery.api;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class SurgeryManagementEditSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String surgery_id;
            public String procedure;
            public String physician;
            public String date;
            public String hospital;
            public String notes;
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
