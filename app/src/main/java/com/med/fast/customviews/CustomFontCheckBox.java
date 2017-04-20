package com.med.fast.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.med.fast.customclasses.FontCache;
import com.med.fast.R;

/**
 * Created by Kevin Murvie on 4/10/2017. Fa
 */

public class CustomFontCheckBox extends AppCompatCheckBox {

    public CustomFontCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomFontCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontCheckBox);
        String fontFace = a.getString(R.styleable.CustomFontCheckBox_cbFontFace);

        try {
            if (fontFace != null) {
                Typeface customFont = FontCache.getTypeface(fontFace, context);
                setTypeface(customFont);
            }
        } finally {
            a.recycle();
        }
    }
}
