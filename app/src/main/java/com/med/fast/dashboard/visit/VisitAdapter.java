package com.med.fast.dashboard.visit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitAdapter extends FastBaseRecyclerAdapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_visit_card, parent, false);
        return new VisitViewHolder(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VisitViewHolder visitViewHolder = (VisitViewHolder)holder;


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private static class VisitViewHolder extends FastBaseViewHolder{

        @BindView(R.id.visit_card_date)
        CustomFontTextView visitDate;
        @BindView(R.id.visit_card_hospital_name)
        CustomFontTextView hospitalName;
        @BindView(R.id.visit_card_doctor_name)
        CustomFontTextView doctorName;
        @BindView(R.id.visit_card_diagnosed_diseases)
        CustomFontTextView diagnosedDisease;

        @BindView(R.id.visit_card_edit)
        ImageView editBtn;
        @BindView(R.id.visit_card_delete)
        ImageView deleteBtn;


        public VisitViewHolder(View view) {
            super(view);

        }
    }
}