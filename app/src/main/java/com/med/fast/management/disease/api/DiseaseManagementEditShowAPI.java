package com.med.fast.management.disease.api;

import com.med.fast.management.disease.DiseaseManagementModel;
import com.med.fast.summary.DiseaseModel;

/**
 * Created by website-development on 27-Apr-17.
 */

public class DiseaseManagementEditShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String disease_id;
            public String user_id;
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
            public String user_id;
            public DiseaseManagementModel disease_item;
        }
    }
}
