package com.med.fast.management.accidenthistory.accidentinterface;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 4/28/2017. FM
 */

public interface AccidentHistoryCreateDeleteIntf {
    void onFinishAccidentHistoryCreateSubmit(ResponseAPI responseAPI);
    void onFinishAccidentHistoryDelete(ResponseAPI responseAPI);
}
