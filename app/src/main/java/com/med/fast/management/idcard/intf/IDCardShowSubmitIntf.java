package com.med.fast.management.idcard.intf;

import com.med.fast.api.ResponseAPI;

/**
 * Created by Kevin Murvie on 5/8/2017. FM
 */

public interface IDCardShowSubmitIntf {
    void onFinishIDCardShow(ResponseAPI responseAPI);
    void onFinishIDCardSubmit(ResponseAPI responseAPI);
}
