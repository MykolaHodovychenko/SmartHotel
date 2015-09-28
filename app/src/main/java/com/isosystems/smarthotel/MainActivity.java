/*
 * SmartHotel, created by NickGodov on 06.09.15 2:06.
 * Last modified: 06.09.15 1:31
 *
 * This software is protected by copyright law and international treaties.
 * Unauthorized reproduction or distribution of this program, or any portion of it, may result in severe
 * civil and criminal penalties, and will be prosecuted to the maximum extent possible under law.
 *
 */

package com.isosystems.smarthotel;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.isosystems.smarthotel.connection.ConnectionManager;
import com.isosystems.smarthotel.settings.SettingsActivity;
import com.isosystems.smarthotel.utils.Notifications;
import com.isosystems.smarthotel.utils.ScreenDimActivity;
import com.isosystems.smarthotel.utils.ScreenSaverActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    Context mContext;

    MyApplication mApplication;

    // Handler для хранителя экрана
    Handler mScreenSaverHandler;
    // Использовать ли хранитель экрана
    Boolean mUseScreenSaver = false;
    // Время бездействия (секунды)
    int mScreenSaverIdleTime = 25;

    // Множество фрагментов
    FragmentMainMenu mFragmentMainMenu;
    FragmentRoomServices mFragmentRoomServices;
    FragmentLight mFragmentLight;
    FragmentTemperature mFragmentTemperature;
    FragmentTransaction mFragmentTransaction;

    // Режим соединения
    ConnectionManager.ConnectionMode mMode = ConnectionManager.ConnectionMode.USB;
    // Менеджер соединений
    ConnectionManager mConnectionManager;
    // Состояние соединения с контроллером
    Boolean isConnected = false;

    // Параметры соединения
    String wifi_name = "";
    private String password = "";
    private Boolean socket_endless_timeout = true;
    private String socket_ip = "";
    private int socket_port = 0;
    private String socket_greetings_message = "1";

    TextView mCurrentHeader; // Текущий заголовок (зависит от фрагмента)
    TextView mRoomNumber;    // Номер комнаты
    TextView mRoomTemperature; // Температура в номере

    ImageButton mWifiIcon; // Иконка соединения
    ImageButton mPowerIcon; // Иконка питания

    MessagesReceiver mReceiver; // Ресивер сообщений

    // region Runnable для хранителя экрана
    /**
     * Запуск хранителя экрана, если сработал счетчик бездействия:
     * 1) Определяется количество режимов хранителя экрана (слайд-шоу И/ИЛИ затемнение)
     * 2) Если два режима, то определить какой режим активен в данный момент:
     * - считать из настроек время работы затемнения - определить по текущему времени,
     * активно ли затемнение
     * 3) В зависимости от режима хранителя, запуск слайд-шоу или затемнение
     */
    private Runnable mScreenSaverRunnable = new Runnable() {
        public void run() {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mApplication);

            // FALSE, если затемнение
            // TRUE, если слайд-шоу
            Boolean screenSaverMode = true;

            // Используется ли затемнение экрана
            Boolean useScreenDim = prefs.getBoolean("enable_screen_dim", false);

            // Если используется затемнение,
            // необходимо определить наступило ли время работы режима
            if (useScreenDim) {
                String time = prefs.getString("screen_dim_enable_time",
                        "19:00-8:00");

                // Парсинг и конца работы
                // Например, 19:00-8:00 разбивается на [19:00] и [8:00]
                String[] time_period = time.split("-");

                // Парсинг значения начала работы
                // Например 19:00 разбивается на [19] и [00]
                String hour_start = time_period[0].split(":")[0];
                String minute_start = time_period[0].split(":")[1];

                // Парсинг значения начала работы
                // Например 8:00 разбивается на [8] и [00]
                String hour_end = time_period[1].split(":")[0];
                String minute_end = time_period[1].split(":")[1];

                // Получение временного промежутка в виде целых числа типа 1800
                // Например 19:45 преобразовывается в 1945
                int start_time = Integer.parseInt(hour_start) * 100
                        + Integer.parseInt(minute_start);
                int end_time = Integer.parseInt(hour_end) * 100
                        + Integer.parseInt(minute_end);

                // Получение текущего времени в виде целого числа типа 1800
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                int current_time = Integer.parseInt(sdf.format(Calendar
                        .getInstance().getTime()));

                // Необходимо сравнить текущее время и время для затемнения,
                // чтобы определить - какой вариант хранителя использовать.
                // Если временной промежуток задан в течение одного дня
                // "с 10 до 18", тогда current_time должно быть
                // больше start_time, но меньше end_time
                // Если промежуток "с 19 до 12", тогда current_time должно быть
                // или больше start_time, или меньше end_time
                // Если старт и конец работы совпадают - круглосуточно действует
                // затемнение
                if (start_time < end_time && current_time >= start_time
                        && current_time <= end_time)
                    screenSaverMode = false;
                else if (start_time > end_time
                        && (current_time >= start_time || current_time <= end_time))
                    screenSaverMode = false;
                else if (start_time == end_time)
                    screenSaverMode = false;
            }

            if (screenSaverMode) {
                // Слайд-шоу
                Intent i = new Intent(getApplicationContext(), ScreenSaverActivity.class);
                startActivity(i);
            } else {
                // Затемнение экрана
                Intent i = new Intent(getApplicationContext(), ScreenDimActivity.class);
                startActivity(i);
            }
        }
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mApplication = (MyApplication) getApplicationContext();
        mContext = this;


        // Handler для крашей
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

        // Считывание настроек подключения
        readConnectionPreferences();

        mConnectionManager = new ConnectionManager(mMode, MainActivity.this, wifi_name, password, socket_endless_timeout, socket_ip, socket_port, socket_greetings_message);
        if (mConnectionManager.getConnectionMode() == ConnectionManager.ConnectionMode.USB) {
            mConnectionManager.startUSBReceiveService();
        }

        // Установка полноэкранного режима
        setFullScreen();

        // Иконки питания и соединения
        mWifiIcon = (ImageButton) findViewById(R.id.image_wifi);
        mPowerIcon = (ImageButton) findViewById(R.id.image_plug);

        // Инициализация фрагментов
        mFragmentMainMenu = new FragmentMainMenu();
        mFragmentRoomServices = new FragmentRoomServices();
        mFragmentLight = new FragmentLight();
        mFragmentTemperature = new FragmentTemperature();
        // Установка начального фрагмента
        if (findViewById(R.id.frame_pager) != null) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();

            mFragmentTransaction.replace(R.id.frame_pager, mFragmentMainMenu, "Main Menu");
            mFragmentTransaction.commit();
        }

        // Инициализация ресивера
        mReceiver = new MessagesReceiver();
        // Настройки ресивера
        setupMessagesReceiver();

        // Настройка кнопок
        setButtons();
        // Текущий заголовок
        mCurrentHeader = (TextView) findViewById(R.id.current_header);

        // Номер номера и температура в номере
        mRoomNumber = (TextView) findViewById(R.id.textView2);
        mRoomTemperature = (TextView) findViewById(R.id.textView3);

        // Проверка и создание директорий
        checkExternalDirectoryStructure();
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

    /**
     * Проверка наличия директорий и их создание, в случае отсутствия
     */
    private void checkExternalDirectoryStructure() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File externalFilesDir = Environment.getExternalStorageDirectory();

            File externalRootDirectory = new File(externalFilesDir + File.separator
                    + Globals.EXTERNAL_ROOT_DIRECTORY);
            externalRootDirectory.mkdirs();

            File externalSSDirectory = new File(externalFilesDir + File.separator
                    + Globals.EXTERNAL_ROOT_DIRECTORY + File.separator
                    + Globals.EXTERNAL_SCREENSAVER_IMAGES_DIRECTORY);
            externalSSDirectory.mkdirs();
        }
    }

    /**
     * Считывание настроек приложения
     */
    private void readConnectionPreferences() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext().getApplicationContext());

        // Тип подключения
        String connection_type = prefs.getString("connection_type", "1");
        if (connection_type.equals("0")) {
            mMode = ConnectionManager.ConnectionMode.WIFI;
        } else if (connection_type.equals("1")) {
            mMode = ConnectionManager.ConnectionMode.USB;
        }

        // Название wifi-точки
        wifi_name = prefs.getString("wifi_name", "");
        // Пароль wifi-точки
        password = prefs.getString("wifi_password", "");
        // ip сокета
        socket_ip = prefs.getString("socket_ip", "");
        // порт сокета
        String s = prefs.getString("socket_port", "");
        try {
            socket_port = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // таймаут сокета
        socket_endless_timeout = prefs.getBoolean("socket_timeout_endless", true);
        // приветственное сообщение
        socket_greetings_message = prefs.getString("socket_message", "");
    }

    // region Настройка Handler`а хранителя

    /**
     * Считывание настроек хранителя экрана
     */
    private void readScreensaverPreferences() {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext().getApplicationContext());
        // Проверка использования хранителя экрана
        mUseScreenSaver = prefs.getBoolean("enable_screen_saver", false);

        if (mUseScreenSaver) {
            mScreenSaverIdleTime = Integer.parseInt(prefs.getString(
                    "screen_saver_idle_time", "25"));
        }

    }

    /**
     * При взаимодействии пользователя с планшетом, обновление счетчика бездействия
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (mUseScreenSaver) {
            // Обновляем счетчик бездействия
            mScreenSaverHandler.removeCallbacks(mScreenSaverRunnable);
            mScreenSaverHandler.postDelayed(mScreenSaverRunnable,
                    mScreenSaverIdleTime * 1000);
        }
    }

    // endregion
    public void changeFragment(int i) {
        try {
            if (i == 0) {
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frame_pager, mFragmentMainMenu, "Main Menu");
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            } else if (i == 1) {
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frame_pager, mFragmentRoomServices, "Room Services");
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            } else if (i == 2) {
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frame_pager, mFragmentLight, "Light");
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            } else if (i == 3) {
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frame_pager, mFragmentTemperature, "Temperatue");
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region Установка кнопок, полноэкранного режима
    /**
     * Установка кнопок "Назад", "Домой" и "Настройки"
     * <p/>
     * <p>При нажатии кнопки "Назад", из backstack менеджера фрагментов извлекается фрагмент.</p>
     * <p>При нажатии кнопки "Домой", очищается backstack и фрагмент меняется на 0 (MainMenu).</p>
     * <p>При нажатии кнопки "Настройки", открывается диалоговое окно ввода пароля.</p>
     */
    private void setButtons() {
        try {
            ImageButton mBackButton = (ImageButton) findViewById(R.id.back_image);
            mBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.this.getSupportFragmentManager().popBackStack();
                }
            });

            ImageButton mHomeButton = (ImageButton) findViewById(R.id.home_button);
            mHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }
                    changeFragment(0);
                }
            });

            ImageButton mSettingsButton = (ImageButton) findViewById(R.id.button_settings);
            mSettingsButton.setOnClickListener(mSettingsButtonListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Установка параметров полноэкранного режима.
     * <p/>
     * <p>
     * 1. Выставляется ориентация экрана; <br>
     * 2. Выставляется запрет на системный хранитель экрана; <br>
     * 3. Уточняется id процесса SystemUI; <br>
     * 4. Запуск task`а на скрытие статус-бара; <br>
     * 5. Скрытие ActionBar`а; <br.
     * 6. Выставление флагов (на случай, если рута нет). <br>
     * </p>
     */
    private void setFullScreen() {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // Запрет на отключение экрана
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            Process proc = null;
            String ProcID = "79"; //HONEYCOMB AND OLDER

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }

            try {
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"});
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Включение полноэкранного режим планшета
            if (getActionBar() != null) {
                getActionBar().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    // region Жизненный цикл

    @Override
    protected void onPause() {
        if (mScreenSaverHandler != null) {
            mScreenSaverHandler.removeCallbacks(mScreenSaverRunnable);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFullScreen();

        // Считывание настроек хранителя экрана
        readScreensaverPreferences();

        if (mUseScreenSaver) {
            mScreenSaverHandler = new Handler();
            // Количество секунд * 1000
            mScreenSaverHandler.postDelayed(mScreenSaverRunnable,
                    mScreenSaverIdleTime * 1000);
        }

        updateGeneralData();
        setPowerIcon(isSupplyEnabled());
        setConnectionIcon(isConnected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
            mConnectionManager.unregisterReceiver();
            mConnectionManager.doUnbindService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec("am startservice --user 0 -n com.android.systemui/.SystemUIService");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Настройка ресивера сообщений
    private void setupMessagesReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionManager.MESSAGE_VALUE);
        filter.addAction(ConnectionManager.MESSAGE_ALARM);
        filter.addAction(Globals.BROADCAST_INTENT_POWER_SUPPLY_CHANGED);
        filter.addAction(ConnectionManager.WIFI_CONNECTED);
        filter.addAction(ConnectionManager.WIFI_DISCONNECTED);
        filter.addAction(ConnectionManager.USB_CONNECTED);
        filter.addAction(ConnectionManager.USB_DISCONNECTED);
        filter.addAction("GENERAL.VALUES.CHANGED");
        try {
            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGeneralData() {
        mRoomNumber.setText("Room № " + String.valueOf(mApplication.values.mRoomNumber));
        mRoomTemperature.setText(String.valueOf(mApplication.values.mRoomTemperature) + " °C");
    }

    private void processMessage(String message) {
        String[] array = message.split(",");
        mApplication.values.processArray(array, getApplicationContext());
    }

    View.OnClickListener mSettingsButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mApplication);
                final View dialog_view = inflater.inflate(
                        R.layout.fragment_dialog_check_password, null);

                // Включение полноэкранного режим планшета
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

                dialog_view.setSystemUiVisibility(uiOptions);
                dialog_view.setSystemUiVisibility(8);


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Please enter the password to enter the settings menu")
                        .setView(dialog_view)
                        .setPositiveButton("Enter",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        String password = ((EditText) dialog_view
                                                .findViewById(R.id.checkpassword_dialog_password))
                                                .getText().toString();

                                        Boolean correct_password = false;
                                        if (password.equals(Globals.SERVICE_PASSWORD)) {
                                            correct_password = true;
                                        }

                                        if (correct_password) {
                                            // Пароль правильный

                                            Intent intent = new Intent(
                                                    MainActivity.this,
                                                    SettingsActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // Пароль неправильный
                                            Notifications.showErrorCrouton(MainActivity.this,"Wrong password!");
                                            //Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).create().show();
            }
        };
    }
    // endregion

    //region Настройка пиктограмм подключения и питания

    private void setConnectionIcon(boolean state) {
        try {
            if (mConnectionManager.getConnectionMode() == ConnectionManager.ConnectionMode.USB) {
                if (state) {
                    mWifiIcon.setImageResource(R.drawable.usb_on);
                } else {
                    mWifiIcon.setImageResource(R.drawable.usb_off);
                }
            } else if (mConnectionManager.getConnectionMode() == ConnectionManager.ConnectionMode.WIFI) {
                if (state) {
                    mWifiIcon.setImageResource(R.drawable.wifi_on);
                } else {
                    mWifiIcon.setImageResource(R.drawable.wifi_off);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPowerIcon(boolean state) {
        try {
            if (state) {
                mPowerIcon.setImageResource(R.drawable.power_on);
            } else {
                mPowerIcon.setImageResource(R.drawable.power_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean isSupplyEnabled() {
        try {
            Intent intent = mApplication.registerReceiver(null, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));
            int plugged = 0;
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            boolean result = (plugged != 0 && plugged != -1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // endregion

    public Boolean sendMessage(int index, int value) {
        return mConnectionManager.sendMessage(MainActivity.this, index, value);
    }

    public class MessagesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectionManager.MESSAGE_VALUE)) {
                String message = intent.getStringExtra(ConnectionManager.MESSAGE_EXTRA);
                message = message.substring(2, message.length());
                processMessage(message);
            } else if (intent.getAction().equals(Globals.BROADCAST_INTENT_POWER_SUPPLY_CHANGED)) {
                setPowerIcon(isSupplyEnabled());
            } else if (intent.getAction().equals(ConnectionManager.WIFI_CONNECTED) ||
                    intent.getAction().equals(ConnectionManager.USB_CONNECTED)) {
                isConnected = true;
                setConnectionIcon(true);
            } else if (intent.getAction().equals(ConnectionManager.WIFI_DISCONNECTED) ||
                    intent.getAction().equals(ConnectionManager.USB_DISCONNECTED)) {
                isConnected = false;
                setConnectionIcon(false);
            } else if (intent.getAction().equals("GENERAL.VALUES.CHANGED")) {
                updateGeneralData();
            }
        }
    }
}