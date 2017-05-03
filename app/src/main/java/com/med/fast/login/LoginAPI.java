package com.med.fast.login;

/**
 * Created by Kevin Murvie on 5/02/2017. FM
 */

public class LoginAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String email;
            public String password;
        }

        public class Status {
            public String code;
            public String description;
        }

        public class Results {
            public String result_status;
            public String description;
            public String user_id;
            public boolean is_verified_email;
            public String initial_data_step;
            public String first_name;
            public String last_name;
            public String email;
            public String dob; // format 'MMM dd, yyyy'
            public String gender; // 0 = male, 1 = female
        }
    }
}
