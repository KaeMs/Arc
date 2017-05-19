package com.med.fast.management.accidenthistory.api;

import com.med.fast.management.accidenthistory.AccidentHistoryManagementModel;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class AccidentHistoryCreateSubmitAPI {
    public Data data = new Data();

    public class Data {
        public Query query = new Query();
        public Status status = new Status();
        public Results results = new Results();

        public class Query {
            public String user_id;
            public String detail;
            public String injury_nature;
            public String injury_location;
            public String injury_date;
            public String injury_date_tmp;
            public String injury_date_custom;
        }

        public class Status {
            public String code;
            public String description;
        }

        public class Results {
            public String result_status;
            public String description;
            public String new_accident_id;
        }
    }
}
