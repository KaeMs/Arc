package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.api.ResponseAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class SummaryAdapter extends FastBaseRecyclerAdapter {

    private final int PROFILE = 0;
    private final int VISIT = 1;
    private final int MEDICINE = 2;
    private final int ANAMNESY = 3;
    private final int DISEASE = 4;
    private final int ALLERGY = 5;
    private final int HABITS = 6;
    private SummaryWrapperModel summaryWrapperModel;
    private Context context;

    public SummaryAdapter(Context context) {
        this.context = context;
        this.summaryWrapperModel = new SummaryWrapperModel();
    }

    public void setModel(SummaryWrapperModel data){
        this.summaryWrapperModel = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return PROFILE;
        else if (position == 1) return VISIT;
        else if (position == 2) return MEDICINE;
        else if (position == 3) return ANAMNESY;
        else if (position == 4) return DISEASE;
        else if (position == 5) return ALLERGY;
        else return HABITS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == PROFILE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else if (viewType == VISIT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else if (viewType == MEDICINE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else if (viewType == ANAMNESY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else if (viewType == DISEASE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else if (viewType == ALLERGY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_profile_card, parent, false);
            viewHolder = new ProfileVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == PROFILE) {
            ProfileVH profileVH = (ProfileVH) holder;
        } else if (getItemViewType(position) == VISIT) {

        } else if (getItemViewType(position) == MEDICINE) {
            SummaryMedicineAdapter summaryMedicineAdapter = new SummaryMedicineAdapter(context);
            summaryMedicineAdapter.addList(summaryWrapperModel.medicine);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryMedicineVH)holder).medicineRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryMedicineVH)holder).medicineRecycler.setAdapter(summaryMedicineAdapter);

        } else if (getItemViewType(position) == ANAMNESY) {

        } else if (getItemViewType(position) == DISEASE) {

        } else if (getItemViewType(position) == ALLERGY) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    class SummaryMedicineVH extends FastBaseViewHolder{

        @BindView(R.id.medicine_card_recycler)
        RecyclerView medicineRecycler;

        public SummaryMedicineVH(View v) {
            super(v);

        }
    }
}
