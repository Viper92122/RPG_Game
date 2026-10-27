package io.github.echoesofnebula.items;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

@FunctionalInterface
public interface MapEntityFactory<T> {
    T create(TiledMapTileMapObject tileObj, String atlasKey, float x, float y,
             float width, float height, AtlasRegion region, Animation<TextureRegion> anim);
}
