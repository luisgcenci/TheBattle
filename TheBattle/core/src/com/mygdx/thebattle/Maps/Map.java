package com.mygdx.thebattle.Maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Map {

    public static final int MAP_WIDTH = 1600;
    public static final int MAP_HEIGHT = 960;
    float unitScale = 1;
    TiledMap map;
    OrthogonalTiledMapRenderer tiledMapRenderer;

    public Map(){

        this.map = new TmxMapLoader().load("Map/map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(this.map, unitScale);
    }

    public OrthogonalTiledMapRenderer getTiledMapRenderer(){
        return this.tiledMapRenderer;
    }

}