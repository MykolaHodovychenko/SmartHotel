package com.isosystems.smarthotel.connection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.isosystems.smarthotel.Globals;
import com.isosystems.smarthotel.MyApplication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для приема сообщений из контроллера Запускается в момент
 */
public class USBReceiveService extends IntentService {
	MyApplication mApplication;

	UsbManager usbManager;
	HashMap<String, UsbDevice> deviceList;
	UsbDevice usbDevice;
	UsbInterface usbInterface;
	UsbEndpoint usbEndPointIn;
	UsbDeviceConnection usbConnection;

	StringBuilder mMessageBuffer;
	static Handler mMessageHandler;

	static Handler mBufferCleanHandler;
	int mBufferClearTimeout = 2000;

	public USBReceiveService() {
		super("USBReceive");
	}

	public void onCreate() {
		super.onCreate();
		mApplication = (MyApplication) getApplicationContext();

		mMessageBuffer = new StringBuilder();

		mBufferCleanHandler = new Handler();
		mBufferCleanHandler.postDelayed(mBufferClearRunnable,
				mBufferClearTimeout);

		mMessageHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Bundle bundle = msg.getData();

				// Сообщение
				String message = bundle.getString("incoming_message");

				// Добавляем сообщение в буфер
				mMessageBuffer.append(message);

				// Поиск подстроки, которая начинается с @ или & или $
				// и заканчивается ¶
				Pattern p = Pattern.compile("[@&$#](.*?)¶");
				Matcher m = p.matcher(mMessageBuffer);
				while (m.find()) {
					messageProcess(m.group());
				}

				// После отправки сообщения на обработку, оно удаляется из
				// буфера
				mMessageBuffer = new StringBuilder(m.replaceAll(""));
			} // handle message
		}; // handler

		/** 1. Получаем список устройств */
		try {
			usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			deviceList = usbManager.getDeviceList();
		} catch (Exception e) {
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		// Устройств нету
		if (deviceList.isEmpty()) {
			this.stopSelf();
			return;
		}


		/** 2. Ищем среди найденных устройств нужное устройство */
		for (UsbDevice device : deviceList.values()) {

			if ((device.getProductId() == 257)
					&& (device.getVendorId() == 65535)) {

				try {
					usbDevice = device;
				} catch (Exception e) {
					e.printStackTrace();
					this.stopSelf();
					return;
				}
			}// if
		}

		// Нужных устройств нет
		if (usbDevice == null) {
			this.stopSelf();
			return;
		}

		/** 3. Берем интерфейс устройства */
		for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
			UsbInterface tempInterfce = usbDevice.getInterface(i);
			if (tempInterfce.getEndpointCount() > 1) {
				usbInterface = tempInterfce;
			}
		}

		// Нужных интерфейсов нет
		if (usbInterface == null) {
			// Logging.v("Подходящих интерфейсов не обнаружено");
			this.stopSelf();
			return;
		}

		/** 4. Берем EndPoint */
		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
			UsbEndpoint tempPoint = usbInterface.getEndpoint(i);
			if (tempPoint.getDirection() == UsbConstants.USB_DIR_IN) {
				usbEndPointIn = tempPoint;
			}
		}// for

		if (usbEndPointIn == null) {
			this.stopSelf();
			return;
		}

		// Открываем соединение для USB-устройства
		try {
			usbConnection = usbManager.openDevice(usbDevice);
		} catch (Exception e) {
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		// Отсылаем броадкаст о том, что пришло алармовое сообщение
		Intent i = new Intent();
		i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
		getApplicationContext().sendBroadcast(i);

		mApplication.isUsbConnected = true;
	}

	// Runnable для очистки буфера после N мсек.
	private Runnable mBufferClearRunnable = new Runnable() {
		public void run() {
			// Очистка буфера
			mMessageBuffer = new StringBuilder();

			mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
			mBufferCleanHandler.postDelayed(mBufferClearRunnable,
					mBufferClearTimeout);
		}
	};

	private Boolean checkUsbDevice() {
		UsbManager manager;
		HashMap<String, UsbDevice> dList;

		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		dList = manager.getDeviceList();

		if (dList.isEmpty())
			return false;

		for (UsbDevice device : dList.values()) {
			if ((device.getProductId() == 257)
					&& (device.getVendorId() == 65535)) {
				return true;
			}
		}

		return false;
	}

	// ---- ПОЛУЧЕНИЕ СООБЩЕНИЯ

	@Override
	protected void onHandleIntent(Intent intent) {
		while (checkUsbDevice()) {
			// Буфер для приема сообщений
			byte[] mReadBuffer = new byte[128];
			// Количество принятых байт
			int transferred = -1;

			// Берем интерфейс
			try {
				usbConnection.claimInterface(usbInterface, true);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			try {
				transferred = usbConnection.bulkTransfer(usbEndPointIn,
						mReadBuffer, mReadBuffer.length, 0);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			// Освобождаем интерфейс
			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			/** Обрабатываем пришедшие данные */
			if (transferred >= 0) {

				// Если пришло сообщение - сброс тайм-аута очистки буфера
				mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
				mBufferCleanHandler.postDelayed(mBufferClearRunnable,
						mBufferClearTimeout);

				String mReceivedMessage = new String(mReadBuffer, 0,
						transferred, Charset.forName("windows-1251"));

				Bundle b = new Bundle();
				b.putString("incoming_message", mReceivedMessage);

				Message msg = new Message();
				msg.setData(b);

				mMessageHandler.sendMessage(msg);

			} // end if transferred
		} // end while
	} // end onHandleIntent

	/**
	 * Обработка пришедшего сообщения: 1) Считываем первый символ, чтобы понять
	 * тип сообщения
	 *
	 * @param message
	 *            Пришедшее сообщение
	 */
	private void messageProcess(String message) {
		Intent i = new Intent();

		// Стирание последнего символа = ¶
		message = message.substring(0, message.length() - 1);

		if (message.charAt(0) == '$') {
			// Пришло алармовое сообщение
			if (message.length() > 2) {
				// Убираем $ и пробел
				String alarmMessage = message.substring(2);

				// Кидаем броадкаст
				i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
				getApplicationContext().sendBroadcast(i);
			} else {
			} // end if length
		} else if (message.charAt(0) == '&') {
			// Пришло интовое значение
			i.setAction(Globals.BROADCAST_INTENT_VALUE_MESSAGE);
			i.putExtra("message", message);
			// Кидаем броадкаст
			getApplicationContext().sendBroadcast(i);
		} else if (message.charAt(0) == '@') {
			// Пришло сообщение форматированного вывода
			i.setAction(Globals.BROADCAST_INTENT_FORMSCREEN_MESSAGE);
			i.putExtra("message", message);
			// Кидаем броадкаст
			getApplicationContext().sendBroadcast(i);
		} else if (message.charAt(0) == '#') {
			// Принудительное открытие окна форматированного вывода
			if (message.length() > 2) {
				i.setAction(Globals.BROADCAST_INTENT_FORCED_FORMSCREEN_MESSAGE);
				i.putExtra("message",message);
				getApplicationContext().sendBroadcast(i);
			}
		}
		else {
		} // end char[0]
	} // end method

	public void onDestroy() {
		try {
			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				// Logging.v("Исключение при попытке освободить интерфейс");
				e.printStackTrace();
			}

			if (mBufferCleanHandler != null) {
				mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
			}

			Intent i = new Intent();
			i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
			getApplicationContext().sendBroadcast(i);

			mApplication.isUsbConnected = false;

			super.onDestroy();
		} catch (Exception e) {
			// Logging.v("Исключение при попытке уничтожить ReceiveService");
			e.printStackTrace();
			super.onDestroy();
		}
	}
}