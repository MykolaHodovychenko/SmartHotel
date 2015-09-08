/*
 * SmartHotel, created by NickGodov on 06.09.15 2:04.
 * Last modified: 06.09.15 2:04
 *
 * This software is protected by copyright law and international treaties.
 * Unauthorized reproduction or distribution of this program, or any portion of it, may result in severe
 * civil and criminal penalties, and will be prosecuted to the maximum extent possible under law.
 *
 */

package com.isosystems.smarthotel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.isosystems.smarthotel.utils.Indexes;

public class FragmentMainMenu extends Fragment {

    ImageButton mRoomServicesButton;
    ImageButton mLightingButton;
    ImageButton mTemperatureButton;
    FlipMenuButton mFrontDoorLockButton;

    UpdatesReceiver mReceiver;
    MyApplication mApplication;

    public FragmentMainMenu() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_menu,
                container, false);
        mApplication = (MyApplication) getActivity().getApplicationContext();

        mReceiver = new UpdatesReceiver();
        setMenuButtons(rootView);
        updateButtons();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).mCurrentHeader.setText("MENU TEST!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mCurrentHeader.setText("Main Menu");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("MAINMENU.VALUES.CHANGED");
        getActivity().registerReceiver(mReceiver, filter);
    }

    private void updateButtons() {
        mFrontDoorLockButton.setButtonState(mApplication.values.mMainMenuFrontDoorOpen, false);
    }

    private void setMenuButtons(View rootView) {
        mRoomServicesButton = (ImageButton) rootView.findViewById(R.id.room_services);
        mRoomServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragment(1);
            }
        });

        mLightingButton = (ImageButton) rootView.findViewById(R.id.lighting);
        mLightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragment(2);
            }
        });

        mTemperatureButton = (ImageButton) rootView.findViewById(R.id.temp_image);
        mTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragment(3);
            }
        });

        mFrontDoorLockButton = (FlipMenuButton) rootView.findViewById(R.id.image_lock);
        mFrontDoorLockButton.setImageResources(R.drawable.frontdoor, R.drawable.frontdoor_active);
        mFrontDoorLockButton.changeStateWithoutAnimation(mApplication.values.mMainMenuFrontDoorOpen);
        mFrontDoorLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = (!mApplication.values.mMainMenuFrontDoorOpen) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.FRONTDOOR_OPEN, state)) {
                    mApplication.values.mMainMenuFrontDoorOpen = !mApplication.values.mMainMenuFrontDoorOpen;
                    mFrontDoorLockButton.setButtonState(mApplication.values.mMainMenuFrontDoorOpen, true);
                }
            }
        });
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    public class UpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MAINMENU.VALUES.CHANGED")) {
                updateButtons();
            }
        }
    }
}