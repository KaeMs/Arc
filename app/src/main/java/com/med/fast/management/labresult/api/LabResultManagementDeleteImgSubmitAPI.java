package com.med.fast.management.labresult.api;

import java.sql.Date;
import java.util.List;
import com.med.fast.management.labresult.api.PhotoDetailLabResultsApps;
/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class LabResultManagementDeleteImgSubmitAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

        public class Query
        {
            public String user_id;
            public String img_id;
            public String img_path;
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
            public List<PhotoDetailLabResultsApps> list_img_detail;
        }
    }
}
