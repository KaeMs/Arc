package com.med.fast.management.allergy.api;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class AllergyManagementEditSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String allergy_id;
            public String agent;
            public String is_drug;
            public String reaction;
            public String first_experience;
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
