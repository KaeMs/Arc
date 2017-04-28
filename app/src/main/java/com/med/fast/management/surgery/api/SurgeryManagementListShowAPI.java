package com.med.fast.management.surgery.api;

import com.med.fast.management.surgery.SurgeryManagementModel;

import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class SurgeryManagementListShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String keyword;
            public String sort;
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
            public List<SurgeryManagementModel> surgery_list;
        }
    }
}
