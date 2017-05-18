package com.med.fast.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/18/2017. FM
 */

public class GeneralImageVH extends FastBaseViewHolder {
    @BindView(R.id.visit_card_image_wrapper)
    public LinearLayout imageWrapper;
    @BindView(R.id.visit_card_image_image_view)
    public ImageView image;

    public GeneralImageVH(View itemView) {
        super(itemView);
    }
}
