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

public class SummaryDiseaseAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<DiseaseModel> mDataset = new ArrayList<>();

    public SummaryDiseaseAdapter(Context context) {
        //super(false);
        this.context = context;
    }

    public void addList(List<DiseaseModel> dataset) {
        this.mDataset.addAll(dataset);
    }

    public void addSingle(DiseaseModel diseaseModel) {
        this.mDataset.add(diseaseModel);
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
        SummaryTextOnlyVH summaryTextOnlyVH = (SummaryTextOnlyVH) holder;

        SpannableStringBuilder sb = new SpannableStringBuilder();
        if (mDataset.get(position) != null) {
            // Append Disease Name
            String diseaseString = context.getString(R.string.disease_colon);
            sb.append(diseaseString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - diseaseString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append(mDataset.get(position).getName());
            sb.append("\n");

            // Append Last Visit
            String formString = context.getString(R.string.disease_last_visit);
            sb.append(formString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - formString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append(mDataset.get(position).getLast_visit());
            sb.append("\n");

            // Append Date
            String routeString = context.getString(R.string.disease_date);
            sb.append(routeString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - routeString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append(mDataset.get(position).getHistoric_date());

            if(position != getItemCount() - 1){
                sb.append("\n");
            }
        } else {
            sb.append(context.getString(R.string.ongoing_disease_not_found));
        }

        summaryTextOnlyVH.summaryText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
