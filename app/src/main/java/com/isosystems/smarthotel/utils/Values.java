package com.isosystems.smarthotel.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.isosystems.smarthotel.FlipMenuButton;
import com.isosystems.smarthotel.FragmentLight;
import com.isosystems.smarthotel.FragmentTemperature;
import com.isosystems.smarthotel.SwitchButton;

public class Values {

    public int int_room_number;
    public int int_room_temperature;
    public TextView room_number;
    public TextView room_temperature;

    public boolean mDNDButton;
    public boolean mGirlButton;
    public boolean mCleanButton;
    public boolean mWashButton;
    public boolean mBarButton;
    public boolean mFoodButton;

    public int mLightMainHallAmount = 0;
    public int mLightBalconyAmount = 0;
    public int mLightBathroomAmount = 0;
    public int mLightBedroomAmount = 0;
    public int mLightDressingRoomAmount = 0;

    //public SwitchButton mLightAllOf;
    public boolean mLightAllOf = true;

    public Boolean mLightMainHallSwitch = true;
    public Boolean mLightBalconySwitch = true;
    public Boolean mLightBathroomSwitch = true;
    public Boolean mLightBedroomSwitch = true;
    public Boolean mLightDressingRoomSwitch = true;

    public Boolean mLightMainHallAuto = false;
    public Boolean mLightBalconyAuto = false;
    public Boolean mLightBathroomAuto = false;
    public Boolean mLightBedroomAuto = false;
    public Boolean mLightDressingRoomAuto = false;

    // TEMPERATURE / FAN

    public int mTempMainHallAmount = 15;
    public int mTempBalconyAmount = 15;
    public int mTempBathroomAmount = 15;
    public int mTempBedroomAmount = 15;
    public int mTempDressingRoomAmount = 15;

    public int mFanMainHallAmount = 0;
    public int mFanBalconyAmount = 0;
    public int mFanBathroomAmount = 0;
    public int mFanBedroomAmount = 0;
    public int mFanDressingRoomAmount = 0;

    //public SwitchButton mTempFanAllOf;
    public boolean mTempFanAllOf = true;

    public Boolean mTempFanMainHallSwitch = true;
    public Boolean mTempFanBalconySwitch = true;
    public Boolean mTempFanBathroomSwitch = true;
    public Boolean mTempFanBedroomSwitch = true;
    public Boolean mTempFanDressingRoomSwitch = true;

    public Boolean mTempFanMainHallAuto = false;
    public Boolean mTempFanBalconyAuto = false;
    public Boolean mTempFanBathroomAuto = false;
    public Boolean mTempFanBedroomAuto = false;
    public Boolean mTempFanDressingRoomAuto = false;

    public Values (Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        //light_min
        String s = prefs.getString("light_min", "0");
        int light_min = 0;
        try {
            light_min = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mLightMainHallAmount = light_min;
        mLightBalconyAmount = light_min;
        mLightBathroomAmount = light_min;
        mLightBedroomAmount = light_min;
        mLightDressingRoomAmount = light_min;

        // comfort_temp_min
        s = prefs.getString("comfort_temp_min", "15");
        int temp_min = 0;
        try {
            temp_min = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mTempMainHallAmount = temp_min;
        mTempBalconyAmount = temp_min;
        mTempBathroomAmount = temp_min;
        mTempBedroomAmount = temp_min;
        mTempDressingRoomAmount = temp_min;

        // comfort_temp_min
        s = prefs.getString("comfort_fan_min", "0");
        int fan_min = 0;
        try {
            fan_min = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mFanMainHallAmount = fan_min;
        mFanBalconyAmount = fan_min;
        mFanBathroomAmount = fan_min;
        mFanBedroomAmount = fan_min;
        mFanDressingRoomAmount = fan_min;
    }


    public void setRoomNumber() {
        room_number.setText("Room № " + String.valueOf(int_room_number));
    }

    public void setRoomTemperature () {
        room_temperature.setText(String.valueOf(int_room_temperature) + " °C");
    }

    public void processArray (String[] array, Context context) {

        boolean roomFragmentValuesChanged = false;
        boolean lightValuesChanged = false;
        boolean tempfanValuesChanged = false;

        for (int i=0; i < array.length; i++) {

            int new_value = -1;
            try {
                new_value = Integer.parseInt(array[i]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            switch (i) {
                case Indexes.ROOM_NUMBER:
                    if (new_value != int_room_number){
                        int_room_number = new_value;
                        setRoomNumber();
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.ROOM_TEMPERATURE:
                    if (new_value != int_room_temperature) {
                        int_room_temperature = new_value;
                        setRoomTemperature();
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_DND:
                    boolean b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mDNDButton) {
                        mDNDButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_MAID:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mGirlButton) {
                        mGirlButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_CLEANER:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mCleanButton) {
                        mCleanButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_WASH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mWashButton) {
                        mWashButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_MINIBAR:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mBarButton) {
                        mBarButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                case Indexes.RS_FOOD:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mFoodButton) {
                        mFoodButton = b;
                        roomFragmentValuesChanged = true;
                    }
                    break;
                // LIGHT
                case Indexes.LIGHT_MAINHALL:
                    if (new_value != mLightMainHallAmount) {
                        mLightMainHallAmount = new_value;
                        //fragmentLight.updateLightValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BALCONY:
                    if (new_value != mLightBalconyAmount) {
                        mLightBalconyAmount = new_value;
                        //fragmentLight.updateLightValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BATHROOM:
                    if (new_value != mLightBathroomAmount) {
                        mLightBathroomAmount = new_value;
                        //fragmentLight.updateLightValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BEDROOM:
                    if (new_value != mLightBedroomAmount) {
                        mLightBedroomAmount = new_value;
                        //fragmentLight.updateLightValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_DRESSING_ROOM:
                    if (new_value != mLightDressingRoomAmount) {
                        //fragmentLight.updateLightValue();
                        mLightDressingRoomAmount = new_value;
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_ALL_OFF:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightAllOf) {
                        mLightAllOf = b;
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_MAINHALL_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightMainHallSwitch) {
                        mLightMainHallSwitch = b;
                        //fragmentLight.updateLightSwitchValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BALCONY_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBalconySwitch) {
                        mLightBalconySwitch = b;
                        //fragmentLight.updateLightSwitchValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BATHROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBathroomSwitch) {
                        mLightBathroomSwitch = b;
                        //fragmentLight.updateLightSwitchValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BEDROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBedroomSwitch) {
                        mLightBedroomSwitch = b;
                        //fragmentLight.updateLightSwitchValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_DRESSING_ROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightDressingRoomSwitch) {
                        mLightDressingRoomSwitch = b;
                        //fragmentLight.updateLightSwitchValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_MAINHALL_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightMainHallAuto) {
                        mLightMainHallAuto = b;
                        //fragmentLight.updateLightAutoValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BALCONY_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBalconyAuto) {
                        mLightBalconyAuto = b;
                        //fragmentLight.updateLightAutoValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BATHROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBathroomAuto) {
                        mLightBathroomAuto = b;
                        //fragmentLight.updateLightAutoValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_BEDROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightBedroomAuto) {
                        mLightBedroomAuto = b;
                        //fragmentLight.updateLightAutoValue();
                        lightValuesChanged = true;
                    }
                    break;
                case Indexes.LIGHT_DRESSING_ROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mLightDressingRoomAuto) {
                        mLightDressingRoomAuto = b;
                        //fragmentLight.updateLightAutoValue();
                        lightValuesChanged = true;
                    }
                    break;
                // TEMPERATURE
                case Indexes.TEMP_MAINHALL:
                    if (new_value != mTempMainHallAmount) {
                        mTempMainHallAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMP_BALCONY:
                    if (new_value != mTempBalconyAmount) {
                        mTempBalconyAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMP_BATHROOM:
                    if (new_value != mTempBalconyAmount) {
                        mTempBalconyAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMP_BEDROOM:
                    if (new_value != mTempBedroomAmount) {
                        mTempBedroomAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMP_DRESSING_ROOM:
                    if (new_value != mTempDressingRoomAmount) {
                        mTempDressingRoomAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.FAN_MAINHALL:
                    if (new_value != mFanMainHallAmount) {
                        mFanMainHallAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.FAN_BALCONY:
                    if (new_value != mFanBalconyAmount) {
                        mFanBalconyAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.FAN_BATHROOM:
                    if (new_value != mFanBathroomAmount) {
                        mFanBathroomAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.FAN_BEDROOM:
                    if (new_value != mFanBedroomAmount) {
                        mFanBedroomAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.FAN_DRESSING_ROOM:
                    if (new_value != mFanDressingRoomAmount) {
                        mFanDressingRoomAmount = new_value;
                        //fragmentTemperature.updateTempFanValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_ALL_OFF:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanAllOf) {
                        mTempFanAllOf = b;
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_MAINHALL_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanMainHallSwitch) {
                        mTempFanMainHallSwitch = b;
                        //fragmentTemperature.updateTempFanSwitchValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BALCONY_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBalconySwitch) {
                        mTempFanBalconySwitch = b;
                        //fragmentTemperature.updateTempFanSwitchValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BATHROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBathroomSwitch) {
                        mTempFanBathroomSwitch = b;
                        //fragmentTemperature.updateTempFanSwitchValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BEDROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBedroomSwitch) {
                        mTempFanBedroomSwitch = b;
                        //fragmentTemperature.updateTempFanSwitchValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_DRESSING_ROOM_SWITCH:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanDressingRoomSwitch) {
                        mTempFanDressingRoomSwitch = b;
                        //fragmentTemperature.updateTempFanSwitchValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_MAINHALL_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanMainHallAuto) {
                        mTempFanMainHallAuto = b;
                        //fragmentTemperature.updateTempFanAutoValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BALCONY_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBalconyAuto) {
                        mTempFanBalconyAuto = b;
                        //fragmentTemperature.updateTempFanAutoValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BATHROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBathroomAuto) {
                        mTempFanBathroomAuto = b;
                        //fragmentTemperature.updateTempFanAutoValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_BEDROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanBedroomAuto) {
                        mTempFanBedroomAuto = b;
                        //fragmentTemperature.updateTempFanAutoValue();
                        tempfanValuesChanged = true;
                    }
                    break;
                case Indexes.TEMPFAN_DRESSING_ROOM_AUTO:
                    b = (new_value > 0) ? Boolean.TRUE : Boolean.FALSE;
                    if (b != mTempFanDressingRoomAuto) {
                        mTempFanDressingRoomAuto = b;
                        //fragmentTemperature.updateTempFanAutoValue();
                        tempfanValuesChanged = true;
                    }
                    break;
            }
        }

        sendBroadcasts(roomFragmentValuesChanged,lightValuesChanged,tempfanValuesChanged, context);
    }

    private void sendBroadcasts(boolean room_service,boolean light, boolean temp_fan, Context context) {
        if (room_service) {
            Intent i = new Intent();
            i.setAction("RS.VALUES.CHANGED");
            context.sendBroadcast(i);
        }
        if (light) {
            Intent i = new Intent();
            i.setAction("LIGHT.VALUES.CHANGED");
            context.sendBroadcast(i);

        }
        if (temp_fan) {
            Intent i = new Intent();
            i.setAction("TEMPFAN.VALUES.CHANGED");
            context.sendBroadcast(i);

        }
    }

}
