package com.med.fast;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kevin Murvie on 5/18/2017. FM
 */

public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public HorizontalItemDecoration(Context context){
        this.margin = (context.getResources().getDimensionPixelSize(R.dimen.image_margin)/* /
                context.getResources().getDisplayMetrics().density*/);
    }

    public HorizontalItemDecoration(int margin){
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position > 0){
            outRect.right = margin / 2;
        } else if (position == 0) {
            outRect.left = margin;
            outRect.right = margin / 2;
        }
    }
}