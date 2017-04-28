package com.med.fast.management.allergy.api;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class AllergyManagementEditSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String allergy_id;
            public String allergy_agent;
            public String allergy_is_drug;
            public String allergy_reaction;
            public String allergy_first_experience;
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
