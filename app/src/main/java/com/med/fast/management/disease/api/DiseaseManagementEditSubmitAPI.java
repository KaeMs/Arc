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
            public String disease_id;
            public String disease_name;
            public String user_id;
            public String is_hereditary;
            public String is_ongoing;
            public String history_date_text;
            public String date;
            public String hereditary_carrier;
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
        }
    }
}

