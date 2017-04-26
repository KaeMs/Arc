package com.med.fast;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/26/2017. FM
 */

public class ImagePlaceholderVH extends FastBaseViewHolder {
    @BindView(R.id.image_placeholder_wrapper)
    public LinearLayout placeholderWrapper;
    @BindView(R.id.image_placeholder_imageview)
    public ImageView placeholderImage;

    public ImagePlaceholderVH(View v) {
        super(v);
    }
}
