package com.example.platformer.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.platformer.utils.BitmapUtils;
import com.example.platformer.utils.Util;

public class StaticEntity extends Entity {

    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xpos, final int ypos) {
        _x = xpos;
        _y = ypos;
        loadBitmap(spriteName, xpos, ypos);
    }

    protected void loadBitmap(final String spriteName, final int xpos, final int ypos) {
        destroy();
        final int widthPixels = _game.worldToScreenX(_width);
        final int heightPixels = _game.worldToScreenY(_height);
        try {
            _bitmap = BitmapUtils.loadScaledBitmap(_game.getContext(), spriteName, widthPixels, heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(final Canvas canvas, final Matrix transform, final Paint paint) {
        canvas.drawBitmap(_bitmap, transform, paint);
    }

    @Override
    public void destroy() {
        if (_bitmap != null) {
            _bitmap.recycle();
            _bitmap = null;
        }
    }

}
