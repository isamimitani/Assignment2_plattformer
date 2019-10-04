package com.example.platformer.input;

import com.example.platformer.utils.Util;

import java.util.ArrayList;

public class CompositeControl extends InputManager {

    private ArrayList<InputManager> _inputs = new ArrayList<>();
    private int _count = 0;

    public CompositeControl(InputManager... inputs) {
        for(InputManager im : inputs){
            _inputs.add(im);
        }
        _count = _inputs.size();
    }

    public void addInput(InputManager im){
        _inputs.add(im);
        _count = _inputs.size();
    }

    @Override
    public void onStart() {
        for(InputManager im : _inputs){
            im.onStart();
        }
    }

    @Override
    public void onStop() {
        for(InputManager im : _inputs){
            im.onStop();
        }
    }

    @Override
    public void onPause() {
        for(InputManager im : _inputs){
            im.onPause();
        }
    }

    @Override
    public void onResume() {
        for(InputManager im : _inputs){
            im.onResume();
        }
    }

    @Override
    public void update(double dt) {
        InputManager temp;
        _isJumping = false;
        _horizontalFactor = 0.0f;
        _verticalFactor = 0.0f;
        for(int i = 0; i < _count; i++){
            temp = _inputs.get(i);
            temp.update(dt);
            _isJumping = _isJumping || temp._isJumping;
            _horizontalFactor += temp._horizontalFactor;
            _verticalFactor += temp._verticalFactor;
        }
        _horizontalFactor= Util.clamp(_horizontalFactor, -1f, 1f);
        _verticalFactor = Util.clamp(_verticalFactor, -1f, 1f);
    }
}
