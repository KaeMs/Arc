package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.med.fast.FastBaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 07-May-17. FM
 */

public class SummaryAllergyAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<AllergyModel> mDataset = new ArrayList<>();

    public SummaryAllergyAdapter(Context context){
        super(false);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AllergyVH allergyVH = (AllergyVH)holder;
        allergyVH.summaryAllergyText.setText(mDataset.get(position).getAllergy_agent());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
