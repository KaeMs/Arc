package com.med.fast.management.medicine.api;

import com.med.fast.management.medicine.MedicineManagementModel;

import java.util.List;

/**
 * Created by website-development on 27-Apr-17.
 */

public class MedicineManagementListShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String keyword;
            public String sort;
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
            public List<MedicineManagementModel> medicine_list;
        }
    }
}
