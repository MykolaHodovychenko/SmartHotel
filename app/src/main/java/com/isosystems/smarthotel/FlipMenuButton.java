package com.isosystems.smarthotel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class FlipMenuButton extends ImageButton{

    Context mContext;

    int mDefaultImageResource;
    int mActiveImageResource;

    Animation mDefault_out;
    Animation mDefault_in;

    Animation mActive_out;
    Animation mActive_in;

    public Boolean buttonPressed = false;

    public void setImageResources(int def_image, int active_image) {
        mDefaultImageResource = def_image;
        mActiveImageResource  = active_image;
    }

    public FlipMenuButton(Context context) {
        super(context);
        mContext = context;
        setActiveStateAnimations();
        setDefaultStateAnimations();
        setDrawingCacheEnabled(true);
        buildDrawingCache();
    }
    public FlipMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setActiveStateAnimations();
        setDefaultStateAnimations();
        setDrawingCacheEnabled(true);
        buildDrawingCache();
    }
    public FlipMenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setActiveStateAnimations();
        setDefaultStateAnimations();
        setDrawingCacheEnabled(true);
        buildDrawingCache();
    }

    // Анимации для перехода в активное состояние
    private void setActiveStateAnimations() {
        mDefault_out = AnimationUtils.loadAnimation(mContext, R.anim.flipin);
        mDefault_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                FlipMenuButton.this.setImageResource(mDefaultImageResource);
                FlipMenuButton.this.startAnimation(mDefault_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mDefault_in = AnimationUtils.loadAnimation(mContext, R.anim.flipout);
    }

    // Анимация для перехода в дефолтное состояние
    private void setDefaultStateAnimations() {
        mActive_out = AnimationUtils.loadAnimation(mContext,R.anim.flipin);
        mActive_out.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {}

            public void onAnimationEnd(Animation animation){
                FlipMenuButton.this.setImageResource(mActiveImageResource);
                FlipMenuButton.this.startAnimation(mActive_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mActive_in = AnimationUtils.loadAnimation(mContext,R.anim.flipout);
    }

    public void changeButtonState() {
        setButtonState(!buttonPressed, true);
    }

    public void setButtonState (boolean state, boolean animation) {
        if (buttonPressed != state) {
            if (buttonPressed) {
                if (animation) FlipMenuButton.this.startAnimation(mDefault_out);
            } else {
                if (animation) FlipMenuButton.this.startAnimation(mActive_out);
            }
            buttonPressed = state;
        }
    }

    public void changeStateWithoutAnimation (boolean state) {
        if (buttonPressed != state) {
            if (buttonPressed) {
                FlipMenuButton.this.setImageResource(mDefaultImageResource);
            } else {
                FlipMenuButton.this.setImageResource(mActiveImageResource);
            }
            buttonPressed = state;
        }
    }

//    @Override
//    public void onClick(View v) {
//        if (mButtonActive) {
//            FlipMenuButton.this.startAnimation(mDefault_out);
//            mButtonActive = false;
//        } else {
//            FlipMenuButton.this.startAnimation(mActive_out);
//            mButtonActive = true;
//        }
//    }
}
