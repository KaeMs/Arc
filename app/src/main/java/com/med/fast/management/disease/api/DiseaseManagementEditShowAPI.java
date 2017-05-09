package com.med.fast.management.disease.api;

import com.med.fast.management.disease.DiseaseManagementModel;
import com.med.fast.summary.DiseaseModel;

/**
 * Created by website-development on 27-Apr-17. FM
 */

public class DiseaseManagementEditShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String disease_id;
            public String user_id;
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
            public String user_id;
            public String id;
            public String name;
            public String is_hereditary;
            public String hereditary_carriers;
            public String last_visit;
            public String is_ongoing;
            public String created_date;
            public String historic_date;
            public String approximate_date;
        }
    }
}
