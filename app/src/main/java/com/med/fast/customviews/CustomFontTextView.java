package com.med.fast.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.med.fast.customclasses.FontCache;
import com.med.fast.R;

/**
 * Created by Kevin Murvie on 3/27/2017. KM
 */

public class CustomFontTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String fontFace;
        Typeface customFont;

        try {
            fontFace = a.getString(R.styleable.CustomFontTextView_fontFace);
        } finally {
            a.recycle();
        }

        if (fontFace != null) {
            customFont = FontCache.getTypeface(fontFace, context);
            setTypeface(customFont);
        }
    }
}
