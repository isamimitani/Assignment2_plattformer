package com.example.platformer.input;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;

import com.example.platformer.MainActivity;
import com.example.platformer.utils.Util;

public class Accelerometer extends InputManager {

    private static final int AXIS_COUNT = 3; //azimuth (z), pitch (x), roll (y)
    private static final float DEGREES_PER_RADIAN = 57.2957795f;
    private static final float MAX_ANGLE = 30f;
    private static final float SHAKE_THRESHOLD = 3.25f; // m/S^2
    private static final long COOLDOWN = 300;//ms

    private MainActivity mActivity = null;

    private float[] mLastAccels = new float[AXIS_COUNT];
    private float[] mLastMagFields = new float[AXIS_COUNT];
    private float[] mRotationMatrix = new float[4*4];
    private float[] mOrientation = new float[AXIS_COUNT];
    private int mRotation = 0;
    private long mLastShake = 0;


    public Accelerometer(MainActivity activity) {
        mActivity = activity;
        mRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void update(double dt) {
        _horizontalFactor = getHorizontalAxis() / MAX_ANGLE;
        _horizontalFactor = Util.clamp(_horizontalFactor, -1.0f, 1.0f);
        _verticalFactor = 0.0f;
    }

    private SensorEventListener mAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, mLastAccels, 0, AXIS_COUNT);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener mMagneticListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, mLastMagFields, 0, AXIS_COUNT);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void registerListeners() {
        SensorManager sm = (SensorManager) mActivity
                .getSystemService(Activity.SENSOR_SERVICE);
        sm.registerListener(mAccelerometerListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(mMagneticListener,
                sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregisterListeners() {
        SensorManager sm = (SensorManager) mActivity
                .getSystemService(Activity.SENSOR_SERVICE);
        sm.unregisterListener(mAccelerometerListener);
        sm.unregisterListener(mMagneticListener);
    }

    @Override
    public void onStart() {
        registerListeners();
    }

    @Override
    public void onStop() {
        unregisterListeners();
    }

    @Override
    public void onResume() {
        registerListeners();
    }

    @Override
    public void onPause() {
        unregisterListeners();
    }

    private float getHorizontalAxis() {
        if (SensorManager.getRotationMatrix(mRotationMatrix, null, mLastAccels, mLastMagFields)) {
            if (mRotation == Surface.ROTATION_0) {
                SensorManager.remapCoordinateSystem(mRotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotationMatrix);
                SensorManager.getOrientation(mRotationMatrix, mOrientation);
                return mOrientation[1] * DEGREES_PER_RADIAN;
            }
            else {
                SensorManager.getOrientation(mRotationMatrix, mOrientation);
                return -mOrientation[1] * DEGREES_PER_RADIAN;
            }
        }
        else {
            // Case for devices that DO NOT have magnetic sensors
            if (mRotation == Surface.ROTATION_0) {
                return -mLastAccels[0] * 5;
            }
            else {
                return -mLastAccels[1] * -5;
            }
        }
    }

    private boolean isJumping(){
        if((System.currentTimeMillis()-mLastShake) < COOLDOWN){
            return false;
        }
        float x = mLastAccels[0];
        float y = mLastAccels[1];
        float z = mLastAccels[2];
        float acceleration = (float) Math.sqrt(x*x + y*y + z*z)
                - SensorManager.GRAVITY_EARTH;
        if(acceleration > SHAKE_THRESHOLD){
            mLastShake = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}
