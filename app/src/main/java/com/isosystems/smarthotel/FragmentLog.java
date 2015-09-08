package com.isosystems.smarthotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isosystems.smarthotel.utils.Indexes;
import com.isosystems.smarthotel.utils.Notifications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FragmentLog extends Fragment {

    TextView mLog;

    UpdatesReceiver mReceiver;

    MyApplication mApplication;

    Button mLogButton;

    View rootView;

    public FragmentLog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_log,
                container, false);

        mApplication = (MyApplication) getActivity().getApplicationContext();

        mLogButton = (Button) rootView.findViewById(R.id.log_button);
        mLogButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              saveLog(mLog.getText().toString());
                                          }
                                      }
        );

        mLog = (TextView) rootView.findViewById(R.id.log);
        mReceiver = new UpdatesReceiver();

        return rootView;
    }


    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mCurrentHeader.setText("Log");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("HOTEL.LOG");
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void pushToLog(String msg) {
//        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
        mLog.append(getCurrentTimeStamp() + ": " + msg + "\n");
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveLog(String  data) {

        if (isExternalStorageWritable()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/hotel_logs");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "log-" + System.currentTimeMillis() + ".txt";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
                out.write(data);
                out.flush();
                out.close();
                Toast.makeText(mApplication,"Файл записан, папка:" + myDir.toString() + " файл: " + fname, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(mApplication,"Исключение при попытен записать файл",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mApplication,"Внешнее хранилище недоступно",Toast.LENGTH_LONG).show();
        }
    }

    public class UpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("HOTEL.LOG")) {
                String msg = intent.getStringExtra("message");
                pushToLog(msg);
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int status = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                if (status == WifiManager.WIFI_STATE_DISABLED) {
                    pushToLog("Wifi модуль отключен");
                } else if (status == WifiManager.WIFI_STATE_ENABLED) {
                    pushToLog("Wifi модуль включен");
                } else if (status == WifiManager.WIFI_STATE_DISABLING) {
                    pushToLog("Wifi модуль отключается");
                } else if (status == WifiManager.WIFI_STATE_ENABLING) {
                    pushToLog("Wifi модуль включается");
                }
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (state == NetworkInfo.DetailedState.DISCONNECTED ||
                        state == NetworkInfo.DetailedState.FAILED ||
                        state == NetworkInfo.DetailedState.BLOCKED) {

                    pushToLog("Состояние wifi-сети DISCONNECTED");

                } else if (state == NetworkInfo.DetailedState.CONNECTED) {
                    pushToLog("Состояние wifi-сети CONNECTED");
                } else {
                }
            }
        }
    }
}