package com.med.fast.management.medicine.medicineinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public interface MedicineFragmentIntf {
    void onFinishMedicineShow(ResponseAPI responseAPI);
    void onFinishMedicineCreate(ResponseAPI responseAPI);
}