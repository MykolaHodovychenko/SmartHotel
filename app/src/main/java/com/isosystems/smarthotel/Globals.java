package com.isosystems.smarthotel;

/**
 * Данный класс содержит глобальные константы для приложения
 */
public final class Globals {

	/** Папка на sd-card с файлами для приложения */
	public static final String EXTERNAL_ROOT_DIRECTORY = "smarthotel";
	/** Папка в EXTERNAL_ROOT_DIRECTORY где хранятся изображения */
	public static final String EXTERNAL_IMAGES_DIRECTORY = "images";
    /** Папка в assets, откуда берутся стандартные картинки */
	public static final String ASSETS_IMAGES_DIRECTORY = "imgs";
    /** Папка в EXTERNAL_ROOT_DIRECTORY где хранятся логи */
	public static final String EXTERNAL_LOGS_DIRECTORY = "logs";
    /** Папка в EXTERNAL_ROOT_DIRECTORY где хранятся картинки для слайд-шоу */
	public static final String EXTERNAL_SCREENSAVER_IMAGES_DIRECTORY = "screensaver";

    /** Имя файла меню, который хранится во внутреннем хранилище */
	public static final String INTERNAL_MENU_FILE = "menu.obj";
    /** Имя файла окон вывода, который хранится во внутреннем хранилище */
	public static final String INTERNAL_FORMATTED_SCREENS_FILE = "fs.obj";
    /** Имя файла с сообщениями, который хранится во внутреннем хранилище */
    public static final String INTERNAL_MESSAGES_FILE = "messages.obj";

    /** Префикс для логов, которые хранятся в папке логов */
	public static final String LOG_TAG = "SMARTHOUSE";

    /** Action для Broadcast Receiver для прихода алармового сообщения */
	public static final String BROADCAST_INTENT_ALARM_MESSAGE = "SMARTHOUSE.ALARM_MESSAGE_RECEIVED";
    /** Action для Broadcast Receiver для прихода сообщения со значением */
	public static final String BROADCAST_INTENT_VALUE_MESSAGE = "SMARTHOUSE.VALUE_MESSAGE_RECEIVED";
    /** Action для Broadcast Receiver для прихода сообщения для окон форматированного вывода */
	public static final String BROADCAST_INTENT_FORMSCREEN_MESSAGE = "SMARTHOUSE.FORMSCREEN_MESSAGE_RECEIVED";
    /** Action для Broadcast Receiver для прихода сообщения принуднительного открытия окна форматированного вывода */
    public static final String BROADCAST_INTENT_FORCED_FORMSCREEN_MESSAGE = "SMARTHOUSE.FORCED_FORMSCREEN_MESSAGE_RECEIVED";
    /** Action для Broadcast Receiver для прихода сообщения о смене режима питания устройства */
    public static final String BROADCAST_INTENT_POWER_SUPPLY_CHANGED = "SMARTHOUSE.POWER_SUPPLY_CHANGED";
    /** Action для Broadcast Receiver для прихода отладочного сообщения */
    public static final String BROADCAST_INTENT_DEBUG_MESSAGE = "SMARTHOUSE.DEBUG_MESSAGE";


    public static final String BROADCAST_INTENT_NO_CONNECT = "SMARTHOTEL.NO_CONNECT";
    public static final String BROADCAST_INTENT_CONNECT = "SMARTHOTEL.CONNECT";


    /** Сервисный пароль для настроек */
    public static final String SERVICE_PASSWORD = "924";

    public static final String NOTIFICATION_NO_CONNECTION = "Error: no connection to the server";

}