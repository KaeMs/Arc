package com.med.fast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.med.fast.login.LoginActivity;
import com.med.fast.management.accidenthistory.AccidentHistoryManagementFragment;
import com.med.fast.management.allergy.AllergyManagementFragment;
import com.med.fast.management.disease.DiseaseManagementFragment;
import com.med.fast.management.idcard.IDCardFragment;
import com.med.fast.management.labresult.LabResultManagementFragment;
import com.med.fast.management.medicine.MedicineManagementFragment;
import com.med.fast.management.misc.MiscManagementFragment;
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
    @BindView(R.id.drawer_logout_wrapper)
    LinearLayout logoutWrapper;

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
                if (!(((MainActivity) getActivity()).currentFragment() instanceof SummaryFragment)){
                    SummaryFragment summaryFragment = new SummaryFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(summaryFragment, Tag.SUMMARY_FRAG, false);
                }
            }
        });

        idcardWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof IDCardFragment)){
                    IDCardFragment idCardFragment = new IDCardFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(idCardFragment, Tag.IDCARD_FRAG, false);
                }
            }
        });

        visitWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof VisitFragment)){
                    VisitFragment visitFragment = new VisitFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(visitFragment, Tag.VISIT_FRAG, false);
                }
            }
        });

        surgeryWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof SurgeryManagementFragment)){
                    SurgeryManagementFragment surgeryManagementFragment = new SurgeryManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(surgeryManagementFragment, Tag.SURGERY_FRAG, false);
                }
            }
        });

        allergyWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof AllergyManagementFragment)){
                    AllergyManagementFragment allergyManagementFragment = new AllergyManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(allergyManagementFragment, Tag.ALLERGY_FRAG, false);
                }
            }
        });

        diseaseWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof DiseaseManagementFragment)){
                    DiseaseManagementFragment diseaseManagementFragment = new DiseaseManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(diseaseManagementFragment, Tag.DISEASE_FRAG, false);
                }
            }
        });

        medicineWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof MedicineManagementFragment)){
                    MedicineManagementFragment medicineManagementFragment = new MedicineManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(medicineManagementFragment, Tag.MEDICINE_FRAG, false);
                }
            }
        });

        accidentWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof AccidentHistoryManagementFragment)){
                    AccidentHistoryManagementFragment accidentHistoryManagementFragment = new AccidentHistoryManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(accidentHistoryManagementFragment, Tag.ACCIDENT_FRAG, false);
                }
            }
        });

        labresultWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof LabResultManagementFragment)){
                    LabResultManagementFragment labResultManagementFragment = new LabResultManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(labResultManagementFragment, Tag.LABRESULT_FRAG, false);
                }
            }
        });

        miscWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                if (!(((MainActivity) getActivity()).currentFragment() instanceof MiscManagementFragment)){
                    MiscManagementFragment miscManagementFragment = new MiscManagementFragment();
                    ((FastBaseActivity)getActivity()).replaceFragment(miscManagementFragment, Tag.MISC_FRAG, false);
                }
            }
        });

        logoutWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayLogoutDialog();
            }
        });
    }
}
