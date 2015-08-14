/*
 * Мобильное приложение для проекта "Умный дом"
 * 
 * author: Годовиченко Николай
 * email: nick.godov@gmail.com
 * last edit: 11.09.2014
 */

package com.isosystems.smarthotel;

import android.app.Application;
import android.view.View;

import com.isosystems.smarthotel.utils.Values;

import java.util.ArrayList;
import java.util.HashMap;

public class MyApplication extends Application {

	public Boolean isUsbConnected = false;

	public Values values;

	@Override
	public void onCreate() {
		super.onCreate();
		values = new Values(getApplicationContext());
	}
}