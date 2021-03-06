package com.med.fast.management.allergy.allergyinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 4/28/2017. FM
 */

public interface AllergyManagementCreateDeleteIntf {
    void onFinishAllergyManagementCreateSubmit(ResponseAPI responseAPI, String tag);
    void onFinishAllergyManagementDelete(ResponseAPI responseAPI, String tag);
}
