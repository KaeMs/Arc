package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.med.fast.management.accidenthistory.AccidentHistoryManagementFragment;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryFragmentIntf;
import com.med.fast.management.allergy.AllergyManagementFragment;
import com.med.fast.management.disease.DiseaseManagementFragment;
import com.med.fast.management.labresult.LabResultManagementFragment;
import com.med.fast.management.medicine.MedicineManagementFragment;
import com.med.fast.management.surgery.SurgeryManagementFragment;
import com.med.fast.management.visit.VisitFragment;
import com.med.fast.summary.SummaryFragment;

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

        summaryWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                SummaryFragment summaryFragment = new SummaryFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(summaryFragment, Tag.SUMMARY_FRAG, false);
            }
        });

        idcardWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                SummaryFragment summaryFragment = new SummaryFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(summaryFragment, Tag.IDCARD_FRAG, false);
            }
        });

        visitWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                VisitFragment visitFragment = new VisitFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(visitFragment, Tag.VISIT_FRAG, false);
            }
        });

        surgeryWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                SurgeryManagementFragment surgeryManagementFragment = new SurgeryManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(surgeryManagementFragment, Tag.SURGERY_FRAG, false);
            }
        });

        allergyWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                AllergyManagementFragment allergyManagementFragment = new AllergyManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(allergyManagementFragment, Tag.ALLERGY_FRAG, false);
            }
        });

        diseaseWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                DiseaseManagementFragment diseaseManagementFragment = new DiseaseManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(diseaseManagementFragment, Tag.DISEASE_FRAG, false);
            }
        });

        medicineWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                MedicineManagementFragment medicineManagementFragment = new MedicineManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(medicineManagementFragment, Tag.MEDICINE_FRAG, false);
            }
        });

        accidentWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                AccidentHistoryManagementFragment accidentHistoryManagementFragment = new AccidentHistoryManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(accidentHistoryManagementFragment, Tag.ACCIDENT_FRAG, false);
            }
        });

        labresultWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                LabResultManagementFragment accidentHistoryManagementFragment = new LabResultManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(accidentHistoryManagementFragment, Tag.LABRESULT_FRAG, false);
            }
        });

        miscWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                LabResultManagementFragment accidentHistoryManagementFragment = new LabResultManagementFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(accidentHistoryManagementFragment, Tag.MISC_FRAG, false);
            }
        });
    }
}
