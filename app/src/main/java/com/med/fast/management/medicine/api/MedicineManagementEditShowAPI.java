package com.med.fast.management.medicine.api;

import com.med.fast.management.medicine.MedicineManagementModel;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class MedicineManagementEditShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

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
            public MedicineManagementModel medicine;
        }
    }
}
