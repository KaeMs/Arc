package com.med.fast.summary;

import java.sql.Date;
import java.util.List;

/**
 * Created by kevindreyar on 27-Apr-17.
 */

public class SummaryShowAPI {
    public Data data ;

    public class Data
    {
        public Query query ;
        public Status status ;
        public Results results ;

        public class Query
        {
            public String user_id ;
        }

        public class Status
        {
            public String code ;
            public String description ;
        }

        public class Results
        {
            public String result_status ;
            public String description ;
            public String name ;
            public Date date_of_birth ;
            public String gender ;
            public String profil_image_path ;
            public List<AllergyModel> allergies ;
            public List<DiseaseModel> disease ;
            public List<DiseaseModel> family_anamnesy ;
            public List<MedicineModel> medicine ;
            public List<VisitModel> visit ;
        }
    }
}
