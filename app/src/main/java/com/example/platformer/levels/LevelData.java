package com.example.platformer.levels;

import com.example.platformer.Game;
import com.example.platformer.R;

public abstract class LevelData {

    public static final String NULLSPRITE = Game.resource.getString(R.string.nullsprite);
    public static final String PLAYER = Game.resource.getString(R.string.player_bitmap_name);
    public static final int NO_TILE = 0;

    int[][] _tiles;
    int _height = 0;
    int _width = 0;

    public int getTile(final int x, final int y){
        return _tiles[y][x];
    }

    int[] getRow(final int y){
        return _tiles[y];
    }

    void updateLevelDimensions(){
        _height = _tiles.length;
        _width = _tiles[0].length;
    }

    abstract public String getSpriteName(final int tileType);

}
