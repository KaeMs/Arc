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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 07-May-17. FM
 */

public class SummaryAnamnesyAdapter extends FastBaseRecyclerAdapter{
    private Context context;
    private List<DiseaseModel> mDataset = new ArrayList<>();

    public SummaryAnamnesyAdapter(Context context){
        //super(false);
        this.context = context;
    }

    public void addList(List<DiseaseModel> dataset){
        this.mDataset.addAll(dataset);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_medicine_card_text, parent, false);
        return new AnamnesyVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AnamnesyVH anamnesyVH = (AnamnesyVH)holder;

        SpannableStringBuilder sb = new SpannableStringBuilder();

        // Append disease Name
        String anamnesyString = context.getString(R.string.anamnesy_colon);
        sb.append(anamnesyString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - anamnesyString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getDisease_name());

        // Append Hereditiary Carrier
        String formString = context.getString(R.string.anamnesy_heriditiary_carrier);
        sb.append(formString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - formString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getDisease_hereditary_carrier());

        anamnesyVH.summaryAnamnesyText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
