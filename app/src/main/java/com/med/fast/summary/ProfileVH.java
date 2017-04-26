package com.med.fast.summary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import butterknife.BindView;

/**
 * Created by kevindreyar on 23-Apr-17.
 */

public class ProfileVH extends FastBaseViewHolder {
    @BindView(R.id.summary_profile_photo)
    ImageView profilePhoto;
    @BindView(R.id.summary_profile_name)
    TextView profileName;

    @BindView(R.id.summary_profile_dob)
    TextView profileDob;
    @BindView(R.id.summary_profile_gender)
    TextView profileGender;

    public ProfileVH(View itemView) {
        super(itemView);
    }
}
