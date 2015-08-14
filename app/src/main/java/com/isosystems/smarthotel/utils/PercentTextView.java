package com.isosystems.smarthotel.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by NickGodov on 11.08.2015.
 */
public class PercentTextView extends TextView {
    public PercentTextView(Context context) {
        super(context);
    }

    public PercentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPercentText(CharSequence text) {
        super.setText(text + "%");
    }
}
