package com.med.fast.summary;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17. FM
 */

public class SummaryVisitVH extends FastBaseViewHolder {
    @BindView(R.id.summary_visit_vh_text)
    CustomFontTextView summaryText;
    @BindView(R.id.summary_visit_vh_recycler)
    RecyclerView recyclerView;

    public SummaryVisitVH(View itemView) {
        super(itemView);
    }
}
