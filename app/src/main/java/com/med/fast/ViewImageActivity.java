package com.med.fast;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.Transition;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.med.fast.api.APIConstants;

import butterknife.BindView;
import ooo.oxo.library.widget.TouchImageView;

/**
 * Created by Kevin Murvie on 5/18/2017. FM
 */

public class ViewImageActivity extends FastBaseActivity {
    public static String IMAGE_PATH_EXTRA = "IMAGE_PATH_EXTRA";
    @BindView(R.id.view_image_wrapper)
    LinearLayout imageWrapper;
    @BindView(R.id.view_image_image_view)
    TouchImageView image;
    private String imagePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_activity_layout);
        if (getIntent() != null) {
            imagePath = getIntent().getStringExtra(IMAGE_PATH_EXTRA);
            supportPostponeEnterTransition();

            Glide.with(this)
                    .load(APIConstants.WEB_URL + imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .error(MediaUtils.image_error_black)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(image);

            /*supportPostponeEnterTransition();
            imageWrapper.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            imageWrapper.getViewTreeObserver().removeOnPreDrawListener(this);
                            supportStartPostponedEnterTransition();
                            return true;
                        }
                    }
            );*/
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
