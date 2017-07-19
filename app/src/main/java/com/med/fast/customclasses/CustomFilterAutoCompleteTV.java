package com.med.fast.customclasses;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.med.fast.management.disease.DiseaseSearchAdapter;

/**
 * Created by Kevin murvie on 7/19/2017. FM
 */

public class CustomFilterAutoCompleteTV extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public CustomFilterAutoCompleteTV(Context context) {
        super(context);
    }

    public CustomFilterAutoCompleteTV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFilterAutoCompleteTV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, boolean filter) {
        if (Build.VERSION.SDK_INT >= 17) {
            super.setText(text, filter);
        } else {
            if (filter) {
                setText(text);
            } else {
                ListAdapter adapter = getAdapter();
                setAdapter(null);
                setText(text);
                if (adapter instanceof DiseaseSearchAdapter)
                    setAdapter((DiseaseSearchAdapter) adapter);
                else
                    setAdapter((ArrayAdapter) adapter);
                //if you use more types of Adapter you can list them here
            }
        }
    }
}
