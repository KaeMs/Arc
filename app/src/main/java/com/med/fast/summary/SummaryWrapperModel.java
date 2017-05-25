package com.med.fast.summary;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Kevin Murvie on 5/7/2017. FM
 */

public class SummaryWrapperModel extends RealmObject {
    public String name;
    public String date_of_birth;
    public String gender;
    public String profil_image_path;
    public RealmList<AllergyModel> allergies = new RealmList<>();
    public RealmList<DiseaseModel> disease = new RealmList<>();
    public RealmList<DiseaseModel> family_anamnesy = new RealmList<>();
    public RealmList<MedicineModel> medicine = new RealmList<>();
    public RealmList<VisitModel> visit = new RealmList<>();
    public String voluptuary_habits;
}
