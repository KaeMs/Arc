package com.med.fast.summary;

import android.view.View;
import android.widget.TextView;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import butterknife.BindView;

/**
 * Created by kevindreyar on 26-Apr-17.
 */

public class HabitsVH extends FastBaseViewHolder {
    @BindView(R.id.summary_habits_text)
    TextView summaryHabitsText;

    public HabitsVH(View itemView) {
        super(itemView);
    }
}
