package com.isosystems.smarthotel;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.isosystems.smarthotel.utils.Indexes;
import com.isosystems.smarthotel.utils.MenuButton;
import com.isosystems.smarthotel.utils.Notifications;
import com.isosystems.smarthotel.utils.PercentTextView;

public class FragmentLight extends Fragment implements View.OnClickListener {

    SwitchButton mAllLightSwitchButton;
    SwitchButton mLightSwitchButton;
    SwitchButton mAutoSwitchButton;

    MenuButton mMainHallButton;
    MenuButton mBalconyButton;
    MenuButton mBathroomButton;
    MenuButton mBedroomButton;
    MenuButton mDressingRoomButton;

    UpdatesReceiver mReceiver;

    CurrentRoom mActiveRoom;

    int mLightMin = 0;
    int mLightMax = 100;
    int mLightChange = 10;
    int mAnimationTick = 20;

    PercentTextView mLightAmount;
    ProgressBar mCircularProgress;

    boolean isRunning = false;

    MyApplication mApplication;

    ImageView mPlusButton;
    ImageView mMinusButton;

    View rootView;

    public FragmentLight() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_light,
                container, false);

        mApplication = (MyApplication) getActivity().getApplicationContext();

        // Считывание из настроек
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mApplication);
        //light_min
        String s = prefs.getString("light_min", "0");
        try {
            mLightMin = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // light_max
        s = prefs.getString("light_max", "100");
        try {
            mLightMax = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // light_change
        s = prefs.getString("light_change", "10");
        try {
            mLightChange = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // light_animation_step_duration
        s = prefs.getString("light_animation_step_duration", "20");
        try {
            mAnimationTick = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (mLightMin > mLightMax) {
            mLightMin = mLightMax;
        }

        mLightAmount = (PercentTextView) rootView.findViewById(R.id.light_amount);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "mono.ttf");
        mLightAmount.setTypeface(font);

        mCircularProgress = (ProgressBar) rootView.findViewById(R.id.light_progress);
        mCircularProgress.setMax(mLightMax - mLightMin);

        mMainHallButton = (MenuButton) rootView.findViewById(R.id.bMainHall);
        mMainHallButton.setActiveState();
        mActiveRoom = CurrentRoom.MainHall;
        mMainHallButton.setOnClickListener(this);

        mBalconyButton = (MenuButton) rootView.findViewById(R.id.bBalcony);
        mBalconyButton.setInactiveState();
        mBalconyButton.setOnClickListener(this);

        mBathroomButton = (MenuButton) rootView.findViewById(R.id.bBathroom);
        mBathroomButton.setInactiveState();
        mBathroomButton.setOnClickListener(this);

        mBedroomButton = (MenuButton) rootView.findViewById(R.id.bBedroom);
        mBedroomButton.setInactiveState();
        mBedroomButton.setOnClickListener(this);

        mDressingRoomButton = (MenuButton) rootView.findViewById(R.id.bDressingRoom);
        mDressingRoomButton.setInactiveState();
        mDressingRoomButton.setOnClickListener(this);

        setSwitchButtons(rootView);

        mReceiver = new UpdatesReceiver();

        updateLightValue();
        updateLightSwitchValue();
        updateLightAutoValue();
        updateAllSwitchValue();

        mPlusButton = (ImageView) rootView.findViewById(R.id.plus_button);
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLightAmount(true);
            }
        });

        mMinusButton = (ImageView) rootView.findViewById(R.id.minus_button);
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLightAmount(false);
            }
        });

        return rootView;
    }

    // region Настройка показателей освещения
    private void setLightAmount(boolean increase) {
        switch (mActiveRoom) {
            case MainHall:
                int new_value = calculateNewLightValue(mApplication.values.mLightMainHallAmount, increase);
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_MAINHALL, new_value)) {
                    mApplication.values.mLightMainHallAmount =
                            changeLightAmount(mApplication.values.mLightMainHallAmount, increase);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case Balcony:
                new_value = calculateNewLightValue(mApplication.values.mLightBalconyAmount, increase);
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BALCONY, new_value)) {
                    mApplication.values.mLightBalconyAmount =
                            changeLightAmount(mApplication.values.mLightBalconyAmount, increase);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case Bathroom:
                new_value = calculateNewLightValue(mApplication.values.mLightBathroomAmount, increase);
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BATHROOM, new_value)) {
                    mApplication.values.mLightBathroomAmount =
                            changeLightAmount(mApplication.values.mLightBathroomAmount, increase);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case Bedroom:
                new_value = calculateNewLightValue(mApplication.values.mLightBedroomAmount, increase);
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BEDROOM, new_value)) {
                    mApplication.values.mLightBedroomAmount =
                            changeLightAmount(mApplication.values.mLightBedroomAmount, increase);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
            case DressingRoom:
                new_value = calculateNewLightValue(mApplication.values.mLightDressingRoomAmount, increase);
                if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_DRESSING_ROOM, new_value)) {
                    mApplication.values.mLightDressingRoomAmount =
                            changeLightAmount(mApplication.values.mLightDressingRoomAmount, increase);
                } else {
                    Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                }
                break;
        }
    }

    private int calculateNewLightValue (int value, boolean isIncrement) {
        if (isIncrement) {
            int increment = mLightChange;
            if (value + increment > mLightMax) {
                increment = mLightMax - value;
            }
            return value + increment;
        } else {
            int decrement = mLightChange;
            if (value - decrement < mLightMin) {
                decrement = value - mLightMin;
            }
            return value - decrement;
        }
    }

    private int changeLightAmount(int value, boolean increase) {
        if (increase) {

            int increment = mLightChange;

            // Если текущее значение + инкремент > 100
            // иначе уменьшаем инкремент, чтоб было равно 100
            if (value + increment > mLightMax) {
                increment = mLightMax - value;
            }
            if (increment == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mCircularProgress, "progress", value - mLightMin, (value + increment) - mLightMin);
            animation.setDuration(mAnimationTick * increment); //in milliseconds
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            isRunning = true;
            new Thread(new DynamicNumbers(value, increment, true)).start();
            animation.start();

            // новое значение = старое + инкремент
            value += increment;
        } else {
            int decrement = mLightChange;

            if (value - decrement < mLightMin) {
                decrement = value - mLightMin;
            }
            if (decrement == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mCircularProgress, "progress", value - mLightMin, (value - decrement) - mLightMin);
            animation.setDuration(mAnimationTick * decrement); //in milliseconds
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            isRunning = true;
            new Thread(new DynamicNumbers(value, decrement, false)).start();
            animation.start();

            // новое значение = старое - декремент
            value -= decrement;
        }

        return value;
    }

    public void updateLightValue() {
        switch (mActiveRoom) {
            case MainHall:
                mLightAmount.setPercentText(String.valueOf(mApplication.values.mLightMainHallAmount));
                mCircularProgress.setProgress(mApplication.values.mLightMainHallAmount - mLightMin);
                break;
            case Balcony:
                mLightAmount.setPercentText(String.valueOf(mApplication.values.mLightBalconyAmount));
                mCircularProgress.setProgress(mApplication.values.mLightBalconyAmount - mLightMin);
                break;
            case Bathroom:
                mLightAmount.setPercentText(String.valueOf(mApplication.values.mLightBathroomAmount));
                mCircularProgress.setProgress(mApplication.values.mLightBathroomAmount - mLightMin);
                break;
            case Bedroom:
                mLightAmount.setPercentText(String.valueOf(mApplication.values.mLightBedroomAmount));
                mCircularProgress.setProgress(mApplication.values.mLightBedroomAmount - mLightMin);
                break;
            case DressingRoom:
                mLightAmount.setPercentText(String.valueOf(mApplication.values.mLightDressingRoomAmount));
                mCircularProgress.setProgress(mApplication.values.mLightDressingRoomAmount - mLightMin);
                break;
        }
    }
    // endregion

    // region Настройка переключателей
    private void setSwitchButtons(View v) {
        mAllLightSwitchButton = (SwitchButton) v.findViewById(R.id.switch_all_light);
        mAllLightSwitchButton.setImageResources(R.drawable.all_lights_on, R.drawable.all_lights_off, R.drawable.all_unknown);
        mAllLightSwitchButton.setOnClickListener(mSwitchAllLightsListener);

        mLightSwitchButton = (SwitchButton) v.findViewById(R.id.switch_room_light);
        mLightSwitchButton.setImageResources(R.drawable.switch_on, R.drawable.switch_off, R.drawable.switch_unknown);
        mLightSwitchButton.setOnClickListener(mLightSwitchListener);

        mAutoSwitchButton = (SwitchButton) v.findViewById(R.id.switch_auto_light);
        mAutoSwitchButton.setImageResources(R.drawable.auto_on, R.drawable.auto_off, R.drawable.auto_unknown);
        mAutoSwitchButton.setOnClickListener(mSwitchAutoListener);
    }

    View.OnClickListener mSwitchAllLightsListener = new View.OnClickListener() {
        public void onClick(View v) {
            int state = (!mApplication.values.mLightAllOf) ? 1 : 0;
            if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_ALL_OFF, state)) {
                mApplication.values.mLightAllOf = !mApplication.values.mLightAllOf;
                mAllLightSwitchButton.setButtonState(mApplication.values.mLightAllOf);
            } else {
                Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
            }
        }
    };

        View.OnClickListener mSwitchAutoListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (mActiveRoom) {
                case MainHall:
                    int state = (!mApplication.values.mLightMainHallAuto) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_MAINHALL_AUTO, state)) {
                        mApplication.values.mLightMainHallAuto = !mApplication.values.mLightMainHallAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mLightMainHallAuto);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Balcony:
                    state = (!mApplication.values.mLightBalconyAuto) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BALCONY_AUTO, state)) {
                        mApplication.values.mLightBalconyAuto = !mApplication.values.mLightBalconyAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mLightBalconyAuto);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Bathroom:
                    state = (!mApplication.values.mLightBathroomAuto) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BATHROOM_AUTO, state)) {
                        mApplication.values.mLightBathroomAuto = !mApplication.values.mLightBathroomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mLightBathroomAuto);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Bedroom:
                    state = (!mApplication.values.mLightBedroomAuto) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BEDROOM_AUTO, state)) {
                        mApplication.values.mLightBedroomAuto = !mApplication.values.mLightBedroomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mLightBedroomAuto);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case DressingRoom:
                    state = (!mApplication.values.mLightDressingRoomAuto) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_DRESSING_ROOM_AUTO, state)) {
                        mApplication.values.mLightDressingRoomAuto = !mApplication.values.mLightDressingRoomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mLightDressingRoomAuto);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
            }
        }
    };

    private void updateAllSwitchValue() {
        mAllLightSwitchButton.setButtonState(mApplication.values.mLightAllOf);
    }

    public void updateLightAutoValue() {
        switch (mActiveRoom) {
            case MainHall:
                mAutoSwitchButton.setButtonState(mApplication.values.mLightMainHallAuto);
                break;
            case Balcony:
                mAutoSwitchButton.setButtonState(mApplication.values.mLightBalconyAuto);
                break;
            case Bathroom:
                mAutoSwitchButton.setButtonState(mApplication.values.mLightBathroomAuto);
                break;
            case Bedroom:
                mAutoSwitchButton.setButtonState(mApplication.values.mLightBedroomAuto);
                break;
            case DressingRoom:
                mAutoSwitchButton.setButtonState(mApplication.values.mLightDressingRoomAuto);
                break;
        }
    }

    public void updateLightSwitchValue() {
        switch (mActiveRoom) {
            case MainHall:
                mLightSwitchButton.setButtonState(mApplication.values.mLightMainHallSwitch);
                break;
            case Balcony:
                mLightSwitchButton.setButtonState(mApplication.values.mLightBalconySwitch);
                break;
            case Bathroom:
                mLightSwitchButton.setButtonState(mApplication.values.mLightBathroomSwitch);
                break;
            case Bedroom:
                mLightSwitchButton.setButtonState(mApplication.values.mLightBedroomSwitch);
                break;
            case DressingRoom:
                mLightSwitchButton.setButtonState(mApplication.values.mLightDressingRoomSwitch);
                break;
        }
    }

    View.OnClickListener mLightSwitchListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (mActiveRoom) {
                case MainHall:
                    int state = (!mApplication.values.mLightMainHallSwitch) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_MAINHALL_SWITCH, state)) {
                        mApplication.values.mLightMainHallSwitch = !mApplication.values.mLightMainHallSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mLightMainHallSwitch);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Balcony:
                    state = (!mApplication.values.mLightBalconySwitch) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BALCONY_SWITCH, state)) {
                        mApplication.values.mLightBalconySwitch = !mApplication.values.mLightBalconySwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mLightBalconySwitch);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Bathroom:
                    state = (!mApplication.values.mLightBathroomSwitch) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BATHROOM_SWITCH, state)) {
                        mApplication.values.mLightBathroomSwitch = !mApplication.values.mLightBathroomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mLightBathroomSwitch);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case Bedroom:
                    state = (!mApplication.values.mLightBedroomSwitch) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_BEDROOM_SWITCH, state)) {
                        mApplication.values.mLightBedroomSwitch = !mApplication.values.mLightBedroomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mLightBedroomSwitch);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
                case DressingRoom:
                    state = (!mApplication.values.mLightDressingRoomSwitch) ? 1 : 0;
                    if (((MainActivity) getActivity()).mBoundService != null && ((MainActivity) getActivity()).mBoundService.sendValue(Indexes.LIGHT_DRESSING_ROOM_SWITCH, state)) {
                        mApplication.values.mLightDressingRoomSwitch = !mApplication.values.mLightDressingRoomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mLightDressingRoomSwitch);
                    } else {
                        Notifications.showErrorCrouton(getActivity(), Globals.NOTIFICATION_NO_CONNECTION);
                    }
                    break;
            }
        }
    };

    // endregion

    // region Настройка динамического изменения значения света

    // background updating
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i = msg.getData().getInt("i");
                mLightAmount.setPercentText(String.valueOf(i));
            } catch (Exception err) {
            }
        }
    };

    public class DynamicNumbers implements Runnable {

        private int value;
        private int step;
        private boolean upwards;

        public DynamicNumbers(int value, int step, boolean upwards) {
            this.value = value;
            this.step = step;
            this.upwards = upwards;
        }

        public void run() {
            try {
                if (upwards) {
                    for (int i = value; i <= value + step && isRunning; i++) {
                        Bundle data = new Bundle();
                        data.putInt("i", i);
                        Message message = handler.obtainMessage();
                        message.setData(data);
                        handler.sendMessage(message);
                        Thread.sleep(mAnimationTick);
                    }
                } else {
                    for (int i = value; i >= value - step && isRunning; i--) {
                        Bundle data = new Bundle();
                        data.putInt("i", i);
                        Message message = handler.obtainMessage();
                        message.setData(data);
                        handler.sendMessage(message);
                        Thread.sleep(mAnimationTick);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    // endregion

    @Override
    public void onClick(View v) {

        mMainHallButton.setInactiveState();
        mBalconyButton.setInactiveState();
        mBathroomButton.setInactiveState();
        mBedroomButton.setInactiveState();
        mDressingRoomButton.setInactiveState();

        switch (v.getId()) {
            case R.id.bMainHall:
                mMainHallButton.setActiveState();
                mActiveRoom = CurrentRoom.MainHall;
                break;
            case R.id.bBalcony:
                mBalconyButton.setActiveState();
                mActiveRoom = CurrentRoom.Balcony;
                break;
            case R.id.bBathroom:
                mBathroomButton.setActiveState();
                mActiveRoom = CurrentRoom.Bathroom;
                break;
            case R.id.bBedroom:
                mBedroomButton.setActiveState();
                mActiveRoom = CurrentRoom.Bedroom;
                break;
            case R.id.bDressingRoom:
                mDressingRoomButton.setActiveState();
                mActiveRoom = CurrentRoom.DressingRoom;
                break;
        }
        updateLightValue();
        updateLightSwitchValue();
        updateLightAutoValue();
    }

    public class UpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LIGHT.VALUES.CHANGED")) {
                updateLightValue();
                updateLightSwitchValue();
                updateLightAutoValue();
                updateAllSwitchValue();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).mCurrentHeader.setText("Light setup");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("LIGHT.VALUES.CHANGED");
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    private enum CurrentRoom {
        MainHall,
        Balcony,
        Bathroom,
        Bedroom,
        DressingRoom
    }
}