package com.med.fast.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import com.med.fast.customclasses.FontCache;
import com.med.fast.R;

/**
 * Created by Kevin Murvie on 3/27/2017. KM
 */

public class CustomFontTextInputEditText extends TextInputEditText {

    public CustomFontTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomFontTextInputEditText(Context context, AttributeSet attrs, int defStyle) {
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
