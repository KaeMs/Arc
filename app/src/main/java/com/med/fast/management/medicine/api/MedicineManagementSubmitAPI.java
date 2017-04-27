package com.med.fast.management.medicine.api;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class MedicineManagementSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String name;
            public String route;
            public String form;
            public String dose;
            public String frequency;
            public String reason;
            public String status;
            public String additional_instruction;
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
