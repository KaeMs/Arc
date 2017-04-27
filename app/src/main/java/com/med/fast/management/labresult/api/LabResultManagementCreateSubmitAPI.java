package com.med.fast.management.labresult.api;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class LabResultManagementCreateSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String lab_result_id;
            public String user_id;
            public String test_name;
            public String desc_result;
            public String img_path;
            public String date;
            public String place;
            public String list_img_uploadeds;

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
