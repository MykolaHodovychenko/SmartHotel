package com.isosystems.smarthotel.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

    SocketService mBoundService = null;
    boolean mIsBound = false;

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.DetailedState state = info.getDetailedState();

            // Проверяем состояние wifi-сети
            // Если сеть отсоединена - пытаемся подключиться
            if (state == NetworkInfo.DetailedState.DISCONNECTED ||
                    state == NetworkInfo.DetailedState.FAILED ||
                    state == NetworkInfo.DetailedState.BLOCKED) {

                Toast.makeText(context,"connecting...",Toast.LENGTH_SHORT).show();

            }
        }
    }
}
