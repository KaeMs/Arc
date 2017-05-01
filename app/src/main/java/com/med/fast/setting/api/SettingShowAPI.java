package com.med.fast.setting.api;

import java.sql.Date;

/**
 * Created by kevindreyar on 01-May-17.
 */

public class SettingShowAPI {
    public Data data;

    public class Data
    {
        public Query query;
        public Status status;
        public Results results;

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
            public Date date_of_birth;
            public int gender; //0="Male", 1= "Female"
            public String profil_image_path;
        }
    }
}
