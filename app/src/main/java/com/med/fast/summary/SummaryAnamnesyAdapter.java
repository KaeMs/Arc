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

public class SummaryAnamnesyAdapter extends FastBaseRecyclerAdapter{
    private Context context;
    private List<DiseaseModel> mDataset = new ArrayList<>();

    public SummaryAnamnesyAdapter(Context context){
        super(false);
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AnamnesyVH anamnesyVH = (AnamnesyVH)holder;
        anamnesyVH.summaryAnamnesyText.setText(mDataset.get(position).getDisease_name());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
