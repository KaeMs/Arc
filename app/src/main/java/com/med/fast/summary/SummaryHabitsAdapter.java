package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
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

    public void addList(List<MiscModel> dataset){
        this.mDataset.addAll(dataset);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_medicine_card_text, parent, false);
        return new HabitsVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HabitsVH habitsVH = (HabitsVH)holder;
        SpannableStringBuilder sb = new SpannableStringBuilder();

        // Append Habits
//        String habitString = context.getString(R.string.medicine_colon);
//        sb.append(habitString);
//        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - habitString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sb.append(" ");
        sb.append(mDataset.get(position).getVoluptuary_habit());

        habitsVH.summaryHabitsText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
