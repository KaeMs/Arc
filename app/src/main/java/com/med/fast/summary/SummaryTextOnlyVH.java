package com.med.fast.summary;

import android.view.View;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17. FM
 */

public class SummaryTextOnlyVH extends FastBaseViewHolder {
    @BindView(R.id.summary_textonly_text)
    CustomFontTextView summaryText;

    public SummaryTextOnlyVH(View itemView) {
        super(itemView);
    }
}
