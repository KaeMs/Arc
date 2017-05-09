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

public class SummaryVisitAdapter extends FastBaseRecyclerAdapter{
    private Context context;
    private List<VisitModel> mDataset = new ArrayList<>();

    public SummaryVisitAdapter(Context context){
        this.context = context;
    }

    public void addList(List<VisitModel> dataset){
        this.mDataset.addAll(dataset);
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

        // Append Date
        String dateString = context.getString(R.string.visit_date);
        sb.append(dateString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - dateString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getCreated_date().toString());
        sb.append("\n");

        // Append Hospital
        String hospitalString = context.getString(R.string.visit_place);
        sb.append(hospitalString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - hospitalString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getHospital_name());
        sb.append("\n");

        // Append Doctor
        String doctorString = context.getString(R.string.visit_doctor);
        sb.append(doctorString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - doctorString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getDoctor_name());
        sb.append("\n");

        // Append Image
        String imageString = context.getString(R.string.visit_image);
        sb.append(imageString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - imageString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append("\n");
        //sb.append(mDataset.get(position).getMedicine_dose());

        summaryTextOnlyVH.summaryText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}