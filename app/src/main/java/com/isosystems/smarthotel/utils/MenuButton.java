package com.isosystems.smarthotel.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import com.isosystems.smarthotel.R;

public class MenuButton extends Button {

    int activeStateBackground = R.drawable.light_button_on;
    int inactiveStateBackground = R.drawable.light_button_off;
    int activeTextColor = Color.parseColor("#42392d");
    int inactiveTextColor = Color.parseColor("#ded0ab");

    public MenuButton(Context context) {
        super(context);
    }

    public MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBackgroundResources(int active, int inactive) {
        this.activeStateBackground = active;
        this.inactiveStateBackground = inactive;
    }

    public void setActiveState(){
        this.setBackgroundResource(activeStateBackground);
        this.setTextColor(activeTextColor);
    }

    public void setInactiveState(){
        this.setBackgroundResource(inactiveStateBackground);
        this.setTextColor(inactiveTextColor);
    }
}
