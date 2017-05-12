package com.med.fast.setting;

import com.med.fast.api.ResponseAPI;

/**
 * Created by kevindreyar on 12-May-17. FM
 */

public interface SettingAPIIntf {
    void onFinishSettingShow(ResponseAPI responseAPI);
    void onFinishSettingSubmit(ResponseAPI responseAPI);
}
