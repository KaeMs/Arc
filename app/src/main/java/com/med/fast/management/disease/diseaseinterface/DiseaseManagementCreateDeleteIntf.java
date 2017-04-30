package com.med.fast.management.disease.diseaseinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public interface DiseaseManagementCreateDeleteIntf {
    void onFinishDiseaseManagementCreate(ResponseAPI responseAPI, String tag);
    void onFinishDiseaseManagementDelete(ResponseAPI responseAPI, String tag);
}
