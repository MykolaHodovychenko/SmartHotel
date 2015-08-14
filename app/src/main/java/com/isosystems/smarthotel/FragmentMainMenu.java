package com.isosystems.smarthotel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentMainMenu extends Fragment {

    ImageButton mRoomServicesButton;
    ImageButton mLightingButton;
    ImageButton mTemperatureButton;


    public FragmentMainMenu() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_menu,
                container, false);


        setMenuButtons(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) {
            ((MainActivity)activity).mCurrentHeader.setText("MENU TEST!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).mCurrentHeader.setText("Main Menu");
        }
    }

    private void setMenuButtons(View rootView) {
        mRoomServicesButton = (ImageButton) rootView.findViewById(R.id.room_services);
        mRoomServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeFragment(1);
                //((MainActivity)getActivity()).changeFragment(1);
//                ((MainActivity)getActivity()).sendMessage("YAHOO!");
            }
        });

        mLightingButton = (ImageButton) rootView.findViewById(R.id.lighting);
        mLightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeFragment(2);
            }
        });

        mTemperatureButton = (ImageButton) rootView.findViewById(R.id.temp_image);
        mTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeFragment(3);
            }
        });
    }
}