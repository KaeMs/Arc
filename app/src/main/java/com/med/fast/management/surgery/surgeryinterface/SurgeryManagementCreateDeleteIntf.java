package com.med.fast.management.surgery.surgeryinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 4/30/2017. FM
 */

public interface SurgeryManagementCreateDeleteIntf {
    void onFinishSurgeryManagementCreate(ResponseAPI responseAPI, String tag);
    void onFinishSurgeryManagementDelete(ResponseAPI responseAPI, String tag);
}