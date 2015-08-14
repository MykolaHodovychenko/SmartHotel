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

import java.util.HashMap;

public class USBSendService extends IntentService {

	UsbManager usbManager;
	HashMap<String, UsbDevice> deviceList;
	UsbDevice usbDevice;
	UsbInterface usbInterface;
	UsbEndpoint usbEndpointOut;
	UsbDeviceConnection usbConnection;

	Boolean wasSend;

	public USBSendService() {
		super("USBSEND");
	}

	public void onCreate() {
		super.onCreate();

		wasSend = false;

		/**1. Получаем список устройств */
		
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

		/**2. Ищем среди найденных устройств нужное устройство */
		
		// Ищем в списке USB-устройств наше устройство
        for (UsbDevice device : deviceList.values()) {
        	if ((device.getProductId() == 257) && (device.getVendorId() == 65535)) {
        		try {
        			usbDevice = device;
        		} catch (Exception e) {
        			e.printStackTrace();
        			this.stopSelf();
        			return;
        		}
        	}//if
        }
        
		// Нужных устройств нет
        if (usbDevice == null) {
			this.stopSelf();
			return;
        }
		/**3. Берем интерфейс устройства */
		
        for (int i = 0; i < usbDevice.getInterfaceCount();i++) {
        	UsbInterface tempInterfce = usbDevice.getInterface(i);
        	if (tempInterfce.getEndpointCount() > 1) {
        		usbInterface = tempInterfce;
        	}
        }
        
		// Нужных интерфейсов нет
        if (usbInterface == null) {
			this.stopSelf();
			return;
        }

		/**4. Берем EndPoint */
		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
			
			UsbEndpoint tempPoint = usbInterface.getEndpoint(i);
			if (tempPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
				usbEndpointOut = tempPoint;
			}
		}//for

		
		// Нужных конечных точек нет
        if (usbEndpointOut == null) {
			this.stopSelf();
			return;
        }
		
		// Открываем соединение для USB-устройства
		try {
			usbConnection = usbManager.openDevice(usbDevice);
		} catch (Exception e) {
			e.printStackTrace();
			this.stopSelf();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		/** 1. Считываем передаваемое сообщение из extras */
		String msg;
		try {
			msg = intent.getStringExtra("message");
		} catch (Exception e) {
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		/** 2. Проверяем наличие устройства и подключения к нему */
		if ((usbConnection !=null) && (usbDevice!=null)) {

			// Берем интерфейс
			try {
				usbConnection.claimInterface(usbInterface, true);
			} catch (Exception e) {
				e.printStackTrace();
				this.stopSelf();
				return;
			}

			/** 
			 * ПЕРЕДАЕМ СООБЩЕНИЕ 
			 */
			
			// Передаем сообщение и получаем количество байт для сообщения
			int result = -1;
			try {
				result = usbConnection.bulkTransfer(usbEndpointOut,
						msg.getBytes(), msg.getBytes().length, 0);
				wasSend = true;

			} catch (Exception e) {
				e.printStackTrace();
				this.stopSelf();
				return;
			}

			// Освобождаем интерфейс
			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				e.printStackTrace();
				this.stopSelf();
				return;
			}

		} else {
			wasSend = false;
		}
	}

	public void onDestroy() {
		try {
			if (wasSend) {
			} else {
			}
			super.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
			super.onDestroy();
		}
	}
}