package com.med.fast.management.disease.api;

/**
 * Created by kevindreyar on 27-Apr-17. FN
 */

public class DiseaseManagementDeleteSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String disease_id;

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
