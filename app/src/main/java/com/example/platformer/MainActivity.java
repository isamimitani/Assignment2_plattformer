package com.example.platformer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.platformer.input.Accelerometer;
import com.example.platformer.input.CompositeControl;
import com.example.platformer.input.Gamepad;
import com.example.platformer.input.GamepadListener;
import com.example.platformer.input.InputManager;
import com.example.platformer.input.TouchController;
import com.example.platformer.input.VirtualJoystick;

public class MainActivity extends AppCompatActivity
        implements android.hardware.input.InputManager.InputDeviceListener {

    public static final String TAG = "MainActivity";
    Game _game = null;
    GamepadListener _gamepadListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _game = findViewById(R.id.game);
        //InputManager controls = new TouchController(findViewById(R.id.touchControl));
        //InputManager controls = new VirtualJoystick(findViewById(R.id.virtual_joystick));
        //InputManager controls = new Gamepad(this);
        InputManager controls = new CompositeControl(
                new VirtualJoystick(findViewById(R.id.virtual_joystick)),
                new Gamepad(this),
                new Accelerometer(this)
        );
        _game.setControls(controls);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if(isGameControllerConnected()){
            Toast.makeText(this, "Gamepad detected!", Toast.LENGTH_LONG).show();
        }
        _game.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _game.onPause();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public void setGamepadListener(GamepadListener listener) {
        _gamepadListener = listener;
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent ev) {
        if(_gamepadListener != null){
            if(_gamepadListener.dispatchGenericMotionEvent(ev)){
                return true;
            }
        }
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent ev) {
        if(_gamepadListener != null){
            if(_gamepadListener.dispatchKeyEvent(ev)){
                return true;
            }
        }
        return super.dispatchKeyEvent(ev);
    }

    public boolean isGameControllerConnected() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInputDeviceAdded(final int deviceId) {
        Toast.makeText(this, "Input Device Added!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInputDeviceRemoved(final int deviceId) {
        //probably pause the game and show some dialog
        Toast.makeText(this, "Input Device Removed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInputDeviceChanged(final int deviceId) {
        Toast.makeText(this, "Input Device Changed!", Toast.LENGTH_LONG).show();
    }
}
