package com.med.fast.management.visit.visitinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin on 4/29/2017. FM
 */

public interface VisitCreateDeleteIntf {
    void onFinishVisitCreate(ResponseAPI responseAPI);
    void onFinishVisitDelete(ResponseAPI responseAPI);
}