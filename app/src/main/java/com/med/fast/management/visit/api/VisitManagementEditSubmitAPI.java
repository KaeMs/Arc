package com.med.fast.management.visit.api;

import com.med.fast.management.visit.VisitDiseaseModel;
import com.med.fast.management.visit.VisitImageItemUpload;
import com.med.fast.management.visit.VisitImageItemUploadWrapper;

import java.io.File;
import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class VisitManagementEditSubmitAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String user_id;
            public String visit_id;
            public String doctor;
            public String hospital;
            public String diagnose;
            public String disease_id_list;
            public String is_image_uploaded;
            public String image_json_str;
            public List<File> image_files;
        }

        public class Status {
            public String code;
            public String description;
        }

        public class Results {
            public String result_status;
            public String description;
        }
    }
}
