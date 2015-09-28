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
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.isosystems.smarthotel.utils.Indexes;

public class FragmentMainMenu extends Fragment {

    ImageButton mRoomServicesButton;
    ImageButton mLightingButton;
    ImageButton mTemperatureButton;
    ImageButton mShowerButton;
    ImageButton mGuardButton;
    FlipMenuButton mFrontDoorLockButton;

    int mMenuButtonRadius = 140;

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

        float density = getActivity().getResources().getDisplayMetrics().density;
        if (density == 2.0f) {
            mMenuButtonRadius = 275;
        }

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

        mShowerButton = (ImageButton) rootView.findViewById(R.id.image_shower);
        mShowerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        if (v instanceof ImageButton) {
                            ((ImageButton)v).setImageResource(R.drawable.menu_shower_deny);
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v instanceof ImageButton) {
                        ((ImageButton)v).setImageResource(R.drawable.menu_shower);
                    }
                }
                return false;
            }
        });

        mGuardButton = (ImageButton) rootView.findViewById(R.id.image_guard);
        mGuardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        if (v instanceof ImageButton) {
                            ((ImageButton)v).setImageResource(R.drawable.menu_guard_deny);
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v instanceof ImageButton) {
                        ((ImageButton)v).setImageResource(R.drawable.menu_guard);
                    }
                }
                return false;
            }
        });

        mRoomServicesButton = (ImageButton) rootView.findViewById(R.id.room_services);
        mRoomServicesButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        if (v instanceof ImageButton) {
                            ((ImageButton)v).setImageResource(R.drawable.menu_rs_pressed);
                        }
                        ((MainActivity) getActivity()).changeFragment(1);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v instanceof ImageButton) {
                        ((ImageButton)v).setImageResource(R.drawable.menu_rs);
                    }
                }
                return false;
            }
        });

        mLightingButton = (ImageButton) rootView.findViewById(R.id.lighting);
        mLightingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        if (v instanceof ImageButton) {
                            ((ImageButton)v).setImageResource(R.drawable.menu_light_pressed);
                        }
                        ((MainActivity) getActivity()).changeFragment(2);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v instanceof ImageButton) {
                        ((ImageButton)v).setImageResource(R.drawable.menu_light);
                    }
                }
                return false;
            }
        });

        mTemperatureButton = (ImageButton) rootView.findViewById(R.id.temp_image);
        mTemperatureButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        if (v instanceof ImageButton) {
                            ((ImageButton)v).setImageResource(R.drawable.menu_temp_pressed);
                        }
                        ((MainActivity) getActivity()).changeFragment(3);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v instanceof ImageButton) {
                        ((ImageButton)v).setImageResource(R.drawable.menu_temp);
                    }
                }
                return false;
            }
        });

        mFrontDoorLockButton = (FlipMenuButton) rootView.findViewById(R.id.image_lock);
        mFrontDoorLockButton.setImageResources(R.drawable.frontdoor, R.drawable.frontdoor_active);
        mFrontDoorLockButton.changeStateWithoutAnimation(mApplication.values.mMainMenuFrontDoorOpen);
        mFrontDoorLockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Rect r = new Rect();
                    v.getGlobalVisibleRect(r);
                    if (inCircle(event, mMenuButtonRadius, r.centerX(), r.centerY())) {
                        int state = (!mApplication.values.mMainMenuFrontDoorOpen) ? 1 : 0;
                        if (((MainActivity) getActivity()).sendMessage(Indexes.FRONTDOOR_OPEN, state)) {
                            mApplication.values.mMainMenuFrontDoorOpen = !mApplication.values.mMainMenuFrontDoorOpen;
                            mFrontDoorLockButton.setButtonState(mApplication.values.mMainMenuFrontDoorOpen, true);
                        }
                    }
                }
                return false;
            }
        });
    }

    public boolean inCircle(MotionEvent e, int radius, float x, float y) {
        float dx = e.getRawX() - x;
        float dy = e.getRawY() - y;
        double d = Math.sqrt((dx * dx) + (dy * dy));
        if(d < radius)
            return true;
        return false;
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