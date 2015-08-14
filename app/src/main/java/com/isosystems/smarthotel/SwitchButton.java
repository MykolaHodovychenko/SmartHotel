package com.isosystems.smarthotel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;

public class SwitchButton extends ImageButton {

    Context mContext;
    MyApplication mApplication;

    int inactiveImage;
    int activeImage;
    int unknownImage;

    public Boolean buttonPressed = false;

    public void setImageResources(int active_image, int inactive_image, int unknown_image) {
        activeImage = active_image;
        inactiveImage = inactive_image;
        unknownImage = unknown_image;
    }

    public SwitchButton(Context context) {
        super(context);
        mContext = context;
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void changeButtonState() {
        setButtonState(!buttonPressed);
    }

    public void setButtonState(boolean state) {
        if (state) {
            setImageResource(activeImage);
        } else {
            setImageResource(inactiveImage);
        }
        buttonPressed = state;
    }
}
