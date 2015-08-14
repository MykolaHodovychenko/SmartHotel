package com.isosystems.smarthotel.model;


import android.util.SparseArray;

public class DataStorage {

SparseArray<String> mDataArray;

    public DataStorage() {
        mDataArray = new SparseArray<String>();
    }

    public void add(int key, String value) {
        mDataArray.put(key,value);
    }

    public void change(int key, String value) {
        mDataArray.setValueAt(key,value);
    }
}
