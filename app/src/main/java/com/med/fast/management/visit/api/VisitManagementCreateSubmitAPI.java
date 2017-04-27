package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitCreateSubmitAPI;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class VisitManagementCreateSubmitAPI {
    public VisitCreateSubmitAPI.Data data;

    public class Data
    {
        public VisitCreateSubmitAPI.Data.Query query;
        public VisitCreateSubmitAPI.Data.Status status;
        public VisitCreateSubmitAPI.Data.Results results;

        public class Query
        {
            public String user_id;
            public String doctor;
            public String hospital;
            public String diagnose;
            public String disease_id_list;
            public String is_image_uploaded;
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
