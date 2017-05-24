package com.med.fast.summary;

import java.sql.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by kevindreyar on 27-Apr-17. FM
 */

public class SummaryShowAPI {
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
            public String name;
            public String date_of_birth;
            public String gender;
            public String profil_image_path;
            public String voluptuary_habits;
            public RealmList<AllergyModel> allergies;
            public RealmList<DiseaseModel> disease;
            public RealmList<DiseaseModel> family_anamnesy;
            public RealmList<MedicineModel> medicine;
            public RealmList<VisitModel> visit;
        }
    }
}
