package com.med.fast.summary;

import android.view.View;
import android.widget.ImageView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class ProfileVH extends FastBaseViewHolder {
    @BindView(R.id.summary_header_title)
    CustomFontTextView summaryTitle;
    @BindView(R.id.summary_profile_photo)
    ImageView profilePhoto;
    @BindView(R.id.summary_profile_name)
    CustomFontTextView profileName;

    @BindView(R.id.summary_profile_dob)
    CustomFontTextView profileDob;
    @BindView(R.id.summary_profile_gender)
    CustomFontTextView profileGender;

    public ProfileVH(View itemView) {
        super(itemView);
    }
}
