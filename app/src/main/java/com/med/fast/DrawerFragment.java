package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.med.fast.management.visit.VisitFragment;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class DrawerFragment extends FastBaseFragment {

    @BindView(R.id.drawer_summary_wrapper)
    LinearLayout summaryWrapper;
    @BindView(R.id.drawer_idcard_wrapper)
    LinearLayout idcardWrapper;
    @BindView(R.id.drawer_visit_wrapper)
    LinearLayout visitWrapper;
    @BindView(R.id.drawer_surgery_wrapper)
    LinearLayout surgeryWrapper;
    @BindView(R.id.drawer_allergy_wrapper)
    LinearLayout allergyWrapper;
    @BindView(R.id.drawer_disease_wrapper)
    LinearLayout diseaseWrapper;
    @BindView(R.id.drawer_medicine_wrapper)
    LinearLayout medicineWrapper;
    @BindView(R.id.drawer_accident_wrapper)
    LinearLayout accidentWrapper;
    @BindView(R.id.drawer_labresult_wrapper)
    LinearLayout labresultWrapper;
    @BindView(R.id.drawer_misc_wrapper)
    LinearLayout miscWrapper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmcontainer_main_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        visitWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                VisitFragment visitFragment = new VisitFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(visitFragment, Tag.VISIT_FRAG, false);
            }
        });
    }
}
