package com.med.fast;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Created by Kevin Murvie on 7/24/2017. FM
 */

public class FontUtils {
    public static SpannableStringBuilder colorify(Context context, int colorRes, SpannableStringBuilder text) {
        SpannableStringBuilder colorSpan = new SpannableStringBuilder(text);
        colorSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorRes)),
                0, colorSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return colorSpan;
    }
    public static SpannableStringBuilder colorify(Context context, int colorRes, String text) {
        SpannableStringBuilder colorSpan = new SpannableStringBuilder(text);
        colorSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorRes)),
                0, colorSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return colorSpan;
    }
}
