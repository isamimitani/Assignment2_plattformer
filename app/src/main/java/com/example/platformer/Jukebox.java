package com.example.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class Jukebox {

    static final String TAG = "Jukebox";

    private static final int MAX_STREAMS = Integer.parseInt(Game.resource.getString(R.string.max_streams));
    private static final int DEFAULT_SFX_VOLUME = Integer.parseInt(Game.resource.getString(R.string.default_sfx_volume));
    private static final int DEFAULT_MUSIC_VOLUME = Integer.parseInt(Game.resource.getString(R.string.max_streams));

    private static final String SOUNDS_PREF_KEY = Game.resource.getString(R.string.sounds_pref_key);
    private static final String MUSIC_PREF_KEY = Game.resource.getString(R.string.music_pref_key);

    public boolean _soundEnabled;
    public boolean _musicEnabled;
    private SoundPool _soundPool = null;
    private HashMap<GameEvent, Integer> _soundsMap = null;
    private MediaPlayer _bgPlayer = null;
    private Context _context = null;
    static int EXPLOSION = 0;
    static int START = 0;
    static int GAME_OVER = 0;
    static int BOOST = 0;

    public Jukebox(final Context context) {
        _context = context;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        _soundEnabled = prefs.getBoolean(SOUNDS_PREF_KEY, true);
        _musicEnabled = prefs.getBoolean(MUSIC_PREF_KEY, true);
        loadIfNeeded();
    }

    private void loadIfNeeded(){
        if(_soundEnabled){
            loadSounds();
        }
        if(_musicEnabled){
            loadMusic();
        }
    }

    @SuppressWarnings("deprecation")
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            _soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            _soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(MAX_STREAMS)
                    .build();
        }
    }

    private void loadSounds() {
        createSoundPool();
        _soundsMap = new HashMap();
        loadEventSound(GameEvent.LevelStart, ".ogg");
        loadEventSound(GameEvent.Jump, "sfx/jump.wav");
        loadEventSound(GameEvent.SpikesUpDown, ".ogg");
        loadEventSound(GameEvent.CoinPickup, "sfx/pickup_coin.wav");
        loadEventSound(GameEvent.LevelGoal, "sfx/start.ogg");
    }

    private void loadEventSound(final GameEvent event, final String fileName){
        try {
            AssetFileDescriptor afd = _context.getAssets().openFd(fileName);
            int soundId = _soundPool.load(afd, 1);
            _soundsMap.put(event, soundId);
        }catch(IOException e){
            Log.e(TAG, "loadEventSound: error loading sound " + e.toString());
        }
    }

    public void playSoundForGameEvent(GameEvent event){
        if(!_soundEnabled){return;}
        final float leftVolume = DEFAULT_SFX_VOLUME;
        final float rightVolume = DEFAULT_SFX_VOLUME;
        final int priority = 1;
        final int loop = 0; //-1 loop forever, 0 play once
        final float rate = 1.0f;
        final Integer soundID = _soundsMap.get(event);
        if(soundID != null){
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    private void loadMusic(){
        try{
            _bgPlayer = new MediaPlayer();
            AssetFileDescriptor afd = _context
                    .getAssets().openFd("sfx/farm_day.mp3");
            _bgPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            _bgPlayer.setLooping(true);
            _bgPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
            _bgPlayer.prepare();
        }catch(IOException e){
            _bgPlayer = null;
            _musicEnabled = false;
            Log.e(TAG, e.getMessage());
        }
    }

    private void unloadMusic(){
        if(_bgPlayer != null) {
            _bgPlayer.stop();
            _bgPlayer.release();
        }
    }

    public void pauseBgMusic(){
        if(_musicEnabled){
            _bgPlayer.pause();
        }
    }
    public void resumeBgMusic(){
        if(_musicEnabled){
            _bgPlayer.start();
        }
    }

    public void toggleSoundStatus(){
        _soundEnabled = !_soundEnabled;
        if(_soundEnabled){
            loadSounds();
        }else{
            unloadSounds();
        }
        PreferenceManager
                .getDefaultSharedPreferences(_context)
                .edit()
                .putBoolean(SOUNDS_PREF_KEY, _soundEnabled)
                .commit();
    }

    public void toggleMusicStatus(){
        _musicEnabled = !_musicEnabled;
        if(_musicEnabled){
            loadMusic();
        }else{
            unloadMusic();
        }
        PreferenceManager
                .getDefaultSharedPreferences(_context)
                .edit()
                .putBoolean(MUSIC_PREF_KEY, _soundEnabled)
                .commit();
    }

    private void unloadSounds(){
        if(_soundPool != null) {
            _soundPool.release();
            _soundPool = null;
            _soundsMap.clear();
        }
    }

    void destroy() {
        unloadSounds();
        unloadMusic();
        _bgPlayer = null;
    }
}
