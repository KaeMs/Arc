package com.med.fast.summary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.Tag;
import com.med.fast.api.APIConstants;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.AllergyManagementFragment;
import com.med.fast.management.disease.DiseaseManagementFragment;
import com.med.fast.management.medicine.MedicineManagementFragment;
import com.med.fast.management.misc.MiscManagementFragment;
import com.med.fast.management.visit.VisitFragment;
import com.med.fast.setting.SettingProfileActivity;

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
    private int width;

    public SummaryAdapter(Context context) {
        this.context = context;
        this.summaryWrapperModel = new SummaryWrapperModel();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setModel(SummaryWrapperModel data) {
        this.summaryWrapperModel = data;
        notifyDataSetChanged();
        itemCount = 7;
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
            ((ProfileVH) holder).summaryTitle.setText(context.getString(R.string.your_profile));
            ((ProfileVH) holder).summarySetting.setVisibility(View.VISIBLE);
            ((ProfileVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SettingProfileActivity.class);
                    context.startActivity(intent);
                }
            });

            Glide.with(context)
                    .load(APIConstants.WEB_URL + summaryWrapperModel.profil_image_path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .placeholder(MediaUtils.image_placeholder_black)
                    .error(MediaUtils.image_error_black)
                    .into(((ProfileVH) holder).profilePhoto);

            ((ProfileVH) holder).profileName.setText(summaryWrapperModel.name);
            ((ProfileVH) holder).profileDob.setText(summaryWrapperModel.date_of_birth);
            ((ProfileVH) holder).profileGender.setText(summaryWrapperModel.gender);
            holder.itemView.setOnClickListener(null);
        } else if (getItemViewType(position) == VISIT) {
            SummaryVisitAdapter summaryVisitAdapter = new SummaryVisitAdapter(context);
            summaryVisitAdapter.setWidth(width);
            summaryVisitAdapter.addList(summaryWrapperModel.visit);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH) holder).summaryTitle.setText(context.getString(R.string.recent_visits));
            ((SummaryRecyclerVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryRecyclerVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((SummaryRecyclerVH) holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH) holder).summaryRecycler.setAdapter(summaryVisitAdapter);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VisitFragment visitFragment = new VisitFragment();
                    ((FastBaseActivity) context).replaceFragment(visitFragment, Tag.VISIT_FRAG, false);
                }
            });
        } else if (getItemViewType(position) == MEDICINE) {
            SummaryMedicineAdapter summaryMedicineAdapter = new SummaryMedicineAdapter(context);
            summaryMedicineAdapter.addList(summaryWrapperModel.medicine);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH) holder).summaryTitle.setText(context.getString(R.string.medicines));
            ((SummaryRecyclerVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryRecyclerVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((SummaryRecyclerVH) holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH) holder).summaryRecycler.setAdapter(summaryMedicineAdapter);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MedicineManagementFragment medicineManagementFragment = new MedicineManagementFragment();
                    ((FastBaseActivity) context).replaceFragment(medicineManagementFragment, Tag.MEDICINE_FRAG, false);
                }
            });
        } else if (getItemViewType(position) == ANAMNESY) {
            SummaryAnamnesyAdapter summaryAnamnesyAdapter = new SummaryAnamnesyAdapter(context);
            summaryAnamnesyAdapter.addList(summaryWrapperModel.family_anamnesy);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH) holder).summaryTitle.setText(context.getString(R.string.family_anamesy));
            ((SummaryRecyclerVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryRecyclerVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((SummaryRecyclerVH) holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH) holder).summaryRecycler.setAdapter(summaryAnamnesyAdapter);
            holder.itemView.setOnClickListener(null);
        } else if (getItemViewType(position) == DISEASE) {
            SummaryDiseaseAdapter summaryDiseaseAdapter = new SummaryDiseaseAdapter(context);
            summaryDiseaseAdapter.addList(summaryWrapperModel.disease);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH) holder).summaryTitle.setText(context.getString(R.string.ongoing_disease));
            ((SummaryRecyclerVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryRecyclerVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((SummaryRecyclerVH) holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH) holder).summaryRecycler.setAdapter(summaryDiseaseAdapter);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiseaseManagementFragment diseaseManagementFragment = new DiseaseManagementFragment();
                    ((FastBaseActivity) context).replaceFragment(diseaseManagementFragment, Tag.DISEASE_FRAG, false);
                }
            });
        } else if (getItemViewType(position) == ALLERGY) {
            SummaryAllergyAdapter summaryAllergyAdapter = new SummaryAllergyAdapter(context);
            summaryAllergyAdapter.addList(summaryWrapperModel.allergies);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            ((SummaryRecyclerVH) holder).summaryTitle.setText(context.getString(R.string.drug_allergies));
            ((SummaryRecyclerVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryRecyclerVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((SummaryRecyclerVH) holder).summaryRecycler.setLayoutManager(linearLayoutManager);
            ((SummaryRecyclerVH) holder).summaryRecycler.setAdapter(summaryAllergyAdapter);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AllergyManagementFragment allergyManagementFragment = new AllergyManagementFragment();
                    ((FastBaseActivity) context).replaceFragment(allergyManagementFragment, Tag.ALLERGY_FRAG, false);
                }
            });
        } else {
            ((SummaryHabitVH) holder).summaryTitle.setText(context.getString(R.string.voluptuary_habits));
            ((SummaryHabitVH) holder).summarySetting.setVisibility(View.GONE);
            ((SummaryHabitVH) holder).summarySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (summaryWrapperModel.voluptary_habits != null &&
                    summaryWrapperModel.voluptary_habits.equals(""))
                ((SummaryHabitVH) holder).habitsTxt.setText(summaryWrapperModel.voluptary_habits);
            else
                ((SummaryHabitVH) holder).habitsTxt.setText(context.getString(R.string.voluptuary_habits_not_found));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MiscManagementFragment miscManagementFragment = new MiscManagementFragment();
                    ((FastBaseActivity) context).replaceFragment(miscManagementFragment, Tag.MISC_FRAG, false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    class SummaryRecyclerVH extends FastBaseViewHolder {
        @BindView(R.id.summary_header_title)
        CustomFontTextView summaryTitle;
        @BindView(R.id.summary_header_setting)
        ImageView summarySetting;
        @BindView(R.id.summary_adapter_recycler)
        RecyclerView summaryRecycler;

        public SummaryRecyclerVH(View v) {
            super(v);

        }
    }

    class SummaryHabitVH extends FastBaseViewHolder {

        @BindView(R.id.summary_header_title)
        CustomFontTextView summaryTitle;
        @BindView(R.id.summary_header_setting)
        ImageView summarySetting;
        @BindView(R.id.habits_card_text)
        CustomFontTextView habitsTxt;

        public SummaryHabitVH(View v) {
            super(v);

        }
    }
}
