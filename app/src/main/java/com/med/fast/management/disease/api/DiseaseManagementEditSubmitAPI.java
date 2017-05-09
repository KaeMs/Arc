package com.med.fast.management.disease.api;

/**
 * Created by wkevindreyar on 27-Apr-17. FM
 */

public class DiseaseManagementEditSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String name;
            public String id;
            public String user_id;
            public String is_hereditary;
            public String is_ongoing;
            public String hereditary_carriers;
            public String historic_date;
            public String approximate_date;

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

