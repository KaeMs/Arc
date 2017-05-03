package com.med.fast.signup.api;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class SignupEmailConfirmationAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String token;
        }

        public class Status {
            public String code;
            public String description;
        }

        public class Results {
            public String result_status;
            public String description;
            public String user_id;
        }
    }
}
