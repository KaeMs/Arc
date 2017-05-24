package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.HorizontalItemDecoration;
import com.med.fast.R;
import com.med.fast.management.visit.VisitImageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevindreyar on 07-May-17. FM
 */

public class SummaryVisitAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<VisitModel> mDataset = new ArrayList<>();
    private int width;
    private HorizontalItemDecoration horizontalItemDecoration;

    public SummaryVisitAdapter(Context context) {
        this.context = context;
        horizontalItemDecoration = new HorizontalItemDecoration(context);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void addList(List<VisitModel> dataset) {
        this.mDataset.addAll(dataset);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_visit_viewholder, parent, false);
        return new SummaryVisitVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SummaryVisitVH summaryVisitVH = (SummaryVisitVH) holder;

        SpannableStringBuilder sb = new SpannableStringBuilder();

        // Append Date
        String dateString = context.getString(R.string.visit_date);
        sb.append(dateString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - dateString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getCreated_date());
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

        if (mDataset.get(position).getImage_list().size() > 0) {
            summaryVisitVH.recyclerView.setVisibility(View.VISIBLE);
            summaryVisitVH.recyclerView.setOnFlingListener(null);
            SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
            snapHelper.attachToRecyclerView(summaryVisitVH.recyclerView);

            VisitImageAdapter visitImageAdapter = new VisitImageAdapter(context, width);
            visitImageAdapter.addList(mDataset.get(position).getImage_list());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

            setItemDecoration(summaryVisitVH.recyclerView, horizontalItemDecoration);
            summaryVisitVH.recyclerView.setLayoutManager(linearLayoutManager);
            summaryVisitVH.recyclerView.setAdapter(visitImageAdapter);

            // Append Image
            String imageString = context.getString(R.string.visit_image);
            sb.append(imageString);
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - imageString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            sb.append("\n");
            //sb.append(mDataset.get(position).getMedicine_dose());
        } else {
            summaryVisitVH.recyclerView.setVisibility(View.GONE);
        }

        summaryVisitVH.summaryText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
