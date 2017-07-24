package com.med.fast.customclasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Kevin Murvie on 7/24/2017. FM
 */

public class EfficientCoordinatorLayout extends CoordinatorLayout {

    private final NestedScrollingChildHelper nestedScrollingChildHelper;

    public EfficientCoordinatorLayout(@NonNull Context context) {
        this(context, null);
    }

    public EfficientCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EfficientCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    /**
     * The same as super.offsetChildToAnchor, except use less CPU time if the child is "gone".
     * In this case, super.offsetChildToAnchor may cause several views to be redrawn.
     * This can happen every display frame, so the app hogs CPU time, repeatedly redrawing the same views.
     * This was reported as https://code.google.com/p/android/issues/detail?id=196879
     */
    /*@Override
    void offsetChildToAnchor(@NonNull View child, int layoutDirection) {
        // Don't waste CPU time:
        if (child.getVisibility() != View.GONE) {
            super.offsetChildToAnchor(child, layoutDirection);
        }
    }*/

    public void setShouldConsumeAndForwardScrollEvents(boolean shouldConsumeAndForwardScrollEvents) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(shouldConsumeAndForwardScrollEvents);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        boolean shouldAcceptNestedScroll = super.onStartNestedScroll(child, target, nestedScrollAxes);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            shouldAcceptNestedScroll |= nestedScrollingChildHelper.startNestedScroll(nestedScrollAxes);
        }

        return shouldAcceptNestedScroll;
    }

    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            nestedScrollingChildHelper.onStopNestedScroll(target);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, null);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        boolean didConsumeFling = super.onNestedFling(target, velocityX, velocityY, consumed);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            didConsumeFling |= nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
        }

        return didConsumeFling;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean didConsumePreFling = super.onNestedPreFling(target, velocityX, velocityY);

        if (nestedScrollingChildHelper.isNestedScrollingEnabled()) {
            didConsumePreFling |= nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
        }

        return didConsumePreFling;
    }
}