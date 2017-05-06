package com.med.fast.management.visit.visitinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin on 4/29/2017. FM
 */

public interface VisitCreateIntf {
    void onFinishVisitCreateShow(ResponseAPI responseAPI);
    void onFinishVisitCreate(ResponseAPI responseAPI, String tag);
}