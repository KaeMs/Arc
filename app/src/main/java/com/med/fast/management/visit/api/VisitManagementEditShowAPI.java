package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitModel;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class VisitManagementEditShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

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
