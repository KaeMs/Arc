package com.med.fast.management.disease;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.med.fast.databinding.ManagementDiseaseSearchItemBindBinding;

/**
 * Created by GMG-Developer on 7/17/2017.
 */

public class DiseaseTestVH extends SortedListAdapter.ViewHolder<DiseaseModelTest>  {

    private final ManagementDiseaseSearchItemBindBinding mBinding;

    public DiseaseTestVH(ManagementDiseaseSearchItemBindBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(DiseaseModelTest diseaseModelTest) {
        mBinding.setModel(diseaseModelTest);
    }
}
