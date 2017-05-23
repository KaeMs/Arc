package com.med.fast.management.labresult.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class LabResultManagementCreateSubmitAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String user_id;
            public String test_name;
            public String desc_result;
            public String place;
            public String date;
            public String img_obj_json;
            public List<File> img_file_list = new ArrayList<>();
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
            public String new_lab_result_id;
        }
    }
}
