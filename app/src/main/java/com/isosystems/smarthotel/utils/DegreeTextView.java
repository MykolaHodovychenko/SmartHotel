package com.isosystems.smarthotel.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by NickGodov on 11.08.2015.
 */
public class DegreeTextView extends TextView {
    public DegreeTextView(Context context) {
        super(context);
    }

    public DegreeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DegreeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDegreeText(CharSequence text) {
        super.setText(text + "°C");
    }
}
