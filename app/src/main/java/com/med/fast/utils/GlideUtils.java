package com.med.fast.utils;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.med.fast.MediaUtils;

public class GlideUtils {
    public static RequestOptions getDefaultRequestOptions() {
        return new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .error(MediaUtils.image_error_black)
                .placeholder(MediaUtils.image_placeholder_black);
    }
}