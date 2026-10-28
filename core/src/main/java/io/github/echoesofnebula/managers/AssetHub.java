package io.github.echoesofnebula.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetHub {
    private final AssetManager manager;
    private static final String ATLAS_PATH = "sprites/game_assets.atlas";
    private TextureAtlas atlas;

    public AssetHub() {
        this.manager = new AssetManager();

        // CRITICAL FIX: Tell the AssetManager how to load TiledMaps!
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void queueInitialAssets() {
        manager.load(ATLAS_PATH, TextureAtlas.class);
    }

    public void queueMap(String mapPath) {
        if (!manager.isLoaded(mapPath, TiledMap.class)) {
            manager.load(mapPath, TiledMap.class);
        }
    }

    public boolean update() {
        return manager.update();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public void finishLoading() {
        manager.finishLoading();
        if (this.atlas == null && manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
            this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
        }
    }

    public TextureAtlas.AtlasRegion getRegion(String name) {
        if (atlas == null) {
            if (manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
                this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
            } else {
                System.err.println("❌ ERROR: AssetHub.getRegion() called before atlas was loaded!");
                return null;
            }
        }
        return atlas.findRegion(name);
    }
    private final ObjectMap<String, BitmapFont> fonts = new ObjectMap<>();

    public void addFont(String name, BitmapFont font) { fonts.put(name, font); }
    public Array<TextureAtlas.AtlasRegion> getRegions(String name) {
        if (atlas == null) {
            if (manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
                this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
            } else {
                System.err.println("❌ ERROR: AssetHub.getRegions() called before atlas was loaded!");
                return null;
            }
        }
        return atlas.findRegions(name);
    }

    public TiledMap getMap(String mapPath) {
        if (manager.isLoaded(mapPath, TiledMap.class)) {
            return manager.get(mapPath, TiledMap.class);
        }
        return null;
    }

    public void dispose() {
        manager.dispose();
    }

    public BitmapFont getFont(String name) { return fonts.get(name); }
}
