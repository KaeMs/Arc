package com.med.fast.management.visit.visitinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public interface VisitFragmentIntf {
    void onFinishVisitShow(ResponseAPI responseAPI);
    void onFinishVisitCreate(ResponseAPI responseAPI);
}