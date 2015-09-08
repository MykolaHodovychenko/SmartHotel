/*
 * SmartHotel, created by NickGodov on 06.09.15 2:25.
 * Last modified: 03.09.15 19:14
 *
 * This software is protected by copyright law and international treaties.
 * Unauthorized reproduction or distribution of this program, or any portion of it, may result in severe
 * civil and criminal penalties, and will be prosecuted to the maximum extent possible under law.
 *
 */

package com.isosystems.smarthotel;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.isosystems.smarthotel.utils.Values;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyApplication extends Application {

	public Values values;

	@Override
	public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				handleUncaughtException(thread, e);
			}
		});

		values = new Values(getApplicationContext());
	}

	/**
	 * Данный метод обабатывает crash приложения.
	 * При краше, в корень внешней директории записывается файл
	 *
	 * @param thread
	 * @param e
	 */
	public void handleUncaughtException(Thread thread, Throwable e) {
		e.printStackTrace(); // not all Android versions will print the stack trace automatically

		InputStreamReader reader = null;
		FileWriter writer = null;

		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = null;

			info = manager.getPackageInfo(this.getPackageName(), 0);

			String model = Build.MODEL;
			if (!model.startsWith(Build.MANUFACTURER))
				model = Build.MANUFACTURER + " " + model;

			String path = Environment.getExternalStorageDirectory().getPath();
			String fullName = path + "/crashlog" + String.valueOf(System.currentTimeMillis());

			File file = new File(fullName);


			String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
					"logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
					"logcat -d -v time";

			Process process = Runtime.getRuntime().exec(cmd);
			reader = new InputStreamReader(process.getInputStream());

			writer = new FileWriter(file);
			writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
			writer.write("Device: " + model + "\n");
			writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

			char[] buffer = new char[10000];
			do {
				int n = reader.read(buffer, 0, buffer.length);
				if (n == -1)
					break;
				writer.write(buffer, 0, n);
			} while (true);

			reader.close();
			writer.close();

			System.exit(1);
		} catch (IOException | PackageManager.NameNotFoundException e1) {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			e1.printStackTrace();
		}
	}
}