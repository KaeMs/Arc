package com.med.fast.management.disease.diseaseinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public interface DiseaseManagementCreateIntf {
    void onFinishDiseaseManagementCreateShow(ResponseAPI responseAPI);
    void onFinishDiseaseManagementCreate(ResponseAPI responseAPI, String tag);
}
