package com.med.fast.management.medicine.medicineinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin on 4/29/2017. FM
 */

public interface MedicineCreateDeleteIntf {
    void onFinishMedicineCreate(ResponseAPI responseAPI, String tag);
    void onFinishMedicineDelete(ResponseAPI responseAPI, String tag);
}