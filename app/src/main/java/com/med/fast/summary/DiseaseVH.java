package com.med.fast.summary;

import android.view.View;
import android.widget.TextView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17.
 */

public class DiseaseVH extends FastBaseViewHolder {
    @BindView(R.id.summary_medicine_text)
    TextView summaryDiseaseText;

    public DiseaseVH(View itemView) {
        super(itemView);
    }
}
