package com.example.platformer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Util {

    static Resources resouces = null;
    private final static java.util.Random RNG = new java.util.Random();
    //static HighScores highScores = null;

    public static Resources getResources() {
        return resouces;
    }

//    public static HighScores getHighScores(Context context) {
//        if (highScores == null) {
//            try {
//                highScores = (HighScores) InternalStorage
//                        .readObject(context, Util.getResources().getString(R.string.highScoreKey));
//            } catch (FileNotFoundException e) {
//                Log.e(Util.class.toString(), e.getMessage());
//                highScores = new HighScores();
//            } catch (IOException e) {
//                Log.e(Util.class.toString(), e.getMessage());
//            } catch (ClassNotFoundException e) {
//                Log.e(Util.class.toString(), e.getMessage());
//            }
//        }
//        return highScores;
//    }
//
//    public static void saveHighScores(Context context, HighScores highScores) {
//        try {
//            InternalStorage.writeObject(
//                    context, getResources().getString(R.string.highScoreKey), highScores);
//        } catch (IOException e) {
//            Log.e(Util.class.toString(), e.getMessage());
//        }
//    }

    public static float wrap(float val, final float min, final float max) {
        if (val < min) {
            val = max;
        } else if (val > max) {
            val = min;
        }
        return val;
    }

    public static float clamp(float val, final float min, final float max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

    public static boolean coinFlip(){
        return RNG.nextFloat() > 0.5 ;
    }

    public static float nextFloat(){
        return RNG.nextFloat();
    }

    public static int nextInt( final int max){
        return RNG.nextInt(max);
    }

    public static int between( final int min, final int max){
        return RNG.nextInt(max-min)+min;
    }

    public static float between( final float min, final float max){
        return min+RNG.nextFloat()*(max-min);
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // code from https://pastebin.com/Yrfy28iW
    public static void expect(final boolean condition, final String tag) {
        Util.expect(condition, tag, "Expectation was broken.");
    }

    public static void expect(final boolean condition, final String tag, final String message) {
        if (!condition) {
            Log.e(tag, message);
        }
    }

    public static void require(final boolean condition) {
        Util.require(condition, "Assertion failed!");
    }

    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
