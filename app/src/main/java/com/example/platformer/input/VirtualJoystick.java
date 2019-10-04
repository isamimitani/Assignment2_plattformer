package com.example.platformer.input;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.platformer.R;
import com.example.platformer.utils.Util;

public class VirtualJoystick extends InputManager {

    public static final String TAG = "VirtualJoystick";

    private int _maxDistance = 0;
    private float _startingPositionX = 0f;
    private float _startingPositionY = 0f;


    public VirtualJoystick(View view) {
        view.findViewById(R.id.joystick_region)
                .setOnTouchListener(new JoystickTouchListener());
        view.findViewById(R.id.button_region)
                .setOnTouchListener(new ActionButtonTouchListener());
        _maxDistance = Util.dpToPx(48*2); //48dp = minimum hit target. maxDistance is in pixels.
        Log.d(TAG, "MaxDistance (pixels): " + _maxDistance);
    }

    private class ActionButtonTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event){
            int action = event.getActionMasked();
            if(action == MotionEvent.ACTION_DOWN){
                _isJumping = true;
            }else if(action == MotionEvent.ACTION_UP){
                _isJumping = false;
            }
            return true;
        }
    }

    private class JoystickTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event){
            int action = event.getActionMasked();
            if(action == MotionEvent.ACTION_DOWN){
                _startingPositionX = event.getX(0);
                _startingPositionY = event.getY(0);
            }else if(action == MotionEvent.ACTION_UP){
                _horizontalFactor = 0.0f;
                _verticalFactor = 0.0f;
            }else if(action == MotionEvent.ACTION_MOVE){
                //get the proportion to the maxDistance
                _horizontalFactor = (event.getX(0) - _startingPositionX)/_maxDistance;
                _horizontalFactor = Util.clamp(_horizontalFactor, -1.0f, 1.0f);

                _verticalFactor = (event.getY(0) - _startingPositionY)/_maxDistance;
                _verticalFactor = Util.clamp(_verticalFactor, -1.0f, 1.0f);
            }
            return true;
        }
    }
}
