package com.example.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.platformer.Game;
import com.example.platformer.R;
import com.example.platformer.utils.Util;

public class DynamicEntity extends StaticEntity {

    private static final float MAX_DELTA = Float.parseFloat(Game.resource.getString(R.string.max_delta));
    static final float GRAVITY = Float.parseFloat(Game.resource.getString(R.string.gravity));

    public float _velX = 0;
    public float _velY = 0;
    public float _gravity = GRAVITY;
    boolean _isOnGround = false;

    public DynamicEntity(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(double dt) {
        _x += Util.clamp((float)(_velX * dt), -MAX_DELTA, MAX_DELTA);

        if(!_isOnGround) {
            final float gravityThisTick = (float) (_gravity * dt);
            _velY += gravityThisTick;
        }
        _y += Util.clamp((float)(_velY * dt), -MAX_DELTA, MAX_DELTA);

        if(_y > _game.getWorldHeight()){
            _y = 0f;
        }
        _isOnGround = false;
    }

    @Override
    public void onCollision(Entity that) {
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if(Entity.overlap.y != 0){
            _velY = 0;
            if(Entity.overlap.y < 0){ // we've hit our feet
                _isOnGround = true;
            } // if overlap.y > 0, we've hit our head
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
