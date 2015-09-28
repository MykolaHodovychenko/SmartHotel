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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.isosystems.smarthotel.utils.DegreeTextView;
import com.isosystems.smarthotel.utils.Indexes;
import com.isosystems.smarthotel.utils.MenuButton;

public class FragmentTemperature extends Fragment implements View.OnClickListener {

    SwitchButton mAllTempFanSwitchButton;
    SwitchButton mLightSwitchButton;
    SwitchButton mAutoSwitchButton;

    ComfortWindowMode mWindowMode;

    int mTempMin       = 15;
    int mTempMax       = 40;
    int mTempChange    = 1;
    int mFanMin        = 0;
    int mFanMax        = 100;
    int mFanChange     = 10;
    int mAnimationTick = 20;

    ViewFlipper mFlipper;
    DegreeTextView mTempAmount;

    ProgressBar mTempProgress;
    ProgressBar mFanProgress;

    UpdatesReceiver mReceiver;

    View rootView;

    boolean isRunning = false;

    CurrentRoom mActiveRoom;

    ImageView mTempUpButton;
    MyApplication mApplication;
    ImageView mTempDownButton;

    ImageView mFanUpButton;
    ImageView mFanDownButton;

    // Кнопки
    MenuButton mMainHallButton;
    MenuButton mBalconyButton;
    MenuButton mBathroomButton;
    MenuButton mBedroomButton;
    MenuButton mDressingRoomButton;

    FragmentTransaction mFragmentTransaction;

    public FragmentTemperature() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_temperature,
                container, false);

        mApplication = (MyApplication) getActivity().getApplicationContext();

        // Считывание из настроек
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mApplication);

        // comfort_temp_min
        String s = prefs.getString("comfort_temp_min", "15");
        try {
            mTempMin = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_temp_max
        s = prefs.getString("comfort_temp_max", "40");
        try {
            mTempMax = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_temp_change
        s = prefs.getString("comfort_temp_change", "1");
        try {
            mTempChange = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_temp_min
        s = prefs.getString("comfort_fan_min", "0");
        try {
            mFanMin = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_temp_max
        s = prefs.getString("comfort_fan_max", "100");
        try {
            mFanMax = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_temp_change
        s = prefs.getString("comfort_fan_change", "10");
        try {
            mFanChange = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // comfort_animation_step_duration
        s = prefs.getString("comfort_animation_step_duration", "20");
        try {
            mAnimationTick = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mReceiver = new UpdatesReceiver();

        // Установка переключателя центральных кругов
        mFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipper);

        // Установка режима окна, изначально - температура
        mWindowMode = ComfortWindowMode.TEMPERATURE;

        // Показания температуры
        mTempAmount = (DegreeTextView) rootView.findViewById(R.id.temp_amount);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "mono.ttf");
        mTempAmount.setTypeface(font);

        // Прогресс-бары
        mTempProgress = (ProgressBar) rootView.findViewById(R.id.temp_progress);
        mTempProgress.setMax(mTempMax);

        mFanProgress = (ProgressBar) rootView.findViewById(R.id.fan_progress);
        mFanProgress.setMax(mFanMax);

        // Установка кнопок +\- для режимов
        setMenuButtons();
        setSwitchButtons(rootView);
        setTemperatureButtons(rootView);
        setFanButtons(rootView);


        return rootView;
    }

    private void setMenuButtons() {
        mMainHallButton = (MenuButton) rootView.findViewById(R.id.tf_mainhall);
        mMainHallButton.setActiveState();
        mActiveRoom = CurrentRoom.MainHall;
        mMainHallButton.setOnClickListener(this);

        mBalconyButton = (MenuButton) rootView.findViewById(R.id.tf_balcony);
        mBalconyButton.setInactiveState();
        mBalconyButton.setOnClickListener(this);

        mBathroomButton = (MenuButton) rootView.findViewById(R.id.tf_bathroom);
        mBathroomButton.setInactiveState();
        mBathroomButton.setOnClickListener(this);

        mBedroomButton = (MenuButton) rootView.findViewById(R.id.tf_bedroom);
        mBedroomButton.setInactiveState();
        mBedroomButton.setOnClickListener(this);

        mDressingRoomButton = (MenuButton) rootView.findViewById(R.id.tf_dressroom);
        mDressingRoomButton.setInactiveState();
        mDressingRoomButton.setOnClickListener(this);
    }

    // Переход из режима "Вентилятор" в режим "Температура"
    private void switchToTempMode() {
        //mTempAmount.setVisibility(View.VISIBLE);
        mWindowMode = ComfortWindowMode.TEMPERATURE;
        mFlipper.showNext();
        updateTempFanValue();
    }

    // Переход из режима "Температура" в режим "Вентилятор"
    private void switchToFanMode() {
        mWindowMode = ComfortWindowMode.FAN;
        mFlipper.showPrevious();
        //mTempAmount.setVisibility(View.INVISIBLE);
        updateTempFanValue();
    }

    private void setSwitchButtons(View v) {
        mAllTempFanSwitchButton = (SwitchButton) v.findViewById(R.id.switch_all_light);
        mAllTempFanSwitchButton.setImageResources(R.drawable.all_lights_on, R.drawable.all_lights_off, R.drawable.all_unknown);
        mAllTempFanSwitchButton.setOnClickListener(mSwitchAllTempFanListener);

        mLightSwitchButton = (SwitchButton) v.findViewById(R.id.switch_room_light);
        mLightSwitchButton.setImageResources(R.drawable.switch_on, R.drawable.switch_off, R.drawable.switch_unknown);
        mLightSwitchButton.setOnClickListener(mTempFanSwitchListener);

        mAutoSwitchButton = (SwitchButton) v.findViewById(R.id.switch_auto_light);
        mAutoSwitchButton.setImageResources(R.drawable.auto_on, R.drawable.auto_off, R.drawable.auto_unknown);
        mAutoSwitchButton.setOnClickListener(mSwitchAutoListener);
    }

    View.OnClickListener mSwitchAllTempFanListener = new View.OnClickListener() {
        public void onClick(View v) {
            int state = (!mApplication.values.mTempFanAllOf) ? 1 : 0;
            if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_ALL_OFF, state)) {
                mApplication.values.mTempFanAllOf = !mApplication.values.mTempFanAllOf;
                mAllTempFanSwitchButton.setButtonState(mApplication.values.mTempFanAllOf);
            }
        }
    };

    View.OnClickListener mSwitchAutoListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (mActiveRoom) {
                case MainHall:
                    int state = (!mApplication.values.mTempFanMainHallAuto) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_MAINHALL_AUTO, state)) {
                        mApplication.values.mTempFanMainHallAuto = !mApplication.values.mTempFanMainHallAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mTempFanMainHallAuto);
                    }
                    break;
                case Balcony:
                    state = (!mApplication.values.mTempFanBalconyAuto) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BALCONY_AUTO, state)) {
                        mApplication.values.mTempFanBalconyAuto = !mApplication.values.mTempFanBalconyAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBalconyAuto);
                    }
                    break;
                case Bathroom:
                    state = (!mApplication.values.mTempFanBathroomAuto) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BATHROOM_AUTO, state)) {
                        mApplication.values.mTempFanBathroomAuto = !mApplication.values.mTempFanBathroomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBathroomAuto);
                    }
                    break;
                case Bedroom:
                    state = (!mApplication.values.mTempFanBedroomAuto) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BEDROOM_AUTO, state)) {
                        mApplication.values.mTempFanBedroomAuto = !mApplication.values.mTempFanBedroomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBedroomAuto);
                    }
                    break;
                case DressingRoom:
                    state = (!mApplication.values.mTempFanDressingRoomAuto) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_DRESSING_ROOM_AUTO, state)) {
                        mApplication.values.mTempFanDressingRoomAuto = !mApplication.values.mTempFanDressingRoomAuto;
                        mAutoSwitchButton.setButtonState(mApplication.values.mTempFanDressingRoomAuto);
                    }
                    break;
            }
        }
    };

    View.OnClickListener mTempFanSwitchListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (mActiveRoom) {
                case MainHall:
                    int state = (!mApplication.values.mTempFanMainHallSwitch) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_MAINHALL_SWITCH, state)) {
                        mApplication.values.mTempFanMainHallSwitch = !mApplication.values.mTempFanMainHallSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mTempFanMainHallSwitch);
                    }
                    break;
                case Balcony:
                    state = (!mApplication.values.mTempFanBalconySwitch) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BALCONY_SWITCH, state)) {
                        mApplication.values.mTempFanBalconySwitch = !mApplication.values.mTempFanBalconySwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mTempFanBalconySwitch);
                    }
                    break;
                case Bathroom:
                    state = (!mApplication.values.mTempFanBathroomSwitch) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BATHROOM_SWITCH, state)) {
                        mApplication.values.mTempFanBathroomSwitch = !mApplication.values.mTempFanBathroomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mTempFanBathroomSwitch);
                    }
                    break;
                case Bedroom:
                    state = (!mApplication.values.mTempFanBedroomSwitch) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_BEDROOM_SWITCH, state)) {
                        mApplication.values.mTempFanBedroomSwitch = !mApplication.values.mTempFanBedroomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mTempFanBedroomSwitch);
                    }
                    break;
                case DressingRoom:
                    state = (!mApplication.values.mTempFanDressingRoomSwitch) ? 1 : 0;
                    if (((MainActivity)getActivity()).sendMessage(Indexes.TEMPFAN_DRESSING_ROOM_SWITCH, state)) {
                        mApplication.values.mTempFanDressingRoomSwitch = !mApplication.values.mTempFanDressingRoomSwitch;
                        mLightSwitchButton.setButtonState(mApplication.values.mTempFanDressingRoomSwitch);
                    }
                    break;
            }
        }
    };

    public void updateTempFanValue() {
        if (mWindowMode == ComfortWindowMode.TEMPERATURE) {
            switch (mActiveRoom) {
                case MainHall:
                    mTempAmount.setDegreeText(String.valueOf(mApplication.values.mTempMainHallAmount));
                    mTempProgress.setProgress(mApplication.values.mTempMainHallAmount - mTempMin);
                    break;
                case Balcony:
                    mTempAmount.setDegreeText(String.valueOf(mApplication.values.mTempBalconyAmount));
                    mTempProgress.setProgress(mApplication.values.mTempBalconyAmount - mTempMin);
                    break;
                case Bathroom:
                    mTempAmount.setDegreeText(String.valueOf(mApplication.values.mTempBathroomAmount));
                    mTempProgress.setProgress(mApplication.values.mTempBathroomAmount - mTempMin);
                    break;
                case Bedroom:
                    mTempAmount.setDegreeText(String.valueOf(mApplication.values.mTempBedroomAmount));
                    mTempProgress.setProgress(mApplication.values.mTempBedroomAmount - mTempMin);
                    break;
                case DressingRoom:
                    mTempAmount.setDegreeText(String.valueOf(mApplication.values.mTempDressingRoomAmount));
                    mTempProgress.setProgress(mApplication.values.mTempDressingRoomAmount - mTempMin);
                    break;
            }
        } else if (mWindowMode == ComfortWindowMode.FAN) {
            switch (mActiveRoom) {
                case MainHall:
                    mFanProgress.setProgress(mApplication.values.mFanMainHallAmount - mFanMin);
                    break;
                case Balcony:
                    mFanProgress.setProgress(mApplication.values.mFanBalconyAmount - mFanMin);
                    break;
                case Bathroom:
                    mFanProgress.setProgress(mApplication.values.mFanBathroomAmount - mFanMin);
                    break;
                case Bedroom:
                    mFanProgress.setProgress(mApplication.values.mFanBedroomAmount - mFanMin);
                    break;
                case DressingRoom:
                    mFanProgress.setProgress(mApplication.values.mFanDressingRoomAmount - mFanMin);
                    break;
            }
        }
    }









    @Override
    public void onClick(View v) {

        mMainHallButton.setInactiveState();
        mBalconyButton.setInactiveState();
        mBathroomButton.setInactiveState();
        mBedroomButton.setInactiveState();
        mDressingRoomButton.setInactiveState();

        switch (v.getId()) {
            case R.id.tf_mainhall:
                mMainHallButton.setActiveState();
                mActiveRoom = CurrentRoom.MainHall;
                break;
            case R.id.tf_balcony:
                mBalconyButton.setActiveState();
                mActiveRoom = CurrentRoom.Balcony;
                break;
            case R.id.tf_bathroom:
                mBathroomButton.setActiveState();
                mActiveRoom = CurrentRoom.Bathroom;
                break;
            case R.id.tf_bedroom:
                mBedroomButton.setActiveState();
                mActiveRoom = CurrentRoom.Bedroom;
                break;
            case R.id.tf_dressroom:
                mDressingRoomButton.setActiveState();
                mActiveRoom = CurrentRoom.DressingRoom;
                break;
        }

        updateTempFanValue();
        updateTempFanAutoValue();
        updateTempFanSwitchValue();
    }

    // region >>> Настройка кнопок для разных режимов

    // Настройка кнопок для режима "Температура"
    private void setTemperatureButtons(View rootView) {
        //Кнопка "Увеличить температуру"
        mTempUpButton = (ImageView) rootView.findViewById(R.id.temp_up);
        mTempUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindowMode == ComfortWindowMode.FAN) {
                    switchToTempMode();
                }
                setTempAmount(true);
            }
        });

        //Кнопка "Уменьшить температуру"
        mTempDownButton = (ImageView) rootView.findViewById(R.id.temp_down);
        mTempDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindowMode == ComfortWindowMode.FAN) {
                    switchToTempMode();
                }
                setTempAmount(false);
            }
        });
    }

    public void updateTempFanAutoValue() {
        switch (mActiveRoom) {
            case MainHall:
                mAutoSwitchButton.setButtonState(mApplication.values.mTempFanMainHallAuto);
                break;
            case Balcony:
                mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBalconyAuto);
                break;
            case Bathroom:
                mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBathroomAuto);
                break;
            case Bedroom:
                mAutoSwitchButton.setButtonState(mApplication.values.mTempFanBedroomAuto);
                break;
            case DressingRoom:
                mAutoSwitchButton.setButtonState(mApplication.values.mTempFanDressingRoomAuto);
                break;
        }
    }

    public void updateTempFanSwitchValue() {
        switch (mActiveRoom) {
            case MainHall:
                mLightSwitchButton.setButtonState(mApplication.values.mTempFanMainHallSwitch);
                break;
            case Balcony:
                mLightSwitchButton.setButtonState(mApplication.values.mTempFanBalconySwitch);
                break;
            case Bathroom:
                mLightSwitchButton.setButtonState(mApplication.values.mTempFanBathroomSwitch);
                break;
            case Bedroom:
                mLightSwitchButton.setButtonState(mApplication.values.mTempFanBedroomSwitch);
                break;
            case DressingRoom:
                mLightSwitchButton.setButtonState(mApplication.values.mTempFanDressingRoomSwitch);
                break;
        }
    }

    // Настройка кнопок для режима "Вентиляция"
    private void setFanButtons(View rootView) {
        // Кнопка "Увеличить мощность вентилятора"
        mFanUpButton = (ImageView) rootView.findViewById(R.id.fan_up);
        mFanUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если мы находимя в режиме "Температура", то сменить режим
                if (mWindowMode == ComfortWindowMode.TEMPERATURE) {
                    switchToFanMode();
                }
                setFanAmount(true);
            }
        });

        // Кнопка "Уменьшить мощность вентилятора"
        mFanDownButton = (ImageView) rootView.findViewById(R.id.fan_down);
        mFanDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindowMode == ComfortWindowMode.TEMPERATURE) {
                    switchToFanMode();
                }
                setFanAmount(false);
            }
        });

    }

    private void setTempAmount(boolean increase) {
        switch (mActiveRoom) {
            case MainHall:
                int new_value = calculateNewTempValue(mApplication.values.mTempMainHallAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.TEMP_MAINHALL, new_value)) {
                    mApplication.values.mTempMainHallAmount =
                            changeTempAmount(mApplication.values.mTempMainHallAmount, increase);
                }
                break;
            case Balcony:
                new_value = calculateNewTempValue(mApplication.values.mTempBalconyAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.TEMP_BALCONY, new_value)) {
                    mApplication.values.mTempBalconyAmount =
                            changeTempAmount(mApplication.values.mTempBalconyAmount, increase);
                }
                break;
            case Bathroom:
                new_value = calculateNewTempValue(mApplication.values.mTempBathroomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.TEMP_BATHROOM, new_value)) {
                    mApplication.values.mTempBathroomAmount =
                            changeTempAmount(mApplication.values.mTempBathroomAmount, increase);
                }
                break;
            case Bedroom:
                new_value = calculateNewTempValue(mApplication.values.mTempBedroomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.TEMP_BEDROOM, new_value)) {
                    mApplication.values.mTempBedroomAmount =
                            changeTempAmount(mApplication.values.mTempBedroomAmount, increase);
                }
                break;
            case DressingRoom:
                new_value = calculateNewTempValue(mApplication.values.mTempDressingRoomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.TEMP_DRESSING_ROOM, new_value)) {
                    mApplication.values.mTempDressingRoomAmount =
                            changeTempAmount(mApplication.values.mTempDressingRoomAmount, increase);
                }
        }
    }

    private void setFanAmount(boolean increase) {
        switch (mActiveRoom) {
            case MainHall:
                int new_value = calculateNewFanValue(mApplication.values.mFanMainHallAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.FAN_MAINHALL, new_value)) {
                    mApplication.values.mFanMainHallAmount =
                            changeFanAmount(mApplication.values.mFanMainHallAmount, increase);
                }
                break;
            case Balcony:
                new_value = calculateNewFanValue(mApplication.values.mFanBalconyAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.FAN_BALCONY, new_value)) {
                    mApplication.values.mFanBalconyAmount =
                            changeFanAmount(mApplication.values.mFanBalconyAmount, increase);
                }
                break;
            case Bathroom:
                new_value = calculateNewFanValue(mApplication.values.mFanBathroomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.FAN_BATHROOM, new_value)) {
                    mApplication.values.mFanBathroomAmount =
                            changeFanAmount(mApplication.values.mFanBathroomAmount, increase);
                }
                break;
            case Bedroom:
                new_value = calculateNewFanValue(mApplication.values.mFanBedroomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.FAN_BEDROOM, new_value)) {
                    mApplication.values.mFanBedroomAmount =
                            changeFanAmount(mApplication.values.mFanBedroomAmount, increase);
                }
                break;
            case DressingRoom:
                new_value = calculateNewFanValue(mApplication.values.mFanDressingRoomAmount, increase);
                if (((MainActivity)getActivity()).sendMessage(Indexes.FAN_DRESSING_ROOM, new_value)) {
                    mApplication.values.mFanDressingRoomAmount =
                            changeFanAmount(mApplication.values.mFanDressingRoomAmount, increase);
                }
                break;
        }
    }

    private int changeFanAmount(int value, boolean increase) {
        if (increase) {
            int increment = mFanChange;

            // Если текущее значение + инкремент > 100
            // иначе уменьшаем инкремент, чтоб было равно 100
            if (value + increment > mFanMax) {
                increment = mFanMax - value;
            }
            if (increment == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mFanProgress, "progress", value - mFanMin, (value + increment) - mFanMin);
            animation.setDuration(mAnimationTick * increment); //in milliseconds
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            isRunning = true;
            //new Thread(new DynamicNumbers(value, increment, true)).start();
            animation.start();

            // новое значение = старое + инкремент
            value += increment;
        } else {
            int decrement = mFanChange;
            if (value - decrement < mFanMin) {
                decrement = value - mFanMin;
            }
            if (decrement == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mFanProgress, "progress", value - mFanMin, (value - decrement) - mFanMin);
            animation.setDuration(mAnimationTick * decrement); //in milliseconds
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            isRunning = true;
            //new Thread(new DynamicNumbers(value, decrement, false)).start();
            animation.start();

            // новое значение = старое - декремент
            value -= decrement;
        }

        return value;
    }

    private int changeTempAmount(int value, boolean increase) {
        if (increase) {
            int increment = mTempChange;

            // Если текущее значение + инкремент > 100
            // иначе уменьшаем инкремент, чтоб было равно 100
            if (value + increment > mTempMax) {
                increment = mTempMax - value;
            }
            if (increment == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mTempProgress, "progress", value - mTempMin, (value + increment) - mTempMin);
            animation.setDuration(mAnimationTick * increment); //in milliseconds
            animation.setInterpolator(new LinearOutSlowInInterpolator());
            isRunning = true;
            new Thread(new DynamicNumbers(value, increment, true)).start();
            animation.start();

            // новое значение = старое + инкремент
            value += increment;
        } else {
            int decrement = mTempChange;
            if (value - decrement < mTempMin) {
                decrement = value - mTempMin;
            }
            if (decrement == 0) return value;

            // Анимируем изменение прогресс бара и цифр
            ObjectAnimator animation = ObjectAnimator.ofInt(mTempProgress, "progress", value - mTempMin, (value - decrement) - mTempMin);
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


    private int calculateNewTempValue (int value, boolean isIncrement) {
        if (isIncrement) {
            int increment = mTempChange;
            if (value + increment > mTempMax) {
                increment = mTempMax - value;
            }
            return value + increment;
        } else {
            int decrement = mTempChange;
            if (value - decrement < mTempMin) {
                decrement = value - mTempMin;
            }
            return value - decrement;
        }
    }

    private int calculateNewFanValue (int value, boolean isIncrement) {
        if (isIncrement) {
            int increment = mFanChange;
            if (value + increment > mFanMax) {
                increment = mFanMax - value;
            }
            return value + increment;
        } else {
            int decrement = mFanChange;
            if (value - decrement < mFanMin) {
                decrement = value - mFanMin;
            }
            return value - decrement;
        }
    }

    // endregion


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
            }
        }
    }

    // background updating
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i = msg.getData().getInt("i");
                mTempAmount.setDegreeText(String.valueOf(i));
            } catch (Exception err) {
            }
        }
    };

    private void updateTempFanAllSwitch() {
        mAllTempFanSwitchButton.setButtonState(mApplication.values.mTempFanAllOf);
    }

    public class UpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("TEMPFAN.VALUES.CHANGED")) {
                updateTempFanValue();
                updateTempFanSwitchValue();
                updateTempFanAutoValue();
                updateTempFanAllSwitch();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).mCurrentHeader.setText("Comfort setup");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("TEMPFAN.VALUES.CHANGED");
        getActivity().registerReceiver(mReceiver, filter);

        updateTempFanValue();
        updateTempFanAutoValue();
        updateTempFanSwitchValue();
        updateTempFanAllSwitch();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    // endregion

    // region Перечисления

    public enum ComfortWindowMode {
        TEMPERATURE,
        FAN
    }

    private enum CurrentRoom {
        MainHall,
        Balcony,
        Bathroom,
        Bedroom,
        DressingRoom
    }
    // endregion


}