package com.med.fast.management.visit;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<VisitModel> mDataset = new ArrayList<>();

    public VisitAdapter(Context context){
        this.context = context;
    }

    public void addList(List<VisitModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_visit_card, parent, false);
        return new VisitViewHolder(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VisitViewHolder visitViewHolder = (VisitViewHolder)holder;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        visitViewHolder.visitDate.setText(simpleDateFormat.format(mDataset.get(position).getCreated_date()));

        visitViewHolder.hospitalName.setText(mDataset.get(position).getHospital_name());
        visitViewHolder.doctorName.setText(mDataset.get(position).getDoctor_name());
        visitViewHolder.diagnose.setText(mDataset.get(position).getDiagnose());
        visitViewHolder.diagnosedDisease.setText(mDataset.get(position).getDisease());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        VisitImageAdapter visitImageAdapter = new VisitImageAdapter(context);
        SnapHelper snapHelper = new PagerSnapHelper();
        visitViewHolder.imageRecycler.setLayoutManager(linearLayoutManager);
        visitViewHolder.imageRecycler.setAdapter(visitImageAdapter);
        snapHelper.attachToRecyclerView(visitViewHolder.imageRecycler);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class VisitViewHolder extends FastBaseViewHolder {

        @BindView(R.id.visit_card_date)
        CustomFontTextView visitDate;
        @BindView(R.id.visit_card_hospital_name)
        CustomFontTextView hospitalName;
        @BindView(R.id.visit_card_doctor_name)
        CustomFontTextView doctorName;
        @BindView(R.id.visit_card_diagnose)
        CustomFontTextView diagnose;
        @BindView(R.id.visit_card_diagnosed_diseases)
        CustomFontTextView diagnosedDisease;
        @BindView(R.id.visit_card_images)
        RecyclerView imageRecycler;
        @BindView(R.id.visit_card_edit)
        ImageView editBtn;
        @BindView(R.id.visit_card_delete)
        ImageView deleteBtn;


        public VisitViewHolder(View view) {
            super(view);

        }
    }
}
