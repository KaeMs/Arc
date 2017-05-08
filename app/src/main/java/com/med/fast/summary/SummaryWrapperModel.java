package com.med.fast.summary;

import java.util.List;

/**
 * Created by Kevin Murvie on 5/7/2017. FM
 */

public class SummaryWrapperModel {
    public String name;
    public String date_of_birth;
    public String gender;
    public String profil_image_path;
    public List<AllergyModel> allergies;
    public List<DiseaseModel> disease;
    public List<DiseaseModel> family_anamnesy;
    public List<MedicineModel> medicine;
    public List<VisitModel> visit;
    public String voluptary_habits;
}
