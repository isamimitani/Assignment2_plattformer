package com.example.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.platformer.Game;
import com.example.platformer.GameEvent;
import com.example.platformer.Jukebox;
import com.example.platformer.R;
import com.example.platformer.input.InputManager;

public class Player extends DynamicEntity {
    static final String TAG = "Player";
    static final float PLAYER_RUN_SPEED = Float.parseFloat(Game.resource.getString(R.string.player_run_speed)); // meter per second
    static final float PLAYER_JUMP_FORCE = -(GRAVITY / 2); // meter per second
    static final float MIN_INPUT_TO_TURN = Float.parseFloat(Game.resource.getString(R.string.min_input_to_turn)); // 5% joystick input before we start turning animations
    private final int LEFT = 1;
    private final int RIGHT = -1;
    private int mFacing = LEFT;


    public Player(final String spriteName, final int xpos, final int ypos) {
        super(spriteName, xpos, ypos);
        _width = DEFAULT_DIMENSION;
        _height = DEFAULT_DIMENSION;
        loadBitmap(spriteName, xpos, ypos);
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        transform.preScale(mFacing, 1.0f);
        if(mFacing == RIGHT){
            final float offset = _game.worldToScreenX(_width);
            transform.postTranslate(offset, 0);
        }
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final Jukebox jukebox = _game.getJukebox();
        final float direction = controls._horizontalFactor;
        _velX = direction * PLAYER_RUN_SPEED;
        updateFacingDirection(direction);
        if (controls._isJumping && _isOnGround) {
            jukebox.playSoundForGameEvent(GameEvent.Jump);
            _velY = PLAYER_JUMP_FORCE;
            _isOnGround = false;
        }
        super.update(dt);
    }

    private void updateFacingDirection(final float controlDirection){
        if(Math.abs(controlDirection) < MIN_INPUT_TO_TURN){
            return;
        }
        if(controlDirection < 0){
            mFacing = LEFT;
        } else if(controlDirection > 0){
            mFacing = RIGHT;
        }
    }

}
