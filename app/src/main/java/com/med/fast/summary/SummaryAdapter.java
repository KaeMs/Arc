package com.med.fast.summary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.api.APIConstants;
import com.med.fast.customviews.CustomFontTextView;

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
    private int itemCount = 0;

    public SummaryAdapter(Context context) {
        this.context = context;
        this.summaryWrapperModel = new SummaryWrapperModel();
    }

    public void setModel(SummaryWrapperModel data){
        this.summaryWrapperModel = data;
        itemCount = 7;
        notifyDataSetChanged();
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
                    .inflate(R.layout.summary_recycler_card, parent, false);
            viewHolder = new SummaryRecyclerVH(view);
        } else if (viewType == MEDICINE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_recycler_card, parent, false);
            viewHolder = new SummaryRecyclerVH(view);
        } else if (viewType == ANAMNESY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_recycler_card, parent, false);
            viewHolder = new SummaryRecyclerVH(view);
        } else if (viewType == DISEASE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_recycler_card, parent, false);
            viewHolder = new SummaryRecyclerVH(view);
        } else if (viewType == ALLERGY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_recycler_card, parent, false);
            viewHolder = new SummaryRecyclerVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.summary_habits_card, parent, false);
            viewHolder = new SummaryHabitVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == PROFILE) {
            ((ProfileVH)holder).summaryTitle.setText(context.getString(R.string.your_profile));

            Glide.with(context)
                    .load(APIConstants.WEB_URL + summaryWrapperModel.profil_image_path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .placeholder(MediaUtils.image_placeholder_black)
                    .error(MediaUtils.image_error_black)
                    .into(((ProfileVH)holder).profilePhoto);

        } else if (getItemViewType(position) == VISIT) {
            SummaryVisitAdapter summaryVisitAdapter = new SummaryVisitAdapter(context);
            summaryVisitAdapter.addList(summaryWrapperModel.visit);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH)holder).summaryTitle.setText(context.getString(R.string.recent_visits));
            ((SummaryRecyclerVH)holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH)holder).summaryRecycler.setAdapter(summaryVisitAdapter);
        } else if (getItemViewType(position) == MEDICINE) {
            SummaryMedicineAdapter summaryMedicineAdapter = new SummaryMedicineAdapter(context);
            summaryMedicineAdapter.addList(summaryWrapperModel.medicine);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH)holder).summaryTitle.setText(context.getString(R.string.medicines));
            ((SummaryRecyclerVH)holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH)holder).summaryRecycler.setAdapter(summaryMedicineAdapter);

        } else if (getItemViewType(position) == ANAMNESY) {
            SummaryAnamnesyAdapter summaryAnamnesyAdapter = new SummaryAnamnesyAdapter(context);
            summaryAnamnesyAdapter.addList(summaryWrapperModel.family_anamnesy);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH)holder).summaryTitle.setText(context.getString(R.string.family_anamesy));
            ((SummaryRecyclerVH)holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH)holder).summaryRecycler.setAdapter(summaryAnamnesyAdapter);
        } else if (getItemViewType(position) == DISEASE) {
            SummaryDiseaseAdapter summaryDiseaseAdapter = new SummaryDiseaseAdapter(context);
            summaryDiseaseAdapter.addList(summaryWrapperModel.disease);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH)holder).summaryTitle.setText(context.getString(R.string.ongoing_disease));
            ((SummaryRecyclerVH)holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH)holder).summaryRecycler.setAdapter(summaryDiseaseAdapter);
        } else if (getItemViewType(position) == ALLERGY) {
            SummaryAllergyAdapter summaryAllergyAdapter = new SummaryAllergyAdapter(context);
            summaryAllergyAdapter.addList(summaryWrapperModel.allergies);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH)holder).summaryTitle.setText(context.getString(R.string.drug_allergies));
            ((SummaryRecyclerVH)holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH)holder).summaryRecycler.setAdapter(summaryAllergyAdapter);
        } else {
            ((SummaryHabitVH)holder).summaryTitle.setText(context.getString(R.string.voluptuary_habits));

            if (summaryWrapperModel.voluptary_habits != null &&
                    summaryWrapperModel.voluptary_habits.equals("")) ((SummaryHabitVH)holder).habitsTxt.setText(summaryWrapperModel.voluptary_habits);
            else ((SummaryHabitVH)holder).habitsTxt.setText(context.getString(R.string.voluptuary_habits_not_found));
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    class SummaryRecyclerVH extends FastBaseViewHolder{
        @BindView(R.id.summary_header_title)
        CustomFontTextView summaryTitle;
        @BindView(R.id.summary_adapter_recycler)
        RecyclerView summaryRecycler;

        public SummaryRecyclerVH(View v) {
            super(v);

        }
    }

    class SummaryHabitVH extends FastBaseViewHolder{

        @BindView(R.id.summary_header_title)
        CustomFontTextView summaryTitle;
        @BindView(R.id.habits_card_text)
        CustomFontTextView habitsTxt;

        public SummaryHabitVH(View v) {
            super(v);

        }
    }
}
