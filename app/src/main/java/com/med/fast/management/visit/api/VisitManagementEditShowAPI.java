package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitModel;

/**
 * Created by kevindreyar on 03-May-17.
 */

public class VisitManagementEditShowAPI {
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
            public VisitModel visit;
        }
    }
}
