/*
 * SmartHotel, created by NickGodov on 06.09.15 11:34.
 * Last modified: 01.09.15 16:43
 *
 * This software is protected by copyright law and international treaties.
 * Unauthorized reproduction or distribution of this program, or any portion of it, may result in severe
 * civil and criminal penalties, and will be prosecuted to the maximum extent possible under law.
 *
 */

package com.isosystems.smarthotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isosystems.smarthotel.utils.Indexes;

/**
 * Класс отвечает за фрагмент с Room Services.
 * Функционал:
 * 1. Инициализация и поддержка FlipMenuButton`s с Room Services
 * 2. Обработка нажатия на кнопки с изменением значений и отсылкой обновленных значений
 */
public class FragmentRoomServices extends Fragment implements View.OnClickListener {

    MyApplication mApplication;
    View rootView;

    // Кнопки Room Services
    public FlipMenuButton mDNDButton;
    public FlipMenuButton mGirlButton;
    public FlipMenuButton mCleanButton;
    public FlipMenuButton mWashButton;
    public FlipMenuButton mBarButton;
    public FlipMenuButton mFoodButton;

    // Ресивер для приема данных
    UpdatesReceiver mReceiver;

    public FragmentRoomServices() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_room_services,
                container, false);
        mApplication = (MyApplication) getActivity().getApplicationContext();
        // Инициализация ресивера
        mReceiver = new UpdatesReceiver();
        // Инициализация и настройка Room Services
        setFlipButtons(rootView);
        return rootView;
    }

    /**
     * Инициализация кнопок и установка начальных значений
     *
     * @param v
     */
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

    /**
     * При клике одну из кнопок Room Services:
     * 1.  Происходит попытка передать обновленные данные
     * 2.  Если данные переданы успешно, данные фактически обновляются:
     * 2а. Значение в values меняется
     * 2б. Меняется состояние и картинка кнопки
     *
     * @param v View, на который кликнули (в данном случае, одна из кнопок)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.do_not_disturb:
                int state = (!mApplication.values.mDNDButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_DND, state)) {
                    mApplication.values.mDNDButton = !mApplication.values.mDNDButton;
                    mDNDButton.setButtonState(mApplication.values.mDNDButton, true);
                }
                break;
            case R.id.girl_image:
                state = (!mApplication.values.mGirlButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_MAID, state)) {
                    mApplication.values.mGirlButton = !mApplication.values.mGirlButton;
                    mGirlButton.setButtonState(mApplication.values.mGirlButton, true);
                }
                break;
            case R.id.clean_image:
                state = (!mApplication.values.mCleanButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_CLEANER, state)) {
                    mApplication.values.mCleanButton = !mApplication.values.mCleanButton;
                    mCleanButton.setButtonState(mApplication.values.mCleanButton, true);
                }
                break;
            case R.id.wash_image:
                state = (!mApplication.values.mWashButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_WASH, state)) {
                    mApplication.values.mWashButton = !mApplication.values.mWashButton;
                    mWashButton.setButtonState(mApplication.values.mWashButton, true);
                }
                break;
            case R.id.bar_image:
                state = (!mApplication.values.mBarButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_MINIBAR, state)) {
                    mApplication.values.mBarButton = !mApplication.values.mBarButton;
                    mBarButton.setButtonState(mApplication.values.mBarButton, true);
                }
                break;
            case R.id.food_image:
                state = (!mApplication.values.mFoodButton) ? 1 : 0;
                if (((MainActivity) getActivity()).sendMessage(Indexes.RS_FOOD, state)) {
                    mApplication.values.mFoodButton = !mApplication.values.mFoodButton;
                    mFoodButton.setButtonState(mApplication.values.mFoodButton, true);
                }
                break;
        }
    }

    /**
     * 1. Устанавливается заголовок
     * 2. Настраивается и регистрируется receiver
     * 3. Обновляется состояние кнопок без анимации
     */
    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mCurrentHeader.setText("Room Services");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("RS.VALUES.CHANGED");
        getActivity().registerReceiver(mReceiver, filter);

        mDNDButton.changeStateWithoutAnimation(mApplication.values.mDNDButton);
        mGirlButton.changeStateWithoutAnimation(mApplication.values.mGirlButton);
        mCleanButton.changeStateWithoutAnimation(mApplication.values.mCleanButton);
        mWashButton.changeStateWithoutAnimation(mApplication.values.mWashButton);
        mBarButton.changeStateWithoutAnimation(mApplication.values.mBarButton);
        mFoodButton.changeStateWithoutAnimation(mApplication.values.mFoodButton);
    }

    /**
     * Дерегистрация ресивера
     */
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    /**
     * Обновление состояний и картинок кнопок
     * Т.к. метод вызывается из ресивера после обработки массива и обновления values
     * то нужно лишь обновить саму кнопку - values уже обновлены
     */
    private void updateRoomServices() {
        mDNDButton.setButtonState(mApplication.values.mDNDButton, true);
        mGirlButton.setButtonState(mApplication.values.mGirlButton, true);
        mCleanButton.setButtonState(mApplication.values.mCleanButton, true);
        mWashButton.setButtonState(mApplication.values.mWashButton, true);
        mBarButton.setButtonState(mApplication.values.mBarButton, true);
        mFoodButton.setButtonState(mApplication.values.mFoodButton, true);
    }

    /**
     * Обновление кнопок при приходе сообщения
     */
    public class UpdatesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("RS.VALUES.CHANGED")) {
                updateRoomServices();
            }
        }
    }
}