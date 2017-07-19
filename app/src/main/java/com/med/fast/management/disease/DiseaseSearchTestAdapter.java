package com.med.fast.management.disease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.med.fast.databinding.ManagementDiseaseSearchItemBindBinding;

import java.util.Comparator;

/**
 * Created by GMG-Developer on 7/18/2017.
 */

public class DiseaseSearchTestAdapter extends SortedListAdapter<DiseaseModelTest> {

    public DiseaseSearchTestAdapter(Context context, Comparator<DiseaseModelTest> comparator) {
        super(context, DiseaseModelTest.class, comparator);
    }

    @Override
    protected ViewHolder<? extends DiseaseModelTest> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        final ManagementDiseaseSearchItemBindBinding binding = ManagementDiseaseSearchItemBindBinding.inflate(inflater, parent, false);
        return new DiseaseTestVH(binding);
    }
}