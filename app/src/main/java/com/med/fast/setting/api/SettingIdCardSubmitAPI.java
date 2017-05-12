package com.med.fast.setting.api;

/**
 * Created by kevindreyar on 01-May-17. FM
 */

public class SettingIdCardSubmitAPI {
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
            public String card_id_new_image_path;
        }
    }
}
