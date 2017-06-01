package com.med.fast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Murvie on 6/1/2017. FM
 */

public class UserGuideUtils {
    public static List<Integer> getDrawerList(){
        List<Integer> drawerRes = new ArrayList<>();

        drawerRes.add(R.id.drawer_idcard_wrapper);
        drawerRes.add(R.id.drawer_visit_wrapper);
        drawerRes.add(R.id.drawer_surgery_wrapper);
        drawerRes.add(R.id.drawer_allergy_wrapper);
        drawerRes.add(R.id.drawer_disease_wrapper);
        drawerRes.add(R.id.drawer_medicine_wrapper);
        drawerRes.add(R.id.drawer_accident_wrapper);
        drawerRes.add(R.id.drawer_labresult_wrapper);
        drawerRes.add(R.id.drawer_misc_wrapper);
        drawerRes.add(R.id.drawer_idcard_wrapper);

        return drawerRes;
    }
}
