package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitModel;

import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class VisitManagementListShowAPI {
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
            public List<VisitModel> visit_list;
        }
    }
}
