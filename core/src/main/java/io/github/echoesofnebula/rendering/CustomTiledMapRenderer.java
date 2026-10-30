package io.github.echoesofnebula.rendering;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class CustomTiledMapRenderer extends OrthogonalTiledMapRenderer {


    public CustomTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    @Override
    public void renderObject(MapObject object) {
        if (object instanceof TiledMapTileMapObject) {
            TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;

            if (tileObject.getTile() != null && tileObject.getTile().getTextureRegion() != null) {

                float x = tileObject.getX() * unitScale;
                float y = tileObject.getY() * unitScale;

                // 1. Read Tiled's built-in scale multipliers (if you scaled it in Tiled)
                float scaleX = tileObject.getScaleX();
                float scaleY = tileObject.getScaleY();

                // 2. Read the native dimensions saved directly inside your Tiled file footprint
                float width = tileObject.getProperties().get("width", Float.class) * unitScale;
                float height = tileObject.getProperties().get("height", Float.class) * unitScale;

                // 3. Draw using Tiled's native configurations!
                getBatch().draw(
                    tileObject.getTile().getTextureRegion(),
                    x, y,
                    0, 0, // Origin X, Y
                    width, height,
                    scaleX, scaleY,
                    tileObject.getRotation()
                );
            }
        }
    }
}
