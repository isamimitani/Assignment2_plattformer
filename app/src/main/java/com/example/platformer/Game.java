package com.example.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.platformer.entities.Entity;
import com.example.platformer.input.InputManager;
import com.example.platformer.levels.LevelManager;
import com.example.platformer.levels.TestLevel;

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    private static final Point _position = new Point();
    public static Resources resource = null;

    public static int STAGE_WIDTH = 0;
    public static int STAGE_HEIGHT = 0;
    private static float METERS_TO_SHOW_X = 0; //set the value you want fixed
    private static float METERS_TO_SHOW_Y = 0;  //the other is calculated at runtime!

    private static int BG_COLOR = 0;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;

//    private TestLevel _testLevel = new TestLevel();

    private SurfaceHolder _holder = null;
    private Canvas _canvas = null;
    private final Paint _paint = null;
    private final Matrix _transform = new Matrix();

    private Jukebox _jukebox = null;
    private MainActivity _activity = null;

    private LevelManager _level = null;
    private InputManager _controls = new InputManager();
    private Viewport _camera = null;
    public final ArrayList<Entity> _visibleEntities = new ArrayList<>();

    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        resource = context.getResources();
        STAGE_WIDTH = Integer.parseInt(resource.getString(R.string.stage_width));
        STAGE_HEIGHT = Integer.parseInt(resource.getString(R.string.stage_height));
        METERS_TO_SHOW_X = Float.parseFloat(resource.getString(R.string.meter_to_show_x));
        METERS_TO_SHOW_Y = Float.parseFloat(resource.getString(R.string.meter_to_show_y));
        BG_COLOR = Color.rgb(Integer.parseInt(resource.getString(R.string.bgcolor_red)),
                Integer.parseInt(resource.getString(R.string.bgcolor_green)),
                Integer.parseInt(resource.getString(R.string.bgcolor_blue)));

        _activity = (MainActivity)context;
        _activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        _jukebox = new Jukebox(context);

        final int TARGET_HEIGHT = Integer.parseInt(resource.getString(R.string.target_height));
        final int actualHeight = getScreenHeight();
        final float ratio = (TARGET_HEIGHT >= actualHeight) ? 1 : (float) TARGET_HEIGHT / actualHeight;
        STAGE_WIDTH = (int) (ratio * getScreenWidth());
        STAGE_HEIGHT = TARGET_HEIGHT;

        _camera = new Viewport(STAGE_WIDTH, STAGE_HEIGHT, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        Log.d(TAG, _camera.toString());

        Entity._game = this;

        _holder = getHolder();
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _holder.addCallback(this);

        _level = new LevelManager(new TestLevel());

        Log.d(TAG, "Resolution:" + STAGE_WIDTH + " : " + STAGE_HEIGHT);
    }

    public Jukebox getJukebox() {
        return _jukebox;
    }
    public InputManager getControls() {
        return _controls;
    }

    public void setControls(final InputManager controls) {
        _controls.onPause();
        _controls.onStop();
        _controls = controls;
    }

    public float getWorldHeight() {
        return _level._levelHeight;
    }

    public float getWorldWidth() {
        return _level._levelWidth;
    }

    public int worldToScreenX(float worldDistance) {
        return (int) (worldDistance * _camera.getPixelsPerMeterX());
    }

    public int worldToScreenY(float worldDistance) {
        return (int) (worldDistance * _camera.getPixelsPerMeterY());
    }

    public float screenToWorldX(float pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterX());
    }

    public float screenWorldY(float pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterY());
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format,
                               final int width, final int height) {
        Log.d(TAG, "surfaceChanged");
        Log.d(TAG, "/t Width: " + width + " Height: " + height);
        if (_gameThread != null && _isRunning) {
            Log.d(TAG, "GameThread started!");
            _gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    public void onGameEvent(GameEvent gameEvent, Entity e /*can be null!*/) {
        _jukebox.playSoundForGameEvent(gameEvent);
    }

    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        while (_isRunning) {
            Log.d(TAG, "running!");
            final double deltaTime = (System.nanoTime() - lastFrame) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            // dynamic entities / player
            // controllers
            _controls.update(deltaTime);
            update(deltaTime);
            buildVisibleSet();
            render(_visibleEntities);
        }
    }

    private void update(final double dt) {
        _camera.lookAt(_level._player);
        _level.update(dt);
    }

    private void buildVisibleSet() {
        _visibleEntities.clear();
        for (final Entity e : _level._entities) {
            if (_camera.inView(e)) {
                _visibleEntities.add(e);
            }
        }
    }

    private void render(final ArrayList<Entity> visibleEntities) {
        if (!lockCanvas()) {
            return;
        }
        try {
            _canvas.drawColor(BG_COLOR);

            for (final Entity e : visibleEntities) {
                _transform.reset();
                _camera.worldToScreen(e, _position);
                _transform.postTranslate(_position.x, _position.y);
                e.render(_canvas, _transform, _paint);
            }
        } finally {
            _holder.unlockCanvasAndPost(_canvas);
        }
    }

    private boolean lockCanvas() {
        if (!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _jukebox.resumeBgMusic();
        _controls.onResume();
        _gameThread = new Thread(this);
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        _controls.onPause();
        _jukebox.pauseBgMusic();
        while (_gameThread.getState() != Thread.State.TERMINATED) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;
        if (_level != null) {
            _level.destroy();
        }
        if(_jukebox != null){
            _jukebox.destroy();
        }
        _controls = null;
        Entity._game = null;
        _holder.removeCallback(this);
    }

}
