package com.example.platformer.input;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.example.platformer.MainActivity;
import com.example.platformer.utils.Util;

public class Gamepad extends InputManager implements GamepadListener {

    private MainActivity _activity = null;

    public Gamepad(MainActivity activity){
        _activity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(_activity != null) {
            _activity.setGamepadListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(_activity != null) {
            _activity.setGamepadListener(null);
        }
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if((event.getSource() & InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK){
            return false; //we don't consume this event
        }
        _horizontalFactor = getInputFactor(event, MotionEvent.AXIS_X, MotionEvent.AXIS_HAT_X);
        _verticalFactor = getInputFactor(event, MotionEvent.AXIS_Y, MotionEvent.AXIS_HAT_Y);
        return true; //we did consume this event
    }

    private float getInputFactor(final MotionEvent event, final int axis, final int fallbackAxis){
        InputDevice device = event.getDevice();
        int source = event.getSource();
        float result = event.getAxisValue(axis);
        InputDevice.MotionRange range = device.getMotionRange(axis, source);
        if(Math.abs(result) <= range.getFlat()){
            result = event.getAxisValue(fallbackAxis);
            range = device.getMotionRange(fallbackAxis, source);
            if(Math.abs(result) <= range.getFlat()){
                result = 0.0f;
            }
        }
        return Util.clamp(result, -1f, 1f);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean wasConsumed = false;
        if(action == MotionEvent.ACTION_DOWN){
            if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                _verticalFactor -= 1;
                wasConsumed = true;
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                _verticalFactor += 1;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                _horizontalFactor -= 1;
                wasConsumed = true;
            } else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                _horizontalFactor += 1;
                wasConsumed = true;
            }
            if(isJumpKey(keyCode)){
                _isJumping = true;
                wasConsumed = true;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                _verticalFactor += 1;
                wasConsumed = true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                _verticalFactor -= 1;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                _horizontalFactor += 1;
                wasConsumed = true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                _horizontalFactor -= 1;
                wasConsumed = true;
            }
            if(isJumpKey(keyCode)){
                _isJumping = false;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                _activity.onBackPressed(); //backwards comp
            }
        }
        return wasConsumed;
    }

    public boolean isJumpKey(final int keyCode){
        return keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_BUTTON_A
                || keyCode == KeyEvent.KEYCODE_BUTTON_X
                || keyCode == KeyEvent.KEYCODE_BUTTON_Y;
    }
}
