package com.med.fast.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.med.fast.FontCache;
import com.med.fast.R;

/**
 * Created by Kevin Murvie on 4/10/2017. Fas
 */

public class CustomFontRadioButton extends AppCompatRadioButton{

    public CustomFontRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomFontRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontRadioButton);
        String fontFace = a.getString(R.styleable.CustomFontRadioButton_rbFontFace);

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
