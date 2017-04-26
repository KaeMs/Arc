package com.med.fast.summary;

import android.view.View;
import android.widget.TextView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17.
 */

public class AnamnesyVH extends FastBaseViewHolder {
    @BindView(R.id.summary_anamnesy_text)
    TextView summaryAnamnesyText;

    public AnamnesyVH(View itemView) {
        super(itemView);
    }
}
