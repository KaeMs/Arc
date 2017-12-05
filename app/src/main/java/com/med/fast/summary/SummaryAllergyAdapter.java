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
import com.med.fast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 07-May-17. FM
 */

public class SummaryAllergyAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<AllergyModel> mDataset = new ArrayList<>();

    public SummaryAllergyAdapter(Context context){
        //super(false);
        this.context = context;
    }

    public void addList(List<AllergyModel> dataset){
        this.mDataset.addAll(dataset);
    }

    public void addSingle(AllergyModel allergyModel) {
        this.mDataset.add(allergyModel);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_textonly, parent, false);
        return new SummaryTextOnlyVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SummaryTextOnlyVH summaryTextOnlyVH = (SummaryTextOnlyVH)holder;

        SpannableStringBuilder sb = new SpannableStringBuilder();

        if (mDataset.get(position) != null){
            // Append ALlergy Name
            String visitString = context.getString(R.string.allergy_agent_colon);
            sb.append(visitString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - visitString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append(mDataset.get(position).getAgent());
            sb.append("\n");

            // Append Allergy Reaction
            String formString = context.getString(R.string.allergy_reaction_colon);
            sb.append(formString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - formString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append(mDataset.get(position).getReaction());

            if(position != getItemCount() - 1){
                sb.append("\n");
            }
        } else {
            sb.append(context.getString(R.string.allergy_not_found));
        }
        summaryTextOnlyVH.summaryText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}