package com.example.platformer.levels;

import android.util.SparseArray;

import com.example.platformer.Game;
import com.example.platformer.R;

public class TestLevel extends LevelData {

    private final SparseArray<String> mTileIdToSpriteName = new SparseArray<>();

    public TestLevel(){
        mTileIdToSpriteName.put(0, Game.resource.getString(R.string.background_bitmap_name));
        mTileIdToSpriteName.put(1, PLAYER);
        mTileIdToSpriteName.put(2, Game.resource.getString(R.string.ground_bitmap_name));
        mTileIdToSpriteName.put(3, Game.resource.getString(R.string.ground_left_bitmap_name));
        mTileIdToSpriteName.put(4, Game.resource.getString(R.string.ground_right_bitmap_name));

        _tiles = new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

        updateLevelDimensions();
    }
    @Override
    public String getSpriteName(int tileType) {
        final String fileName = mTileIdToSpriteName.get(tileType);
        if(fileName != null){
            return fileName;
        }
        return NULLSPRITE;
    }
}
