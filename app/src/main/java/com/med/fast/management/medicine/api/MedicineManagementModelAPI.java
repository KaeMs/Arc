package com.med.fast.management.medicine.api;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class MedicineManagementModelAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String medicine_id;
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
            //public master_medicine medicine;
        }
    }
}
