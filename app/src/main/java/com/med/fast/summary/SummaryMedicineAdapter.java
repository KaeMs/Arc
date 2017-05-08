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
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class SummaryMedicineAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<MedicineModel> mDataset = new ArrayList<>();

    public SummaryMedicineAdapter(Context context){
        this.context = context;
    }

    public void addList(List<MedicineModel> dataset){
        this.mDataset.addAll(dataset);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_medicine_card_text, parent, false);
            return new MedicineVH(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MedicineVH medicineVH = (MedicineVH)holder;

        SpannableStringBuilder sb = new SpannableStringBuilder();

        // Append Medicine
        String medicineString = context.getString(R.string.medicine_colon);
        sb.append(medicineString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - medicineString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getMedicine_name());

        // Append Form
        String formString = context.getString(R.string.medicine_form_colon);
        sb.append(formString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - formString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getMedicine_form());

        // Append Route
        String routeString = context.getString(R.string.medicine_route_colon);
        sb.append(routeString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - routeString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getMedicine_route());

        // Append Dose
        String doseString = context.getString(R.string.medicine_dose_colon);
        sb.append(doseString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - doseString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getMedicine_dose());

        // Append Frequency
        String frequencyString = context.getString(R.string.medicine_frequency_colon);
        sb.append(frequencyString);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), sb.length() - frequencyString.length(), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" ");
        sb.append(mDataset.get(position).getMedicine_frequency());

        medicineVH.summaryMedicineText.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
