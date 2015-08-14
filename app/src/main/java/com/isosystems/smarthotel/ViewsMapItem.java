package com.isosystems.smarthotel;

import android.view.View;

public class ViewsMapItem {

    public int value;
    public View view;

    public ViewsMapItem(){
        value = 0;
    }

    public ViewsMapItem(int value, View v) {
        this.value = value;
        this.view = v;
    }

    public void setView(View v) {
        this.view =v;
    }

    public void setValue (int value) {
        this.value = value;
    }
}
