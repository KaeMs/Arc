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
    public RealmList<AllergyModel> allergies;
    public RealmList<DiseaseModel> disease;
    public RealmList<DiseaseModel> family_anamnesy;
    public RealmList<MedicineModel> medicine;
    public RealmList<VisitModel> visit;
    public String voluptuary_habits;
}
