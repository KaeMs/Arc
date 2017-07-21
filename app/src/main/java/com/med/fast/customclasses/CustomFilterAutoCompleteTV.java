package com.med.fast.customclasses;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.med.fast.management.disease.DiseaseSearchAdapter;

/**
 * Created by Kevin murvie on 7/19/2017. FM
 */

public class CustomFilterAutoCompleteTV extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /** no-op */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /** no-op */
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSelectionFromPopUp = false;
            }
        });
    }

    /**
     * A true value indicates that the user selected a suggested completion
     * from the popup, false otherwise.
     * @see #replaceText(CharSequence)
     */
    private boolean mSelectionFromPopUp;

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
    public boolean enoughToFilter() {
        /**
         * There is no filtering standard; it is always enough to filter.
         */
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getAdapter() != null) {
            performFiltering(getText(), 0);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void replaceText(CharSequence text) {
        super.replaceText(text);
        /**
         * The user selected an item from the suggested completion list.
         * The selection got converted to String, and replaced the whole content
         * of the edit box.
         */
        mSelectionFromPopUp = true;
    }

    public boolean isSelectionFromPopUp() {
        return mSelectionFromPopUp;
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
