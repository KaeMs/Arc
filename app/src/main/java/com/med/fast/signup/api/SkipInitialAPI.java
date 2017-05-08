package com.med.fast.signup.api;

/**
 * Created by Kevin Murvie on 5/8/2017. FM
 */

public class SkipInitialAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String user_id;
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
