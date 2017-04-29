package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.med.fast.FastBaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class SummaryMedicineAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<MedicineModel> mDataset = new ArrayList<>();

    public SummaryMedicineAdapter(Context context){
        super(false);
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MedicineVH medicineVH = (MedicineVH)holder;
        medicineVH.summaryMedicineText.setText(mDataset.get(position).getMedicine_name());

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
