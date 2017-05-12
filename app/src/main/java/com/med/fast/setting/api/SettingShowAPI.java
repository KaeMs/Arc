package com.med.fast.setting.api;

import java.sql.Date;

/**
 * Created by kevindreyar on 01-May-17. FM
 */

public class SettingShowAPI {
    public Data data = new Data();

    public class Data
    {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query
        {
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
            public String first_name;
            public String last_name;
            public String date_of_birth;
            public int gender; //0="Male", 1= "Female"
            public String profil_image_path;
        }
    }
}
