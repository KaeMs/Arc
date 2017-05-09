package com.med.fast.management.medicine.api;

import com.med.fast.management.medicine.MedicineManagementModel;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class MedicineManagementEditShowAPI {
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
            public String id;
            public String name;
            public String form;
            public String administration_method;
            public String administration_dose;
            public String frequency;
            public String medication_reason;
            public String medication_status;
            public String additional_instruction;
            public String created_date;
        }
    }
}
