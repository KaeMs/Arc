package com.med.fast.management.visit.api;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class VisitManagementEditSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String visit_id;
            public String doctor;
            public String hospital;
            public String diagnose;
            public String disease_id_list;
            public String is_image_uploaded;
            public String image_json_str;
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
