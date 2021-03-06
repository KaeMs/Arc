package com.med.fast.signup.api;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class RegisterSubmitAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String first_name;
            public String last_name;
            public String email;
            public String dob; // format 'MMM dd, yyyy'
            public String password;
            public String gender; // 0 = male, 1 = female
        }

        public class Status {
            public String code;
            public String description;
        }

        public class Results {
            public String result_status;
            public String description;
            public String saved_user_id;
        }
    }
}
