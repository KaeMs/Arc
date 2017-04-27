package com.med.fast.viewholders;

import android.view.View;
import android.widget.ProgressBar;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/27/2017. FM
 */

public class InfiScrollProgressVH extends FastBaseViewHolder {
    @BindView(R.id.infscroll_progressbar)
    public ProgressBar progressBar;
    @BindView(R.id.infscroll_text)
    public CustomFontTextView failTxt;

    public InfiScrollProgressVH(View v) {
        super(v);
    }

    public void setFailLoad(boolean failLoad){
        if (failLoad){
            failTxt.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            failTxt.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
