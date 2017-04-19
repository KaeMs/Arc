package com.med.fast.adapters;

import android.view.View;
import android.widget.ImageView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/19/2017. FM
 */

public class AllergyViewHolder extends FastBaseViewHolder {
    @BindView(R.id.allergy_item_agent) CustomFontTextView agent;
    @BindView(R.id.allergy_item_drugallergy) CustomFontTextView drugAllergy;
    @BindView(R.id.allergy_item_reaction) CustomFontTextView reaction;
    @BindView(R.id.allergy_item_firstexperience) CustomFontTextView firstExperience;
    @BindView(R.id.allergy_item_delete) ImageView delete;

    public AllergyViewHolder(View itemView) {
        super(itemView);
    }
}
