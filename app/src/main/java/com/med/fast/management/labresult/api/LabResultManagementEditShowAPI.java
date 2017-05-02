package com.med.fast.management.labresult.api;

import com.med.fast.management.labresult.LabResultManagementModel;
import com.med.fast.management.labresult.api.PhotoDetailLabResultsApps;

import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class LabResultManagementEditShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
            public String lab_result_id;
            public String user_id;
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
            public String user_id;
            public LabResultManagementModel lab_result;
            public List<PhotoDetailLabResultsApps> list_img_detail;
        }
    }
}
