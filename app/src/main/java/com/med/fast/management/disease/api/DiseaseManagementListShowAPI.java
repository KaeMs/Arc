package com.med.fast.management.disease.api;

import com.med.fast.management.disease.DiseaseManagementModel;

import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class DiseaseManagementListShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

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
            public List<DiseaseManagementModel> disease_list;

        }
    }
}
