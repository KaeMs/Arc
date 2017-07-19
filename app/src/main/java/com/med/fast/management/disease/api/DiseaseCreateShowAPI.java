package com.med.fast.management.disease.api;

import com.med.fast.management.disease.DiseaseNameModel;

import java.util.List;

/**
 * Created by Kevin Murvie on 7/19/2017. FMR
 */

public class DiseaseCreateShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
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
            public List<DiseaseNameModel> disease_name_list;
        }
    }
}
