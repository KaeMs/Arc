package com.med.fast.management.labresult.api;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class LabResultManagementEditSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

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
            public String img_obj_json;
            public String added_img_obj_json;
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
