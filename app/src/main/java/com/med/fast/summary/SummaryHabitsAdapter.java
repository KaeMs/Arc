package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.management.misc.MiscModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 07-May-17. FM
 */

public class SummaryHabitsAdapter extends FastBaseRecyclerAdapter{
    private Context context;
    private List<MiscModel> mDataset = new ArrayList<>();

    public SummaryHabitsAdapter(Context context){
        super(false);
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HabitsVH habitsVH = (HabitsVH)holder;
        habitsVH.summaryHabitsText.setText(mDataset.get(position).getVoluptuary_habit());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
