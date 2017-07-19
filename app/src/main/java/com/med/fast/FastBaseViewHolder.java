package com.med.fast;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Kevin Murvie on 4/19/2017. FM
 */

public abstract class FastBaseViewHolder extends RecyclerView.ViewHolder {
    public FastBaseViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
