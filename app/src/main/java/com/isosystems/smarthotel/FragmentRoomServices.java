package com.isosystems.smarthotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isosystems.smarthotel.utils.Indexes;
import com.isosystems.smarthotel.utils.Notifications;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class FragmentRoomServices extends Fragment implements View.OnClickListener {

    public FlipMenuButton mDNDButton;
    public FlipMenuButton mGirlButton;
    public FlipMenuButton mCleanButton;
    public FlipMenuButton mWashButton;
    public FlipMenuButton mBarButton;
    public FlipMenuButton mFoodButton;

    UpdatesReceiver mReceiver;

    MyApplication mApplication;

    View rootView;

    public FragmentRoomServices() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_room_services,
                container, false);

        mApplication = (MyApplication) getActivity().getApplicationContext();

        mReceiver = new UpdatesReceiver();

        setFlipButtons(rootView);
        return rootView;
    }

    private void setFlipButtons(View v) {
        // Кнопка "Не беспокоить"
        mDNDButton = (FlipMenuButton) v.findViewById(R.id.do_not_disturb);
        mDNDButton.setOnClickListener(this);
        mDNDButton.setImageResources(R.drawable.menu_dnd, R.drawable.menu_dnd_active);
        mDNDButton.changeStateWithoutAnimation(mApplication.values.mDNDButton);

        // Кнопка "Вызов горничной"
        mGirlButton = (FlipMenuButton) v.findViewById(R.id.girl_image);
        mGirlButton.setOnClickListener(this);
        mGirlButton.setImageResources(R.drawable.menu_girl, R.drawable.menu_girl_active);
        mGirlButton.changeStateWithoutAnimation(mApplication.values.mGirlButton);

        // Кнопка "Уборка номера"
        mCleanButton = (FlipMenuButton) v.findViewById(R.id.clean_image);
        mCleanButton.setOnClickListener(this);
        mCleanButton.setImageResources(R.drawable.menu_clean, R.drawable.menu_clean_active);
        mCleanButton.changeStateWithoutAnimation(mApplication.values.mCleanButton);

        // Кнопка "Стирка вещей"
        mWashButton = (FlipMenuButton) v.findViewById(R.id.wash_image);
        mWashButton.setOnClickListener(this);
        mWashButton.setImageResources(R.drawable.menu_wash, R.drawable.menu_wash_active);
        mWashButton.changeStateWithoutAnimation(mApplication.values.mWashButton);

        // Кнопка "Пополнение минибара"
        mBarButton = (FlipMenuButton) v.findViewById(R.id.bar_image);
        mBarButton.setOnClickListener(this);
        mBarButton.setImageResources(R.drawable.menu_bar, R.drawable.menu_bar_active);
        mBarButton.changeStateWithoutAnimation(mApplication.values.mBarButton);

        // Кнопка "Еда в номер"
        mFoodButton = (FlipMenuButton) v.findViewById(R.id.food_image);
        mFoodButton.setOnClickListener(this);
        mFoodButton.setImageResources(R.drawable.menu_food, R.drawable.menu_food_active);
        mFoodButton.changeStateWithoutAnimation(mApplication.values.mFoodButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.do_not_disturb:

                int state = (!mApplication.values.mDNDButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_DND, state)) {
                    mApplication.values.mDNDButton = !mApplication.values.mDNDButton;
                    mDNDButton.setButtonState(mApplication.values.mDNDButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case R.id.girl_image:
                state = (!mApplication.values.mGirlButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_MAID, state)) {
                    mApplication.values.mGirlButton = !mApplication.values.mGirlButton;
                    mGirlButton.setButtonState(mApplication.values.mGirlButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case R.id.clean_image:
                state = (!mApplication.values.mCleanButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_CLEANER, state)) {
                    mApplication.values.mCleanButton = !mApplication.values.mCleanButton;
                    mCleanButton.setButtonState(mApplication.values.mCleanButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case R.id.wash_image:
                state = (!mApplication.values.mWashButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_WASH, state)) {
                    mApplication.values.mWashButton = !mApplication.values.mWashButton;
                    mWashButton.setButtonState(mApplication.values.mWashButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case R.id.bar_image:
                state = (!mApplication.values.mBarButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_MINIBAR, state)) {
                    mApplication.values.mBarButton = !mApplication.values.mBarButton;
                    mBarButton.setButtonState(mApplication.values.mBarButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case R.id.food_image:
                state = (!mApplication.values.mFoodButton) ? 1 : 0;
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.RS_FOOD, state)) {
                    mApplication.values.mFoodButton = !mApplication.values.mFoodButton;
                    mFoodButton.setButtonState(mApplication.values.mFoodButton, true);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mCurrentHeader.setText("Room Services");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("RS.VALUES.CHANGED");
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void updateRoomServices() {
        mDNDButton.setButtonState(mApplication.values.mDNDButton, true);
        mGirlButton.setButtonState(mApplication.values.mGirlButton, true);
        mCleanButton.setButtonState(mApplication.values.mCleanButton, true);
        mWashButton.setButtonState(mApplication.values.mWashButton, true);
        mBarButton.setButtonState(mApplication.values.mBarButton, true);
        mFoodButton.setButtonState(mApplication.values.mFoodButton, true);
    }

    public class UpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("RS.VALUES.CHANGED")) {
                updateRoomServices();
            }
        }
    }
}