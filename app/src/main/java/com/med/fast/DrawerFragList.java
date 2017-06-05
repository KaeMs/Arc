package com.med.fast;

import android.app.Activity;
import android.content.Context;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Murvie on 6/5/2017. FM
 */

public class DrawerFragList {

    public static List<Integer> getFragResList(){
        List<Integer> drawerFragResList = new ArrayList<>();

        drawerFragResList.add(R.id.drawer_summary_wrapper);
        drawerFragResList.add(R.id.drawer_idcard_wrapper);
        drawerFragResList.add(R.id.drawer_visit_wrapper);
        drawerFragResList.add(R.id.drawer_surgery_wrapper);
        drawerFragResList.add(R.id.drawer_allergy_wrapper);
        drawerFragResList.add(R.id.drawer_disease_wrapper);
        drawerFragResList.add(R.id.drawer_medicine_wrapper);
        drawerFragResList.add(R.id.drawer_accident_wrapper);
        drawerFragResList.add(R.id.drawer_labresult_wrapper);
        drawerFragResList.add(R.id.drawer_misc_wrapper);

        return drawerFragResList;
    }

    public static List<Target> getFragTargetList(Activity activity){
        List<Target> drawerFragResList = new ArrayList<>();

        for (Integer drawerRes :
                getFragResList()) {
            drawerFragResList.add(new ViewTarget(drawerRes, activity));
        }

        return drawerFragResList;
    }
}
