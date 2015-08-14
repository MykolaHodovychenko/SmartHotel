// TODO: Сделать загрузку всех фрагментов сразу
// TODO: Сделать чтобы после кнопки домой очищался стек
package com.isosystems.smarthotel;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.isosystems.smarthotel.connection.SocketService;
import com.isosystems.smarthotel.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    Context mContext;

    MyApplication mApplication;

    FragmentMainMenu mFragmentMainMenu;
    FragmentRoomServices mFragmentRoomServices;
    FragmentLight mFragmentLight;
    FragmentTemperature mFragmentTemperature;
    FragmentTransaction mFragmentTransaction;

    String mWifiName = "";

    SocketService mBoundService = null;
    boolean mIsBound = false;
    WifiReceiver mReceiver;
    WifiManager mWifiManager;

    ImageButton mWifiIcon;
    ImageButton mPowerIcon;

    /* FIXME: Часы отключены, т.к. планшет не поддерживает */
    //TextClock mClock;

    TextView mCurrentHeader;

    ArrayList<Integer> mFragmentsList = new ArrayList<Integer>(8);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (MyApplication) getApplicationContext();

        // Считывание из настроек
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        mWifiName = prefs.getString("wifi_name", "YAM-AP-00000002");

        // TODO: Убрано в целях отладки
        setFullScreen();

        mContext = this;

        mWifiIcon = (ImageButton) findViewById(R.id.image_wifi);
        mPowerIcon = (ImageButton) findViewById(R.id.image_plug);

        setPowerIcon(isSupplyEnabled());

        mFragmentMainMenu = new FragmentMainMenu();
        mFragmentRoomServices = new FragmentRoomServices();
        mFragmentLight = new FragmentLight();
        mFragmentTemperature = new FragmentTemperature();

        if (findViewById(R.id.frame_pager)!=null) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();

            //MainMenu
            mFragmentTransaction.replace(R.id.frame_pager, mFragmentMainMenu, "Main Menu");

            mFragmentTransaction.commit();
        }

        mReceiver = new WifiReceiver();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        /* FIXME : Убрано, т.к. планшет не поддерживает */
        // mClock = (TextClock) findViewById(R.id.textClock);
        // mClock.setFormat24Hour("k:mm");

        setButtons();
        mCurrentHeader = (TextView)findViewById(R.id.current_header);

        mApplication.values.room_number = (TextView) findViewById(R.id.textView2);
        mApplication.values.room_temperature = (TextView) findViewById(R.id.textView3);
    }

    private void setWifiIcon(boolean state) {
        if (state) {
            mWifiIcon.setImageResource(R.drawable.wifi_green);
        } else {
            mWifiIcon.setImageResource(R.drawable.wifi_red);
        }
    }

    private void setPowerIcon (boolean state) {
        if (state) {
            mPowerIcon.setImageResource(R.drawable.plug_green);
        } else {
            mPowerIcon.setImageResource(R.drawable.plug_red);
        }
    }

    public void changeFragment(int i) {
        if (i==0) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.frame_pager, mFragmentMainMenu, "Main Menu");
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        } else if (i==1) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.frame_pager, mFragmentRoomServices, "Room Services");
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        } else if (i==2) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.frame_pager, mFragmentLight, "Light");
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        } else if (i==3) {
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.frame_pager, mFragmentTemperature, "Temperatue");
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }
    }

    // region Установка кнопок, полноэкранного режима

    /* TODO: добавить javadoc */
    private void setButtons() {

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
                changeFragment(0);
            }
        });

        ImageButton mSettingsButton = (ImageButton) findViewById(R.id.button_settings);
        mSettingsButton.setOnClickListener(mSettingsButtonListener());

    }

    /* TODO: добавить javadoc */
    private void setFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Запрет на отключение экрана
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setSystemUiVisibility(8);
        // <<-----------------------------------
    }
    //endregion

    public void changeHeader(String header) {
        mCurrentHeader.setText(header);
    }

    // region Жизненный цикл
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(Globals.BROADCAST_INTENT_POWER_SUPPLY_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(Globals.BROADCAST_INTENT_NO_CONNECT);
        filter.addAction(Globals.BROADCAST_INTENT_CONNECT);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction("SOCKET.DATA");

        registerReceiver(mReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
    // endregion

    // region Бинд сервиса
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketService.LocalBinder)service).getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(MainActivity.this,SocketService.class),mConnection,Context.BIND_AUTO_CREATE);
        if (mBoundService != null) {
        }
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    // endregion

    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (state == NetworkInfo.DetailedState.DISCONNECTED ||
                        state == NetworkInfo.DetailedState.FAILED ||
                        state == NetworkInfo.DetailedState.BLOCKED) {

//                    setWifiIcon(false);

                    // Если нет коннекта к точке, стартуем сервис, который коннектится к точке
                } else if (state == NetworkInfo.DetailedState.CONNECTED) {
                    // Если есть коннект, но точка называется иначе - дисконнект
                    // В этом случае, будет подхвачено первое условие и произойдет старт сервиса
                    if (!mWifiManager.getConnectionInfo().getSSID().equals(mWifiName)) {
                        if (mBoundService!=null) {
                            mBoundService.reconnectToWifi();
                        }
                    }
//                    setWifiIcon(true);
                } else {
//                    setWifiIcon(false);
                }
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int status = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (status == WifiManager.WIFI_STATE_ENABLED) {
                    //startService(new Intent(MainActivity.this,SocketService.class));
                    doBindService();
                } else if (status == WifiManager.WIFI_STATE_DISABLED) {
                    doUnbindService();
                }
            } else if (intent.getAction().equals("SOCKET.DATA")) {
                String msg = intent.getStringExtra("message");
                msg = msg.substring(2,msg.length());
                processMessage(msg);
                //Toast.makeText(mContext,msg +".символов: " +msg.length(),Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(Globals.BROADCAST_INTENT_POWER_SUPPLY_CHANGED)) {
                setPowerIcon(isSupplyEnabled());
            } else if (intent.getAction().equals(Globals.BROADCAST_INTENT_NO_CONNECT)) {
                setWifiIcon(false);
            } else if (intent.getAction().equals(Globals.BROADCAST_INTENT_CONNECT)) {
                setWifiIcon(true);
            }
        } //onReceive
    } // WifiReceiver

    private Boolean isSupplyEnabled() {
        Intent intent = mApplication.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int plugged = 0;
        plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        boolean result = (plugged != 0 && plugged!=-1);
        return result;
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
                builder.setMessage("Введите пароль для входа в настройки:")
                        .setView(dialog_view)
                        .setPositiveButton("Войти",
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
                                            Toast.makeText(MainActivity.this,"Неверный пароль",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).create().show();
            }
        };
    }
}