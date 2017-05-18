package com.med.fast.management.visit.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class VisitManagementCreateSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String doctor;
            public String hospital;
            public String diagnose;
            public String disease_id_list;
            public String is_image_uploaded;
            public List<File> image_list = new ArrayList<>();
            public String tag;
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
            public String new_visit_id;
        }
    }
}
