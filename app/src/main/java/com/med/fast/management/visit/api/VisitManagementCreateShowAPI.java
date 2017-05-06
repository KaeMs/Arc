package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitDiseaseModel;

import java.util.List;

/**
 * Created by Kevin Murvie on 5/6/2017. FM
 */

public class VisitManagementCreateShowAPI {
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
            public List<VisitDiseaseModel> disease_list;
        }
    }
}
