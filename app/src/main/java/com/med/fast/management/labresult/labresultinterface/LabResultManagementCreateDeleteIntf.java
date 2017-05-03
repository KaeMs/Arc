package com.med.fast.management.labresult.labresultinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public interface LabResultManagementCreateDeleteIntf {
    void onFinishLabResultManagementCreate(ResponseAPI responseAPI, String tag);
    void onFinishLabResultManagementDelete(ResponseAPI responseAPI, String tag);
}
