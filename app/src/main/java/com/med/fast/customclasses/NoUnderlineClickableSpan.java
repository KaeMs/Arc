package com.med.fast.customclasses;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Kevin Murvie on 4/12/2017. FM
 */

public abstract class NoUnderlineClickableSpan extends ClickableSpan {
    public abstract void onClick(View tv);

    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }
}
