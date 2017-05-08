package com.med.fast.summary;

import android.view.View;
import android.widget.TextView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17. FM
 */

public class VisitVH extends FastBaseViewHolder {
    @BindView(R.id.summary_visit_text)
    CustomFontTextView summaryVisitText;

    public VisitVH(View itemView) {
        super(itemView);
    }
}
